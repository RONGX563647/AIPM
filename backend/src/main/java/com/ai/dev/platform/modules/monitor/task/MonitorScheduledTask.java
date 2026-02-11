package com.ai.dev.platform.modules.monitor.task;

import com.ai.dev.platform.modules.monitor.entity.Monitor;
import com.ai.dev.platform.modules.monitor.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 系统监控定时任务类
 * 
 * <p>负责执行周期性的系统监控检查任务：
 * <ul>
 *   <li>定时检测监控目标的可用性</li>
 *   <li>收集服务响应时间数据</li>
 *   <li>更新监控任务状态</li>
 *   <li>识别慢响应和故障服务</li>
 * </ul>
 * 
 * <p>该组件通过@Component注解标记为Spring组件，
 * 使用@Scheduled注解配置定时执行策略。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class MonitorScheduledTask {

    /**
     * 监控服务实例
     * 
     * <p>用于执行监控相关的业务逻辑操作。
     * 
     * @since 1.0.0
     */
    @Autowired
    private MonitorService monitorService;

    /**
     * REST客户端实例
     * 
     * <p>用于发起HTTP请求检测监控目标的可用性。
     * 
     * @since 1.0.0
     */
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 慢响应阈值（毫秒）
     * 
     * <p>定义服务响应时间的性能基准，超过此值视为慢响应。
     * 设置为500毫秒，可根据实际需求调整。
     * 
     * @since 1.0.0
     */
    private static final int SLOW_THRESHOLD = 500;

    /**
     * 定时检查监控状态
     * 
     * <p>每5分钟执行一次全面的监控检查：
     * <ol>
     *   <li>获取所有激活的监控任务</li>
     *   <li>逐个检测监控目标的可用性</li>
     *   <li>测量服务响应时间</li>
     *   <li>根据结果更新监控状态</li>
     *   <li>处理检测异常情况</li>
     * </ol>
     * 
     * <p><b>检测逻辑：</b>
     * <ul>
     *   <li>记录请求开始时间</li>
     *   <li>发起HTTP GET请求</li>
     *   <li>记录请求结束时间</li>
     *   <li>计算响应时间</li>
     *   <li>调用服务层更新状态</li>
     * </ul>
     * 
     * <p><b>异常处理：</b>
     * <ul>
     *   <li>网络异常时响应时间设为-1</li>
     *   <li>服务不可达时标记为故障状态</li>
     *   <li>继续处理其他监控任务</li>
     * </ul>
     * 
     * @since 1.0.0
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void checkMonitorStatus() {
        // 获取所有监控任务
        List<Monitor> monitors = monitorService.list();
        
        // 逐个检查监控目标状态
        for (Monitor monitor : monitors) {
            try {
                // 记录请求开始时间
                long startTime = System.currentTimeMillis();
                // 发起HTTP请求检测服务可用性
                restTemplate.getForObject(monitor.getMonitorUrl(), String.class);
                // 记录请求结束时间
                long endTime = System.currentTimeMillis();
                // 计算响应时间
                int responseTime = (int) (endTime - startTime);
                
                // 更新监控状态，传入响应时间和慢响应阈值
                monitorService.checkMonitorStatus(monitor, responseTime, SLOW_THRESHOLD);
            } catch (Exception e) {
                // 异常情况下，响应时间设为-1表示服务不可达
                monitorService.checkMonitorStatus(monitor, -1, SLOW_THRESHOLD);
            }
        }
    }
}
