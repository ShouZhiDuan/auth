package com.nvxclouds.auth.biz.service.impl;

import com.nvxclouds.common.core.enums.ExceptionEnum;
import com.nvxclouds.common.core.exception.GlobalException;
import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.auth.biz.domain.Authority;

import com.nvxclouds.auth.biz.dto.AuthorityDTO;
import com.nvxclouds.auth.biz.mapper.AuthorityMapper;
import com.nvxclouds.auth.biz.service.AuthorityService;
import com.nvxclouds.auth.biz.vo.AuthorityDetailVO;
import com.nvxclouds.auth.biz.vo.AuthorityTreeListVO;
import com.nvxclouds.auth.biz.vo.AuthorityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl extends AbstractService<Authority> implements AuthorityService {

    @Autowired
    private AuthorityMapper authorityMapper;

    public List<Authority> selectAuthorityByRoleID(Long roleID) {
        return authorityMapper.selectAuthorityByRoleID(roleID);
    }

    @Override
    public List<Authority> selectAuthoritiesByParentID(List<Authority> authorities, Long parentID) {
        return authorities
                .stream()
                .filter(a -> a.getParentId().equals(parentID))
                .collect(Collectors.toList());
    }


    private List<Authority> selectAuthorityIncludeParent(List<Authority> authorities) {
        Set<String> ids = new HashSet<>();
        List<Authority> authorities1 = new ArrayList<>(authorities);
        for (Authority authority : authorities) {
            String treeNodes = authority.getTreeNodes();
            String[] nodes = treeNodes.split(",");
            if (nodes.length > 1) {
                for (int i = 0; i < nodes.length - 1; i++) {
                    if (ids.contains(nodes[i])) {
                        break;
                    }
                    Optional.ofNullable(selectByPK(nodes[i])).ifPresent(authorities1::add);
                    ids.add(nodes[i]);
                }
            }
        }
        return authorities1;
    }

    @Override
    public List<Authority> selectByUserID(Long userID) {
        return authorityMapper.selectByUserID(userID);
    }

    @Override
    public List<AuthorityVO> getAll() {
        List<AuthorityVO> authorityVOS = new ArrayList<>();
        getRoleAuthoritiesTree(selectAll(), 0L, authorityVOS);
        return authorityVOS;
    }

    @Override
    public List<String> selectPermissionByUserID(Long userId) {
        return authorityMapper.selectPermissionByUserID(userId);
    }

    @Override
    public List<AuthorityTreeListVO> getAuthorityTreeList() {
        List<AuthorityTreeListVO> authorityTreeList = new ArrayList<>();
        getAuthoritiesTreeByParent(selectAllOrderByTreeSort(), 0L, authorityTreeList);
        return authorityTreeList;
    }

    private List<Authority> selectAllOrderByTreeSort() {
        Example example = new Example(Authority.class);
        example.orderBy("treeSort");
        return selectByExample(example);
    }

    @Override
    @Transactional
    public void addAuthority(AuthorityDTO authority) {
        Authority record = new Authority();
        Optional.ofNullable(authority.getRouterName()).ifPresent(record::setFunctionName);
        Optional.ofNullable(authority.getIcon()).ifPresent(record::setIcon);
        Optional.ofNullable(authority.getPermission()).ifPresent(record::setPermission);
        record.setModelName(authority.getName());
        record.setParentId(authority.getParentID());
        record.setCreateTime(new Date());
        record.setType(authority.getType());
        record.setTreeSort(authority.getSort());
        operate(insertSelective(record), () -> new GlobalException(ExceptionEnum.AUTHORITY_ADD_FAILED));
        String treeNodes = getAuthorityTreeNodes(record.getID());
        record.setTreeNodes(treeNodes);
        String treeNames = getAuthorityTreeNames(record.getID());
        record.setTreeNames(treeNames);
        Integer level = getAuthorityLevel(record.getID());
        record.setTreeLevel(level);
        record.setTreeRoot(getAuthorityTreeRoot(record.getID()));
        operate(updateSelective(record), () -> new GlobalException(ExceptionEnum.AUTHORITY_ADD_FAILED));
        updateParentAuthorityNotTreeLeaf(record.getParentId());
    }

    @Override
    @Transactional
    public void changeAuthority(Long id, AuthorityDTO authority) {

        Authority record = selectByPK(id);
        validateAuthorityNull(record);
        record.setFunctionName(authority.getRouterName());
        record.setModelName(authority.getName());
        Optional.ofNullable(authority.getIcon()).ifPresent(record::setIcon);
        Optional.ofNullable(authority.getPermission()).ifPresent(record::setPermission);
        record.setParentId(authority.getParentID());
        record.setType(authority.getType());
        record.setTreeSort(authority.getSort());
        String treeNodes = getAuthorityTreeNodes(record.getID());
        record.setTreeNodes(treeNodes);
        String treeNames = getAuthorityTreeNames(record.getID());
        record.setTreeNames(treeNames);
        Integer level = getAuthorityLevel(record.getID());
        record.setTreeLevel(level);
        Long treeRoot = getAuthorityTreeRoot(record.getID());
        record.setTreeRoot(treeRoot);
        operate(updateSelective(record), () -> new GlobalException(ExceptionEnum.AUTHORITY_UPDATE_FAILED));
        updateParentAuthorityNotTreeLeaf(record.getParentId());
    }

    private Long getAuthorityTreeRoot(Long id) {
        Stack<Authority> authorityStack = getAuthorityStack(id);
        if (!authorityStack.isEmpty()) {
            return authorityStack.peek().getID();
        }
        return id;
    }

    @Override
    public void removeAuthority(Long id) {
        Authority authority = selectByPK(id);
        validateAuthorityNull(authority);
        List<Authority> childAuthorities = selectAuthoritiesByParentID(authority.getID());
        if (!CollectionUtils.isEmpty(childAuthorities)) {
            throw new GlobalException(ExceptionEnum.AUTHORITY_CHILD_EXIST);
        }
        operate(deleteByPK(id), () -> new GlobalException(ExceptionEnum.AUTHORITY_DELETE_FAILED));
    }

    private List<Authority> selectAuthoritiesByParentID(Long parentId) {
        Authority record = new Authority();
        record.setParentId(parentId);
        return select(record);
    }

    @Override
    public AuthorityDetailVO getAuthorityDetail(Long id) {
        Authority authority = selectByPK(id);
        validateAuthorityNull(authority);
        return AuthorityDetailVO.builder()
                .parentID(authority.getParentId())
                .id(authority.getID())
                .name(authority.getModelName())
                .routerName(authority.getFunctionName())
                .icon(authority.getIcon() == null ? "" : authority.getIcon())
                .permission(authority.getPermission() == null ? "" : authority.getPermission())
                .type(authority.getType())
                .sort(authority.getTreeSort())
                .build();
    }

    private void validateAuthorityNull(Authority authority) {
        validateNull(authority, () -> new GlobalException(ExceptionEnum.AUTHORITY_NOT_EXIST));
    }

    private void updateParentAuthorityNotTreeLeaf(Long parentId) {
        if (parentId != 0) {
            Authority parentAuthority = selectByPK(parentId);
            Optional.ofNullable(parentAuthority).ifPresent((authority) -> {
                if (authority.getTreeLeaf() == 1) {
                    return;
                }
                authority.setTreeLeaf(1);
                updateSelective(authority);
            });
        }

    }

    private int getAuthorityLevel(Long id) {
        Stack<Authority> authorityStack = getAuthorityStack(id);
        return authorityStack.size();
    }

    private String getAuthorityTreeNames(Long id) {
        Stack<Authority> authorityStack = getAuthorityStack(id);
        StringBuilder treeNames = new StringBuilder();
        while (!authorityStack.isEmpty()) {
            Authority authority = authorityStack.peek();
            treeNames.append(authority.getModelName()).append("/");
            authorityStack.pop();
        }
        if (treeNames.length() > 0) {
            treeNames.deleteCharAt(treeNames.lastIndexOf("/"));
        }
        return treeNames.toString();
    }


    private String getAuthorityTreeNodes(Long id) {
        Stack<Authority> authorityStack = getAuthorityStack(id);
        StringBuilder treeNodes = new StringBuilder();
        while (!authorityStack.isEmpty()) {
            Authority authority = authorityStack.peek();
            treeNodes.append(authority.getID()).append(",");
            authorityStack.pop();
        }
        if (treeNodes.length() > 0) {
            treeNodes.deleteCharAt(treeNodes.lastIndexOf(","));
        }
        return treeNodes.toString();
    }

    private Stack<Authority> getAuthorityStack(Long id) {
        Stack<Authority> stack = new Stack<Authority>();
        Long parentId = id;
        while (parentId != 0) {
            Authority authority = selectByPK(parentId);
            if (authority != null) {
                parentId = authority.getParentId();
                stack.push(authority);
            }
        }
        return stack;
    }

    private void getRoleAuthoritiesTree(List<Authority> authorities, Long parentID, List<AuthorityVO> roleAuthorities) {
        List<Authority> authorities1 = selectAuthoritiesByParentID(authorities, parentID);
        if (!CollectionUtils.isEmpty(authorities1)) {
            authorities1.forEach(a -> {
                List<AuthorityVO> childAuthorities = new ArrayList<>();
                roleAuthorities.add(AuthorityVO.builder()
                        .id(a.getID())
                        .name(a.getModelName())
                        .child(childAuthorities)
                        .build());
                getRoleAuthoritiesTree(authorities, a.getID(), childAuthorities);
            });
        }
    }

    void getAuthoritiesTreeByParent(List<Authority> authorities, Long parentID, List<AuthorityTreeListVO> roleAuthorities) {

        List<Authority> authorities1 = selectAuthoritiesByParentID(authorities, parentID);
        if (!CollectionUtils.isEmpty(authorities1)) {
            authorities1.forEach(a -> {
                List<AuthorityTreeListVO> childAuthorities = new ArrayList<>();
                roleAuthorities.add(AuthorityTreeListVO.builder()
                        .id(a.getID())
                        .name(a.getModelName())
                        .icon(a.getIcon())
                        .permission(a.getPermission() == null ? "" : a.getPermission())
                        .routerName(a.getFunctionName() == null ? "" : a.getFunctionName())
                        .type(a.getType())
                        .sort(a.getTreeSort())
                        .child(childAuthorities)
                        .build());
                getAuthoritiesTreeByParent(authorities, a.getID(), childAuthorities);
            });
        }
    }
}
