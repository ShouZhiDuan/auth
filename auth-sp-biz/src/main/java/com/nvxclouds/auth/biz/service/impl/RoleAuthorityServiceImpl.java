package com.nvxclouds.auth.biz.service.impl;

import com.nvxclouds.common.core.service.AbstractService;
import com.nvxclouds.auth.biz.domain.RoleAuthority;
import com.nvxclouds.auth.biz.mapper.RoleAuthorityMapper;
import com.nvxclouds.auth.biz.service.RoleAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class RoleAuthorityServiceImpl extends AbstractService<RoleAuthority> implements RoleAuthorityService {

    @Autowired
    private RoleAuthorityMapper roleAuthorityMapper;


    @Override
    public List<RoleAuthority> selectByRoleID(Long id) {
        RoleAuthority record = new RoleAuthority();
        record.setRoleId(id);
        return select(record);

    }

    @Override
    public void deleteRoleAuthorityByRoleId(Long roleId) {
        Example example = new Example(RoleAuthority.class);
        example.createCriteria().andEqualTo("roleId",roleId);
        roleAuthorityMapper.deleteByExample(example);
    }
}
