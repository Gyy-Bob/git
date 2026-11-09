package com.gyy.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gyy.common.exception.BusinessException;
import com.gyy.common.result.ErrorCode;
import com.gyy.user.dto.ReportQuery;
import com.gyy.user.dto.ReportSummaryVO;
import com.gyy.user.dto.ReportUserVO;
import com.gyy.user.dto.ReportVO;
import com.gyy.user.entity.UserEntity;
import com.gyy.user.mapper.UserMapper;
import com.gyy.user.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserMapper userMapper;

    public ReportServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ReportVO report(ReportQuery query) {
        int pageNum = query.getPageNum() == null ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null ? 10 : query.getPageSize();
        if (pageNum < 1) {
            throw new BusinessException(ErrorCode.PARAM_OUT_OF_RANGE, "参数超出允许范围：pageNum 须 ≥ 1");
        }
        if (pageSize < 1 || pageSize > 100) {
            throw new BusinessException(ErrorCode.PARAM_OUT_OF_RANGE, "参数超出允许范围：pageSize 须在 1-100");
        }

        LocalDateTime start = null;
        LocalDateTime endExclusive = null;
        if (StringUtils.hasText(query.getStartDate())) {
            start = parseDate(query.getStartDate()).atStartOfDay();
        }
        if (StringUtils.hasText(query.getEndDate())) {
            endExclusive = parseDate(query.getEndDate()).plusDays(1).atStartOfDay();
        }
        if (start != null && endExclusive != null && !endExclusive.isAfter(start)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数格式错误：endDate 须晚于 startDate");
        }

        // ---- 列表（不含 user_img / user_password 大字段）----
        QueryWrapper<UserEntity> listQw = new QueryWrapper<>();
        listQw.select(UserEntity.class, f -> !"user_img".equals(f.getColumn())
                        && !"user_password".equals(f.getColumn()))
                .eq("is_deleted", 0);
        if (StringUtils.hasText(query.getUserName())) {
            listQw.like("user_name", query.getUserName().trim());
        }
        if (start != null) {
            listQw.ge("created_at", start);
        }
        if (endExclusive != null) {
            listQw.lt("created_at", endExclusive);
        }
        listQw.orderByDesc("created_at");

        Page<UserEntity> page = userMapper.selectPage(new Page<>(pageNum, pageSize), listQw);
        List<ReportUserVO> records = page.getRecords().stream().map(this::toItem).toList();

        // ---- 统计摘要（全局口径，不随列表筛选变化）----
        ReportSummaryVO summary = new ReportSummaryVO();
        summary.setTotal(userMapper.selectCount(
                new QueryWrapper<UserEntity>().eq("is_deleted", 0)));
        summary.setTodayCount(userMapper.selectCount(
                new QueryWrapper<UserEntity>().eq("is_deleted", 0)
                        .ge("created_at", LocalDate.now().atStartOfDay())));
        summary.setDisabledCount(userMapper.selectCount(
                new QueryWrapper<UserEntity>().eq("is_deleted", 0).eq("status", 0)));

        ReportVO vo = new ReportVO();
        vo.setSummary(summary);
        vo.setList(records);
        vo.setPageNum(page.getCurrent());
        vo.setPageSize(page.getSize());
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        return vo;
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "参数格式错误：日期格式须为 yyyy-MM-dd");
        }
    }

    private ReportUserVO toItem(UserEntity entity) {
        ReportUserVO item = new ReportUserVO();
        item.setUserId(entity.getUserId());
        item.setUserName(entity.getUserName());
        item.setUserAge(entity.getUserAge());
        item.setStatus(entity.getStatus());
        item.setCreatedAt(entity.getCreatedAt() == null ? null : entity.getCreatedAt().format(TIME_FMT));
        return item;
    }
}
