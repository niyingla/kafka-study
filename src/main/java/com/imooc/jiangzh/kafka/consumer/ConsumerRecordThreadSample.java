package com.imooc.jiangzh.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ConsumerRecordThreadSample {

    private final static String TOPIC_NAME = "jiangzh-topic";

    /**
     * 适合一致性和完整行不强的数据
     */
    public static void main(String[] args) throws InterruptedException {
        String brokerList = "192.168.220.128:9092";
        String groupId = "test";
        int workerNum = 5;

        //创建线程池消费对象
        ConsumerExecutor consumers = new ConsumerExecutor(brokerList, groupId, TOPIC_NAME);
        //指定线程数开始消费
        consumers.execute(workerNum);

        Thread.sleep(1000000);

        consumers.shutdown();

    }

    /**
     * 多线程消费类
     */
    public static class ConsumerExecutor {
        private final KafkaConsumer<String, String> consumer;
        private ExecutorService executors;

        public ConsumerExecutor(String brokerList, String groupId, String topic) {
            Properties props = new Properties();
            props.put("bootstrap.servers", brokerList);
            props.put("group.id", groupId);
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Arrays.asList(topic));
        }

        /**
         * 执行线程数据拉去
         *
         * @param workerNum
         */
        public void execute(int workerNum) {
            //创建消费线程池
            executors = new ThreadPoolExecutor(workerNum, workerNum, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.CallerRunsPolicy());

            while (true) {
                //拉去数据
                ConsumerRecords<String, String> records = consumer.poll(200);
                for (final ConsumerRecord record : records) {
                    //线程池循环消费
                    executors.submit(new ConsumerRecordWorker(record));
                }
            }
        }

        public void shutdown() {
            //关闭客户端
            if (consumer != null) {
                consumer.close();
            }
            //关闭线程池
            if (executors != null) {
                executors.shutdown();
            }
            try {
                if (!executors.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println("Timeout.... Ignore for this case");
                }
            } catch (InterruptedException ignored) {
                System.out.println("Other thread interrupted this shutdown, ignore for this case.");
                Thread.currentThread().interrupt();
            }
        }


    }

    /**
     * 记录处理类
     */
    public static class ConsumerRecordWorker implements Runnable {

        private ConsumerRecord<String, String> record;

        public ConsumerRecordWorker(ConsumerRecord record) {
            this.record = record;
        }

        @Override
        public void run() {
            // 假如说数据入库操作
            System.out.println("Thread - " + Thread.currentThread().getName());
            System.err.printf("patition = %d , offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
        }

    }
}
