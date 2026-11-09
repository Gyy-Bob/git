package com.gyy.user.service;

import com.gyy.user.dto.LoginRequest;
import com.gyy.user.dto.LoginVO;
import com.gyy.user.dto.RegisterRequest;
import com.gyy.user.dto.RegisterVO;

public interface AuthService {

    RegisterVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);
}
