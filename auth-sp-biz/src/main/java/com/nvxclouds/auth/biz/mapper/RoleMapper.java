package com.nvxclouds.auth.biz.mapper;

import com.nvxclouds.auth.biz.query.RoleQuery;
import com.nvxclouds.auth.biz.vo.RoleVO;
import com.nvxclouds.common.core.mapper.Mapper;
import com.nvxclouds.auth.biz.domain.Role;

import java.util.List;

public interface RoleMapper extends Mapper<Role> {

    List<RoleVO> selectRoleByPage(RoleQuery query);
}