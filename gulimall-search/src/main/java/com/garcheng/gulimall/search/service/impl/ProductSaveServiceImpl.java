package com.garcheng.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.garcheng.gulimall.common.to.es.SkuUpTo;
import com.garcheng.gulimall.search.config.EsConfig;
import com.garcheng.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean saveEsModel(List<SkuUpTo> esSaveModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuUpTo esSaveModel : esSaveModels) {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.id(esSaveModel.getSkuId().toString());
            String s = JSON.toJSONString(esSaveModel);
            indexRequest.source(s, XContentType.JSON);

            bulkRequest.add(indexRequest);
        }
        BulkResponse responses = restHighLevelClient.bulk(bulkRequest, EsConfig.COMMON_OPTIONS);

        boolean b = responses.hasFailures();

        if (b){
            List<String> faultIds = Arrays.stream(responses.getItems()).map(obj -> {
                return obj.getId();
            }).collect(Collectors.toList());
            log.error("es保存出错了，具体情况：{}",faultIds);
        }

        return !b;
    }
}
