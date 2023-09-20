package com.garcheng.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.common.to.es.EsModel;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {

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
        SearchResult result = buildSearchResult(searchResponse, Params);
        return result;
    }

    private SearchRequest buildSearchRequest(SearchParams params) {
        SearchRequest searchRequest = new SearchRequest(ElasticsearchConstant.PRODUCT_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(params.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", params.getKeyword()));
        }

        if (params.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", params.getCatalog3Id()));
        }

        if (params.getBrandId() != null && params.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", params.getBrandId()));
        }

        if (params.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", params.getHasStock() == 1));
        }
        // SkuPrice _500/0_500/500_
        if (!StringUtils.isEmpty(params.getSkuPrice())) {
            String paramsSkuPrice = params.getSkuPrice();
            String[] split = paramsSkuPrice.split("_");
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
            if (split.length == 2) {
                rangeQueryBuilder.gte(split[0]).lte(split[1]);
            } else if (split.length == 1) {
                if (paramsSkuPrice.startsWith("_")) {
                    rangeQueryBuilder.lte(split[0]);
                } else if (paramsSkuPrice.endsWith("_")) {
                    rangeQueryBuilder.gte(split[0]);
                }
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }

        //attrs 1_8寸:6寸
        if (params.getAttrs() != null && params.getAttrs().size() > 0) {
            for (String attr : params.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();

                String[] attrSplit = attr.split("_");
                String attrId = attrSplit[0];
                String[] attrValues = attrSplit[1].split(":");

                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //sort=skuPrice_desc
        if (!StringUtils.isEmpty(params.getSort())) {
            String[] sortSplit = params.getSort().split("_");
            SortOrder sortOrder = sortSplit[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(sortSplit[0], sortOrder);
        }

        searchSourceBuilder.from((params.getPageNum() - 1) * ElasticsearchConstant.PRODUCT_SEARCH_PAGE_SIZE);
        searchSourceBuilder.size(ElasticsearchConstant.PRODUCT_SEARCH_PAGE_SIZE);

        if (!StringUtils.isEmpty(params.getKeyword())) {
            HighlightBuilder highlighter = new HighlightBuilder();
            highlighter.field("skuTitle");
            highlighter.preTags("<b style='color:red'>");
            highlighter.postTags("</b>");
            searchSourceBuilder.highlighter(highlighter);
        }

        //聚合dsl

        //分类聚合
        TermsAggregationBuilder catelog_name_agg = AggregationBuilders.terms("catelog_name_agg").field("catalogName").size(1);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("catelog_agg").field("catalogId").size(50).subAggregation(catelog_name_agg));
        //品牌聚合
        TermsAggregationBuilder brand_name_agg = AggregationBuilders.terms("brand_name_agg").field("brandName").size(1);
        TermsAggregationBuilder brand_image_agg = AggregationBuilders.terms("brand_image_agg").field("brandImg").size(1);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("brand_agg").field("brandId").size(50).subAggregation(brand_name_agg).subAggregation(brand_image_agg));
        //属性聚合
        NestedAggregationBuilder nestedAggregationBuilder = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1);
        TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);
        nestedAggregationBuilder.subAggregation(AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50).subAggregation(attr_name_agg).subAggregation(attr_value_agg));
        searchSourceBuilder.aggregation(nestedAggregationBuilder);


        searchRequest.source(searchSourceBuilder);
        System.out.println("DSL构建语句：" + searchSourceBuilder.toString());

        return searchRequest;
    }

    private SearchResult buildSearchResult(SearchResponse searchResponse, SearchParams Params) {
        SearchResult result = new SearchResult();
        SearchHits hits = searchResponse.getHits();

        //封装esmodel
        List<EsModel> products = new ArrayList<>();
        if (hits.getHits()!=null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                EsModel esModel = JSON.parseObject(sourceAsString, EsModel.class);
                //高亮
                if (!StringUtils.isEmpty(Params.getKeyword())){
                    String skuTitle = hit.getHighlightFields().get("skuTitle").fragments()[0].string();
                    esModel.setSkuTitle(skuTitle);
                }
                products.add(esModel);
            }
            result.setProducts(products);
        }

        result.setPageNum(Params.getPageNum());
        result.setTotal(hits.getTotalHits().value);
        result.setTotalPage((int) hits.getTotalHits().value % ElasticsearchConstant.PRODUCT_SEARCH_PAGE_SIZE == 0 ? (int) hits.getTotalHits().value / ElasticsearchConstant.PRODUCT_SEARCH_PAGE_SIZE : (int) hits.getTotalHits().value / ElasticsearchConstant.PRODUCT_SEARCH_PAGE_SIZE + 1);

        //封装分类信息
        List<SearchResult.CategoryVo> categoryVos = new ArrayList<>();
        ParsedLongTerms catelog_agg = searchResponse.getAggregations().get("catelog_agg");
        for (Terms.Bucket bucket : catelog_agg.getBuckets()) {
            SearchResult.CategoryVo categoryVo = new SearchResult.CategoryVo();
            categoryVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));
            ParsedStringTerms catelog_name_agg = bucket.getAggregations().get("catelog_name_agg");
            String catelogName = catelog_name_agg.getBuckets().get(0).getKeyAsString();
            categoryVo.setCatalogName(catelogName);

            categoryVos.add(categoryVo);
        }
        result.setCategoryVos(categoryVos);

        //封装品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = searchResponse.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            brandVo.setBrandId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms brand_image_agg = bucket.getAggregations().get("brand_image_agg");
            brandVo.setBrandImg(brand_image_agg.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            brandVo.setBrandName(brand_name_agg.getBuckets().get(0).getKeyAsString());

            brandVos.add(brandVo);
        }
        result.setBrandVos(brandVos);

        //封装属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attr_agg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();

            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());

            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            attrVo.setAttrName(attr_name_agg.getBuckets().get(0).getKeyAsString());

            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attr_value_agg.getBuckets().stream().map(valueBucket -> {
                return valueBucket.getKeyAsString();
            }).collect(Collectors.toList());

            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrVos(attrVos);

        return result;
    }
}
