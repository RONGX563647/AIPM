package com.ai.dev.platform.modules.setting.service;

import com.ai.dev.platform.modules.setting.entity.Notification;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 通知消息服务接口
 * 
 * <p>定义通知消息管理的核心业务逻辑接口，提供：
 * <ul>
 *   <li>通知消息的增删改查操作</li>
 *   <li>通知状态的管理和更新</li>
 *   <li>未读通知统计和批量操作</li>
 *   <li>基于模板的通知生成</li>
 *   <li>通知的分类和筛选查询</li>
 * </ul>
 * 
 * <p>该接口继承自MyBatis-Plus的IService，提供了基础的CRUD操作，
 * 同时扩展了通知管理特有的业务方法。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
public interface NotificationService extends IService<Notification> {
    
    /**
     * 分页查询用户通知
     * 
     * <p>支持多条件筛选的用户通知分页查询功能：
     * <ul>
     *   <li>按通知状态筛选(unread/read/archived)</li>
     *   <li>按通知类型筛选(system/project/task等)</li>
     *   <li>按优先级筛选</li>
     *   <li>按创建时间倒序排列</li>
     * </ul>
     * 
     * @param userId 用户ID
     * @param pageNum 页码，从1开始计数
     * @param pageSize 每页记录数
     * @param status 通知状态(可选)
     * @param type 通知类型(可选)
     * @param priority 优先级(可选)
     * @return IPage<Notification> 分页查询结果
     * @since 1.0.0
     */
    IPage<Notification> getUserNotificationsPage(Long userId, Integer pageNum, Integer pageSize, 
                                               String status, String type, String priority);
    
    /**
     * 获取用户未读通知列表
     * 
     * <p>查询指定用户的所有未读通知消息。
     * 
     * @param userId 用户ID
     * @return List<Notification> 未读通知列表
     * @since 1.0.0
     */
    List<Notification> getUnreadNotifications(Long userId);
    
    /**
     * 获取用户未读通知数量
     * 
     * <p>统计指定用户的未读通知数量，用于显示提醒。
     * 
     * @param userId 用户ID
     * @return Integer 未读通知数量
     * @since 1.0.0
     */
    Integer getUnreadCount(Long userId);
    
    /**
     * 发送通知
     * 
     * <p>向指定用户发送通知消息：
     * <ul>
     *   <li>创建新的通知记录</li>
     *   <li>设置默认状态和优先级</li>
     *   <li>关联相关业务信息</li>
     *   <li>记录发送时间</li>
     * </ul>
     * 
     * @param userId 接收用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param priority 通知优先级
     * @param relatedType 关联业务类型
     * @param relatedId 关联业务ID
     * @return Notification 创建的通知对象
     * @since 1.0.0
     */
    Notification sendNotification(Long userId, String title, String content, String type, 
                                String priority, String relatedType, Long relatedId);
    
    /**
     * 发送系统通知
     * 
     * <p>发送系统级别的通知消息给所有用户或特定用户组。
     * 
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param priority 通知优先级
     * @param userIds 接收用户ID列表(null表示所有用户)
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean sendSystemNotification(String title, String content, String type, 
                                 String priority, List<Long> userIds);
    
    /**
     * 基于模板发送通知
     * 
     * <p>使用预定义的通知模板发送参数化的通知消息。
     * 
     * @param userId 接收用户ID
     * @param templateCode 模板编码
     * @param parameters 模板参数
     * @return Notification 创建的通知对象
     * @since 1.0.0
     */
    Notification sendNotificationByTemplate(Long userId, String templateCode, Map<String, Object> parameters);
    
    /**
     * 标记通知为已读
     * 
     * <p>将指定通知标记为已读状态。
     * 
     * @param notificationId 通知ID
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean markAsRead(Long notificationId);
    
    /**
     * 批量标记通知为已读
     * 
     * <p>将用户的所有未读通知批量标记为已读。
     * 
     * @param userId 用户ID
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean markAllAsRead(Long userId);
    
    /**
     * 归档通知
     * 
     * <p>将指定通知归档处理。
     * 
     * @param notificationId 通知ID
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean archiveNotification(Long notificationId);
    
    /**
     * 删除通知
     * 
     * <p>逻辑删除指定通知消息。
     * 
     * @param notificationId 通知ID
     * @return boolean 操作是否成功
     * @since 1.0.0
     */
    boolean deleteNotification(Long notificationId);
    
    /**
     * 获取通知统计信息
     * 
     * <p>获取用户的通知统计概况信息。
     * 
     * @param userId 用户ID
     * @return Map<String, Object> 统计信息映射
     * @since 1.0.0
     */
    Map<String, Object> getNotificationStats(Long userId);
}