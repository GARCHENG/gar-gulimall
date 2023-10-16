package com.garcheng.gulimall.order.web;

import com.garcheng.gulimall.order.exception.NoStockException;
import com.garcheng.gulimall.order.service.OrderService;
import com.garcheng.gulimall.order.vo.ConfirmOrderVo;
import com.garcheng.gulimall.order.vo.OrderItemVo;
import com.garcheng.gulimall.order.vo.SubmitOrderResponseVo;
import com.garcheng.gulimall.order.vo.SubmitOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        ConfirmOrderVo confirmOrderData = orderService.confirmOrder();
        model.addAttribute("confirmOrderData", confirmOrderData);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(SubmitOrderVo submitOrderVo, Model model, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        try {
            SubmitOrderResponseVo response = orderService.submitOrder(submitOrderVo);
            if (response.getCode() == 0) {
                model.addAttribute("orderResponse", response);
                return "pay";
            } else {
                String msg = "";
                switch (response.getCode()) {
                    case 1:
                        msg = msg + "令牌校验失败";
                        break;
                    case 2:
                        msg = msg + "验价失败";
                        break;
                    case 3:
                        msg = msg + "锁库存失败";
                        break;
                }
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }catch (NoStockException e){
            redirectAttributes.addFlashAttribute("msg", "库存不足");
            return "redirect:http://order.gulimall.com/toTrade";
        }

    }
}
