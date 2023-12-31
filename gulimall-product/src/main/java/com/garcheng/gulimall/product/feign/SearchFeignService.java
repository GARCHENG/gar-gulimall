package com.garcheng.gulimall.product.feign;

import com.garcheng.gulimall.common.to.es.EsModel;
import com.garcheng.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@FeignClient("gulimall-search")
public interface SearchFeignService {

    @RequestMapping("search/save/productup")
    R upSpuInfo(@RequestBody List<EsModel> esSaveModels);
}
