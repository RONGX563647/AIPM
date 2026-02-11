-- 模块1：用户登录（sys_user）
CREATE TABLE sys_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (username)
);

-- 模块2：项目管理（project）
CREATE TABLE project (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) DEFAULT '',
    git_url VARCHAR(255) DEFAULT '',
    tech_stack VARCHAR(100) DEFAULT '',
    status VARCHAR(20) DEFAULT 'developing',
    online_url VARCHAR(255) DEFAULT '',
    doc_url VARCHAR(255) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name)
);

CREATE INDEX idx_project_git_url ON project(git_url);
CREATE INDEX idx_project_status ON project(status);

-- 模块3：任务&进度管理（task）
CREATE TABLE task (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content VARCHAR(1000) DEFAULT '',
    status VARCHAR(20) DEFAULT 'todo',
    priority VARCHAR(20) DEFAULT 'medium',
    progress INT DEFAULT 0 CHECK (progress >= 0 AND progress <= 100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE INDEX idx_task_project_id ON task(project_id);
CREATE INDEX idx_task_status ON task(status);

-- 模块4：智能接口中心（api_info）
CREATE TABLE api_info (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    path VARCHAR(255) NOT NULL,
    method VARCHAR(10) NOT NULL,
    params TEXT DEFAULT '',
    header TEXT DEFAULT '',
    response TEXT DEFAULT '',
    remark VARCHAR(500) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_api_info_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE INDEX idx_api_info_project_id ON api_info(project_id);
CREATE INDEX idx_api_info_path ON api_info(path);
CREATE INDEX idx_api_info_method ON api_info(method);

-- 模块5：AI代码评审（code_ai_review）
CREATE TABLE code_ai_review (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    code_content TEXT NOT NULL,
    score INT DEFAULT 0 CHECK (score >= 0 AND score <= 100),
    suggestion TEXT DEFAULT '',
    improved_code TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_code_ai_review_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE INDEX idx_code_ai_review_project_id ON code_ai_review(project_id);

-- 模块6：自动化接口测试（api_test_case/api_test_report）
-- 6.1 api_test_case（测试用例表）
CREATE TABLE api_test_case (
    id BIGSERIAL PRIMARY KEY,
    api_id BIGINT NOT NULL,
    case_name VARCHAR(100) NOT NULL,
    params TEXT DEFAULT '',
    expected_result TEXT DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_api_test_case_api FOREIGN KEY (api_id) REFERENCES api_info(id) ON DELETE CASCADE,
    UNIQUE (case_name)
);

CREATE INDEX idx_api_test_case_api_id ON api_test_case(api_id);

-- 6.2 api_test_report（测试报告表）
CREATE TABLE api_test_report (
    id BIGSERIAL PRIMARY KEY,
    api_id BIGINT NOT NULL,
    case_id BIGINT NOT NULL,
    actual_result TEXT DEFAULT '',
    status VARCHAR(20) DEFAULT 'fail',
    test_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remark VARCHAR(500) DEFAULT '',
    CONSTRAINT fk_api_test_report_api FOREIGN KEY (api_id) REFERENCES api_info(id) ON DELETE CASCADE,
    CONSTRAINT fk_api_test_report_case FOREIGN KEY (case_id) REFERENCES api_test_case(id) ON DELETE CASCADE
);

CREATE INDEX idx_api_test_report_api_id ON api_test_report(api_id);
CREATE INDEX idx_api_test_report_case_id ON api_test_report(case_id);
CREATE INDEX idx_api_test_report_status ON api_test_report(status);

-- 模块7：自动监控&告警（monitor）
CREATE TABLE monitor (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    monitor_url VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'normal',
    response_time INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_monitor_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE,
    UNIQUE (monitor_url)
);

CREATE INDEX idx_monitor_project_id ON monitor(project_id);
CREATE INDEX idx_monitor_status ON monitor(status);

-- 创建更新触发器，实现update_time自动更新
CREATE OR REPLACE FUNCTION update_update_time_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_monitor_update_time 
BEFORE UPDATE ON monitor 
FOR EACH ROW EXECUTE FUNCTION update_update_time_column();

-- 模块8：部署记录（deploy_record）
CREATE TABLE deploy_record (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    version VARCHAR(50) NOT NULL,
    content VARCHAR(1000) DEFAULT '',
    env VARCHAR(20) DEFAULT 'dev',
    status VARCHAR(20) DEFAULT 'success',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_deploy_record_project FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE
);

CREATE INDEX idx_deploy_record_project_id ON deploy_record(project_id);
CREATE INDEX idx_deploy_record_env ON deploy_record(env);
CREATE INDEX idx_deploy_record_status ON deploy_record(status);
CREATE INDEX idx_deploy_record_version ON deploy_record(version);

-- 模块9：数据中心（data_metric）
CREATE TABLE data_metric (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    value DECIMAL(20, 2) NOT NULL,
    unit VARCHAR(50) DEFAULT '',
    category VARCHAR(50) DEFAULT '',
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_data_metric_category ON data_metric(category);
CREATE INDEX idx_data_metric_name ON data_metric(name);
CREATE INDEX idx_data_metric_timestamp ON data_metric(timestamp);

-- 插入初始测试数据
-- 1. 插入一个测试用户
INSERT INTO sys_user (username, password, nickname) 
VALUES ('admin', '$2a$10$YourBcryptEncryptedPasswordHere', '管理员');

-- 2. 插入一个测试项目
INSERT INTO project (name, description, tech_stack, status, online_url) 
VALUES ('AI研发大脑平台', '基于Vibecoding的AI全自动研发平台', 'SpringBoot3,Vue3,PostgreSQL', 'developing', 'http://localhost:3000');

-- 3. 为测试项目插入一个任务
INSERT INTO task (project_id, title, content, status, priority, progress)
SELECT id, '搭建项目基础框架', '完成SpringBoot3 + Vue3的基础框架搭建', 'done', 'high', 100
FROM project WHERE name = 'AI研发大脑平台';

-- 4. 为测试项目插入一个接口
INSERT INTO api_info (project_id, path, method, params, header, response, remark)
SELECT id, '/sys/user/login', 'POST', 
    '{"username":"string","password":"string"}',
    '{"Content-Type":"application/json"}',
    '{"code":200,"msg":"success","data":"jwt_token"}',
    '用户登录接口'
FROM project WHERE name = 'AI研发大脑平台';

-- 5. 为测试项目插入一个监控配置
INSERT INTO monitor (project_id, monitor_url, status, response_time)
SELECT id, 'http://localhost:8080/actuator/health', 'normal', 150
FROM project WHERE name = 'AI研发大脑平台';

-- 6. 为测试项目插入一个部署记录
INSERT INTO deploy_record (project_id, version, content, env, status)
SELECT id, 'v1.0.0', '初始版本发布', 'dev', 'success'
FROM project WHERE name = 'AI研发大脑平台';

-- 7. 为数据中心插入测试数据
INSERT INTO data_metric (name, value, unit, category, timestamp) VALUES
('系统CPU使用率', 45.5, '%', 'system', CURRENT_TIMESTAMP - INTERVAL '5 minutes'),
('系统内存使用率', 62.3, '%', 'system', CURRENT_TIMESTAMP - INTERVAL '10 minutes'),
('系统磁盘使用率', 78.2, '%', 'system', CURRENT_TIMESTAMP - INTERVAL '15 minutes'),
('API响应时间', 120, 'ms', 'performance', CURRENT_TIMESTAMP - INTERVAL '3 minutes'),
('数据库连接数', 25, '个', 'system', CURRENT_TIMESTAMP - INTERVAL '8 minutes'),
('今日活跃用户', 1250, '人', 'business', CURRENT_TIMESTAMP - INTERVAL '1 minute'),
('今日订单数', 342, '单', 'business', CURRENT_TIMESTAMP - INTERVAL '2 minutes'),
('系统负载', 1.2, '', 'system', CURRENT_TIMESTAMP - INTERVAL '4 minutes'),
('网络带宽使用', 85.6, 'Mbps', 'performance', CURRENT_TIMESTAMP - INTERVAL '6 minutes'),
('错误率', 0.05, '%', 'performance', CURRENT_TIMESTAMP - INTERVAL '7 minutes');

-- 查看表结构验证
SELECT 
    table_name,
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'public'
ORDER BY table_name, ordinal_position;

-- 查看索引验证
SELECT 
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;