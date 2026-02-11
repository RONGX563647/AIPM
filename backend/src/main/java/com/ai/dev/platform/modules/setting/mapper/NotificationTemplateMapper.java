package com.ai.dev.platform.modules.setting.mapper;

import com.ai.dev.platform.modules.setting.entity.NotificationTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知模板数据访问接口
 * 
 * <p>定义通知模板相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的通知模板数据CRUD操作和查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的模板记录</li>
 *   <li>deleteById：根据ID删除模板记录</li>
 *   <li>updateById：根据ID更新模板信息</li>
 *   <li>selectById：根据ID查询模板记录</li>
 *   <li>selectList：查询模板记录列表</li>
 *   <li>selectPage：分页查询模板记录</li>
 * </ul>
 * 
 * <p><b>自定义查询方法：</b>
 * <ul>
 *   <li>findByCode：根据模板编码查询</li>
 *   <li>findByType：根据模板类型查询</li>
 *   <li>findEnabled：查询启用的模板</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface NotificationTemplateMapper extends BaseMapper<NotificationTemplate> {
    
    /**
     * 根据模板编码查询模板
     * 
     * <p>根据唯一的模板编码查询对应的模板记录。
     * 
     * @param templateCode 模板编码
     * @return NotificationTemplate 对应的模板记录，未找到返回null
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification_template WHERE template_code = #{templateCode}")
    NotificationTemplate findByCode(@Param("templateCode") String templateCode);
    
    /**
     * 根据模板类型查询模板
     * 
     * <p>查询指定类型的所有模板记录。
     * 
     * @param type 模板类型
     * @return List<NotificationTemplate> 指定类型的模板记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification_template WHERE type = #{type} ORDER BY create_time DESC")
    List<NotificationTemplate> findByType(@Param("type") String type);
    
    /**
     * 查询启用的模板
     * 
     * <p>查询所有处于启用状态的模板记录。
     * 
     * @return List<NotificationTemplate> 启用的模板记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification_template WHERE enabled = true ORDER BY type, create_time")
    List<NotificationTemplate> findEnabled();
    
    /**
     * 查询启用的指定类型模板
     * 
     * <p>查询指定类型且处于启用状态的模板记录。
     * 
     * @param type 模板类型
     * @return List<NotificationTemplate> 符合条件的模板记录列表
     * @since 1.0.0
     */
    @Select("SELECT * FROM notification_template WHERE type = #{type} AND enabled = true ORDER BY create_time DESC")
    List<NotificationTemplate> findEnabledByType(@Param("type") String type);
}