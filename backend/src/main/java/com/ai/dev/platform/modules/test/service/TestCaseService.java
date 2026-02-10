package com.ai.dev.platform.modules.test.service;

import com.ai.dev.platform.modules.test.entity.TestCase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ai.dev.platform.modules.test.entity.TestReport;

import java.util.Map;

public interface TestCaseService extends IService<TestCase> {

    IPage<TestCase> getTestCasePage(Integer pageNum, Integer pageSize, Long apiId);

    Map<String, Object> runTestCase(Long caseId);
}
