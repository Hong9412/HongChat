package com.hong.test.user.model.mapper;

import com.hong.test.user.model.dto.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    int insertUser(String userId, String userPw);

    User findByUserId(User user);
}
