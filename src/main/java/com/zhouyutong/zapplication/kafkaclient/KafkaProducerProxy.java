package com.zhouyutong.zapplication.kafkaclient;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaProducerProxy {
  private static Map<String, KafkaProducerProxy> instanceFactoryMap;
  private String kafkaBrokers;
  private Producer<String, String> producer;

  private KafkaProducerProxy(String kafkaBrokers) {
    this.kafkaBrokers = kafkaBrokers;
    ProducerConfig config = new ProducerConfig(initProducerConfigProperties(kafkaBrokers));
    //第一个String是partition key的类型，第二个key是消息的类型
    this.producer = new Producer<String, String>(config);
  }

  public static KafkaProducerProxy getInstance(String kafkaBrokers) throws Exception {
    if (kafkaBrokers == null) {
      throw new Exception("kafkaBrokers is blank");
    }
    String key = kafkaBrokers;
    if (instanceFactoryMap == null) {
      instanceFactoryMap = new HashMap<>();
      KafkaProducerProxy kafkaProducerProxy = new KafkaProducerProxy(kafkaBrokers);
      instanceFactoryMap.put(key, kafkaProducerProxy);
      return kafkaProducerProxy;
    } else if (instanceFactoryMap.get(key) == null) {
      KafkaProducerProxy kafkaProducerProxy = new KafkaProducerProxy(kafkaBrokers);
      instanceFactoryMap.put(key, kafkaProducerProxy);
      return kafkaProducerProxy;
    } else {
      return instanceFactoryMap.get(key);
    }
  }

  private static Properties initProducerConfigProperties(String kafkaBrokers) {
    Properties props = new Properties();
    //这个列表可以是broker的一个子集
    props.put("metadata.broker.list", kafkaBrokers);
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    //可选
    props.put("partitioner.class", "kafka.producer.DefaultPartitioner");
    //生产消息确认，避免消息丢失
    //0，意味着producer永远不会等待一个来自broker的ack，这就是0.7版本的行为。这个选项提供了最低的延迟，但是持久化的保证是最弱的，当server挂掉的时候会丢失一些数据
    //1，意味着在leader replica已经接收到数据后，producer会得到一个ack。这个选项提供了更好的持久性，因为在server确认请求成功处理后，client才会返回。如果刚写到leader上，还没来得及复制leader就挂了，那么消息才可能会丢失
    //-1，意味着在所有的ISR都接收到数据后，producer才得到一个ack。这个选项提供了最好的持久性，只要还有一个replica存活，那么数据就不会丢失
    props.put("request.required.acks", "1");
    //决定消息是否应在一个后台线程异步发送。合法的值为sync，表示异步发送；sync表示同步发送。设置为async则允许批量发送请求，这回带来更高的吞吐量，但是client的机器挂了的话会丢失还没有发送的数据。异步意味着消息将会在本地buffer，并适时批量发送，推荐使用
    props.put("producer.type", "sync");
    //当使用异步模式时async，缓冲数据的最大时间，默认5000
    props.put("queue.buffering.max.ms", "100000");
    return props;
  }

  public void sendMsg(String topic, String routeKey, String msg) {
    try {
      if (topic != null && msg != null) {
        //topic 、分区key【如果不指定这个值，即使设置了partitioner.class，也会发到随机的分区上】、消息
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, routeKey, msg);
        this.producer.send(data);
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      this.producer.close();
      instanceFactoryMap.remove(this.kafkaBrokers);
    }

  }

  public static void main(String[] args) throws Exception {
    String topic = "order_test";
    String routeKey = null;
    String msg = "hello world";
    KafkaProducerProxy.getInstance("kafkatest02.edaijia-inc.cn:9092,kafkatest02.edaijia-inc.cn:9093,kafkatest02.edaijia-inc.cn:9094").sendMsg(topic, routeKey, msg);
  }
}
