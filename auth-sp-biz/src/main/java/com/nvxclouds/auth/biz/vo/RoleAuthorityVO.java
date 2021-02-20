package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 19:21
 * @Description:
 */
@Data
@Builder
public class RoleAuthorityVO {
    private Long id;
    private String name;
    private List<RoleAuthorityVO> child;

}
