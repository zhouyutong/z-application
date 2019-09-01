package com.zhouyutong.zapplication.kafkaclient;

import com.alibaba.fastjson.JSONObject;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author tianhong
 * 1、默认开启线程池了，不需要再显示的调用线程池
 * 2、TOPIC_FETCH_THREAD_SIZE：依赖topic分区数目和消费者数目,需使用者自己判断，多节点部署需特别注意
 * <dependency>
 * <groupId>org.apache.kafka</groupId>
 * <artifactId>kafka_2.10</artifactId>
 * <version>0.8.2.1</version>
 * </dependency>
 */
@Slf4j
public class KafkaConsumerProxy {
    private static Map<String, KafkaConsumerProxy> instanceFactoryMap;
    private String zkHosts;
    private String topic;
    private String group;
    private Map<String, Integer> topicMap;
    /**
     * 这个值见注意2，合适的值能够提高消费的效率
     */
    private static final Integer TOPIC_FETCH_THREAD_SIZE = 1;
    private ConsumerConnector consumerConnector;
    private AtomicBoolean hasStartConsumer = new AtomicBoolean(false);
    /**
     * 负责启动消费，避免方法调用阻塞上下文
     */
    private ExecutorService singleThreadExecutor;
    /**
     * 负责并发处理消息
     */
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private Vector<KafkaMessageHandler> handlers = new Vector<>();

    //消费速度,间隔多少毫秒
    private Long consumerInterval;

    private KafkaConsumerProxy(String zkHosts, String topic, String group, Integer fetchThreadNum, Long consumerInterval) {
        this.zkHosts = zkHosts;
        this.topic = topic;
        this.group = group;
        ConsumerConfig consumerConfig = new ConsumerConfig(initConsumerConfigProperties(zkHosts, group));
        Map<String, Integer> map = new HashMap<>();
        if (fetchThreadNum == null || fetchThreadNum <= 0) {
            map.put(topic, TOPIC_FETCH_THREAD_SIZE);
        } else {
            map.put(topic, fetchThreadNum);
        }
        this.topicMap = map;
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        this.singleThreadExecutor = Executors.newSingleThreadExecutor();
        this.consumerInterval = consumerInterval;
    }

    private static Properties initConsumerConfigProperties(String zkHosts, String group) {
        Properties consumerConfigProperties = new Properties();
        //必选
        consumerConfigProperties.put("zookeeper.connect", zkHosts);
        //必选
        consumerConfigProperties.put("group.id", group);

        //socket超时时间
        consumerConfigProperties.put("socket.timeout.ms", "30000");
        //socket receive buffer
        consumerConfigProperties.put("socket.buffersize", "64*1024");
        //每次fetch请求将得到多条消息,控制在一个请求中获取的消息的字节数
        consumerConfigProperties.put("fetch.messages.max.bytes", "1024*1024");
        //这个参数避免在没有新数据的情况下重复频繁的拉数据。 如果拉到空数据，则多推后这个时间
        consumerConfigProperties.put("backoff.increment.ms", "1000");
        //high level consumer内部缓存拉回来的消息到一个队列中。 这个值控制这个队列的大小
        consumerConfigProperties.put("queued.max.message.chunks", "1");
        //如果true,consumer定期地往zookeeper写入每个分区的offset
        consumerConfigProperties.put("auto.commit.enable", "true");
        //往zookeeper上写offset的频率
        consumerConfigProperties.put("auto.commit.interval.ms", "1000");
        //如果offset出了返回，则 smallest: 自动设置reset到最小的offset. largest : 自动设置offset到最大的offset. 其它值不允许，会抛出异常
        consumerConfigProperties.put("auto.offset.reset", "largest");
        //默认-1,consumer在没有新消息时无限期的block。如果设置一个正值， 一个超时异常会抛出
        consumerConfigProperties.put("consumer.timeout.ms", "-1");
        //rebalance时的最大尝试次数
        consumerConfigProperties.put("rebalance.retries.max", "4");

        return consumerConfigProperties;
    }

    public static synchronized KafkaConsumerProxy getInstance(String zkHosts, String topic, String group, Integer fetchThreadNum, Long consumerInterval) throws Exception {
        String key = zkHosts + ":" + topic + ":" + group;
        if (instanceFactoryMap == null) {
            instanceFactoryMap = new HashMap<>();
            KafkaConsumerProxy kafkaConsumerProxy = new KafkaConsumerProxy(zkHosts, topic, group, fetchThreadNum, consumerInterval);
            instanceFactoryMap.put(key, kafkaConsumerProxy);
            return kafkaConsumerProxy;
        } else if (instanceFactoryMap.get(key) == null) {
            KafkaConsumerProxy kafkaConsumerProxy = new KafkaConsumerProxy(zkHosts, topic, group, fetchThreadNum, consumerInterval);
            instanceFactoryMap.put(key, kafkaConsumerProxy);
            return kafkaConsumerProxy;
        } else {
            return instanceFactoryMap.get(key);
        }
    }

    public KafkaConsumerProxy addHandler(KafkaMessageHandler handler) {
        this.handlers.add(handler);
        return this;
    }

    public void startConsumer() throws Exception {
        if (hasStartConsumer.compareAndSet(false, true)) {

            this.singleThreadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicMap);
                    for (String key : consumerMap.keySet()) {
                        //因为只有一个key，所以不会被阻塞住
                        List<KafkaStream<byte[], byte[]>> value = consumerMap.get(key);
                        final Iterator<KafkaStream<byte[], byte[]>> iterator = value.iterator();
                        while (iterator.hasNext()) {
                            //topicMap 指定了多少个消费线程，就会有几个subValue,这个线程可以控制消费速度
                            //重启的时候消息丢失,改为原生线程池,队列里最多放50个任务,处理不过来交给调用者,所以要根据业务生产速度,做好消费控制
                            //ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
                            ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(50), new ThreadPoolExecutor.CallerRunsPolicy());
                            singleThreadExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (consumerInterval != null && consumerInterval.longValue() > 0) {
                                        try {
                                            Thread.sleep(consumerInterval);
                                        } catch (InterruptedException e) {

                                        }
                                    }
                                    if (iterator.hasNext()) {
                                        KafkaStream<byte[], byte[]> subValue = iterator.next();
                                        ConsumerIterator<byte[], byte[]> msgIterator = subValue.iterator();
                                        while (msgIterator.hasNext()) {
                                            MessageAndMetadata<byte[], byte[]> msgObj = msgIterator.next();
                                            final String message = new String(msgObj.message());
                                            for (int i = 0; i < handlers.size(); i++) {
                                                final KafkaMessageHandler handler = handlers.get(i);
                                                fixedThreadPool.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            handler.handle(message);
                                                        } catch (Throwable throwable) {
                                                            log.error("kafka 消费异常，message {}, cause {}", throwable.getMessage(), throwable.getCause(), throwable);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                            singleThreadExecutor.shutdown();
                        }

                    }
                }
            });
        } else {
            throw new Exception("已经开始消费了，可以忽略此异常");
        }

    }

    public interface KafkaMessageHandler {
        void handle(String message);
    }

    public static void main(String[] args) {
        try {
            KafkaConsumerProxy.getInstance("ZK_HOSTS", "KAFKA_ORDER_TOPIC", "KAFKA_ORDER_GROUP", 3, 1000l).addHandler(new KafkaConsumerProxy.KafkaMessageHandler() {
                @Override
                public void handle(String message) {
                    if (StringUtils.isBlank(message)) {
                        return;
                    }
                    log.debug("kafka order message {}", message);
                    JSONObject json = JSONObject.parseObject(message);
                    try {
                        //处理json
                    } catch (Exception e) {

                    }
                }
            }).startConsumer();
        } catch (Exception e) {
            log.error("kafka消费订单数据，启动异常，message {}, cause {}", e.getMessage(), e.getCause(), e);
        }
    }
}
