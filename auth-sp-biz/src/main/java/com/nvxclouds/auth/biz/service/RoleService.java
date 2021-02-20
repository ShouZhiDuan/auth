package com.nvxclouds.auth.biz.service;

import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.Role;
import com.nvxclouds.auth.biz.dto.RoleDTO;
import com.nvxclouds.auth.biz.query.RoleQuery;
import com.nvxclouds.auth.biz.vo.RoleAllVO;
import com.nvxclouds.auth.biz.vo.RoleAuthorityVO;
import com.nvxclouds.auth.biz.vo.RoleDetailVO;
import com.nvxclouds.auth.biz.vo.RoleVO;
import com.nvxclouds.common.base.pojo.Pagination;

import java.util.List;

public interface RoleService extends Service<Role> {

    void add(RoleDTO role);

    void remove(Long id);

    Pagination<RoleVO> query(RoleQuery roleQuery);

    List<RoleAllVO> getAll();

    List<RoleAuthorityVO> getRoleAuthority(Long id);

    void change(Long id, RoleDTO role);

    RoleDetailVO getRole(Long id);

    List<Role> getRoleByUserID(Long userID);

}
