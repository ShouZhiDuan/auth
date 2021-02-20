package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/24 19:05
 * @Description:
 */
@Builder
@Data
public class AuthorityVO {

    private Long id;
    private String name;
    private List<AuthorityVO> child;
}
