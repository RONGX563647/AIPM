package com.ai.dev.platform.modules.monitor.task;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.ai.dev.platform.modules.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class MonitorScheduledTask {

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private RestTemplate restTemplate;

    private static final int SLOW_THRESHOLD = 500;

    @Scheduled(fixedRate = 300000)
    public void checkMonitorStatus() {
        List<Monitor> monitors = monitorService.list();
        
        for (Monitor monitor : monitors) {
            try {
                long startTime = System.currentTimeMillis();
                restTemplate.getForObject(monitor.getMonitorUrl(), String.class);
                long endTime = System.currentTimeMillis();
                int responseTime = (int) (endTime - startTime);
                
                monitorService.checkMonitorStatus(monitor, responseTime, SLOW_THRESHOLD);
            } catch (Exception e) {
                monitorService.checkMonitorStatus(monitor, -1, SLOW_THRESHOLD);
            }
        }
    }
}
