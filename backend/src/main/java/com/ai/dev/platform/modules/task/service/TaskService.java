package com.ai.dev.platform.modules.task.service;

import com.ai.dev.platform.modules.task.entity.Task;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TaskService extends IService<Task> {
    Page<Task> getTaskPage(Integer pageNum, Integer pageSize, Long projectId, String status);
    Integer getProjectProgress(Long projectId);
}
