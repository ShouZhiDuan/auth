package com.nvxclouds.auth.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/10 10:05
 * @Description: 用户注册请求参数
 */
@Data
public class RegisterDTO {

    @NotBlank
    private String mobile;

    @NotBlank
    private String name;

    private String expireTime;

    private List<Long> roles;


}
