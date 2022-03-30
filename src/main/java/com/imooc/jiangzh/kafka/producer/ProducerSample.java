package com.imooc.jiangzh.kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerSample {

    private final static String TOPIC_NAME="jiangzh-topic";
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Producer异步发送演示
//        producerSend();
        // Producer异步阻塞发送演示
//        producerSyncSend();
        // Producer异步发送带回调函数
//        producerSendWithCallback();
        // Producer异步发送带回调函数和Partition负载均衡
        producerSendWithCallbackAndPartition();
    }


    /**
     *  Producer异步发送带回调函数和Partition负载均衡
     */
    public static void producerSendWithCallbackAndPartition(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"106.52.197.116:11092");
        //producer 需要 server 接收到数据之后发出的 确认接收的信号，此项配置就是指 procuder 需要多少个这样的确认信号。此配置实际上代表。
        // acks=0： 设置为 0 表示 producer 不需 要等待任何确认收到的信息
        // acks=1： 这意味着至少要等待 leader 已经成功将数据写入本地 log，但是并没有等待 所有follower是否成功写入
        // acks=all： 这意味着 leader 需要等待所 有备份都成功写入日志，这种策略会保证只要有 一个备份存活就不会丢失数据。这是最强的保证。
        properties.put(ProducerConfig.ACKS_CONFIG,"all");
        //重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,"0");
        //批次大小
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,"16384");
        //多久发送一个批次到服务端
        properties.put(ProducerConfig.LINGER_MS_CONFIG,"1");
        //重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,"3");
        //缓存最大
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");
        //指定序列化类
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //指定分区负载类
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG,"com.imooc.jiangzh.kafka.producer.SamplePartition");

        // Producer的主对象
        Producer<String,String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for(int i=0;i<10;i++){
            ProducerRecord<String,String> record = new ProducerRecord<>(TOPIC_NAME,"key-"+i,"value-"+i);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("partition : "+recordMetadata.partition()+" , offset : "+recordMetadata.offset());
                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }

    /*
        Producer异步发送带回调函数
     */
    public static void producerSendWithCallback(){
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"106.52.197.116:11092");
        properties.put(ProducerConfig.ACKS_CONFIG,"all");
        properties.put(ProducerConfig.RETRIES_CONFIG,"0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,"16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG,"1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String,String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for(int i=0;i<10;i++){
            ProducerRecord<String,String> record = new ProducerRecord<>(TOPIC_NAME,"key-"+i,"value-"+i);

            //发送完成回调
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("partition : "+recordMetadata.partition()+" , offset : "+recordMetadata.offset());
                }
            });
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }

    /*
        Producer异步发送演示
     */
    public static void producerSend(){
        Properties properties = new Properties();
        //服务地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"106.52.197.116:11092");
        //
        properties.put(ProducerConfig.ACKS_CONFIG,"all");
        //批量大小
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,"16384");
        //
        properties.put(ProducerConfig.LINGER_MS_CONFIG,"1");
        //发送失败 重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG,"3");
        //
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");
        //key 序列化方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        //value 序列化方式
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String,String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for(int i=0;i<1;i++){
            //创建需要发送的消息记录
            ProducerRecord<String,String> record = new ProducerRecord<>(TOPIC_NAME,"key-"+i,"value-"+i);
            producer.send(record);
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }

    /**
     *  Producer异步阻塞发送/同步发送
     */
    public static void producerSyncSend() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"106.52.197.116:11092");
        properties.put(ProducerConfig.ACKS_CONFIG,"all");
        properties.put(ProducerConfig.RETRIES_CONFIG,"0");
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG,"16384");
        properties.put(ProducerConfig.LINGER_MS_CONFIG,"1");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG,"33554432");

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        // Producer的主对象
        Producer<String,String> producer = new KafkaProducer<>(properties);

        // 消息对象 - ProducerRecoder
        for(int i=0;i<10;i++){
            String key = "key-"+i;
            ProducerRecord<String,String> record = new ProducerRecord<>(TOPIC_NAME,key,"value-"+i);
            Future<RecordMetadata> send = producer.send(record);

            RecordMetadata recordMetadata = send.get();
            //1 接受目标分片 2 位置
            System.out.println(key + "partition : "+recordMetadata.partition()+" , offset : "+recordMetadata.offset());
        }

        // 所有的通道打开都需要关闭
        producer.close();
    }

}
