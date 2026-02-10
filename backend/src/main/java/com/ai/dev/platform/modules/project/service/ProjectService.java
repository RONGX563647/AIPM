package com.ai.dev.platform.modules.project.service;

import com.ai.dev.platform.modules.project.entity.Project;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProjectService extends IService<Project> {
    Page<Project> getProjectPage(Integer pageNum, Integer pageSize, String name, String status);
}