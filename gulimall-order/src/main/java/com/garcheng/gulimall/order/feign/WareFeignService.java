package com.garcheng.gulimall.order.feign;

import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.order.vo.WareLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeignService {

    @RequestMapping("ware/waresku/hasstock")
    R getSkusStock(@RequestBody List<Long> skuIds);

    @GetMapping("ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @PostMapping("ware/waresku/lock/order")
    R lockStock(@RequestBody WareLockVo wareLockVo);

}
