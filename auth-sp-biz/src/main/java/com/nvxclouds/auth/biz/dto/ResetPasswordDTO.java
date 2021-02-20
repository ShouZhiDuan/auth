package com.nvxclouds.auth.biz.dto;

import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/21 17:27
 * @Description:
 */
@Data
public class ResetPasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String verifyCode;

}
