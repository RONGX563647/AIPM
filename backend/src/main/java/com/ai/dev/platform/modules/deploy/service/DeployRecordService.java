package com.ai.dev.platform.modules.deploy.service;

import com.ai.dev.platform.modules.deploy.entity.DeployRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeployRecordService extends IService<DeployRecord> {
    IPage<DeployRecord> getDeployPage(Integer pageNum, Integer pageSize, Long projectId, String env, String status, String version);
}
