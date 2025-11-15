#!/bin/bash

echo "========================================"
echo "  EnterpriseX Framework 快速启动脚本"
echo "========================================"

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker未运行，请先启动Docker"
    exit 1
fi

echo "📦 1. 启动基础服务（MySQL、Redis、Nacos、MinIO）..."
docker-compose up -d mysql redis nacos minio

echo "⏳ 等待服务启动（30秒）..."
sleep 30

echo "✅ 基础服务启动完成"
echo ""
echo "📊 服务地址："
echo "   - Nacos: http://localhost:8848/nacos (nacos/nacos)"
echo "   - MinIO: http://localhost:9001 (admin/admin123)"
echo "   - MySQL: localhost:3306 (root/root)"
echo "   - Redis: localhost:6379"
echo ""
echo "📝 下一步："
echo "   1. 编译项目: mvn clean install -DskipTests"
echo "   2. 启动网关: cd enterprisex-gateway && mvn spring-boot:run"
echo "   3. 启动认证服务: cd enterprisex-auth && mvn spring-boot:run"
echo "   4. 启动系统服务: cd enterprisex-modules/enterprisex-system && mvn spring-boot:run"
echo "   5. 启动前端: cd enterprisex-ui && npm install && npm run dev"
echo ""
echo "🌐 访问地址："
echo "   - 前端: http://localhost:3000"
echo "   - 网关: http://localhost:8080"
echo "   - 系统服务API文档: http://localhost:9201/doc.html"
echo ""
echo "👤 默认账号："
echo "   - 用户名: admin"
echo "   - 密码: admin123"
echo ""
echo "========================================"
