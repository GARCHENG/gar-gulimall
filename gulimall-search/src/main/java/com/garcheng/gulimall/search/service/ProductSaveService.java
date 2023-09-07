package com.garcheng.gulimall.search.service;

import com.garcheng.gulimall.common.to.es.SkuUpTo;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    Boolean saveEsModel(List<SkuUpTo> esSaveModels) throws IOException;
}
