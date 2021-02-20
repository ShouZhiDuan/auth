package com.nvxclouds.auth.biz.query;

import com.nvxclouds.common.base.pojo.Page;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/4/23 19:41
 * @Description:
 */
@Getter
@Setter
@AllArgsConstructor
public class SystemLogQuery  extends Page {
    private String keyWord;
    private String loginIp;
    private String startTime;
    private String endTime;
}
