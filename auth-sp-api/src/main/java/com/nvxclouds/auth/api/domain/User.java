package com.nvxclouds.auth.api.domain;

import com.nvxclouds.common.base.domain.BaseDO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/16 10:22
 * @Description:
 */
@Getter
@Setter
@Table(name = "user")
public class User extends BaseDO implements Serializable {

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "salt")
    private String salt;

    @Column(name = "token")
    private String token;

    @Column(name = "status")
    private Integer status;

    @Column(name = "name")
    private String name;

    @Column(name = "expire_time")
    private Date expireTime;

    @Column(name = "user_code")
    private String userCode;
}
