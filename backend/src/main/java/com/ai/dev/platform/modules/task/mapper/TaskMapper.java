package com.ai.dev.platform.modules.task.mapper;

import com.ai.dev.platform.modules.task.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务数据访问接口
 * 
 * <p>定义任务相关的数据库操作接口，继承自MyBatis-Plus的BaseMapper，
 * 提供标准的任务数据CRUD操作和状态查询方法。
 * 
 * <p>该Mapper接口通过@Mapper注解标记为MyBatis Mapper，
 * 由MyBatis-Plus自动实现基础的数据库操作方法。
 * 
 * <p><b>继承的基础操作包括：</b>
 * <ul>
 *   <li>insert：插入新的任务记录</li>
 *   <li>deleteById：根据ID删除任务记录</li>
 *   <li>updateById：根据ID更新任务信息</li>
 *   <li>selectById：根据ID查询任务记录</li>
 *   <li>selectList：查询任务记录列表</li>
 *   <li>selectPage：分页查询任务记录</li>
 * </ul>
 * 
 * <p><b>业务查询建议：</b>
 * <ul>
 *   <li>为projectId、status字段建立复合索引</li>
 *   <li>为createTime字段建立索引支持排序查询</li>
 *   <li>考虑添加任务优先级相关的查询方法</li>
 * </ul>
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    // 继承自BaseMapper的所有方法都可直接使用
    // 如需自定义SQL查询，可在此接口中添加方法声明
}
