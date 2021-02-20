package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 16:15
 * @Description:
 */
@Data
@Builder
public class SidebarMenuVO {
    private Long id;
    private String routerName;
    private String name;
    private String icon;
    private String permission;
    private Integer type;
    //true is leaf
    private boolean leaf;
    private List<SidebarMenuVO> children;
}
