package com.nvxclouds.auth.biz.service.impl;

import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.common.core.util.PageHelper;
import com.nvxclouds.common.core.util.TimeUtils;
import com.nvxclouds.auth.biz.config.Oauth2Config;
import com.nvxclouds.auth.biz.domain.SystemLog;
import com.nvxclouds.auth.biz.feign.Oauth2Client;
import com.nvxclouds.auth.biz.query.SystemLogQuery;
import com.nvxclouds.auth.biz.service.SystemLogService;
import com.nvxclouds.auth.biz.vo.AccessTokenVO;
import com.nvxclouds.auth.biz.vo.RefreshTokenVO;
import com.nvxclouds.auth.biz.vo.SystemLogVO;
import com.nvxclouds.common.base.pojo.Pagination;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemLogServiceImpl extends AbstractService<SystemLog> implements SystemLogService {

    @Autowired
    private Oauth2Client oauth2Client;

    @Autowired
    private Oauth2Config oauth2Config;

    @Override
    public Pagination<SystemLogVO> query(SystemLogQuery systemLogQuery) {
        PageHelper.startPage(systemLogQuery.getPage(), systemLogQuery.getPerPage());
        Example example = new Example(SystemLog.class);
        example.setOrderByClause("create_time desc");
        Example.Criteria criteria = example.createCriteria();
        Optional.ofNullable(systemLogQuery.getKeyWord())
                .ifPresent((k) -> criteria.andLike("keyWord", k + "%"));
        Optional.ofNullable(systemLogQuery.getLoginIp())
                .ifPresent(ip -> criteria.andLike("remoteIp", ip + "%"));
        Optional.ofNullable(systemLogQuery.getStartTime())
                .ifPresent(startTime -> criteria.andGreaterThan("createTime", startTime));
        Optional.ofNullable(systemLogQuery.getEndTime())
                .ifPresent(endTime -> criteria.andLessThan("createTime", endTime));
        List<SystemLog> systemLogs = selectByExample(example);
        return PageHelper.end(systemLogs, (systemLog) -> {
            SystemLogVO systemLogVO = new SystemLogVO();
            BeanUtils.copyProperties(systemLog, systemLogVO);
            systemLogVO.setLoginIp(systemLog.getRemoteIp());
            systemLogVO.setId(systemLog.getID());
            systemLogVO.setOperateTime(TimeUtils.getYYYYMMDDHHMMSS(systemLog.getCreateTime()));
            systemLogVO.setOperator(systemLog.getUserName() == null ? "" : systemLog.getUserName());
            return systemLogVO;
        });
    }

    @Override
    public RefreshTokenVO refreshToken(String refreshToken) {
        Map<String, String> formData = new HashMap<>();
        formData.put("grant_type", "refresh_token");
        formData.put("client_id", oauth2Config.getClientId());
        formData.put("client_secret", oauth2Config.getClientSecret());
        formData.put("refresh_token", refreshToken);
        AccessTokenVO accessTokenVO = oauth2Client.accessToken(formData);
        return RefreshTokenVO.builder()
                .token(accessTokenVO.getAccess_token())
                .refreshToken(accessTokenVO.getRefresh_token())
                .build();
    }
}
