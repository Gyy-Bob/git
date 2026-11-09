package com.gyy.user.controller;

import com.gyy.common.result.PageResult;
import com.gyy.common.result.Result;
import com.gyy.user.dto.UserCreateRequest;
import com.gyy.user.dto.UserPageQuery;
import com.gyy.user.dto.UserUpdateRequest;
import com.gyy.user.dto.UserVO;
import com.gyy.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 用户接口（Apifox 目录「用户管理」对应此处，见 docs/backend/02-接口规范与Apifox.md）
 */
@Tag(name = "用户管理", description = "user 库 user_db 表 CRUD 与头像存取")
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户")
    @GetMapping
    public Result<PageResult<UserVO>> page(@Valid UserPageQuery query,
                                           @RequestParam(value = "userName", required = false) String userName) {
        query.setUserName(userName);
        return Result.ok(userService.page(query));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{userId}")
    public Result<UserVO> detail(@PathVariable("userId") Long userId) {
        return Result.ok(userService.getById(userId));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.ok(userService.create(request));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/{userId}")
    public Result<UserVO> update(@PathVariable Long userId,
                                 @Valid @RequestBody UserUpdateRequest request) {
        return Result.ok(userService.update(userId, request));
    }

    @Operation(summary = "删除用户（表暂无逻辑删除字段，物理删除）")
    @DeleteMapping("/{userId}")
    public Result<Void> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return Result.ok();
    }

    @Operation(summary = "上传头像（存 user_img LONGBLOB，≤16MB）")
    @PostMapping(value = "/{userId}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Boolean> uploadAvatar(@PathVariable Long userId,
                                        @RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadAvatar(userId, file.getBytes());
        return Result.ok(Boolean.TRUE);
    }

    @Operation(summary = "下载头像（原始字节）")
    @GetMapping(value = "/{userId}/avatar", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] downloadAvatar(@PathVariable Long userId) {
        return userService.getAvatar(userId);
    }
}
