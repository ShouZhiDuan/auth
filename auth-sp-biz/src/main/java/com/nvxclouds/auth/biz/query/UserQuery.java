package com.nvxclouds.auth.biz.query;

import com.nvxclouds.common.base.pojo.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/21 11:09
 * @Description:
 */
@Setter
@Getter
public class UserQuery extends Page {

    private String userName;
}
