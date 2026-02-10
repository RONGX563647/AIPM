package com.ai.dev.platform.modules.data.service.impl;

import com.ai.dev.platform.modules.data.entity.DataMetric;
import com.ai.dev.platform.modules.data.mapper.DataMetricMapper;
import com.ai.dev.platform.modules.data.service.DataMetricService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataMetricServiceImpl extends ServiceImpl<DataMetricMapper, DataMetric> implements DataMetricService {

    @Override
    public IPage<DataMetric> getMetricPage(Integer pageNum, Integer pageSize, String category, String name) {
        Page<DataMetric> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DataMetric> wrapper = new LambdaQueryWrapper<>();
        
        if (category != null && !category.isEmpty()) {
            wrapper.eq(DataMetric::getCategory, category);
        }
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(DataMetric::getName, name);
        }
        
        wrapper.orderByDesc(DataMetric::getTimestamp);
        
        return page(page, wrapper);
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        LambdaQueryWrapper<DataMetric> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(DataMetric::getCategory);
        wrapper.groupBy(DataMetric::getCategory);
        
        List<DataMetric> categories = list(wrapper);
        List<Map<String, Object>> stats = new ArrayList<>();
        
        for (DataMetric category : categories) {
            String categoryName = category.getCategory();
            LambdaQueryWrapper<DataMetric> categoryWrapper = new LambdaQueryWrapper<>();
            categoryWrapper.eq(DataMetric::getCategory, categoryName);
            
            List<DataMetric> metrics = list(categoryWrapper);
            
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal max = BigDecimal.ZERO;
            BigDecimal min = null;
            
            for (DataMetric metric : metrics) {
                BigDecimal value = metric.getValue();
                sum = sum.add(value);
                
                if (value.compareTo(max) > 0) {
                    max = value;
                }
                
                if (min == null || value.compareTo(min) < 0) {
                    min = value;
                }
            }
            
            BigDecimal avg = metrics.isEmpty() ? BigDecimal.ZERO : 
                sum.divide(BigDecimal.valueOf(metrics.size()), 2, RoundingMode.HALF_UP);
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("category", categoryName);
            stat.put("count", metrics.size());
            stat.put("avgValue", avg);
            stat.put("maxValue", max);
            stat.put("minValue", min != null ? min : BigDecimal.ZERO);
            
            stats.add(stat);
        }
        
        return stats;
    }
}
