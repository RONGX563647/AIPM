package com.ai.dev.platform.modules.sync.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@TableName("sync_config")
@Schema(description = "同步配置实体")
public class SyncConfig {

    @TableId(type = IdType.AUTO)
    @Schema(description = "配置唯一标识")
    private Long id;

    @Schema(description = "数据源（github/gitee）")
    private String source;

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "同步间隔（分钟）")
    private Integer syncInterval;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(Integer syncInterval) {
        this.syncInterval = syncInterval;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
