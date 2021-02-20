package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/21 11:22
 * @Description:
 */
@Builder
@Data
public class UserDetailVO {

    private Long id;

    private String mobile;

    private String name;

    private String userName;

    private String expireTime;

    private List<Long> roles;

}
