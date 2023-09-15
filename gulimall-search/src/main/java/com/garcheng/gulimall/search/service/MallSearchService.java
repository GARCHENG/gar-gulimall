package com.garcheng.gulimall.search.service;

import com.garcheng.gulimall.search.vo.SearchParams;
import com.garcheng.gulimall.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParams searchParams);
}
