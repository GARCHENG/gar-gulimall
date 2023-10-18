package com.garcheng.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.garcheng.gulimall.common.to.mq.OrderTo;
import com.garcheng.gulimall.common.to.mq.StockLockDetail;
import com.garcheng.gulimall.common.to.mq.StockLockedTo;
import com.garcheng.gulimall.common.utils.R;
import com.garcheng.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.garcheng.gulimall.ware.entity.WareOrderTaskEntity;
import com.garcheng.gulimall.ware.service.WareSkuService;
import com.garcheng.gulimall.ware.vo.OrderVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    //处理解锁库存
    @RabbitHandler
    public void handleStockRelease(StockLockedTo stockLockedTo , Message message , Channel channel) throws IOException {

        try {
            wareSkuService.ReleaseStock(stockLockedTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

    //处理关单时主动解锁库存
    @RabbitHandler
    public void handleStockRelease(OrderTo orderTo ,  Message message , Channel channel) throws IOException {
        try {
            wareSkuService.ReleaseStock(orderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

}
