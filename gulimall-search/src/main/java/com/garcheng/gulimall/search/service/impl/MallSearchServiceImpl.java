package com.garcheng.gulimall.search.service.impl;

import com.garcheng.gulimall.search.config.EsConfig;
import com.garcheng.gulimall.search.constant.ElasticsearchConstant;
import com.garcheng.gulimall.search.service.MallSearchService;
import com.garcheng.gulimall.search.vo.SearchParams;
import com.garcheng.gulimall.search.vo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (params.getBrandId() != null && params.getBrandId().size() > 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",params.getBrandId()));
        }

        if(params.getHasStock() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",params.getHasStock() ==1));
        }
        // SkuPrice _500/0_500/500_
        if (!StringUtils.isEmpty(params.getSkuPrice())){
            String paramsSkuPrice = params.getSkuPrice();
            String[] split = paramsSkuPrice.split("_");
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("hasStock");
            if (split.length == 2){
                rangeQueryBuilder.gte(split[0]).lte(split[1]);
            }else if (split.length ==1){
                if (paramsSkuPrice.startsWith("_")){
                    rangeQueryBuilder.lte(split[0]);
                }else if (paramsSkuPrice.endsWith("_")){
                    rangeQueryBuilder.gte(split[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //attrs 1_8寸:6寸
        if (params.getAttrs() != null && params.getAttrs().size() >0){
            for (String attr : params.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();

                String[] attrSplit = attr.split("_");
                String attrId = attrSplit[0];
                String[] attrValues = attrSplit[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs",nestedBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }




        }





        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        return searchRequest;
    }

    private SearchResult buildSearchResult(SearchRequest searchRequest) {
        return null;
    }
}
