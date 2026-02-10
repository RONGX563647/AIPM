package com.ai.dev.platform.modules.task.service.impl;

import com.ai.dev.platform.modules.task.entity.Task;
import com.ai.dev.platform.modules.task.mapper.TaskMapper;
import com.ai.dev.platform.modules.task.service.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    @Override
    public Page<Task> getTaskPage(Integer pageNum, Integer pageSize, Long projectId, String status) {
        Page<Task> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(Task::getProjectId, projectId);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Task::getStatus, status);
        }
        
        wrapper.orderByDesc(Task::getCreateTime);
        
        return this.page(page, wrapper);
    }
    
    @Override
    public Integer getProjectProgress(Long projectId) {
        LambdaQueryWrapper<Task> totalWrapper = new LambdaQueryWrapper<>();
        totalWrapper.eq(Task::getProjectId, projectId);
        Long totalCount = this.count(totalWrapper);
        
        if (totalCount == null || totalCount == 0) {
            return 0;
        }
        
        LambdaQueryWrapper<Task> doneWrapper = new LambdaQueryWrapper<>();
        doneWrapper.eq(Task::getProjectId, projectId);
        doneWrapper.eq(Task::getStatus, "done");
        Long doneCount = this.count(doneWrapper);
        
        if (doneCount == null) {
            doneCount = 0L;
        }
        
        return (int) (doneCount * 100 / totalCount);
    }
}
