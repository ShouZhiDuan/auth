package com.nvxclouds.auth.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 19:26
 * @Description:
 */
@Data
public class RoleDTO {
    @NotBlank
    private String roleName;
    private String comments;
    private List<List<Long>> authorities;

}

