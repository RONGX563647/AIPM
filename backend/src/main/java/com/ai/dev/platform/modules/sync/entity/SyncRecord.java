package com.ai.dev.platform.modules.sync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@TableName("sync_record")
@Schema(description = "同步记录实体")
public class SyncRecord {

    @TableId(type = IdType.AUTO)
    @Schema(description = "同步记录唯一标识")
    private Long id;

    @Schema(description = "数据源（github/gitee）")
    private String source;

    @Schema(description = "仓库名称")
    private String repoName;

    @Schema(description = "同步时间")
    private LocalDateTime syncTime;

    @Schema(description = "同步状态（success/failed/pending）")
    private String syncStatus;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "同步数据数量")
    private Integer dataCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public LocalDateTime getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(LocalDateTime syncTime) {
        this.syncTime = syncTime;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
