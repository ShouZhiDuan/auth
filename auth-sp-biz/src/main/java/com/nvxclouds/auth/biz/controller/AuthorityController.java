package com.nvxclouds.auth.biz.controller;

import com.nvxclouds.common.core.annotation.Log;
import com.nvxclouds.common.core.annotation.Permission;
import com.nvxclouds.auth.biz.dto.AuthorityDTO;
import com.nvxclouds.auth.biz.service.AuthorityService;
import com.nvxclouds.auth.biz.vo.AuthorityDetailVO;
import com.nvxclouds.auth.biz.vo.AuthorityTreeListVO;
import com.nvxclouds.auth.biz.vo.AuthorityVO;
import com.nvxclouds.common.base.pojo.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @PostMapping()
    @Log(name = "菜单添加")
    @Permission(name = "authority:add")
    public BaseResult<Void> saveAuthority(@Valid @RequestBody AuthorityDTO authority) {
        authorityService.addAuthority(authority);
        return BaseResult.ok();
    }

    @DeleteMapping("/{id}")
    @Log(name = "菜单删除")
    @Permission(name = "authority:delete")
    public BaseResult<Void> removeAuthority(@PathVariable("id") Long id) {
        authorityService.removeAuthority(id);
        return BaseResult.ok();
    }

    @PutMapping("/{id}")
    @Log(name = "菜单修改")
    @Permission(name = "authority:update")
    public BaseResult<Void> modifyAuthority(@PathVariable("id") Long id, @Valid @RequestBody AuthorityDTO authority) {
        authorityService.changeAuthority(id,authority);
        return BaseResult.ok();
    }

    @GetMapping("{id}")
    @Log(name = "菜单详情")
    @Permission(name = "authority:detail")
    public BaseResult<AuthorityDetailVO> findAuthority(@PathVariable("id") Long id) {
        return BaseResult.ok(authorityService.getAuthorityDetail(id));
    }

    @GetMapping("/all")
    public BaseResult<List<AuthorityVO>> findAllAuthority() {
        return BaseResult.ok(authorityService.getAll());
    }

    @GetMapping("/tree")
    @Log(name = "菜单列表")
    @Permission(name = "authority:list")
    public BaseResult<List<AuthorityTreeListVO>> findAuthorityTreeList() {
        return BaseResult.ok(authorityService.getAuthorityTreeList());
    }
}
