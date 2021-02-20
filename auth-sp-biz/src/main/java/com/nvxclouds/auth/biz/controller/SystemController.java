package com.nvxclouds.auth.biz.controller;

import com.nvxclouds.common.core.annotation.Log;
import com.nvxclouds.common.core.annotation.Permission;
import com.nvxclouds.common.core.enums.ExceptionEnum;
import com.nvxclouds.common.core.exception.GlobalException;
import com.nvxclouds.common.core.util.VerifyCodeUtils;
import com.nvxclouds.auth.biz.domain.User;
import com.nvxclouds.auth.biz.dto.RefreshTokenDTO;
import com.nvxclouds.auth.biz.dto.VerificationCodeDTO;
import com.nvxclouds.auth.biz.query.SystemLogQuery;
import com.nvxclouds.auth.biz.service.SystemLogService;
import com.nvxclouds.auth.biz.service.SystemService;
import com.nvxclouds.auth.biz.service.UserService;
import com.nvxclouds.auth.biz.vo.RefreshTokenVO;
import com.nvxclouds.auth.biz.vo.SystemLogVO;
import com.nvxclouds.common.base.pojo.BaseResult;
import com.nvxclouds.common.base.pojo.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.nvxclouds.common.base.constant.GlobalConstants.Verification.IMAGE_VERIFICATION_CODE_REDIS_KEY;
import static com.nvxclouds.common.base.constant.GlobalConstants.Verification.IMAGE_VERIFICATION_EXPIRATION_TIMEOUT;


/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 10:07
 * @Description:
 */
@RestController
@Slf4j
public class SystemController {
    @Autowired
    private SystemService systemService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemLogService systemLogService;

    /**
     * 验证码生成
     */
    @Log(name = "获取图片验证码")
    @GetMapping(value = "/imageVerifyCode")
    public BaseResult<String> getVerifyCode(HttpServletRequest request, HttpServletResponse response, String mobile) {
        if (StringUtils.isBlank(mobile)) {
            throw new GlobalException(ExceptionEnum.MOBILE_IS_NULL);
        }
        User user = userService.selectByUserNameOrPhone(mobile, () -> new GlobalException(ExceptionEnum.USER_NOT_EXIST));
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
//        int i = 1/0;
        //生成随机字串
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        log.info("verify code:{}", verifyCode);
        //存入会话session
        HttpSession session = request.getSession(true);
        session.setAttribute("_code", verifyCode.toLowerCase());
        //生成图片
        int w = 150, h = 50;
        String base64;
        try {
            base64 = VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalException(ExceptionEnum.GET_IMAGE_VERIFY_CODE_FAILED);
        }
        mobile = user.getMobile();
        //保存redis
        redisTemplate.opsForValue().set(String.format(IMAGE_VERIFICATION_CODE_REDIS_KEY, mobile), verifyCode, IMAGE_VERIFICATION_EXPIRATION_TIMEOUT, TimeUnit.SECONDS);
        return BaseResult.ok(base64);

    }

    /**
     * 获取验证
     *
     * @return
     */
    @Log(name = "获取短信验证码")
    @PostMapping(value = "/verifyCode")
    public BaseResult<Void> getVerificationCode(@RequestBody @Validated VerificationCodeDTO verificationCodeDTO) {
        systemService.getVerificationCode(verificationCodeDTO);
        return BaseResult.ok();
    }


    @Log(name = "查询系统日志")
    @GetMapping("/system/log")
    @Permission(name = "system:log")
    public BaseResult<Pagination<SystemLogVO>> query(SystemLogQuery systemLogQuery) {
        return BaseResult.ok(systemLogService.query(systemLogQuery));
    }

    @Log(name = "刷新TOKEN")
    @PostMapping("/refreshToken")
    public BaseResult<RefreshTokenVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshToken) {
        return BaseResult.ok(systemService.refreshToken(refreshToken.getRefreshToken()));
    }

}
