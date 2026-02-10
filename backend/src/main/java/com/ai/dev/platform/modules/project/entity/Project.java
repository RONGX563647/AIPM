package com.ai.dev.platform.modules.project.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@TableName("project")
@Schema(description = "项目实体")
public class Project {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "项目唯一标识")
    private Long id;

    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "项目描述")
    private String description;

    @TableField("git_url")
    @Schema(description = "Git仓库地址")
    private String gitUrl;

    @TableField("tech_stack")
    @Schema(description = "技术栈（逗号分隔）")
    private String techStack;

    @Schema(description = "项目状态")
    private String status;

    @TableField("online_url")
    @Schema(description = "线上访问地址")
    private String onlineUrl;

    @TableField("doc_url")
    @Schema(description = "文档地址")
    private String docUrl;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGitUrl() { return gitUrl; }
    public void setGitUrl(String gitUrl) { this.gitUrl = gitUrl; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOnlineUrl() { return onlineUrl; }
    public void setOnlineUrl(String onlineUrl) { this.onlineUrl = onlineUrl; }
    public String getDocUrl() { return docUrl; }
    public void setDocUrl(String docUrl) { this.docUrl = docUrl; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}