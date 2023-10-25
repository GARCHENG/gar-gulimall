package com.garcheng.gulimall.seckill.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.seckill.service.SeckillService;
import com.garcheng.gulimall.seckill.to.SeckillSkuRedisTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import java.util.List;

@Slf4j
@Controller
public class SeckillController {

    @Autowired
    SeckillService seckillService;

//    /**
//     * blockHandler 函数访问范围需要是 public，返回类型需要与原方法相匹配，参数类型需要和原方法相匹配并且最后加一个额外的参数，类型为 BlockException。blockHandler 函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 blockHandlerClass 为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
//     * @return
//     */
//    //自定义sentinel资源（注解方式）
//    @SentinelResource(value = "currentSeckillProduct", blockHandler = "blockHandlerForGetUser")
//    @GetMapping("/currentSeckillProduct")
//    @ResponseBody
//    public R getCurrentSeckillProduct() {
//        List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillProduct();
//        return R.ok().setData(data);
//    }
//
//    public R blockHandlerForGetUser(BlockException ex) {
//        log.warn("被限流了【{}】", ex.getMessage());
//        return R.error();
//    }

//    //自定义sentinel资源（代码方式）
//    @GetMapping("/currentSeckillProduct")
//    @ResponseBody
//    public R getCurrentSeckillProduct(){
//        try (Entry entry = SphU.entry("getCurrentSeckillProduct")) {
//            List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillProduct();
//            return R.ok().setData(data);
//        }catch (BlockException e){
//            log.warn("被限流了【{}】",e.getMessage());
//            return R.error();
//        }
//
//    }

    @GetMapping("/currentSeckillProduct")
    @ResponseBody
    public R getCurrentSeckillProduct(){
            List<SeckillSkuRedisTo> data = seckillService.getCurrentSeckillProduct();
            return R.ok().setData(data);
    }

    @GetMapping("sku/seckillinfo/{skuId}")
    @ResponseBody
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {
        SeckillSkuRedisTo to = seckillService.getSeckillInfoBySkuId(skuId);
        return R.ok().setData(to);
    }

    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {
        String orderSn = seckillService.kill(killId, key, num);
        model.addAttribute("orderSn", orderSn);
        return "success";

    }
}
