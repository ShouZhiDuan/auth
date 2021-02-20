package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/5/14 16:03
 * @Description:
 */
@Data
@Builder
public class RefreshTokenVO {

    private String token;
    private String refreshToken;
}
