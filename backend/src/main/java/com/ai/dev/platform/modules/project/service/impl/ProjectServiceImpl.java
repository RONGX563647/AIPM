package com.ai.dev.platform.modules.project.service.impl;

import com.ai.dev.platform.modules.project.entity.Project;
import com.ai.dev.platform.modules.project.mapper.ProjectMapper;
import com.ai.dev.platform.modules.project.service.ProjectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    @Override
    public Page<Project> getProjectPage(Integer pageNum, Integer pageSize, String name, String status) {
        Page<Project> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Project::getName, name);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Project::getStatus, status);
        }
        
        wrapper.orderByDesc(Project::getCreateTime);
        
        return this.page(page, wrapper);
    }
}