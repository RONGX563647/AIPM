package com.ai.dev.platform.modules.test.service.impl;

import com.ai.dev.platform.modules.api.entity.ApiInfo;
import com.ai.dev.platform.modules.api.service.ApiInfoService;
import com.ai.dev.platform.modules.test.entity.TestCase;
import com.ai.dev.platform.modules.test.entity.TestReport;
import com.ai.dev.platform.modules.test.mapper.TestCaseMapper;
import com.ai.dev.platform.modules.test.mapper.TestReportMapper;
import com.ai.dev.platform.modules.test.service.TestCaseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class TestCaseServiceImpl extends ServiceImpl<TestCaseMapper, TestCase> implements TestCaseService {

    @Autowired
    private TestReportMapper testReportMapper;

    @Autowired
    private ApiInfoService apiInfoService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public IPage<TestCase> getTestCasePage(Integer pageNum, Integer pageSize, Long apiId) {
        Page<TestCase> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<TestCase> wrapper = new LambdaQueryWrapper<>();
        
        if (apiId != null) {
            wrapper.eq(TestCase::getApiId, apiId);
        }
        
        wrapper.orderByDesc(TestCase::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> runTestCase(Long caseId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            TestCase testCase = this.getById(caseId);
            if (testCase == null) {
                result.put("success", false);
                result.put("error", "测试用例不存在");
                return result;
            }
            
            ApiInfo apiInfo = apiInfoService.getById(testCase.getApiId());
            if (apiInfo == null) {
                result.put("success", false);
                result.put("error", "关联接口不存在");
                return result;
            }
            
            String url = "http://localhost:8080" + apiInfo.getPath();
            HttpMethod httpMethod = HttpMethod.valueOf(apiInfo.getMethod().toUpperCase());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String params = testCase.getParams() != null ? testCase.getParams() : "{}";
            String header = apiInfo.getHeader() != null ? apiInfo.getHeader() : "{}";
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode headerNode = objectMapper.readTree(header);
            headerNode.fields().forEachRemaining(entry -> {
                headers.add(entry.getKey(), entry.getValue().asText());
            });
            
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);
            
            String actualResult = response.getBody();
            String expectedResult = testCase.getExpectedResult();
            
            boolean isSuccess = compareResults(expectedResult, actualResult);
            
            TestReport testReport = new TestReport();
            testReport.setApiId(testCase.getApiId());
            testReport.setCaseId(testCase.getId());
            testReport.setActualResult(actualResult);
            testReport.setStatus(isSuccess ? "success" : "fail");
            testReport.setTestTime(LocalDateTime.now());
            testReport.setRemark(isSuccess ? "" : "预期结果与实际结果不匹配");
            
            testReportMapper.insert(testReport);
            
            result.put("success", true);
            result.put("reportId", testReport.getId());
            result.put("status", testReport.getStatus());
            result.put("actualResult", actualResult);
            result.put("expectedResult", expectedResult);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    private boolean compareResults(String expected, String actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null || actual == null) {
            return false;
        }
        return expected.equals(actual);
    }
}
