package com.nvxclouds.auth.biz.dto;

import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 17:06
 * @Description:
 */
@Data
public class ForgetPasswordDTO {

    private String mobile;
    private String newPassword;
    private String verifyCode;
}
