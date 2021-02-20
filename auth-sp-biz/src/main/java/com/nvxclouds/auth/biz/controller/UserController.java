package com.nvxclouds.auth.biz.controller;

import com.nvxclouds.auth.biz.dto.*;
import com.nvxclouds.auth.biz.query.UserQuery;
import com.nvxclouds.auth.biz.service.UserService;
import com.nvxclouds.auth.biz.vo.LoginVO;
import com.nvxclouds.auth.biz.vo.SidebarMenuVO;
import com.nvxclouds.auth.biz.vo.UserDetailVO;
import com.nvxclouds.auth.biz.vo.UserVO;
import com.nvxclouds.common.base.pojo.BaseResult;
import com.nvxclouds.common.base.pojo.Pagination;
import com.nvxclouds.common.core.annotation.Log;
import com.nvxclouds.common.core.annotation.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/16 10:25
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @Log(name = "用户登录")
    @PostMapping(value = "/login")
    public BaseResult<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO) {
        return BaseResult.ok(userService.login(loginDTO));
    }

    @Log(name = "用户退出")
    @PostMapping(value = "/logout")
    public BaseResult<Void> logout(@RequestHeader(value = "Authorization") String token) {
//        String[] split = token.split(" ");
//        String accessToken = split[split.length - 1];
//        OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
//        tokenStore.removeAccessToken(oAuth2AccessToken);
        return BaseResult.ok();
    }


    @Log(name = "忘记密码")
    @PostMapping("/forgetPassword")
    @Permission(name = "user:forgetPassword")
    public BaseResult<Void> forgetPassword(@RequestBody @Valid ForgetPasswordDTO forgetPasswordDTO) {
        userService.forgetPassword(forgetPasswordDTO);
        return BaseResult.ok();
    }

    @Log(name = "重置密码")
    @PostMapping("/resetPassword")
    @Permission(name = "user:resetPassword")
    public BaseResult<Void> resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO, @RequestHeader(value = "Authorization") String token) {
        userService.resetPassword(resetPasswordDTO, token);
        return BaseResult.ok();
    }

    @Log(name = "查询用户列表")
    @GetMapping
    @Permission(name = "user:list")
    public BaseResult<Pagination<UserVO>> queryUser(UserQuery userQuery) {
        Pagination<UserVO> pagination = userService.queryUser(userQuery);
        return BaseResult.ok(pagination);
    }

    @Log(name = "查询用户详情")
    @GetMapping("/{id}")
    @Permission(name = "user:detail")
    public BaseResult<UserDetailVO> findUserDetail(@PathVariable(value = "id") Long id) {
        UserDetailVO userDetail = userService.getUserDetail(id);
        return BaseResult.ok(userDetail);
    }

    @Log(name = "修改用户信息")
    @PutMapping("/{id}")
    @Permission(name = "user:update")
    public BaseResult<Void> modifyUser(@PathVariable(value = "id") Long id, @RequestBody @Valid UserDTO userDTO) {
        userService.changeUser(id, userDTO);
        return BaseResult.ok();
    }

    @Log(name = "添加用户")
    @PostMapping
    @Permission(name = "user:add")
    public BaseResult<Void> createUser(@RequestBody @Valid RegisterDTO registerDTO) {
        userService.addUser(registerDTO);
        return BaseResult.ok();
    }

    @Log(name = "侧边栏")
    @DeleteMapping("{id}")
    @Permission(name = "user:delete")
    public BaseResult<Void> removeUser(@PathVariable(value = "id") Long id) {
        userService.removeUser(id);
        return BaseResult.ok();
    }


    @Log(name = "获取侧边栏")
    @GetMapping("/{id}/sidebar")
    public BaseResult<List<SidebarMenuVO>> getSidebarMenu(@PathVariable(value = "id") Long id) {
        return BaseResult.ok(userService.getSidebarMenu(id));
    }

}
