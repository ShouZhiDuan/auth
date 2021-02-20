package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 18:34
 * @Description:
 */
@Data
@Builder
public class RoleAllVO {
    private Long id;
    private String roleName;
}
