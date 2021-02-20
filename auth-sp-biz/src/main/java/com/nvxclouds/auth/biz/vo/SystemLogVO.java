package com.nvxclouds.auth.biz.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/23 19:43
 * @Description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemLogVO {
    private Long id;
    private String keyWord;
    private String loginIp;
    private String operateTime;
    private String params;
    private Integer result;
    private String method;
    private String operator;
}
