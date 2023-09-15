package com.garcheng.gulimall.search.web;

import com.garcheng.gulimall.search.service.MallSearchService;
import com.garcheng.gulimall.search.vo.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {


    @Autowired
    private MallSearchService mallSearchService;

    @RequestMapping("list.html")
    public String listPage(SearchParams searchParams){
        return "list";
    }

}
