package com.nvxclouds.auth.biz.service;
import com.nvxclouds.common.core.service.Service;
import com.nvxclouds.auth.biz.domain.SystemLog;
import com.nvxclouds.auth.biz.query.SystemLogQuery;
import com.nvxclouds.auth.biz.vo.RefreshTokenVO;
import com.nvxclouds.auth.biz.vo.SystemLogVO;
import com.nvxclouds.common.base.pojo.Pagination;


public interface  SystemLogService extends Service<SystemLog> {

    Pagination<SystemLogVO> query(SystemLogQuery systemLogQuery);

    RefreshTokenVO refreshToken(String refreshToken);
}

