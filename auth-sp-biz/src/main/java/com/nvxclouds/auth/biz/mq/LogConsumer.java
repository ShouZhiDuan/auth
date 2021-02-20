package com.nvxclouds.auth.biz.mq;

import com.alibaba.fastjson.JSONObject;
import com.nvxclouds.auth.biz.domain.SystemLog;
import com.nvxclouds.auth.biz.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/23 16:55
 * @Description:
 */
@Component
@Slf4j
public class LogConsumer {

    @Autowired
    private SystemLogService systemLogService;

    @KafkaListener(topics = "operation")
    public void listen(String data) {
        SystemLog systemLog = JSONObject.parseObject(data, SystemLog.class);
        systemLog.setCreateTime(new Date());
        systemLogService.insertSelective(systemLog);
        log.info("接收消息:{}", data);
    }


}
