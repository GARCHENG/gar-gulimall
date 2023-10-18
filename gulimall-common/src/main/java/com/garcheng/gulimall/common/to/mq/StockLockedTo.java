package com.garcheng.gulimall.common.to.mq;

import lombok.Data;

@Data
public class StockLockedTo {

    private Long taskId;

    private StockLockDetail detail;
}
