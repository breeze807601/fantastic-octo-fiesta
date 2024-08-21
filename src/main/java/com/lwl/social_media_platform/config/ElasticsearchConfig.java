package com.lwl.social_media_platform.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.uris}")
    private String host;

    @Bean
    RestHighLevelClient restHighLevelClient() {

        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create(host)
        ).setRequestConfigCallback(
                builder -> builder.setConnectTimeout(5000).setSocketTimeout(60000)
        ));
    }
}
