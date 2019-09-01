package com.zhouyutong.zapplication.kafkaclient;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kafka消费客户端，手动提交offset
 */
@Slf4j
public class KafkaConsumerManualCommitProxy {

    private static Map<String, KafkaConsumerManualCommitProxy> instanceFactoryMap;
    //消费线程数
    private static final Integer TOPIC_FETCH_THREAD_SIZE = 1;

    private String zkHosts;
    private String topic;
    private String group;

    private Map<String, Integer> topicMap;
    private ConsumerConnector consumerConnector;

    //同一实例（zkHosts、topic、group组合一样）只启动一次
    private AtomicBoolean hasStartConsumer = new AtomicBoolean(false);
    //启动线程
    private ExecutorService singleThreadExecutor;
    //拉取消息，业务处理，提交offset线程
    private ExecutorService executor;

    //业务处理过滤器
    private Vector<KafkaConsumerManualCommitProxy.KafkaManualCommitMessageHandler> handlers = new Vector<>();

    private KafkaConsumerManualCommitProxy(String zkHosts, String topic, String group, Integer fetchThreadNum) {
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
    }

    private static Properties initConsumerConfigProperties(String zkHosts, String group) {
        Properties consumerConfigProperties = new Properties();
        //必选
        consumerConfigProperties.put("zookeeper.connect", zkHosts);
        //必选
        consumerConfigProperties.put("group.id", group);

        //socket超时时间,30 * 1000,The socket timeout for network requests. The actual timeout set will be max.fetch.wait + socket.timeout.ms.
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
        consumerConfigProperties.put("auto.commit.enable", "false");
        //往zookeeper上写offset的频率
        //consumerConfigProperties.put("auto.commit.interval.ms", "1000");
        //如果offset出了返回，则 smallest: 自动设置reset到最小的offset. largest : 自动设置offset到最大的offset. 其它值不允许，会抛出异常
        consumerConfigProperties.put("auto.offset.reset", "largest");
        //默认-1,consumer在没有新消息时无限期的block。如果设置一个正值， 一个超时异常会抛出
        consumerConfigProperties.put("consumer.timeout.ms", "-1");
        //rebalance时的最大尝试次数
        consumerConfigProperties.put("rebalance.retries.max", "4");

        return consumerConfigProperties;
    }

    public static synchronized KafkaConsumerManualCommitProxy getInstance(String zkHosts, String topic, String group, Integer fetchThreadNum) throws Exception {
        String key = zkHosts + ":" + topic + ":" + group;
        if (instanceFactoryMap == null) {
            instanceFactoryMap = new ConcurrentHashMap<>();
            KafkaConsumerManualCommitProxy kafkaConsumerManualCommitProxy = new KafkaConsumerManualCommitProxy(zkHosts, topic, group, fetchThreadNum);
            instanceFactoryMap.put(key, kafkaConsumerManualCommitProxy);
            return kafkaConsumerManualCommitProxy;
        } else if (instanceFactoryMap.get(key) == null) {
            KafkaConsumerManualCommitProxy kafkaConsumerManualCommitProxy = new KafkaConsumerManualCommitProxy(zkHosts, topic, group, fetchThreadNum);
            instanceFactoryMap.put(key, kafkaConsumerManualCommitProxy);
            return kafkaConsumerManualCommitProxy;
        } else {
            return instanceFactoryMap.get(key);
        }
    }

    public KafkaConsumerManualCommitProxy addHandler(KafkaConsumerManualCommitProxy.KafkaManualCommitMessageHandler handler) {
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
                        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(key);

                        log.info("kafka topic {} streams size ", topic, streams.size());
                        executor = Executors.newFixedThreadPool(streams.size());
                        for (final KafkaStream stream : streams) {
                            executor.execute(new Runnable() {
                                @Override
                                public void run() {

                                    ConsumerIterator<byte[], byte[]> it = stream.iterator();
                                    while (it.hasNext()) {
                                        MessageAndMetadata<byte[], byte[]> msgObj = it.next();
                                        log.debug("kafka consumer thread {}, partition {}, offset {}", Thread.currentThread().getName(), msgObj.partition(), msgObj.offset());
                                        for (int i = 0; i < handlers.size(); i++) {
                                            final KafkaConsumerManualCommitProxy.KafkaManualCommitMessageHandler handler = handlers.get(i);
                                            boolean result = handler.handle(new String(msgObj.message()));
                                            if (result) {
                                                consumerConnector.commitOffsets(true);
                                            } else {
                                                //业务处理失败
                                                log.error("kafka consumer thread {}, partition {}, offset {}, message {}", Thread.currentThread().getName(), msgObj.partition(), msgObj.offset(), new String(msgObj.message()));
                                            }

                                        }

                                    }

                                }
                            });
                        }
                    }
                }
            });
        } else {
            throw new Exception("已经开始消费了");
        }

    }

    public interface KafkaManualCommitMessageHandler {
        boolean handle(String message);
    }

    public static void main(String[] args) {
        try {
            KafkaConsumerManualCommitProxy.getInstance("172.16.170.70:2181", "srv-monitor", "thg", 3).addHandler(new KafkaManualCommitMessageHandler() {
                @Override
                public boolean handle(String message) {

                    try {
                        log.info("kafka receive msg {}", message);
                        //处理业务逻辑
                        return true;
                    } catch (Throwable t) {
                        return false;
                    }

                }
            }).startConsumer();
        } catch (Exception e) {

        }
    }
}
