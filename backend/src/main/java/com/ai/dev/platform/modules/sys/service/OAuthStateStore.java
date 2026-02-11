package com.ai.dev.platform.modules.sys.service;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.DisposableBean;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * OAuth状态存储器
 * 
 * <p>用于存储和验证OAuth认证流程中的"state"参数，提供：
 * <ul>
 *   <li>短期的状态值存储（5分钟TTL）</li>
 *   <li>自动清理过期状态值</li>
 *   <li>防止CSRF攻击的安全机制</li>
 *   <li>无状态的认证流程支持</li>
 * </ul>
 * 
 * <p>该组件使用内存存储避免依赖客户端会话，通过定时清理
 * 机制确保内存使用效率和安全性。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class OAuthStateStore implements DisposableBean {
    
    /**
     * 状态值存储映射表
     * 
     * <p>使用ConcurrentHashMap保证线程安全的并发访问。
     * Key为状态值字符串，Value为创建时间戳。
     * 
     * @since 1.0.0
     */
    private final ConcurrentMap<String, Long> store = new ConcurrentHashMap<>();
    
    /**
     * 状态值生存时间（毫秒）
     * 
     * <p>设置为5分钟，超过此时间的状态值将被视为无效。
     * 这个时间应该足够完成OAuth认证流程，同时避免长期占用内存。
     * 
     * @since 1.0.0
     */
    private final long ttlMs = TimeUnit.MINUTES.toMillis(5);
    
    /**
     * 清理任务调度器
     * 
     * <p>使用单线程调度器定期清理过期的状态值，
     * 设置为守护线程确保应用关闭时能正常退出。
     * 
     * @since 1.0.0
     */
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "oauth-state-cleaner");
        t.setDaemon(true);
        return t;
    });

    /**
     * 构造函数
     * 
     * <p>初始化时启动定时清理任务，每5分钟执行一次清理操作。
     * 
     * @since 1.0.0
     */
    public OAuthStateStore() {
        cleaner.scheduleAtFixedRate(this::cleanup, ttlMs, ttlMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 创建新的状态值
     * 
     * <p>生成唯一的UUID作为状态值，并记录当前时间戳。
     * 用于OAuth认证流程的CSRF保护。
     * 
     * @return String 新生成的唯一状态值
     * @since 1.0.0
     */
    public String createState() {
        String s = UUID.randomUUID().toString();
        store.put(s, System.currentTimeMillis());
        return s;
    }

    /**
     * 验证并移除状态值
     * 
     * <p>验证状态值的有效性并从存储中移除：
     * <ol>
     *   <li>检查状态值是否存在</li>
     *   <li>验证是否在有效期内</li>
     *   <li>移除已使用状态值</li>
     * </ol>
     * 
     * @param state 待验证的状态值
     * @return boolean 验证成功返回true，失败返回false
     * @since 1.0.0
     */
    public boolean validateAndRemoveState(String state) {
        if (state == null) return false;
        Long ts = store.remove(state);
        if (ts == null) return false;
        return (System.currentTimeMillis() - ts) <= ttlMs;
    }

    /**
     * 清理过期状态值
     * 
     * <p>遍历所有存储的状态值，移除超过生存时间的记录。
     * 该方法由定时任务定期调用。
     * 
     * @since 1.0.0
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> e : store.entrySet()) {
            if (now - e.getValue() > ttlMs) store.remove(e.getKey());
        }
    }

    /**
     * 销毁方法
     * 
     * <p>实现DisposableBean接口，在Spring容器关闭时调用。
     * 立即关闭清理任务调度器，释放相关资源。
     * 
     * @throws Exception 关闭过程中可能抛出的异常
     * @since 1.0.0
     */
    @Override
    public void destroy() {
        cleaner.shutdownNow();
    }
}
