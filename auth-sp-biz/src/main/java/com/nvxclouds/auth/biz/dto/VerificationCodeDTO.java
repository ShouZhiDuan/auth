package com.nvxclouds.auth.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 16:50
 * @Description:
 */
@Data
public class VerificationCodeDTO {

    @NotBlank
    private String mobile;

    /**
     *
     *  0:注册
     *  1:重置密码
     *
     */
    @NotBlank
    private String state;


    /**
     * 图片验证码
     */
    @NotBlank
    private String imageVerifyCode;

}
