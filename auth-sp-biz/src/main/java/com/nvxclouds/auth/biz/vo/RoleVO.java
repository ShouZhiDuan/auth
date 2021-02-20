package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 11:05
 * @Description:
 */
@Data
@Builder
public class RoleVO {
    private Long id;
    private String roleName;
    private String createTime;
    private String comments;
}
