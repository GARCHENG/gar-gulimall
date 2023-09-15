package com.garcheng.gulimall.search.vo;

import com.garcheng.gulimall.common.to.es.EsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    private List<EsModel> products;

    private Integer pageNum;

    private Long total;

    private Integer totalPage;

    private List<BrandVo> brandVos;

    private List<AttrVo> attrVos;

    private List<CategoryVo> categoryVos;


    @Data
    public static class BrandVo{

        private Long brandId;

        private String brandName;

        private String brandImg;

    }

    @Data
    public static class AttrVo{

        private Long attrId;

        private String attrName;

        private List<String> attrValue;

    }

    @Data
    public static class CategoryVo{

        private Long catalogId;

        private String catalogName;

    }

}
