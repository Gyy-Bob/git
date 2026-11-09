package com.gyy.user.service;

import com.gyy.common.result.PageResult;
import com.gyy.user.dto.UserCreateRequest;
import com.gyy.user.dto.UserPageQuery;
import com.gyy.user.dto.UserUpdateRequest;
import com.gyy.user.dto.UserVO;

public interface UserService {

    PageResult<UserVO> page(UserPageQuery query);

    UserVO getById(Long userId);

    UserVO create(UserCreateRequest request);

    UserVO update(Long userId, UserUpdateRequest request);

    void delete(Long userId);

    void uploadAvatar(Long userId, byte[] imageBytes);

    byte[] getAvatar(Long userId);
}
