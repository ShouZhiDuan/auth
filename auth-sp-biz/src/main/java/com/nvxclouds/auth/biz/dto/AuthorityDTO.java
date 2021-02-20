package com.nvxclouds.auth.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/5/12 17:16
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDTO {

    @NotNull
    private Long parentID;
    private String routerName;
    @NotBlank
    private String name;
    private String icon;
    private String permission;
    @Range(min = 1,max = 2)
    private Integer type;
    @NotNull
    private Long sort;
}
