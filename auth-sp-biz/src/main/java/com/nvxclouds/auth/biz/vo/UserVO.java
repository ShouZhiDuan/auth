package com.nvxclouds.auth.biz.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/21 11:09
 * @Description:
 */
@Data
@Builder
public class UserVO {

    private Long id;
    /**
     * 用户名
     */
    private String userName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 姓名
     */
    private String name;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 失效时间
     */
    private String expireTime;

    private Integer status;
}
