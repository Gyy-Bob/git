package com.gyy.user.service;

import com.gyy.user.dto.ReportQuery;
import com.gyy.user.dto.ReportVO;

public interface ReportService {

    ReportVO report(ReportQuery query);
}
