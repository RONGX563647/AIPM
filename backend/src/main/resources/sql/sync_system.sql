-- 数据同步系统数据库表创建脚本

-- 1. 同步记录表
CREATE TABLE IF NOT EXISTS `sync_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '同步记录唯一标识',
  `source` varchar(20) NOT NULL COMMENT '数据源（github/gitee）',
  `repo_name` varchar(100) DEFAULT NULL COMMENT '仓库名称',
  `sync_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
  `sync_status` varchar(20) NOT NULL COMMENT '同步状态（success/failed/pending）',
  `error_message` text COMMENT '错误信息',
  `data_count` int(11) DEFAULT 0 COMMENT '同步数据数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_source` (`source`),
  KEY `idx_sync_time` (`sync_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步记录表';

-- 2. GitHub仓库表
CREATE TABLE IF NOT EXISTS `github_repo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库唯一标识',
  `repo_id` bigint(20) NOT NULL COMMENT 'GitHub仓库ID',
  `repo_name` varchar(100) NOT NULL COMMENT '仓库名称（owner/repo）',
  `repo_url` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `description` varchar(500) DEFAULT NULL COMMENT '仓库描述',
  `language` varchar(50) DEFAULT NULL COMMENT '主要语言',
  `stars_count` int(11) DEFAULT 0 COMMENT '星标数',
  `forks_count` int(11) DEFAULT 0 COMMENT '分支数',
  `open_issues_count` int(11) DEFAULT 0 COMMENT '开放问题数',
  `updated_at` datetime DEFAULT NULL COMMENT 'GitHub仓库最后更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_repo_id` (`repo_id`),
  KEY `idx_repo_name` (`repo_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GitHub仓库表';

-- 3. Gitee仓库表
CREATE TABLE IF NOT EXISTS `gitee_repo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '仓库唯一标识',
  `repo_id` bigint(20) NOT NULL COMMENT 'Gitee仓库ID',
  `repo_name` varchar(100) NOT NULL COMMENT '仓库名称（owner/repo）',
  `repo_url` varchar(255) DEFAULT NULL COMMENT '仓库地址',
  `description` varchar(500) DEFAULT NULL COMMENT '仓库描述',
  `language` varchar(50) DEFAULT NULL COMMENT '主要语言',
  `stars_count` int(11) DEFAULT 0 COMMENT '星标数',
  `forks_count` int(11) DEFAULT 0 COMMENT '分支数',
  `open_issues_count` int(11) DEFAULT 0 COMMENT '开放问题数',
  `updated_at` datetime DEFAULT NULL COMMENT 'Gitee仓库最后更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_repo_id` (`repo_id`),
  KEY `idx_repo_name` (`repo_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Gitee仓库表';

-- 4. 同步配置表
CREATE TABLE IF NOT EXISTS `sync_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置唯一标识',
  `source` varchar(20) NOT NULL COMMENT '数据源（github/gitee）',
  `access_token` varchar(255) DEFAULT NULL COMMENT '访问令牌',
  `sync_interval` int(11) NOT NULL DEFAULT 60 COMMENT '同步间隔（分钟）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_source` (`source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='同步配置表';

-- 插入默认同步配置
INSERT INTO `sync_config` (`source`, `sync_interval`, `is_enabled`) VALUES 
('github', 60, 1),
('gitee', 60, 1)
ON DUPLICATE KEY UPDATE `sync_interval` = VALUES(`sync_interval`);
