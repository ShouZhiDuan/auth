package com.nvxclouds.auth.biz.vo;

import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/8 15:36
 * @Description:
 */
@Data
public class AccessTokenVO {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String jti;
    private String error;
    private String error_description;
}
