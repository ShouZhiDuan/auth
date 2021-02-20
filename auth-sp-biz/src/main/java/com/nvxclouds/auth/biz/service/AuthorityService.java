package com.nvxclouds.auth.biz.service;
import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.Authority;
import com.nvxclouds.auth.biz.dto.AuthorityDTO;
import com.nvxclouds.auth.biz.vo.AuthorityDetailVO;
import com.nvxclouds.auth.biz.vo.AuthorityTreeListVO;
import com.nvxclouds.auth.biz.vo.AuthorityVO;


import java.util.List;

public interface  AuthorityService extends Service<Authority> {

    List<Authority> selectAuthorityByRoleID(Long id);

    List<Authority> selectAuthoritiesByParentID(List<Authority> authorities,Long parentID);

    List<Authority> selectByUserID(Long id);

    List<AuthorityVO> getAll();

    List<String> selectPermissionByUserID(Long id);

    List<AuthorityTreeListVO> getAuthorityTreeList();

    void addAuthority(AuthorityDTO authority);

    void changeAuthority(Long id, AuthorityDTO authority);

    void removeAuthority(Long id);

    AuthorityDetailVO getAuthorityDetail(Long id);
}
