package com.lwl.social_media_platform;

import cn.hutool.json.JSONUtil;
import com.lwl.social_media_platform.domain.pojo.Treads;
import com.lwl.social_media_platform.service.TreadsService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class AppTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private TreadsService treadsService;

    public static final String MAPPING_TEMPLATE = """
             {
                "mappings": {
                    "properties": {
                        "id": {
                            "type": "keyword"
                        },
                        "userId":{
                            "type": "long"
                        },
                        "content": {
                            "type":"text",
                            "analyzer":"ik_max_word"
                        },
                        "state":{
                            "type":"text",
                            "index":false
                        },
                        "createTime":{
                            "type":"date",
                            "index":false
                        }
                   }
                }
            }
            """;

    @Test
    void contextLoads() {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("treads");
        createIndexRequest.source(MAPPING_TEMPLATE, XContentType.JSON);
        try {
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addTreadsToES() throws IOException {
        List<Treads> list = treadsService.list();
        BulkRequest request = new BulkRequest();
        list.forEach(treads -> request.add(new IndexRequest("treads")
                .id(treads.getId().toString())
                .source(JSONUtil.toJsonStr(treads),XContentType.JSON)));
        restHighLevelClient.bulk(request,RequestOptions.DEFAULT);
    }

}
