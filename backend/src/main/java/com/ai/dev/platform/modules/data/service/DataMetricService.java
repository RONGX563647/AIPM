package com.ai.dev.platform.modules.data.service;

import com.ai.dev.platform.modules.data.entity.DataMetric;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface DataMetricService extends IService<DataMetric> {
    IPage<DataMetric> getMetricPage(Integer pageNum, Integer pageSize, String category, String name);
    List<Map<String, Object>> getCategoryStats();
}
