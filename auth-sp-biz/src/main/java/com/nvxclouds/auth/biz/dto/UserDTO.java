package com.nvxclouds.auth.biz.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/21 11:28
 * @Description:
 */
@Data
@Builder
public class UserDTO {

    private String mobile;

    private String name;

    private String expireTime;

    private String password;

    private List<Long> roles;
}
