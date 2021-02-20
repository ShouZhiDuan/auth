package com.nvxclouds.auth.biz.service;
import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.UserRole;

import java.util.List;

public interface  UserRoleService extends Service<UserRole> {

    List<UserRole> selectUserRoleByUserID(Long userID);

    void insertByRoleIdsAndUserID(List<Long> roleIds, Long userId);

    List<UserRole> selectByRoleId(Long id);
}
