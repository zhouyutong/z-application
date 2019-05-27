package com.zhouyutong.zapplication.kafkaclient;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KafkaReConsumerProxy {

  private List<String> m_replicaBrokers = new ArrayList<String>();

  private Vector<KafkaMessageReHandler> reHandlers = new Vector<>();

  private KafkaReConsumerProxy() {
    m_replicaBrokers = new ArrayList<String>();
  }

  private static KafkaReConsumerProxy kafkaReConsumerProxy = new KafkaReConsumerProxy();

  public static KafkaReConsumerProxy getInstance() {
    return kafkaReConsumerProxy;
  }

  public KafkaReConsumerProxy addHandler(KafkaReConsumerProxy.KafkaMessageReHandler reHandler) {
    this.reHandlers.add(reHandler);
    return this;
  }

  public void run(final List<String> a_seedBrokers, final Integer a_port, final String a_topic, final Integer a_partition, final Long userOffset, final Long maxReads) throws Exception {
    ExecutorService singlePool = Executors.newSingleThreadExecutor();
    singlePool.execute(new Runnable() {
      @Override
      public void run() {
        // find the meta data about the topic and partition we are interested in
        Long a_maxReads = maxReads;
        PartitionMetadata metadata = findLeader(a_seedBrokers, a_port, a_topic, a_partition);
        if (metadata == null) {
          log.info("Can't find metadata for Topic and Partition. Exiting");
          return;
        }
        if (metadata.leader() == null) {
          log.info("Can't find Leader for Topic and Partition. Exiting");
          return;
        }
        String leadBroker = metadata.leader().host();
        String clientName = "Client_" + a_topic + "_" + a_partition;

        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
        long readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime(), clientName);

        int numErrors = 0;
        while (a_maxReads > 0) {
          if (consumer == null) {
            consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
          }
          FetchRequest req = new FetchRequestBuilder()
            .clientId(clientName)
            .addFetch(a_topic, a_partition, readOffset, 100000) // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
            .build();
          FetchResponse fetchResponse = consumer.fetch(req);

          if (fetchResponse.hasError()) {
            numErrors++;
            // Something went wrong!
            short code = fetchResponse.errorCode(a_topic, a_partition);
            log.info("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
            if (numErrors > 5) break;
            if (code == ErrorMapping.OffsetOutOfRangeCode()) {
              // We asked for an invalid offset. For simple case ask for the last element to reset
              readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
              continue;
            }
            consumer.close();
            consumer = null;
            try {
              leadBroker = findNewLeader(leadBroker, a_topic, a_partition, a_port);
            } catch (Exception e) {

            }
            continue;
          }
          numErrors = 0;

          long numRead = 0;
          for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) {
            long currentOffset = messageAndOffset.offset();
            log.info("重消费offset, currentOffset {}, readOffset {}", currentOffset, readOffset);
            if (currentOffset < readOffset) {
              log.info("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
              continue;
            }
            if (currentOffset < userOffset) {
              log.info("重消费消息跳过老数据");
              continue;
            }
            readOffset = messageAndOffset.nextOffset();
            ByteBuffer payload = messageAndOffset.message().payload();

            byte[] bytes = new byte[payload.limit()];
            payload.get(bytes);
            String msg = null;
            try {
              msg = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
            }
            log.info("重消费消息 : " + "a_maxReads " + a_maxReads + " , " + String.valueOf(messageAndOffset.offset()) + ": " + msg);
            for (int i = 0; i < reHandlers.size(); i++) {
              KafkaReConsumerProxy.KafkaMessageReHandler reHandler = reHandlers.get(i);
              reHandler.reHandle(msg);
            }
            numRead++;
            a_maxReads--;
            if (a_maxReads == 0) {
              break;
            }
          }

          if (numRead == 0) {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException ie) {
            }
          }
        }
        if (consumer != null) consumer.close();
      }
    });
    singlePool.shutdown();
  }

  private long getLastOffset(SimpleConsumer consumer, String topic, int partition,
                             long whichTime, String clientName) {
    TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
    Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
    requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
    kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
      requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
    OffsetResponse response = consumer.getOffsetsBefore(request);

    if (response.hasError()) {
      log.info("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
      return 0;
    }
    long[] offsets = response.offsets(topic, partition);
    return offsets[0];
  }

  private String findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_port) throws Exception {
    for (int i = 0; i < 3; i++) {
      boolean goToSleep = false;
      PartitionMetadata metadata = findLeader(m_replicaBrokers, a_port, a_topic, a_partition);
      if (metadata == null) {
        goToSleep = true;
      } else if (metadata.leader() == null) {
        goToSleep = true;
      } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
        // first time through if the leader hasn't changed give ZooKeeper a second to recover
        // second time, assume the broker did recover before failover, or it was a non-Broker issue
        //
        goToSleep = true;
      } else {
        return metadata.leader().host();
      }
      if (goToSleep) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }
      }
    }
    log.info("Unable to find new leader after Broker failure. Exiting");
    throw new Exception("Unable to find new leader after Broker failure. Exiting");
  }

  private PartitionMetadata findLeader(List<String> a_seedBrokers, int a_port, String a_topic, int a_partition) {
    PartitionMetadata returnMetaData = null;
    loop:
    for (String seed : a_seedBrokers) {
      SimpleConsumer consumer = null;
      try {
        consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024, "leaderLookup");
        List<String> topics = Collections.singletonList(a_topic);
        TopicMetadataRequest req = new TopicMetadataRequest(topics);
        kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);

        List<TopicMetadata> metaData = resp.topicsMetadata();
        for (TopicMetadata item : metaData) {
          for (PartitionMetadata part : item.partitionsMetadata()) {
            if (part.partitionId() == a_partition) {
              returnMetaData = part;
              break loop;
            }
          }
        }
      } catch (Exception e) {
        log.info("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic
          + ", " + a_partition + "] Reason: " + e);
      } finally {
        if (consumer != null) consumer.close();
      }
    }
    if (returnMetaData != null) {
      m_replicaBrokers.clear();
      for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
        m_replicaBrokers.add(replica.host());
      }
    }
    return returnMetaData;
  }

  public interface KafkaMessageReHandler {
    void reHandle(String message);
  }

  public static void main(String[] args) {
    //可以在controller里触发
    try {
      List<String> seeds = new ArrayList<String>();
      seeds.add("kafkatest02.edaijia-inc.cn");

      Integer port = 9092;

      String topic = "driver_location_test";

      Integer partition = 0;

      Long userOffset = 2729833l;

      Long maxReads = 10l;

      KafkaReConsumerProxy.getInstance().addHandler(new KafkaReConsumerProxy.KafkaMessageReHandler() {
        @Override
        public void reHandle(String message) {
          try {
            Thread.sleep(200);
            log.info("rehand : {}", message);
          } catch (Throwable t) {

          }
        }
      }).run(seeds, port, topic, partition, userOffset, maxReads);
    } catch (Throwable e) {
      log.info("kafkaReConsumerProxy run error, message {}, cause {}", e.getMessage(), e.getCause(), e);
    }
  }
}
