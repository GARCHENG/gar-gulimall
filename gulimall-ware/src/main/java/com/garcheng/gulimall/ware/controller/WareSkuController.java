package com.garcheng.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.garcheng.gulimall.common.exception.BaseCodeEnum;
import com.garcheng.gulimall.common.to.SkuStockTo;
import com.garcheng.gulimall.ware.exception.NoStockException;
import com.garcheng.gulimall.ware.vo.LockStockResult;
import com.garcheng.gulimall.ware.vo.WareLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.garcheng.gulimall.ware.entity.WareSkuEntity;
import com.garcheng.gulimall.ware.service.WareSkuService;
import com.garcheng.gulimall.common.utils.PageUtils;
import com.garcheng.gulimall.common.utils.R;



/**
 * 商品库存
 *
 * @author garcheng
 * @email 631450632@qq.com
 * @date 2023-08-17 10:08:09
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/hasstock")
    public R getSkusStock(@RequestBody List<Long> skuIds){
        List<SkuStockTo> skuStockTos = wareSkuService.getSkusStock(skuIds);
        return R.ok().setData(skuStockTos);
    }

    @PostMapping("lock/order")
    public R lockStock(@RequestBody WareLockVo wareLockVo){
        try {
            boolean result = wareSkuService.orderLockStock(wareLockVo);
            return R.ok().setData(result);
        }catch (NoStockException e){
            return R.error(BaseCodeEnum.NO_STOCK_EXCEPTION.getCode(),BaseCodeEnum.NO_STOCK_EXCEPTION.getMessage());
        }
    }



    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
