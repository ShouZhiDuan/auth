package com.nvxclouds.auth.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 16:02
 * @Description:
 */
@Data
public class LoginDTO {

    @NotBlank
    private String mobile;

    @NotBlank
    private String password;

    @NotBlank
    private String verifyCode;
}
