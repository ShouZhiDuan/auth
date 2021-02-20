package com.nvxclouds.auth.biz.service;
import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.RoleAuthority;

import java.util.List;

public interface  RoleAuthorityService extends Service<RoleAuthority> {

    List<RoleAuthority> selectByRoleID(Long id);

    void deleteRoleAuthorityByRoleId(Long roleId);
}
