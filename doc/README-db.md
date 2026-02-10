数据库连接（Supabase）快速使用说明

1. 在 Supabase 控制台获取连接信息：
   - 打开项目 -> Settings -> Database -> Connection info
   - 复制 `Host`、`DB password`（用户名通常为 `postgres`）

2. 填写本地环境变量：
   - 复制 `backend/.env.example` 为 `backend/.env` 并填入实际值
   - 确保 `SPRING_DATASOURCE_URL` 包含 `?sslmode=require`，例如：
     `jdbc:postgresql://your-db-host:5432/postgres?sslmode=require`

3. 启动后端：
   - 从仓库根目录执行：
     `./run-backend-with-supabase.sh`
   - 脚本会在第一次运行时复制 `.env.example` 到 `backend/.env`，请编辑后再次运行

4. 验证
   - 后端启动后访问 `${SERVER_SERVLET_CONTEXT_PATH:-/api}/actuator/health` 或日志确认连接成功

注意：不要将实际密码提交到 git；将 `backend/.env` 加入 `.gitignore`（如尚未加入）。如果需要，我可以为你检查 `.gitignore` 并添加相应条目。
