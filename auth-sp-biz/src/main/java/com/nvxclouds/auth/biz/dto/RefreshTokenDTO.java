package com.nvxclouds.auth.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/5/14 18:35
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenDTO {

    private String refreshToken;
}
