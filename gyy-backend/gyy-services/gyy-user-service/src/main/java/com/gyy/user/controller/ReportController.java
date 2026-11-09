package com.gyy.user.controller;

import com.gyy.common.result.Result;
import com.gyy.user.dto.ReportQuery;
import com.gyy.user.dto.ReportVO;
import com.gyy.user.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "报表", description = "注册人员报表（docs/backend/04-业务逻辑文档.md §3）")
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "业务3：查询注册人员（含统计摘要）")
    @GetMapping("/users")
    public Result<ReportVO> report(ReportQuery query) {
        return Result.ok(reportService.report(query));
    }
}
