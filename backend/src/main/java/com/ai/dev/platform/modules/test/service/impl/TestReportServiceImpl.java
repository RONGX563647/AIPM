package com.ai.dev.platform.modules.test.service.impl;

import com.ai.dev.platform.modules.test.entity.TestReport;
import com.ai.dev.platform.modules.test.mapper.TestReportMapper;
import com.ai.dev.platform.modules.test.service.TestReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TestReportServiceImpl extends ServiceImpl<TestReportMapper, TestReport> implements TestReportService {

    @Override
    public IPage<TestReport> getTestReportPage(Integer pageNum, Integer pageSize, Long apiId, Long caseId, String status) {
        Page<TestReport> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TestReport> wrapper = new LambdaQueryWrapper<>();
        
        if (apiId != null) {
            wrapper.eq(TestReport::getApiId, apiId);
        }
        if (caseId != null) {
            wrapper.eq(TestReport::getCaseId, caseId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(TestReport::getStatus, status);
        }
        
        wrapper.orderByDesc(TestReport::getTestTime);
        return this.page(page, wrapper);
    }
}
