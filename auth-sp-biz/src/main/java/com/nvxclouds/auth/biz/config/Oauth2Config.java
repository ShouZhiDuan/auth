package com.nvxclouds.auth.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/8 18:08
 * @Description:
 */
@Data
@Component
@ConfigurationProperties(prefix = "oauth2")
public class Oauth2Config {
    private String clientId;
    private String grantType;
    private String clientSecret;
}
