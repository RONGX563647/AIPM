package com.ai.dev.platform.modules.deploy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("deploy_record")
@Schema(description = "部署记录实体")
public class DeployRecord {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "部署记录唯一标识")
    private Long id;

    @TableField("project_id")
    @Schema(description = "关联项目ID")
    private Long projectId;

    @Schema(description = "部署版本")
    private String version;

    @Schema(description = "部署环境")
    private String env;

    @Schema(description = "部署状态")
    private String status;

    @Schema(description = "部署内容")
    private String content;

    @TableField("create_time")
    @Schema(description = "部署时间")
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
