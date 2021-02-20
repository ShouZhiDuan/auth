package com.nvxclouds.auth.biz.feign;

import com.nvxclouds.notify.api.feign.NotifyFeign;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/10 11:05
 * @Description:
 */
@FeignClient(value = "notify")
public interface NotifyClient extends NotifyFeign {
}
