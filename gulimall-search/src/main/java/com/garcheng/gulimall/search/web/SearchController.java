package com.garcheng.gulimall.search.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SearchController {

    @RequestMapping("list.html")
    public String listPage(){
        return "list";
    }

}
