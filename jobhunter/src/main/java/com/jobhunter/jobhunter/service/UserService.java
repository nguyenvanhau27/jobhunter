package com.jobhunter.jobhunter.service;

import com.jobhunter.jobhunter.dto.RegisterDTO;
import com.jobhunter.jobhunter.entity.User;

public interface UserService {
    User register(RegisterDTO dto);
    User findByEmail(String email);
}
