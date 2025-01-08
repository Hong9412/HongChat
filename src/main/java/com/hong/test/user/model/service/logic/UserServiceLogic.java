package com.hong.test.user.model.service.logic;

import com.hong.test.user.model.dto.User;
import com.hong.test.user.model.mapper.UserMapper;
import com.hong.test.user.model.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceLogic implements UserService {

    private final UserMapper userMapper;

    public UserServiceLogic(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int insertUser(String userId, String userPw) {
        int result = userMapper.insertUser(userId, userPw);
        return result;
    }

    @Override
    public User findByUserId(User user) {
        user = userMapper.findByUserId(user);
        return user;
    }
}
