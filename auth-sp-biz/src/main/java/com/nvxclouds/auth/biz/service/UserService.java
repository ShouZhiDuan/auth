package com.nvxclouds.auth.biz.service;
import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.User;
import com.nvxclouds.auth.biz.dto.*;
import com.nvxclouds.auth.biz.query.UserQuery;
import com.nvxclouds.auth.biz.vo.LoginVO;
import com.nvxclouds.auth.biz.vo.SidebarMenuVO;
import com.nvxclouds.auth.biz.vo.UserDetailVO;
import com.nvxclouds.auth.biz.vo.UserVO;
import com.nvxclouds.common.base.pojo.Pagination;

import java.util.List;
import java.util.function.Supplier;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/2 16:55
 * @Description:
 */
public interface UserService extends Service<User> {

    User selectByPhone(String phone);

    LoginVO login(LoginDTO loginDTO);

    User selectByUserNameOrPhone(String userNameOrPhone);

    <X extends Throwable> User selectByUserNameOrPhone(String userNameOrPhone, Supplier<? extends X> nullHandleException) throws X;

    <X extends Throwable> User selectByUserCode(String userCode, Supplier<? extends X> nullHandleException)throws X;

    void addUser(RegisterDTO registerDTO);

    void forgetPassword(ForgetPasswordDTO forgetPasswordDTO);

    Pagination<UserVO> queryUser(UserQuery userQuery);

    UserDetailVO getUserDetail(Long id);

    void changeUser(Long id, UserDTO userDTO);

    void removeUser(Long id);

    void resetPassword(ResetPasswordDTO resetPasswordDTO, String token);

    User getUserByToken(String token);

    List<SidebarMenuVO> getSidebarMenu(Long id);

    void logout(String token);

    void changeUserAuthorityStore(String token);
}
