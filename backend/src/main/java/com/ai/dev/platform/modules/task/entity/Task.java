package com.ai.dev.platform.modules.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@TableName("task")
@Schema(description = "任务实体")
public class Task {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "任务唯一标识")
    private Long id;

    @TableField("project_id")
    @Schema(description = "关联项目ID")
    private Long projectId;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务内容")
    private String content;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "优先级")
    private String priority;

    @Schema(description = "进度百分比")
    private Integer progress;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
