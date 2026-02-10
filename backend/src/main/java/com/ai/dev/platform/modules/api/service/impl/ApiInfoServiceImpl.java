package com.ai.dev.platform.modules.api.service.impl;

import com.ai.dev.platform.modules.api.entity.ApiInfo;
import com.ai.dev.platform.modules.api.mapper.ApiInfoMapper;
import com.ai.dev.platform.modules.api.service.ApiInfoService;
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

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiInfoServiceImpl extends ServiceImpl<ApiInfoMapper, ApiInfo> implements ApiInfoService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public IPage<ApiInfo> getApiInfoPage(Integer pageNum, Integer pageSize, Long projectId, String path, String method) {
        Page<ApiInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApiInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(ApiInfo::getProjectId, projectId);
        }
        if (path != null && !path.isEmpty()) {
            wrapper.like(ApiInfo::getPath, path);
        }
        if (method != null && !method.isEmpty()) {
            wrapper.eq(ApiInfo::getMethod, method);
        }
        
        wrapper.orderByDesc(ApiInfo::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Map<String, Object> debugApi(String path, String method, String params, String header) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            String url = "http://localhost:8080" + path;
            HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            if (header != null && !header.isEmpty()) {
                JsonNode headerNode = objectMapper.readTree(header);
                headerNode.fields().forEachRemaining(entry -> {
                    headers.add(entry.getKey(), entry.getValue().asText());
                });
            }
            
            HttpEntity<String> entity = new HttpEntity<>(params, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, entity, String.class);
            
            result.put("success", true);
            result.put("statusCode", response.getStatusCodeValue());
            result.put("body", response.getBody());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
