package com.nvxclouds.auth.biz.query;

import com.nvxclouds.common.base.pojo.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/22 11:16
 * @Description:
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleQuery  extends Page {

    private String roleName;
}
