package com.nvxclouds.auth.biz;

import com.nvxclouds.common.core.annotation.NVXCloudApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/2 09:36
 * @Description:
 */
@EnableFeignClients
@NVXCloudApplication
public class AuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

}
