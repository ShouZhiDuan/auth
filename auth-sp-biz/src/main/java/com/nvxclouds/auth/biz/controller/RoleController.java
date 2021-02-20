package com.nvxclouds.auth.biz.controller;


import com.nvxclouds.common.core.annotation.Log;
import com.nvxclouds.common.core.annotation.Permission;
import com.nvxclouds.auth.biz.dto.RoleDTO;
import com.nvxclouds.auth.biz.query.RoleQuery;
import com.nvxclouds.auth.biz.service.RoleService;
import com.nvxclouds.auth.biz.service.UserService;
import com.nvxclouds.auth.biz.vo.RoleAllVO;
import com.nvxclouds.auth.biz.vo.RoleAuthorityVO;
import com.nvxclouds.auth.biz.vo.RoleDetailVO;
import com.nvxclouds.auth.biz.vo.RoleVO;
import com.nvxclouds.common.base.constant.GlobalConstants;
import com.nvxclouds.common.base.pojo.BaseResult;
import com.nvxclouds.common.base.pojo.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/role", name = "用户登录")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @PostMapping()
    @Log(name = "角色添加")
    @Permission(name = "role:add")
    public BaseResult<Void> save(@RequestBody RoleDTO role) {
        roleService.add(role);
        return BaseResult.ok();
    }

    @DeleteMapping("{id}")
    @Log(name = "角色删除")
    @Permission(name = "role:delete")
    public BaseResult<Void> remove(@PathVariable("id") Long id) {
        roleService.remove(id);
        return BaseResult.ok();
    }

    @PutMapping("/{id}")
    @Log(name = "角色修改")
    @Permission(name = "role:update")
    public BaseResult<Void> modify(@RequestHeader(value = GlobalConstants.HEADER_AUTHORIZATION) String token, @PathVariable("id") Long id, @RequestBody RoleDTO role) {
        roleService.change(id, role);
        Optional.ofNullable(token).ifPresent(userService::changeUserAuthorityStore);
        return BaseResult.ok();
    }

    @GetMapping("{id}")
    @Log(name = "角色详情")
    @Permission(name = "role:detail")
    public BaseResult<RoleDetailVO> get(@PathVariable("id") Long id) {
        RoleDetailVO role = roleService.getRole(id);
        return BaseResult.ok(role);
    }


    @GetMapping("/{id}/authority")
    @Log(name = "角色权限")
    @Permission(name = "role:authority")
    public BaseResult<List<RoleAuthorityVO>> getRoleAuthority(@PathVariable("id") Long id) {
        return BaseResult.ok(roleService.getRoleAuthority(id));
    }

    @GetMapping()
    @Log(name = "角色列表")
    @Permission(name = "role:list")
    public BaseResult<Pagination<RoleVO>> query(RoleQuery roleQuery) {
        return BaseResult.ok(roleService.query(roleQuery));
    }


    @GetMapping("/all")
    public BaseResult<List<RoleAllVO>> getAll() {
        return BaseResult.ok(roleService.getAll());
    }

}
