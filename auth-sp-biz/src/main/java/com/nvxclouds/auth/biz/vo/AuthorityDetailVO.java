package com.nvxclouds.auth.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Auther: zhengxing.hu
 * @Date: 2020/5/13 15:03
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDetailVO {

    private Long parentID;
    private Long id;
    private String routerName;
    private String name;
    private String icon;
    private String permission;
    private Integer type;
    private Long sort;
}
