package com.ai.dev.platform.modules.sync.task;

import com.ai.dev.platform.modules.sync.service.DataSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduledTask.class);

    @Autowired
    private DataSyncService dataSyncService;

    @Scheduled(cron = "0 0 * * * ?")
    public void syncDataHourly() {
        logger.info("定时任务：开始执行每小时数据同步");
        
        try {
            dataSyncService.syncAll();
            logger.info("定时任务：每小时数据同步完成");
        } catch (Exception e) {
            logger.error("定时任务：每小时数据同步失败", e);
        }
    }
}
