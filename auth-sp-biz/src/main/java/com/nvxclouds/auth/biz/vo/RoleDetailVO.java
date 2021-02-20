package com.nvxclouds.auth.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/23 11:41
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDetailVO {

    private Long id;
    private String roleName;
    private String comments;
    private List<List<Long>> authorities;
}
