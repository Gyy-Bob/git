package com.gyy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gyy.common.exception.BusinessException;
import com.gyy.common.result.ErrorCode;
import com.gyy.common.util.JwtUtil;
import com.gyy.user.dto.LoginRequest;
import com.gyy.user.dto.LoginVO;
import com.gyy.user.dto.RegisterRequest;
import com.gyy.user.dto.RegisterVO;
import com.gyy.user.entity.UserEntity;
import com.gyy.user.mapper.UserMapper;
import com.gyy.user.service.AuthService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class AuthServiceImpl implements AuthService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public RegisterVO register(RegisterRequest request) {
        String name = request.getUserName().trim();

        // 应用层查重 + 唯一索引 uk_user_name 双兜底（并发竞态由 DuplicateKeyException 兜住）
        Long exists = userMapper.selectCount(new QueryWrapper<UserEntity>().eq("user_name", name));
        if (exists != null && exists > 0) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN);
        }

        UserEntity entity = new UserEntity();
        entity.setUserName(name);
        entity.setUserPassword(encoder.encode(request.getPassword()));
        entity.setUserAge(request.getUserAge());
        entity.setUserNote(request.getUserNote());
        try {
            userMapper.insert(entity);
        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN);
        }

        // 重新查回以取得 DB 默认值（created_at 等）
        UserEntity saved = userMapper.selectById(entity.getUserId());
        RegisterVO vo = new RegisterVO();
        vo.setUserId(saved.getUserId());
        vo.setUserName(saved.getUserName());
        vo.setCreatedAt(saved.getCreatedAt() == null ? null : saved.getCreatedAt().format(FMT));
        return vo;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        UserEntity user = userMapper.selectOne(
                new QueryWrapper<UserEntity>().eq("user_name", request.getUserName().trim()));
        // 用户不存在与密码错误对外统一提示，防止账号枚举
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        if (!encoder.matches(request.getPassword(), user.getUserPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }
        String token = jwtUtil.createToken(user.getUserId(), user.getUserName());
        return new LoginVO(token, jwtUtil.getTtlSeconds(), user.getUserId(), user.getUserName());
    }
}
