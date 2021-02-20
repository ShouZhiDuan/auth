package com.nvxclouds.auth.biz.service;


import com.nvxclouds.auth.biz.dto.VerificationCodeDTO;
import com.nvxclouds.auth.biz.vo.RefreshTokenVO;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/20 16:51
 * @Description:
 */
public interface SystemService {
    void getVerificationCode(VerificationCodeDTO verificationCodeDTO);

    RefreshTokenVO refreshToken(String refreshToken);
}
