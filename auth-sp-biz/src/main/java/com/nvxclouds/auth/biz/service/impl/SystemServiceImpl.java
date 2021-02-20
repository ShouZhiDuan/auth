package com.nvxclouds.auth.biz.service.impl;


import com.nvxclouds.common.core.enums.ExceptionEnum;
import com.nvxclouds.common.core.exception.FeignException;
import com.nvxclouds.common.core.exception.GlobalException;
import com.nvxclouds.auth.biz.config.Oauth2Config;
import com.nvxclouds.auth.biz.domain.User;
import com.nvxclouds.auth.biz.dto.VerificationCodeDTO;
import com.nvxclouds.auth.biz.feign.NotifyClient;
import com.nvxclouds.auth.biz.feign.Oauth2Client;
import com.nvxclouds.auth.biz.service.SystemService;
import com.nvxclouds.auth.biz.service.UserService;
import com.nvxclouds.auth.biz.vo.AccessTokenVO;
import com.nvxclouds.auth.biz.vo.RefreshTokenVO;
import com.nvxclouds.common.base.pojo.BaseResult;
import com.nvxclouds.notify.api.dto.LoginMessageDTO;
import com.nvxclouds.notify.api.dto.PasswordResetMessageDTO;
import com.nvxclouds.notify.api.dto.RegisterMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.nvxclouds.common.base.constant.GlobalConstants.Verification.*;


/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 16:51
 * @Description:
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private Oauth2Client oauth2Client;

    @Autowired
    private Oauth2Config oauth2Config;


    @Autowired
    private NotifyClient notifyClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Override
    public void getVerificationCode(VerificationCodeDTO verificationCodeDTO) {

        @NotBlank String mobile = verificationCodeDTO.getMobile();
        User user = userService.selectByUserNameOrPhone(mobile,()->new GlobalException(ExceptionEnum.USER_NOT_EXIST));
        mobile = user.getMobile();
        String imageVerifyCode = redisTemplate.opsForValue().get(String.format(IMAGE_VERIFICATION_CODE_REDIS_KEY, mobile));
        if (imageVerifyCode ==null || !imageVerifyCode.equals(verificationCodeDTO.getImageVerifyCode())) {
            throw new GlobalException(ExceptionEnum.IMAGE_VALIDATION_INVALID);
        }
        int state = Integer.parseInt(verificationCodeDTO.getState());
        int sixRandomNumber = (int) ((Math.random() * 9 + 1) * 100000);
        BaseResult<Void> sendMessageResult;
        switch (state) {
            case REGISTER:
                RegisterMessageDTO registerMessage = new RegisterMessageDTO();
                registerMessage.setPhone(mobile);
                registerMessage.setMessage(String.valueOf(sixRandomNumber));
                sendMessageResult = notifyClient.sendRegisterMessage(registerMessage);
                break;
            case RESET_PASSWORD:
                PasswordResetMessageDTO passwordReset = new PasswordResetMessageDTO();
                passwordReset.setPhone(mobile);
                passwordReset.setMessage(String.valueOf(sixRandomNumber));
                sendMessageResult = notifyClient.sendPasswordResetMessage(passwordReset);
                break;
            case LOGIN:
                LoginMessageDTO loginMessage = new LoginMessageDTO();
                loginMessage.setPhone(mobile);
                loginMessage.setMessage(String.valueOf(sixRandomNumber));
                sendMessageResult = notifyClient.sendLoginMessage(loginMessage);
                break;
            default:
                throw new GlobalException(ExceptionEnum.VERIFICATION_STATE_NOT_EXIST);
        }
        if (sendMessageResult == null) {
            throw new GlobalException(ExceptionEnum.SEND_MESSAGE_ERROR);
        }
        if (sendMessageResult.error()) {
            throw new FeignException(sendMessageResult);
        }
        //保存redis
        redisTemplate.opsForValue().set(String.format(VERIFICATION_CODE_REDIS_KEY, mobile), String.valueOf(sixRandomNumber), EXPIRATION_TIMEOUT, TimeUnit.SECONDS);

    }


    @Override
    public RefreshTokenVO refreshToken(String refreshToken) {
        Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", "refresh_token");
        formData.put("client_id", oauth2Config.getClientId());
        formData.put("client_secret", oauth2Config.getClientSecret());
        formData.put("refresh_token", refreshToken);
        AccessTokenVO accessTokenVO = null;
        try {
            accessTokenVO = oauth2Client.accessToken(formData);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new GlobalException(ExceptionEnum.REFRESH_TOKEN_FAILED);
        }
        return RefreshTokenVO.builder()
                .token(accessTokenVO.getAccess_token())
                .refreshToken(accessTokenVO.getRefresh_token())
                .build();
    }
}