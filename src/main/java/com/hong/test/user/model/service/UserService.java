package com.hong.test.user.model.service;

import com.hong.test.user.model.dto.User;

public interface UserService {
    int insertUser(String userId, String userPw);

    User findByUserId(User user);
}
