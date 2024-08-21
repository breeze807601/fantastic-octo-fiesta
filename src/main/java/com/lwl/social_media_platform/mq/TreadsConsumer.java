package com.lwl.social_media_platform.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.producer.topic}")
public class TreadsConsumer implements RocketMQListener<Map<String,String>> {
    private final RestHighLevelClient restHighLevelClient;
    @Value("${rocketmq.producer.topic}")
    private String topic;

    @Override
    public void onMessage(Map<String, String> treadsMap) {
        String treadsJSON = treadsMap.get("treadsJSON");
        IndexRequest request = new IndexRequest(topic).source(treadsJSON);
        try {
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            log.info("队列异步添加文档结果为:{}",indexResponse.getShardInfo().status().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
