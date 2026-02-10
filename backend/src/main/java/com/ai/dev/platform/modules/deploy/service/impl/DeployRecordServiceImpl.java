package com.ai.dev.platform.modules.deploy.service.impl;

import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.ai.dev.platform.modules.deploy.mapper.DeployRecordMapper;
import com.ai.dev.platform.modules.deploy.service.DeployRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DeployRecordServiceImpl extends ServiceImpl<DeployRecordMapper, DeployRecord> implements DeployRecordService {

    @Override
    public IPage<DeployRecord> getDeployPage(Integer pageNum, Integer pageSize, Long projectId, String env, String status, String version) {
        Page<DeployRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DeployRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(DeployRecord::getProjectId, projectId);
        }
        
        if (env != null && !env.isEmpty()) {
            wrapper.eq(DeployRecord::getEnv, env);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(DeployRecord::getStatus, status);
        }
        
        if (version != null && !version.isEmpty()) {
            wrapper.like(DeployRecord::getVersion, version);
        }
        
        wrapper.orderByDesc(DeployRecord::getCreateTime);
        
        return page(page, wrapper);
    }
}
