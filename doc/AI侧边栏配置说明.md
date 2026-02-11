# AI侧边栏配置说明

## 功能说明

AI侧边栏已成功集成阿里云通义千问（Qwen）大模型API，提供真实的AI对话功能。

## 配置步骤

### 1. 获取DashScope API Key

1. 访问阿里云DashScope控制台：https://dashscope.console.aliyun.com/
2. 登录或注册阿里云账号
3. 进入API-KEY管理页面
4. 创建新的API Key并复制

### 2. 配置后端API Key

编辑 `/Users/rongx/Desktop/Code/git/AIPM/backend/.env` 文件：

```env
# DashScope AI API Key
DASHSCOPE_API_KEY=your-actual-api-key-here
```

将 `your-actual-api-key-here` 替换为你在步骤1中获取的真实API Key。

### 3. 重启后端服务

```bash
cd /Users/rongx/Desktop/Code/git/AIPM/backend
mvn spring-boot:run
```

### 4. 测试AI聊天

启动前端服务后，点击页面右下角的悬浮球打开AI侧边栏，即可与Qwen大模型进行对话。

## API接口说明

### 发送消息接口

**请求路径：** `POST /api/ai/chat/send`

**请求参数：**
```json
{
  "message": "你好"
}
```

**响应格式：**
```json
{
  "code": 0,
  "msg": "success",
  "data": "AI回复内容"
}
```

## 注意事项

1. API Key请妥善保管，不要泄露给他人
2. DashScope API有调用次数和费用限制，请关注阿里云控制台的用量统计
3. 如果API Key配置错误或无效，系统会返回错误提示
4. 当前使用的是qwen-plus模型，如需更换模型，请修改 `AIChatServiceImpl.java` 中的model参数

## 技术实现

- **后端：** 使用DashScope SDK Java 2.22.3调用Qwen API
- **前端：** 通过axios调用后端接口获取AI回复
- **模型：** 默认使用qwen-plus模型
- **配置：** 通过环境变量DASHSCOPE_API_KEY配置API Key
