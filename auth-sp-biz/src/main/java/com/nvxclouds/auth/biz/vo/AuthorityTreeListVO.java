package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/5/12 16:48
 * @Description:
 */
@Data
@Builder
public class AuthorityTreeListVO {
    private Long id;
    private String routerName;
    private String name;
    private String icon;
    private String permission;
    private Integer type;
    private Long sort;
    private List<AuthorityTreeListVO> child;
}
