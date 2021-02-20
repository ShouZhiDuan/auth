package com.nvxclouds.auth.biz.domain;

import com.nvxclouds.common.base.domain.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "role_authority")
public class RoleAuthority extends BaseDO implements Serializable {


    @Column(name = "role_ID")
    private Long roleId;
    @Column(name = "authority_ID")
    private Long authorityId;


}
