package com.ai.dev.platform.modules.setting.mapper;

import com.ai.dev.platform.modules.setting.entity.Notification;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 通知消息数据访问接口
 * 
 * <p>定义通知消息相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的通知消息数据CRUD操作和查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的通知记录</li>
 *   <li>deleteById：根据ID删除通知记录</li>
 *   <li>updateById：根据ID更新通知信息</li>
 *   <li>selectById：根据ID查询通知记录</li>
 *   <li>selectList：查询通知记录列表</li>
 *   <li>selectPage：分页查询通知记录</li>
 * </ul>
 * 
 * <p><b>自定义查询方法：</b>
 * <ul>
 *   <li>findByUserId：根据用户ID查询通知</li>
 *   <li>findByStatus：根据状态查询通知</li>
 *   <li>findByType：根据类型查询通知</li>
 *   <li>findUnreadCount：查询未读通知数量</li>
 *   <li>markAsRead：标记通知为已读</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 根据用户ID查询通知
     * 
     * <p>查询指定用户的所有通知消息，按创建时间倒序排列。
     * 
     * @param userId 用户ID
     * @return List<Notification> 用户的所有通知记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和状态查询通知
     * 
     * <p>查询指定用户特定状态的通知消息。
     * 
     * @param userId 用户ID
     * @param status 通知状态(unread/read/archived)
     * @return List<Notification> 符合条件的通知记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND status = #{status} ORDER BY create_time DESC")
    List<Notification> findByStatus(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 根据用户ID和类型查询通知
     * 
     * <p>查询指定用户特定类型的通知消息。
     * 
     * @param userId 用户ID
     * @param type 通知类型(system/project/task等)
     * @return List<Notification> 符合条件的通知记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification WHERE user_id = #{userId} AND type = #{type} ORDER BY create_time DESC")
    List<Notification> findByType(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 查询用户未读通知数量
     * 
     * <p>统计指定用户的未读通知数量，用于显示未读提醒。
     * 
     * @param userId 用户ID
     * @return Integer 未读通知数量
     * @since 1.0.0
     */
    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND status = 'unread'")
    Integer findUnreadCount(@Param("userId") Long userId);
    
    /**
     * 标记通知为已读
     * 
     * <p>将指定通知标记为已读状态，并记录阅读时间。
     * 
     * @param notificationId 通知ID
     * @return int 更新的记录数
     * @since 1.0.0
     */
    @Update("UPDATE notification SET status = 'read', is_read = true, read_time = CURRENT_TIMESTAMP WHERE id = #{notificationId}")
    int markAsRead(@Param("notificationId") Long notificationId);
    
    /**
     * 批量标记通知为已读
     * 
     * <p>将用户的所有未读通知批量标记为已读。
     * 
     * @param userId 用户ID
     * @return int 更新的记录数
     * @since 1.0.0
     */
    @Update("UPDATE notification SET status = 'read', is_read = true, read_time = CURRENT_TIMESTAMP WHERE user_id = #{userId} AND status = 'unread'")
    int markAllAsRead(@Param("userId") Long userId);
}