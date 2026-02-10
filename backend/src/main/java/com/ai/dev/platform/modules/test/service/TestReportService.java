package com.ai.dev.platform.modules.test.service;

import com.ai.dev.platform.modules.test.entity.TestReport;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TestReportService extends IService<TestReport> {

    IPage<TestReport> getTestReportPage(Integer pageNum, Integer pageSize, Long apiId, Long caseId, String status);
}
