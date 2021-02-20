package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/8 15:46
 * @Description:
 */
@Data
@Builder
public class LoginVO {
    private Long id;
    private String userName;
    private String token;
    private String avatar;
    private String refreshToken;
}
