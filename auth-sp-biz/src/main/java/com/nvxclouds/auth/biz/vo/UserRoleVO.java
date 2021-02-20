package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/24 11:50
 * @Description:
 */
@Data
@Builder
public class UserRoleVO {
    private Long id;
    private String roleName;
}
