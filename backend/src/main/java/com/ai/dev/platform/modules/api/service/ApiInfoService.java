package com.ai.dev.platform.modules.api.service;

import com.ai.dev.platform.modules.api.entity.ApiInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ApiInfoService extends IService<ApiInfo> {

    IPage<ApiInfo> getApiInfoPage(Integer pageNum, Integer pageSize, Long projectId, String path, String method);

    Map<String, Object> debugApi(String path, String method, String params, String header);
}
