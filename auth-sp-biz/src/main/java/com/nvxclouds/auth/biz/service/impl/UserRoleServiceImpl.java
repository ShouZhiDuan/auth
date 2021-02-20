package com.nvxclouds.auth.biz.service.impl;

import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.auth.biz.domain.UserRole;
import com.nvxclouds.auth.biz.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleServiceImpl extends AbstractService<UserRole> implements UserRoleService {

    @Override
    public List<UserRole> selectUserRoleByUserID(Long userID) {
        UserRole record = new UserRole();
        record.setUserId(userID);
        record.setStatus(0);
        return select(record);
    }

    @Override
    public void insertByRoleIdsAndUserID(List<Long> roleIds, Long userId) {
        insert(roleIds.stream().map((roleId) -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId);
            userRole.setUserId(userId);
            userRole.setCreateTime(new Date());
            userRole.setStatus(0);
            return userRole;
        }).collect(Collectors.toList()));
    }

    @Override
    public List<UserRole> selectByRoleId(Long id) {
        UserRole record = new UserRole();
        record.setRoleId(id);
        return select(record);
    }
}
