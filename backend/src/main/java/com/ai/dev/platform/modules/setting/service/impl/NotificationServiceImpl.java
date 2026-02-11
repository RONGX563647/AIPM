package com.ai.dev.platform.modules.setting.service.impl;

import com.ai.dev.platform.modules.setting.entity.Notification;
import com.ai.dev.platform.modules.setting.entity.NotificationTemplate;
import com.ai.dev.platform.modules.setting.mapper.NotificationMapper;
import com.ai.dev.platform.modules.setting.mapper.NotificationTemplateMapper;
import com.ai.dev.platform.modules.setting.service.NotificationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知消息服务实现类
 * 
 * <p>通知消息管理业务逻辑的具体实现，负责处理：
 * <ul>
 *   <li>通知消息的创建和发送</li>
 *   <li>通知状态的更新和管理</li>
 *   <li>基于模板的通知生成</li>
 *   <li>通知统计和批量操作</li>
 *   <li>通知的分类查询和筛选</li>
 * </ul>
 * 
 * <p>该实现类继承自MyBatis-Plus的ServiceImpl，利用其提供的
 * 基础CRUD操作，专注于通知管理的业务逻辑实现。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    @Autowired
    private NotificationTemplateMapper notificationTemplateMapper;

    @Override
    public IPage<Notification> getUserNotificationsPage(Long userId, Integer pageNum, Integer pageSize,
                                                      String status, String type, String priority) {
        Page<Notification> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        
        if (StringUtils.hasText(status)) {
            wrapper.eq("status", status);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq("type", type);
        }
        if (StringUtils.hasText(priority)) {
            wrapper.eq("priority", priority);
        }
        
        wrapper.orderByDesc("create_time");
        return this.page(page, wrapper);
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId) {
        return this.baseMapper.findByStatus(userId, "unread");
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return this.baseMapper.findUnreadCount(userId);
    }

    @Override
    public Notification sendNotification(Long userId, String title, String content, String type,
                                       String priority, String relatedType, Long relatedId) {
        try {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType(type != null ? type : "info");
            notification.setPriority(priority != null ? priority : "medium");
            notification.setStatus("unread");
            notification.setIsRead(false);
            notification.setRelatedType(relatedType);
            notification.setRelatedId(relatedId);
            notification.setCreateTime(LocalDateTime.now());
            
            this.save(notification);
            return notification;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean sendSystemNotification(String title, String content, String type,
                                        String priority, List<Long> userIds) {
        try {
            // 如果userIds为null，获取所有用户ID（这里简化处理）
            if (userIds == null || userIds.isEmpty()) {
                // 模拟获取所有用户ID的逻辑
                userIds = List.of(1L, 2L, 3L); // 示例用户ID
            }
            
            for (Long userId : userIds) {
                sendNotification(userId, title, content, type, priority, "system", null);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Notification sendNotificationByTemplate(Long userId, String templateCode, Map<String, Object> parameters) {
        try {
            // 获取模板
            NotificationTemplate template = notificationTemplateMapper.findByCode(templateCode);
            if (template == null || !template.getEnabled()) {
                return null;
            }
            
            // 替换模板参数
            String title = replaceTemplateParameters(template.getTitleTemplate(), parameters);
            String content = replaceTemplateParameters(template.getContentTemplate(), parameters);
            
            // 发送通知
            return sendNotification(userId, title, content, template.getType(), "medium", 
                                  "template", null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean markAsRead(Long notificationId) {
        try {
            return this.baseMapper.markAsRead(notificationId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        try {
            return this.baseMapper.markAllAsRead(userId) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean archiveNotification(Long notificationId) {
        try {
            Notification notification = this.getById(notificationId);
            if (notification != null) {
                notification.setStatus("archived");
                return this.updateById(notification);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteNotification(Long notificationId) {
        try {
            return this.removeById(notificationId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getNotificationStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各类状态的通知数量
        QueryWrapper<Notification> unreadWrapper = new QueryWrapper<>();
        unreadWrapper.eq("user_id", userId).eq("status", "unread");
        stats.put("unreadCount", this.count(unreadWrapper));
        
        QueryWrapper<Notification> readWrapper = new QueryWrapper<>();
        readWrapper.eq("user_id", userId).eq("status", "read");
        stats.put("readCount", this.count(readWrapper));
        
        QueryWrapper<Notification> archivedWrapper = new QueryWrapper<>();
        archivedWrapper.eq("user_id", userId).eq("status", "archived");
        stats.put("archivedCount", this.count(archivedWrapper));
        
        // 统计各类别的通知数量
        List<String> types = List.of("system", "project", "task", "alert", "info");
        Map<String, Integer> typeStats = new HashMap<>();
        for (String type : types) {
            QueryWrapper<Notification> typeWrapper = new QueryWrapper<>();
            typeWrapper.eq("user_id", userId).eq("type", type);
            typeStats.put(type, Math.toIntExact(this.count(typeWrapper)));
        }
        stats.put("typeStats", typeStats);
        
        return stats;
    }

    /**
     * 替换模板中的参数占位符
     */
    private String replaceTemplateParameters(String template, Map<String, Object> parameters) {
        if (template == null || parameters == null || parameters.isEmpty()) {
            return template;
        }
        
        String result = template;
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
}