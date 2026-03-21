package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.entity.User;

public interface UserService {
    User register(User user);

    User findByEmail(String email);
}
