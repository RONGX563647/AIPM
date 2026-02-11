package com.ai.dev.platform.modules.sync.service;

import com.ai.dev.platform.modules.sync.entity.SyncRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SyncRecordService extends IService<SyncRecord> {
    void recordSync(String source, String repoName, String status, String errorMessage, Integer dataCount);
}
