package com.garcheng.gulimall.search.controller;

import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.common.to.es.EsModel;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("search/save")
@RestController
public class ElasticsearchSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @RequestMapping("productup")
    public R upSpuInfo(@RequestBody List<EsModel> esSaveModels)  {
        Boolean isSuccess = null;
        try {
            isSuccess = productSaveService.saveEsModel(esSaveModels);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(BaseCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BaseCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if (isSuccess){
            return R.ok();
        }else {
            return R.error(BaseCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BaseCodeEnum.PRODUCT_UP_EXCEPTION.getMessage());
        }


    }

}
