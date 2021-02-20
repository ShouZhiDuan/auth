package com.nvxclouds.auth.biz.feign;

import com.nvxclouds.common.core.config.FeignFormSupportConfig;
import com.nvxclouds.auth.biz.vo.AccessTokenVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/8 15:33
 * @Description:
 */
//@FeignClient(value = "auth", configuration = FeignFormSupportConfig.class, url = "http://127.0.0.1:10210")
@FeignClient(value = "oauth2", configuration = FeignFormSupportConfig.class)
public interface Oauth2Client {

    @PostMapping(value = "/oauth/token", consumes = {"application/x-www-form-urlencoded"})
    AccessTokenVO accessToken(Map<String, ?> formData);

}


