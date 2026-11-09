package com.gyy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyy.common.exception.BusinessException;
import com.gyy.common.result.ErrorCode;
import com.gyy.common.result.PageResult;
import com.gyy.user.dto.UserCreateRequest;
import com.gyy.user.dto.UserPageQuery;
import com.gyy.user.dto.UserUpdateRequest;
import com.gyy.user.dto.UserVO;
import com.gyy.user.entity.UserEntity;
import com.gyy.user.mapper.UserMapper;
import com.gyy.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    /** 单张头像上限 16MB（MEDIUMBLOB 级别，低于 max_allowed_packet=64MB） */
    private static final int MAX_AVATAR_BYTES = 16 * 1024 * 1024;

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<UserVO> page(UserPageQuery query) {
        QueryWrapper<UserEntity> qw = new QueryWrapper<>();
        // 列表查询显式排除 user_img 大字段（见 docs/backend/SQL/user_db.md §5.2）
        qw.select(UserEntity.class, f -> !"user_img".equals(f.getColumn()));
        if (StringUtils.hasText(query.getUserName())) {
            qw.like("user_name", query.getUserName().trim());
        }
        qw.orderByDesc("user_id");

        Page<UserEntity> page = userMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), qw);

        List<UserVO> records = page.getRecords().stream()
                .map(e -> toVO(e, false))
                .toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    public UserVO getById(Long userId) {
        // 详情同样不拉大字段，hasAvatar 通过 exists 单独判断
        QueryWrapper<UserEntity> qw = new QueryWrapper<>();
        qw.select(UserEntity.class, f -> !"user_img".equals(f.getColumn()))
          .eq("user_id", userId);
        UserEntity entity = userMapper.selectOne(qw);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在: " + userId);
        }
        boolean hasAvatar = userMapper.exists(new QueryWrapper<UserEntity>().eq("user_id", userId)
                .isNotNull("user_img"));
        return toVO(entity, hasAvatar);
    }

    @Override
    public UserVO create(UserCreateRequest request) {
        UserEntity entity = new UserEntity();
        entity.setUserName(request.getUserName().trim());
        entity.setUserAge(request.getUserAge());
        entity.setUserNote(request.getUserNote());
        userMapper.insert(entity);
        return toVO(entity, false);
    }

    @Override
    public UserVO update(Long userId, UserUpdateRequest request) {
        UserEntity exists = requireExists(userId);

        UpdateWrapper<UserEntity> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId);
        if (StringUtils.hasText(request.getUserName())) {
            uw.set("user_name", request.getUserName().trim());
        }
        if (request.getUserAge() != null) {
            uw.set("user_age", request.getUserAge());
        }
        if (request.getUserNote() != null) {
            uw.set("user_note", request.getUserNote());
        }
        userMapper.update(null, uw);

        UserVO vo = toVO(exists, false);
        if (StringUtils.hasText(request.getUserName())) {
            vo.setUserName(request.getUserName().trim());
        }
        if (request.getUserAge() != null) {
            vo.setUserAge(request.getUserAge());
        }
        if (request.getUserNote() != null) {
            vo.setUserNote(request.getUserNote());
        }
        return vo;
    }

    @Override
    public void delete(Long userId) {
        requireExists(userId);
        // 表暂无 is_deleted 字段，物理删除；接入逻辑删除后改为 UPDATE is_deleted=1
        userMapper.deleteById(userId);
    }

    @Override
    public void uploadAvatar(Long userId, byte[] imageBytes) {
        requireExists(userId);
        if (imageBytes == null || imageBytes.length == 0) {
            throw new BusinessException(ErrorCode.PARAM_MISSING, "头像文件为空");
        }
        if (imageBytes.length > MAX_AVATAR_BYTES) {
            throw new BusinessException(ErrorCode.PARAM_OUT_OF_RANGE,
                    "头像超过 16MB 上限，实际 " + imageBytes.length + " bytes");
        }
        UpdateWrapper<UserEntity> uw = new UpdateWrapper<>();
        uw.eq("user_id", userId).set("user_img", imageBytes);
        userMapper.update(null, uw);
    }

    @Override
    public byte[] getAvatar(Long userId) {
        requireExists(userId);
        QueryWrapper<UserEntity> qw = new QueryWrapper<>();
        qw.select("user_img").eq("user_id", userId);
        UserEntity entity = userMapper.selectOne(qw);
        if (entity == null || entity.getUserImg() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "该用户未上传头像");
        }
        return entity.getUserImg();
    }

    private UserEntity requireExists(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在: " + userId);
        }
        return entity;
    }

    private UserVO toVO(UserEntity entity, boolean hasAvatar) {
        UserVO vo = new UserVO();
        vo.setUserId(entity.getUserId());
        vo.setUserName(entity.getUserName());
        vo.setUserAge(entity.getUserAge());
        vo.setUserNote(entity.getUserNote());
        vo.setHasAvatar(hasAvatar);
        return vo;
    }
}
