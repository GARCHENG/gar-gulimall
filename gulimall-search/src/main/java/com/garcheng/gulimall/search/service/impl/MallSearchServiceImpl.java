package com.garcheng.gulimall.search.service.impl;

import com.garcheng.gulimall.search.config.EsConfig;
import com.garcheng.gulimall.search.constant.ElasticsearchConstant;
import com.garcheng.gulimall.search.service.MallSearchService;
import com.garcheng.gulimall.search.vo.SearchParams;
import com.garcheng.gulimall.search.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MallSearchServiceImpl  implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParams Params) {

        SearchRequest searchRequest = buildSearchRequest(Params);

        SearchResponse searchResponse = null;
        try {

             searchResponse = client.search(searchRequest, EsConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchResult result = buildSearchResult(searchRequest);
        return result;
    }

    private SearchRequest buildSearchRequest(SearchParams params) {
        SearchRequest searchRequest = new SearchRequest(ElasticsearchConstant.PRODUCT_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(params.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",params.getKeyword()));
        }

        if (!StringUtils.isEmpty(params.getCatalog3Id().toString())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",params.getCatalog3Id()));
        }

        if (params.getBrandId() != null){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",params.getBrandId()));
        }

        if (params.getAttrs() != null){

        }





        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private SearchResult buildSearchResult(SearchRequest searchRequest) {
        return null;
    }
}
