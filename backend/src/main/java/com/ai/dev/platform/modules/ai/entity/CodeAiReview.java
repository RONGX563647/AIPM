package com.ai.dev.platform.modules.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("code_ai_review")
@Schema(description = "AI代码评审实体")
public class CodeAiReview {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "评审记录唯一标识")
    private Long id;

    @TableField("project_id")
    @Schema(description = "关联项目ID")
    private Long projectId;

    @Schema(description = "待评审代码内容")
    private String codeContent;

    @Schema(description = "评审分数")
    private Integer score;

    @Schema(description = "优化建议")
    private String suggestion;

    @Schema(description = "优化后的代码")
    private String improvedCode;

    @TableField("create_time")
    @Schema(description = "评审时间")
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

    public String getCodeContent() {
        return codeContent;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getImprovedCode() {
        return improvedCode;
    }

    public void setImprovedCode(String improvedCode) {
        this.improvedCode = improvedCode;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
