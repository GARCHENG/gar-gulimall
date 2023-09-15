package com.garcheng.gulimall.search.web;

import com.garcheng.gulimall.search.service.MallSearchService;
import com.garcheng.gulimall.search.vo.SearchParams;
import com.garcheng.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {


    @Autowired
    private MallSearchService mallSearchService;

    @RequestMapping("list.html")
    public String listPage(SearchParams searchParams, Model model){

        SearchResult result = mallSearchService.search(searchParams);

        model.addAttribute("result",result);

        return "list";
    }

}
