package com.nvxclouds.auth.biz.domain;

import com.nvxclouds.common.base.domain.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Table(name = "role")
public class Role extends BaseDO implements Serializable {

    @NotBlank
    @Column(name = "role_name")
    private String roleName;


}
