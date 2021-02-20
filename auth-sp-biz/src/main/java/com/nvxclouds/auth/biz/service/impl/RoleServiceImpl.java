package com.nvxclouds.auth.biz.service.impl;

import com.nvxclouds.common.core.enums.ExceptionEnum;
import com.nvxclouds.common.core.exception.GlobalException;
import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.common.core.util.PageHelper;
import com.nvxclouds.common.core.util.TimeUtils;
import com.nvxclouds.auth.biz.domain.Authority;
import com.nvxclouds.auth.biz.domain.Role;
import com.nvxclouds.auth.biz.domain.RoleAuthority;
import com.nvxclouds.auth.biz.domain.UserRole;
import com.nvxclouds.auth.biz.dto.RoleDTO;
import com.nvxclouds.auth.biz.mapper.RoleMapper;
import com.nvxclouds.auth.biz.query.RoleQuery;
import com.nvxclouds.auth.biz.service.AuthorityService;
import com.nvxclouds.auth.biz.service.RoleAuthorityService;
import com.nvxclouds.auth.biz.service.RoleService;
import com.nvxclouds.auth.biz.service.UserRoleService;
import com.nvxclouds.auth.biz.vo.RoleAllVO;
import com.nvxclouds.auth.biz.vo.RoleAuthorityVO;
import com.nvxclouds.auth.biz.vo.RoleDetailVO;
import com.nvxclouds.auth.biz.vo.RoleVO;
import com.nvxclouds.common.base.pojo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends AbstractService<Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleAuthorityService roleAuthorityService;

    @Autowired
    private UserRoleService userRoleService;


    @Override
    @Transactional
    public void add(RoleDTO roleDTO) {
        Role role = getByRoleName(roleDTO.getRoleName());
        validateExist(role, () -> new GlobalException(ExceptionEnum.ROLE_NAME_EXIST));
        role = new Role();
        role.setRoleName(roleDTO.getRoleName());
        role.setComments(roleDTO.getComments());
        role.setCreateTime(new Date());
        role.setStatus(0);
        int insertRole = insert(role);
        operate(insertRole, () -> new GlobalException(ExceptionEnum.ROLE_INSERT_FAILED));
        updateRoleAuthority(role.getID(), roleDTO.getAuthorities());
    }

    private void updateRoleAuthority(Long roleId, List<List<Long>> authorities) {
        if (roleId == null || StringUtils.isEmpty(authorities)) {
            return;
        }
        roleAuthorityService.deleteRoleAuthorityByRoleId(roleId);
        Set<Long> authorityIds = new HashSet<>();
        authorities.forEach(authorityIds::addAll);
        List<RoleAuthority> authorities1 = new ArrayList<>();
        authorityIds.forEach(id -> {
            RoleAuthority roleAuthority = new RoleAuthority();
            roleAuthority.setAuthorityId(id);
            roleAuthority.setRoleId(roleId);
            roleAuthority.setCreateTime(new Date());
            roleAuthority.setStatus(0);
            authorities1.add(roleAuthority);

        });
        if (CollectionUtils.isEmpty(authorities1)) {
            return;
        }
        roleAuthorityService.insert(authorities1);
    }


    @Override
    @Transactional
    public void change(Long id, RoleDTO roleDTO) {
        Role role = selectByPK(id);
        validateNotExist(role, () -> new GlobalException(ExceptionEnum.ROLE_NOT_EXIST));
        if (!roleDTO.getRoleName().equals(role.getRoleName())) {
            validateExist(getByRoleName(roleDTO.getRoleName()), () -> new GlobalException(ExceptionEnum.ROLE_NAME_EXIST));
            role.setRoleName(roleDTO.getRoleName());
        }
        Optional.ofNullable(roleDTO.getComments()).ifPresent(role::setComments);
        operate(updateSelective(role), () -> new GlobalException(ExceptionEnum.ROLE_UPDATE_FAILED));
        updateRoleAuthority(role.getID(), roleDTO.getAuthorities());
    }

    @Override
    public RoleDetailVO getRole(Long id) {
        Role role = selectByPKAndValidate(id);
        RoleDetailVO roleDetail = new RoleDetailVO();
        roleDetail.setId(id);
        roleDetail.setRoleName(role.getRoleName());
        roleDetail.setComments(role.getComments() == null ? "" : role.getComments());
        List<Authority> authorities = authorityService.selectAuthorityByRoleID(id);
        List<List<Long>> authorityIds = authorities.stream().map(a -> {
            String[] split = a.getTreeNodes().split(",");
            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                ids.add(Long.valueOf(split[i]));
            }
            return ids;
        }).collect(Collectors.toList());
        roleDetail.setAuthorities(authorityIds);
        return roleDetail;
    }

    @Override
    public List<Role> getRoleByUserID(Long userID) {
        List<UserRole> userRoles = userRoleService.selectUserRoleByUserID(userID);
        if (CollectionUtils.isEmpty(userRoles)) {
            return new ArrayList<>();
        }
        return userRoles.stream().map(userRole -> selectByPK(userRole.getRoleId())).collect(Collectors.toList());
    }

    @Override
    public void remove(Long id) {
        List<UserRole> userRoles = userRoleService.selectByRoleId(id);
        if (!CollectionUtils.isEmpty(userRoles)) {
            throw new GlobalException(ExceptionEnum.USER_ROLE_EXIST);
        }
        Role role = selectByPKAndValidate(id);
        List<Authority> authorities = authorityService.selectAuthorityByRoleID(id);
        if (!CollectionUtils.isEmpty(authorities)) {
            throw new GlobalException(ExceptionEnum.ROLE_AUTHORITY_NOT_EMPTY);
        }
        operate(deleteByPK(role.getID()), () -> new GlobalException(ExceptionEnum.ROLE_DELETE_FAILED));
    }


    private Role selectByPKAndValidate(Long id) {
        Role role = selectByPK(id);
        validateNull(role, () -> new GlobalException(ExceptionEnum.ROLE_NOT_EXIST));
        return role;
    }


    public Role getByRoleName(String roleName) {
        Role record = new Role();
        record.setRoleName(roleName);
        return selectOne(record);
    }

    @Override
    public Pagination<RoleVO> query(RoleQuery roleQuery) {
        return handlePage(roleQuery,query-> roleMapper.selectRoleByPage(query));


//        PageHelper.startPage(roleQuery.getPage(), roleQuery.getPerPage());
//        Role role = new Role();
//        role.setStatus(0);
//        Optional.ofNullable(role.getRoleName()).ifPresent(role::setRoleName);
//        List<Role> roles = select(role);
//        if (CollectionUtils.isEmpty(roles)) {
//            return new Pagination<>();
//        }
//        return PageHelper.end(roles, (r) -> RoleVO.builder()
//                .id(r.getID())
//                .roleName(r.getRoleName())
//                .comments(r.getComments() == null ? "" : r.getComments())
//                .createTime(TimeUtils.getYYYYMMDDHHMMSS(r.getCreateTime()))
//                .build());
    }

    @Override
    public List<RoleAllVO> getAll() {
        List<Role> roles = selectAll();
        validateEmpty(roles, () -> new GlobalException(ExceptionEnum.ROLE_IS_EMPTY));
        return roles.stream().map((r) -> RoleAllVO.builder().id(r.getID()).roleName(r.getRoleName()).build()).collect(Collectors.toList());
    }

    @Override
    public List<RoleAuthorityVO> getRoleAuthority(Long id) {
        //role 为0 是管理员权限查询所有
        List<Authority> authorities;
        if (id == 0) {
            authorities = authorityService.selectAll();
        } else {
            authorities = authorityService.selectAuthorityByRoleID(id);
        }
        List<RoleAuthorityVO> roleAuthorities = new ArrayList<>();
        getRoleAuthoritiesTree(authorities, 0L, roleAuthorities);
        return roleAuthorities;
    }

    void getRoleAuthoritiesTree(List<Authority> authorities, Long parentID, List<RoleAuthorityVO> roleAuthorities) {

        List<Authority> authorities1 = authorityService.selectAuthoritiesByParentID(authorities, parentID);
        if (!CollectionUtils.isEmpty(authorities1)) {
            authorities1.forEach(a -> {
                List<RoleAuthorityVO> childAuthorities = new ArrayList<>();
                roleAuthorities.add(RoleAuthorityVO.builder()
                        .id(a.getID())
                        .name(a.getModelName())
                        .child(childAuthorities)
                        .build());
                getRoleAuthoritiesTree(authorities, a.getID(), childAuthorities);
            });
        }

    }


}
