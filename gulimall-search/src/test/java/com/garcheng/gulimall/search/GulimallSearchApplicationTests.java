package com.garcheng.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.search.config.EsConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void contextLoads() throws IOException {

        IndexRequest request = new IndexRequest("posts");
        String jsonString = "{" +
                "\"user\":\"garcheng\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = client.index(request, EsConfig.COMMON_OPTIONS);
        System.out.println(indexResponse);
    }

    @Test
    public void test1() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//        searchSourceBuilder.query(QueryBuilders.termQuery("age",30));
//        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("address","mill road"));

        searchSourceBuilder.aggregation(AggregationBuilders.terms("group_age").field("age"));

        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, EsConfig.COMMON_OPTIONS);

        Terms groupAge = searchResponse.getAggregations().get("group_age");

        for (Terms.Bucket bucket : groupAge.getBuckets()) {
            System.out.println(bucket.getKeyAsNumber());
        }

        System.out.println();

//        System.out.println(searchResponse.toString());


    }

}
