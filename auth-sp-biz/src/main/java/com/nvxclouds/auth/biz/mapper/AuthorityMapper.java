package com.nvxclouds.auth.biz.mapper;
import com.nvxclouds.common.core.mapper.Mapper;
import com.nvxclouds.auth.biz.domain.Authority;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityMapper extends Mapper<Authority> {

    List<Authority> selectAuthorityByRoleID(@Param(value = "roleID") Long roleID);

    List<Authority> selectByUserID(@Param(value = "userID") Long userID);

    List<String> selectPermissionByUserID(@Param("userId") Long userId);
}