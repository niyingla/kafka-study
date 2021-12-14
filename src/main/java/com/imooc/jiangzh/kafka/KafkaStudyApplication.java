package com.imooc.jiangzh.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaStudyApplication {

    /**
     * broker 集群中的节点
     * leader 每个分片都有一个自己的leader 节点接收到消息会转发到对应节点
     * follower 分片备份节点 备份数据 从leader 拉数据
     *
     * 1 分片(partition)一般都会分布在不同的 节点上（broke）上
     * 2 可以为每个分片单独设置副本数（每份分片都是一份保障）
     * 3 生产者随便访问哪个节点都可发送数据（有同步机制）
     * @param args
     */

    public static void main(String[] args) {
        SpringApplication.run(KafkaStudyApplication.class, args);
    }

}
