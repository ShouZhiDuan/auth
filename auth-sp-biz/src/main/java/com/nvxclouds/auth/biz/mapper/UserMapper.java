package com.nvxclouds.auth.biz.mapper;
import com.nvxclouds.auth.biz.query.UserQuery;
import com.nvxclouds.auth.biz.vo.UserVO;
import com.nvxclouds.common.core.mapper.Mapper;
import com.nvxclouds.auth.biz.domain.User;

import java.util.List;

public interface UserMapper extends Mapper<User> {

    List<UserVO> selectUserByPage(UserQuery query);
}