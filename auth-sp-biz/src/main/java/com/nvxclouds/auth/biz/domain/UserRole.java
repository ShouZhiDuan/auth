package com.nvxclouds.auth.biz.domain;

import com.nvxclouds.common.base.domain.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "user_role")
public class UserRole extends BaseDO implements Serializable {


    @Column(name = "user_ID")
    private Long userId;
    @Column(name = "role_ID")
    private Long roleId;

}
