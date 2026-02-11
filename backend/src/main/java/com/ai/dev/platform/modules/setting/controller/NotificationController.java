package com.ai.dev.platform.modules.setting.controller;

import com.ai.dev.platform.common.Result;
import com.ai.dev.platform.modules.setting.entity.Notification;
import com.ai.dev.platform.modules.setting.service.NotificationService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知消息管理控制器
 * 
 * <p>负责处理通知消息相关的HTTP请求，包括：
 * <ul>
 *   <li>通知消息的查询和管理</li>
 *   <li>通知状态的更新操作</li>
 *   <li>通知的发送和模板使用</li>
 *   <li>通知统计信息查询</li>
 * </ul>
 * 
 * <p>该控制器提供RESTful风格的API接口，支持通知消息的完整管理功能。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Tag(name = "通知消息管理", description = "系统通知消息管理接口")
@RestController
@RequestMapping("/setting/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 分页查询用户通知
     * 
     * <p>支持多条件筛选的用户通知分页查询。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param status 通知状态（可选）
     * @param type 通知类型（可选）
     * @param priority 优先级（可选）
     * @return Result<IPage<Notification>> 分页查询结果
     * @since 1.0.0
     */
    @Operation(summary = "分页查询通知", description = "支持多条件筛选的通知分页查询")
    @GetMapping("/page")
    public Result<IPage<Notification>> getUserNotificationsPage(
            @RequestHeader("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priority) {
        
        IPage<Notification> page = notificationService.getUserNotificationsPage(
                userId, pageNum, pageSize, status, type, priority);
        return Result.ok(page);
    }

    /**
     * 获取用户未读通知列表
     * 
     * <p>查询当前登录用户的所有未读通知消息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<List<Notification>> 未读通知列表
     * @since 1.0.0
     */
    @Operation(summary = "获取未读通知", description = "查询用户所有未读通知")
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(@RequestHeader("userId") Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return Result.ok(notifications);
    }

    /**
     * 获取用户未读通知数量
     * 
     * <p>统计当前登录用户的未读通知数量。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<Integer> 未读通知数量
     * @since 1.0.0
     */
    @Operation(summary = "获取未读数量", description = "统计用户未读通知数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(@RequestHeader("userId") Long userId) {
        Integer count = notificationService.getUnreadCount(userId);
        return Result.ok(count);
    }

    /**
     * 发送通知
     * 
     * <p>向指定用户发送通知消息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param targetUserId 接收用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param priority 通知优先级
     * @param relatedType 关联业务类型
     * @param relatedId 关联业务ID
     * @return Result<Notification> 发送的通知对象
     * @since 1.0.0
     */
    @Operation(summary = "发送通知", description = "向用户发送通知消息")
    @PostMapping("/send")
    public Result<Notification> sendNotification(
            @RequestHeader("userId") Long userId,
            @RequestParam Long targetUserId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "info") String type,
            @RequestParam(defaultValue = "medium") String priority,
            @RequestParam(required = false) String relatedType,
            @RequestParam(required = false) Long relatedId) {
        
        Notification notification = notificationService.sendNotification(
                targetUserId, title, content, type, priority, relatedType, relatedId);
        
        if (notification != null) {
            return Result.ok(notification);
        } else {
            return Result.error("通知发送失败");
        }
    }

    /**
     * 发送系统通知
     * 
     * <p>发送系统级别的通知消息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param title 通知标题
     * @param content 通知内容
     * @param type 通知类型
     * @param priority 通知优先级
     * @param userIds 接收用户ID列表（可选，null表示所有用户）
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "发送系统通知", description = "发送系统级别的通知消息")
    @PostMapping("/send-system")
    public Result<String> sendSystemNotification(
            @RequestHeader("userId") Long userId,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "system") String type,
            @RequestParam(defaultValue = "medium") String priority,
            @RequestBody(required = false) List<Long> userIds) {
        
        boolean success = notificationService.sendSystemNotification(title, content, type, priority, userIds);
        if (success) {
            return Result.ok("系统通知发送成功");
        } else {
            return Result.error("系统通知发送失败");
        }
    }

    /**
     * 基于模板发送通知
     * 
     * <p>使用预定义的通知模板发送参数化的通知消息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param targetUserId 接收用户ID
     * @param templateCode 模板编码
     * @param parameters 模板参数
     * @return Result<Notification> 发送的通知对象
     * @since 1.0.0
     */
    @Operation(summary = "模板发送通知", description = "使用模板发送参数化通知")
    @PostMapping("/send-template")
    public Result<Notification> sendNotificationByTemplate(
            @RequestHeader("userId") Long userId,
            @RequestParam Long targetUserId,
            @RequestParam String templateCode,
            @RequestBody Map<String, Object> parameters) {
        
        Notification notification = notificationService.sendNotificationByTemplate(
                targetUserId, templateCode, parameters);
        
        if (notification != null) {
            return Result.ok(notification);
        } else {
            return Result.error("基于模板的通知发送失败");
        }
    }

    /**
     * 标记通知为已读
     * 
     * <p>将指定通知标记为已读状态。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param notificationId 通知ID
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "标记已读", description = "将通知标记为已读状态")
    @PutMapping("/mark-read/{notificationId}")
    public Result<String> markAsRead(
            @RequestHeader("userId") Long userId,
            @PathVariable Long notificationId) {
        
        boolean success = notificationService.markAsRead(notificationId);
        if (success) {
            return Result.ok("通知标记为已读");
        } else {
            return Result.error("标记已读失败");
        }
    }

    /**
     * 批量标记通知为已读
     * 
     * <p>将用户的所有未读通知批量标记为已读。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "批量标记已读", description = "批量标记所有未读通知为已读")
    @PutMapping("/mark-all-read")
    public Result<String> markAllAsRead(@RequestHeader("userId") Long userId) {
        boolean success = notificationService.markAllAsRead(userId);
        if (success) {
            return Result.ok("所有通知标记为已读");
        } else {
            return Result.error("批量标记已读失败");
        }
    }

    /**
     * 归档通知
     * 
     * <p>将指定通知归档处理。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param notificationId 通知ID
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "归档通知", description = "将通知归档处理")
    @PutMapping("/archive/{notificationId}")
    public Result<String> archiveNotification(
            @RequestHeader("userId") Long userId,
            @PathVariable Long notificationId) {
        
        boolean success = notificationService.archiveNotification(notificationId);
        if (success) {
            return Result.ok("通知归档成功");
        } else {
            return Result.error("通知归档失败");
        }
    }

    /**
     * 删除通知
     * 
     * <p>删除指定通知消息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @param notificationId 通知ID
     * @return Result<String> 操作结果
     * @since 1.0.0
     */
    @Operation(summary = "删除通知", description = "删除通知消息")
    @DeleteMapping("/delete/{notificationId}")
    public Result<String> deleteNotification(
            @RequestHeader("userId") Long userId,
            @PathVariable Long notificationId) {
        
        boolean success = notificationService.deleteNotification(notificationId);
        if (success) {
            return Result.ok("通知删除成功");
        } else {
            return Result.error("通知删除失败");
        }
    }

    /**
     * 获取通知统计信息
     * 
     * <p>获取当前登录用户的通知统计概况信息。
     * 
     * @param userId 用户ID（从JWT token中获取）
     * @return Result<Map<String, Object>> 通知统计信息
     * @since 1.0.0
     */
    @Operation(summary = "获取通知统计", description = "获取用户通知统计信息")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getNotificationStats(@RequestHeader("userId") Long userId) {
        Map<String, Object> stats = notificationService.getNotificationStats(userId);
        return Result.ok(stats);
    }
}