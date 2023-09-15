package com.garcheng.gulimall.search.service;

import com.garcheng.gulimall.common.to.es.EsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
    Boolean saveEsModel(List<EsModel> esSaveModels) throws IOException;
}
