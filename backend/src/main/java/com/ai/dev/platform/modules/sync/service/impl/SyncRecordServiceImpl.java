package com.ai.dev.platform.modules.sync.service.impl;

import com.ai.dev.platform.modules.sync.entity.SyncRecord;
import com.ai.dev.platform.modules.sync.mapper.SyncRecordMapper;
import com.ai.dev.platform.modules.sync.service.SyncRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 同步记录服务实现类
 * 
 * <p>同步记录管理业务逻辑的具体实现，负责处理：
 * <ul>
 *   <li>同步操作的记录和追踪</li>
 *   <li>同步状态的管理和更新</li>
 *   <li>同步错误信息的收集</li>
 *   <li>同步数据量的统计</li>
 * </ul>
 * 
 * <p>该实现类继承自MyBatis-Plus的ServiceImpl，利用其提供的
 * 基础CRUD操作，专注于同步管理的业务逻辑实现。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class SyncRecordServiceImpl extends ServiceImpl<SyncRecordMapper, SyncRecord> implements SyncRecordService {

    /**
     * 记录同步操作详情
     * 
     * <p>创建新的同步记录，详细记录同步操作的各项信息：
     * <ul>
     *   <li>同步来源平台(GitHub/Gitee)</li>
     *   <li>同步的仓库名称</li>
     *   <li>同步操作的时间戳</li>
     *   <li>同步执行的状态结果</li>
     *   <li>同步过程中的错误信息</li>
     *   <li>同步处理的数据条数</li>
     * </ul>
     * 
     * <p><b>记录流程：</b>
     * <ol>
     *   <li>创建新的SyncRecord对象</li>
     *   <li>设置同步来源和仓库信息</li>
     *   <li>记录当前时间和同步状态</li>
     *   <li>保存错误信息(如有)</li>
     *   <li>记录处理的数据量</li>
     *   <li>保存到数据库</li>
     * </ol>
     * 
     * @param source 同步来源平台，如"GitHub"或"Gitee"
     * @param repoName 同步的仓库名称
     * @param status 同步状态，如"success"/"failed"/"processing"
     * @param errorMessage 同步错误信息，成功时可为null
     * @param dataCount 同步处理的数据条数，null时默认为0
     * @since 1.0.0
     */
    @Override
    public void recordSync(String source, String repoName, String status, String errorMessage, Integer dataCount) {
        // 创建新的同步记录对象
        SyncRecord record = new SyncRecord();
        // 设置同步来源平台
        record.setSource(source);
        // 设置同步的仓库名称
        record.setRepoName(repoName);
        // 设置同步时间
        record.setSyncTime(LocalDateTime.now());
        // 设置同步状态
        record.setSyncStatus(status);
        // 设置错误信息(可能为null)
        record.setErrorMessage(errorMessage);
        // 设置同步数据量，null时默认为0
        record.setDataCount(dataCount != null ? dataCount : 0);
        // 设置记录创建时间
        record.setCreateTime(LocalDateTime.now());
        
        // 保存同步记录到数据库
        this.save(record);
    }
}
