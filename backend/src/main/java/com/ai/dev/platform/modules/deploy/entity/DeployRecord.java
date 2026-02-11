package com.ai.dev.platform.modules.deploy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部署记录实体类
 * 
 * <p>映射数据库中的deploy_record表，记录项目部署的详细信息：
 * <ul>
 *   <li>部署的基本信息（项目、版本、环境）</li>
 *   <li>部署的状态和结果</li>
 *   <li>部署的时间和操作人</li>
 *   <li>部署的详细内容描述</li>
 * </ul>
 * 
 * <p>该实体类使用Lombok的@Data注解自动生成getter/setter方法，
 * 并通过MyBatis-Plus注解配置数据库映射关系。
 * 
 * @author AI开发平台团队
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@TableName("deploy_record")
@Schema(description = "部署记录实体")
public class DeployRecord {

    /**
     * 部署记录唯一标识
     * 
     * <p>数据库主键，使用自增策略生成唯一ID。
     * 用于唯一标识每一条部署记录，支持精确查询和关联操作。
     * 
     * @since 1.0.0
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "部署记录唯一标识")
    private Long id;

    /**
     * 关联项目ID
     * 
     * <p>外键关联到project表的主键，标识该部署记录属于哪个项目。
     * 用于项目维度的部署记录查询和统计分析。
     * 
     * @since 1.0.0
     */
    @TableField("project_id")
    @Schema(description = "关联项目ID")
    private Long projectId;

    /**
     * 部署版本
     * 
     * <p>部署的应用程序版本号，通常遵循语义化版本规范（如1.0.0）。
     * 用于版本追踪、回滚操作和版本对比分析。
     * 
     * @since 1.0.0
     */
    @Schema(description = "部署版本")
    private String version;

    /**
     * 部署环境
     * 
     * <p>部署的目标环境标识，常见的值包括：
     * <ul>
     *   <li>dev：开发环境</li>
     *   <li>test：测试环境</li>
     *   <li>staging：预发布环境</li>
     *   <li>prod：生产环境</li>
     * </ul>
     * 用于环境隔离和环境特定的部署策略。
     * 
     * @since 1.0.0
     */
    @Schema(description = "部署环境")
    private String env;

    /**
     * 部署状态
     * 
     * <p>部署任务的当前状态，可能的值包括：
     * <ul>
     *   <li>pending：待部署</li>
     *   <li>running：部署中</li>
     *   <li>success：部署成功</li>
     *   <li>failed：部署失败</li>
     *   <li>cancelled：已取消</li>
     * </ul>
     * 用于部署流程监控和状态跟踪。
     * 
     * @since 1.0.0
     */
    @Schema(description = "部署状态")
    private String status;

    /**
     * 部署内容
     * 
     * <p>部署的详细内容描述，可能包含：
     * <ul>
     *   <li>部署的具体变更内容</li>
     *   <li>部署配置信息</li>
     *   <li>部署失败原因（失败状态时）</li>
     *   <li>部署验证结果</li>
     * </ul>
     * 用于部署审计和问题排查。
     * 
     * @since 1.0.0
     */
    @Schema(description = "部署内容")
    private String content;

    /**
     * 部署时间
     * 
     * <p>部署记录的创建时间，使用LocalDateTime类型存储。
     * 用于部署时间线分析、部署频率统计和时效性监控。
     * 
     * @since 1.0.0
     */
    @TableField("create_time")
    @Schema(description = "部署时间")
    private LocalDateTime createTime;

    // 以下为手动编写的getter/setter方法，用于保持代码的完整性和兼容性
    // 在实际项目中，这些方法通常由Lombok的@Data注解自动生成

    /**
     * 获取部署记录ID
     * 
     * @return Long 部署记录唯一标识
     * @since 1.0.0
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置部署记录ID
     * 
     * @param id 部署记录唯一标识
     * @since 1.0.0
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取关联项目ID
     * 
     * @return Long 关联项目唯一标识
     * @since 1.0.0
     */
    public Long getProjectId() {
        return projectId;
    }

    /**
     * 设置关联项目ID
     * 
     * @param projectId 关联项目唯一标识
     * @since 1.0.0
     */
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    /**
     * 获取部署版本号
     * 
     * @return String 部署版本
     * @since 1.0.0
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置部署版本号
     * 
     * @param version 部署版本
     * @since 1.0.0
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取部署环境
     * 
     * @return String 部署环境标识
     * @since 1.0.0
     */
    public String getEnv() {
        return env;
    }

    /**
     * 设置部署环境
     * 
     * @param env 部署环境标识
     * @since 1.0.0
     */
    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * 获取部署状态
     * 
     * @return String 部署状态
     * @since 1.0.0
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置部署状态
     * 
     * @param status 部署状态
     * @since 1.0.0
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取部署内容描述
     * 
     * @return String 部署内容详情
     * @since 1.0.0
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置部署内容描述
     * 
     * @param content 部署内容详情
     * @since 1.0.0
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取部署创建时间
     * 
     * @return LocalDateTime 部署时间
     * @since 1.0.0
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置部署创建时间
     * 
     * @param createTime 部署时间
     * @since 1.0.0
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
