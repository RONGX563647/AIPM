#!/bin/bash

# 后端启动脚本 - 自动加载环境变量
# 使用方法: ./start-with-env.sh

set -e

echo "🚀 正在启动后端服务..."

# 检查 .env 文件是否存在
if [ ! -f ".env" ]; then
    echo "❌ 错误: 找不到 .env 文件"
    exit 1
fi

# 读取 .env 文件并设置环境变量（处理特殊字符）
while IFS= read -r line || [[ -n "$line" ]]; do
    # 跳过空行和注释行
    if [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]]; then
        continue
    fi
    
    # 处理带引号的值
    if [[ "$line" =~ ^([^=]+)=(.*)$ ]]; then
        key="${BASH_REMATCH[1]}"
        value="${BASH_REMATCH[2]}"
        
        # 移除首尾引号（如果存在）
        value="${value%\"}"
        value="${value#\"}"
        value="${value%\'}"
        value="${value#\'}"
        
        export "$key=$value"
        echo "✅ 已设置: $key"
    fi
done < .env

echo "🔧 环境变量加载完成，正在启动应用..."

# 启动 Spring Boot 应用
mvn spring-boot:run