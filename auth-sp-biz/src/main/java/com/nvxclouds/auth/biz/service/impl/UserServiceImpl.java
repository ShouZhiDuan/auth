package com.nvxclouds.auth.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nvxclouds.auth.biz.config.Oauth2Config;
import com.nvxclouds.auth.biz.domain.Authority;
import com.nvxclouds.auth.biz.domain.User;
import com.nvxclouds.auth.biz.domain.UserRole;
import com.nvxclouds.auth.biz.dto.*;
import com.nvxclouds.auth.biz.feign.Oauth2Client;
import com.nvxclouds.auth.biz.mapper.UserMapper;
import com.nvxclouds.auth.biz.query.UserQuery;
import com.nvxclouds.auth.biz.service.AuthorityService;
import com.nvxclouds.auth.biz.service.RoleService;
import com.nvxclouds.auth.biz.service.UserRoleService;
import com.nvxclouds.auth.biz.service.UserService;
import com.nvxclouds.auth.biz.vo.*;
import com.nvxclouds.common.base.constant.GlobalConstants;
import com.nvxclouds.common.base.pojo.Pagination;
import com.nvxclouds.common.core.enums.ExceptionEnum;
import com.nvxclouds.common.core.exception.GlobalException;
import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.common.core.util.JwtUtils;
import com.nvxclouds.common.core.util.SecurityUtils;
import com.nvxclouds.common.core.util.TimeUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.nvxclouds.common.base.constant.GlobalConstants.USER_PASSWORD_REDIS_KEY;
import static com.nvxclouds.common.base.constant.GlobalConstants.Verification.VERIFICATION_CODE_REDIS_KEY;
import static com.nvxclouds.common.core.enums.DBTableEnum.StatusEnum.DELETED;
import static com.nvxclouds.common.core.enums.DBTableEnum.StatusEnum.NORMAL;


/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/16 10:26
 * @Description:
 */
@Service
public class UserServiceImpl extends AbstractService<User> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private Oauth2Client oauth2Client;

    @Autowired
    private Oauth2Config oauth2Config;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserMapper userMapper;


    public User selectByPhone(String phone) {
        User record = new User();
        record.setMobile(phone);
        record.setStatus(0);
        return selectOne(record);
    }

    public User selectByUserName(String userName) {
        User record = new User();
        record.setUserName(userName);
        record.setStatus(0);
        return selectOne(record);
    }

    /**
     * 返回存在null
     *
     * @param userNameOrPhone
     * @return
     */
    public User selectByUserNameOrPhone(String userNameOrPhone) {
        User user = selectByUserName(userNameOrPhone);
        if (user == null) {
            user = selectByPhone(userNameOrPhone);
        }
        return user;
    }

    public <X extends Throwable> User selectByUserNameOrPhone(String userNameOrPhone, Supplier<? extends X> nullHandleException) throws X {
        User user = selectByUserNameOrPhone(userNameOrPhone);
        if (user == null) {
            throw nullHandleException.get();
        }
        return user;
    }


    public User selectByUserCode(String userCode) {
        if (userCode == null) {
            return null;
        }
        User record = new User();
        record.setUserCode(userCode);
        return selectOne(record);
    }

    public <X extends Throwable> User selectByUserCode(String userCode, Supplier<? extends X> nullHandleException) throws X {
        User user = selectByUserCode(userCode);
        if (user == null) {
            throw nullHandleException.get();
        }
        return user;
    }


    public AccessTokenVO auth(String userName, String password) {
        Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", oauth2Config.getGrantType());
        formData.put("client_id", oauth2Config.getClientId());
        formData.put("client_secret", oauth2Config.getClientSecret());
        formData.put("username", userName);
        formData.put("password", password);
        return oauth2Client.accessToken(formData);
    }


    @Override
    public LoginVO login(LoginDTO loginDTO) {

        User user = getUserByUsername(loginDTO.getMobile());
        Optional.ofNullable(user).orElseThrow(() -> new GlobalException(ExceptionEnum.USER_NOT_EXIST));
        //验证用户是否过期
        validateUserExpired(user);
        //授权
        String password = DigestUtils.md5Hex(loginDTO.getPassword() + user.getSalt());
        if (!password.equals(user.getPassword())) {
            throw new GlobalException(ExceptionEnum.PASSWORD_ERROR);
        }
        redisTemplate.opsForValue().set(String.format(USER_PASSWORD_REDIS_KEY, user.getUserCode()), password, 60, TimeUnit.SECONDS);
        AccessTokenVO auth = auth(user.getUserCode(), password);
        //校验验证码
        validateVerifyCodeByMobile(loginDTO.getVerifyCode(), user.getMobile());

        storeAuthoritiesToCacheByUser(user);

        //校验成将用户信息存入redis中
        redisTemplate.opsForValue().set("auth-sp-user-" + user.getUserCode(), JSON.toJSONString(user));
        return LoginVO.builder().userName(user.getUserName())
                .id(user.getID())
                .token(auth.getAccess_token())
                .avatar("")
                .refreshToken(auth.getRefresh_token())
                .build();

    }

    private void storeAuthoritiesToCacheByUser(User user) {
        String key = String.format(GlobalConstants.USER_AUTHORITY, user.getUserCode());
        List<String> permissions = authorityService.selectPermissionByUserID(user.getID());
        if (CollectionUtils.isEmpty(permissions)) {
            return;
        }
        redisTemplate.opsForValue().set(key, StringUtils.join(permissions, ","));
    }

    private User getUserByUsername(String username) {
        User user;
        //通过redis查询
        String value = redisTemplate.opsForValue().get(String.format(GlobalConstants.USER_USERNAME, username));
        if (StringUtils.isNotBlank(value)) {
            user = JSONObject.parseObject(value, User.class);
            return user;
        }
        //数据库查询
        user = selectByUserNameOrPhone(username);
        if (user != null) {
            String userString = JSONObject.toJSONString(user);
            redisTemplate.opsForValue().set(String.format(GlobalConstants.USER_USERNAME, username), userString, GlobalConstants.USER_USERNAME_EXPIRED, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set(String.format(GlobalConstants.USER_USERCODE, user.getUserCode()), userString, GlobalConstants.USER_USERNAME_EXPIRED, TimeUnit.SECONDS);
        }
        return user;
    }

    private void validateUserExpired(User user) {
        if (user.getExpireTime() != null && user.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new GlobalException(ExceptionEnum.USER_EXPIRED);
        }
    }

    private void validateVerifyCodeByMobile(String verifyCode, String mobile) {
        String verificationCode = getVerifyCodeByMobile(mobile);
        if (!verificationCode.equals(verifyCode)) {
            throw new GlobalException(ExceptionEnum.VERIFICATION_CODE_INVALID);
        }
    }

    private String getVerifyCodeByMobile(String mobile) {
        String verificationCode = redisTemplate.opsForValue().get(String.format(VERIFICATION_CODE_REDIS_KEY, mobile));
        Optional.ofNullable(verificationCode).orElseThrow(() -> new GlobalException(ExceptionEnum.VERIFICATION_CODE_INVALID));
        return verificationCode;
    }

    @Override
    @Transactional
    public void addUser(RegisterDTO registerDTO) {
        User user = selectByUserNameOrPhone(registerDTO.getMobile());
        if (user != null) {
            throw new GlobalException(ExceptionEnum.USER_EXIST);
        }
        //3. 密码加密 ,通过盐值加密码的方式
        //todo 添加密码生成，和用户名生成策略
        String salt = generatorSalt();
        String password = DigestUtils.md5Hex(123456 + salt);
        String userName = String.valueOf(System.currentTimeMillis());
        //4. 保存数据
        user = new User();
        user.setMobile(registerDTO.getMobile());
        user.setPassword(password);
        user.setSalt(salt);
        user.setName(registerDTO.getName());
        user.setStatus(NORMAL.value);
        user.setUserName(userName);
        user.setUserCode(UUID.randomUUID().toString());
        if (registerDTO.getExpireTime() != null) {
            user.setExpireTime(TimeUtils.parseDate(registerDTO.getExpireTime()));
        }
        operate(insert(user), () -> new GlobalException(ExceptionEnum.CREATE_USER_FAILED));
        if (!CollectionUtils.isEmpty(registerDTO.getRoles())) {
            userRoleService.insertByRoleIdsAndUserID(registerDTO.getRoles(), user.getID());
        }
    }

    @Override
    public void forgetPassword(ForgetPasswordDTO forgetPasswordDTO) {
        User user = selectByUserNameOrPhone(forgetPasswordDTO.getMobile());
        Optional.ofNullable(user).orElseThrow(() -> new GlobalException(ExceptionEnum.USER_NOT_EXIST));
        validateVerifyCodeByMobile(forgetPasswordDTO.getVerifyCode(), forgetPasswordDTO.getMobile());
        String salt = generatorSalt();
        user.setSalt(salt);
        user.setPassword(DigestUtils.md5Hex(forgetPasswordDTO.getNewPassword() + salt));
        operate(updateSelective(user), () -> new GlobalException(ExceptionEnum.CHANGE_PASSWORD_FAILED));
        redisTemplate.delete(String.format(GlobalConstants.USER_USERNAME, user.getUserName()));
        redisTemplate.delete(String.format(GlobalConstants.USER_USERNAME, user.getMobile()));
    }

    @Override
    public Pagination<UserVO> queryUser(UserQuery userQuery) {
        return handlePage(userQuery, query -> userMapper.selectUserByPage((UserQuery) query), u -> {
            u.setMobile(SecurityUtils.hideMobile(u.getMobile()));
            return u;
        });
    }


    @Override
    public UserDetailVO getUserDetail(Long id) {
        User user = selectUserByPKAndValidate(id);
        List<UserRole> userRoles = userRoleService.selectUserRoleByUserID(id);
        List<Long> roleIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoles)) {
            roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        }
        return UserDetailVO.builder()
                .id(user.getID())
                .name(user.getName() == null ? "" : user.getName())
                .userName(user.getUserName())
                .expireTime(user.getExpireTime() == null ? "" : TimeUtils.getYYYYMMDD(user.getExpireTime()))
                .mobile(user.getMobile())
                .roles(roleIds)
                .build();
    }

    private User selectUserByPKAndValidate(Long id) {
        User user = Optional.ofNullable(selectByPK(id)).orElseThrow(
                () -> new GlobalException(ExceptionEnum.USER_NOT_EXIST)
        );
        Integer status = user.getStatus();
        if (status != 0) {
            throw new GlobalException(ExceptionEnum.USER_DELETED);
        }
        return user;
    }

    @Override
    public void changeUser(Long id, UserDTO userDTO) {
        User user = selectUserByPKAndValidate(id);
        Optional.ofNullable(userDTO.getMobile()).ifPresent(user::setMobile);
        Optional.ofNullable(userDTO.getName()).ifPresent(user::setName);
        if (StringUtils.isNotBlank(userDTO.getExpireTime())) {
            user.setExpireTime(TimeUtils.parseDate(userDTO.getExpireTime()));
        }
        Optional.ofNullable(userDTO.getPassword()).ifPresent(password -> {
            String salt = generatorSalt();
            user.setSalt(salt);
            user.setPassword(DigestUtils.md5Hex(password + salt));
        });
        operate(updateSelective(user), () -> new GlobalException(ExceptionEnum.USER_UPDATE_FAILED));
        List<UserRole> userRoles = userRoleService.selectUserRoleByUserID(user.getID());
        if (!CollectionUtils.isEmpty(userRoles)) {
            userRoles.forEach(ur -> userRoleService.deleteByPK(ur.getID()));
        }
        if (!CollectionUtils.isEmpty(userDTO.getRoles())) {
            userRoleService.insertByRoleIdsAndUserID(userDTO.getRoles(), user.getID());
        }
    }

    public User getUserByToken(String token) {
        String tokenJson = JwtUtils.decodeToken(token);
        JSONObject jsonObject = JSONObject.parseObject(tokenJson);
        String userName = (String) jsonObject.get("user_name");
        if (userName == null) {
            throw new GlobalException(ExceptionEnum.TOKEN_INVALID);
        }
        return selectByUserCode(userName, () -> new GlobalException(ExceptionEnum.USER_NOT_EXIST));
    }

    @Override
    public List<SidebarMenuVO> getSidebarMenu(Long id) {
        List<Authority> authorities = authorityService.selectByUserID(id);
        if (CollectionUtils.isEmpty(authorities)) {
            return new ArrayList<>();
        }
        List<SidebarMenuVO> parentMenu = getSidebarMenuByParentId(authorities, 0L);
        for (SidebarMenuVO pm : parentMenu) {
            List<SidebarMenuVO> childMenu = getSidebarMenuByParentId(authorities, pm.getId());
            if (CollectionUtils.isEmpty(childMenu)) {
                pm.setLeaf(true);
                pm.setChildren(new ArrayList<>());
            } else {
                pm.setLeaf(false);
                pm.setChildren(childMenu);
                for (SidebarMenuVO btnMenu : childMenu) {
                    List<SidebarMenuVO> buttons = getSidebarMenuByParentId(authorities, btnMenu.getId());
                    btnMenu.setLeaf(true);
                    if (CollectionUtils.isEmpty(buttons)) {
                        btnMenu.setChildren(new ArrayList<>());
                    } else {
                        btnMenu.setChildren(buttons);
                    }
                }
            }
        }
        return parentMenu;

    }

    @Override
    public void logout(String token) {
        User user = getUserByToken(token);
//        redisTemplate.delete(GlobalConstants.Verification)
    }

    @Override
    public void changeUserAuthorityStore(String token) {
        Optional.ofNullable(getUserByToken(token)).ifPresent(this::storeAuthoritiesToCacheByUser);
    }

    private List<SidebarMenuVO> getSidebarMenuByParentId(List<Authority> authorities, Long parentId) {
        return authorities
                .stream()
                .filter(a -> a.getParentId().equals(parentId))
                .map(this::toSidebarMenuVO)
                .collect(Collectors.toList());
    }

    private SidebarMenuVO toSidebarMenuVO(Authority a) {
        return SidebarMenuVO.builder()
                .id(a.getID())
                .permission(a.getPermission() == null ? "" : a.getPermission())
                .icon(a.getIcon() == null ? "" : a.getIcon())
                .name(a.getModelName())
                .routerName(a.getFunctionName())
                .type(a.getType())
                .children(new ArrayList<>())
                .build();
    }

    @Override
    public void removeUser(Long id) {
        User user = selectUserByPKAndValidate(id);
        user.setID(id);
        user.setStatus(DELETED.value);
        operate(updateSelective(user), () -> new GlobalException(ExceptionEnum.USER_DELETE_FAILED));
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO, String token) {
        User user = getUserByToken(token);
        validateVerifyCodeByMobile(resetPasswordDTO.getVerifyCode(), user.getMobile());
        //验证旧密码
        if (!DigestUtils.md5Hex(resetPasswordDTO.getOldPassword() + user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(ExceptionEnum.USER_OLD_PASSWORD_ERROR);
        }
        String salt = generatorSalt();
        user.setPassword(DigestUtils.md5Hex(resetPasswordDTO.getNewPassword() + salt));
        user.setSalt(salt);
        operate(updateSelective(user), () -> new GlobalException(ExceptionEnum.USER_PASSWORD_UPDATE_FAILED));
        redisTemplate.delete(String.format(GlobalConstants.USER_USERNAME, user.getUserName()));
        redisTemplate.delete(String.format(GlobalConstants.USER_USERNAME, user.getMobile()));
    }


    /**
     * 当前时间戳MD5
     *
     * @return
     */
    private String generatorSalt() {
        return DigestUtils.md5Hex(String.valueOf(System.currentTimeMillis()));
    }

}
