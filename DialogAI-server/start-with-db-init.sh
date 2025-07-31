#!/bin/bash

# DialogAI 后端服务启动脚本（数据库自动初始化版本）
# 
# 功能：
# 1. 检查Java环境
# 2. 检查MySQL服务状态
# 3. 启动应用并自动初始化数据库
# 4. 提供健康检查
#
# 使用方法：
# ./start-with-db-init.sh [环境] [端口]
# 
# 示例：
# ./start-with-db-init.sh dev 8080
# ./start-with-db-init.sh prod 8080

set -e

# 默认配置
DEFAULT_PROFILE="dev"
DEFAULT_PORT="8080"
DEFAULT_DB_HOST="localhost"
DEFAULT_DB_PORT="3306"
DEFAULT_DB_NAME="dialog_ai"
DEFAULT_DB_USER="root"

# 获取参数
PROFILE=${1:-$DEFAULT_PROFILE}
PORT=${2:-$DEFAULT_PORT}

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $(date '+%Y-%m-%d %H:%M:%S') $1"
}

# 检查Java环境
check_java() {
    log_info "检查Java环境..."
    
    if ! command -v java &> /dev/null; then
        log_error "Java未安装或未配置到PATH"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
    log_info "Java版本: $JAVA_VERSION"
    
    # 检查Java版本是否支持（需要Java 8+）
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d '.' -f 1)
    if [ "$JAVA_MAJOR_VERSION" -lt "8" ] && [ "$JAVA_MAJOR_VERSION" != "1" ]; then
        log_error "需要Java 8或更高版本，当前版本: $JAVA_VERSION"
        exit 1
    fi
    
    log_info "Java环境检查通过"
}

# 检查MySQL服务
check_mysql() {
    log_info "检查MySQL服务状态..."
    
    # 从环境变量或使用默认值获取数据库配置
    DB_HOST=${DB_HOST:-$DEFAULT_DB_HOST}
    DB_PORT=${DB_PORT:-$DEFAULT_DB_PORT}
    DB_NAME=${DB_NAME:-$DEFAULT_DB_NAME}
    DB_USER=${DB_USERNAME:-$DEFAULT_DB_USER}
    
    log_info "数据库配置: $DB_USER@$DB_HOST:$DB_PORT/$DB_NAME"
    
    # 检查MySQL服务是否运行
    if ! command -v mysql &> /dev/null; then
        log_warn "MySQL客户端未安装，跳过连接测试"
        return 0
    fi
    
    # 尝试连接MySQL
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"${DB_PASSWORD:-}" -e "SELECT 1;" &> /dev/null; then
        log_info "MySQL连接测试成功"
    else
        log_warn "MySQL连接测试失败，应用启动时将尝试自动处理"
    fi
    
    # 检查目标数据库是否存在
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"${DB_PASSWORD:-}" -e "USE $DB_NAME;" &> /dev/null; then
        log_info "数据库 '$DB_NAME' 已存在"
    else
        log_info "数据库 '$DB_NAME' 不存在，应用启动时将自动创建"
    fi
}

# 构建应用
build_app() {
    log_info "构建应用..."
    
    if [ ! -f "pom.xml" ]; then
        log_error "未找到pom.xml文件，请确保在项目根目录执行脚本"
        exit 1
    fi
    
    if command -v mvn &> /dev/null; then
        log_info "使用Maven构建应用..."
        mvn clean package -DskipTests -q
        
        if [ $? -eq 0 ]; then
            log_info "应用构建成功"
        else
            log_error "应用构建失败"
            exit 1
        fi
    else
        log_warn "Maven未安装，跳过构建步骤"
    fi
}

# 启动应用
start_app() {
    log_info "启动DialogAI后端服务..."
    log_info "配置: 环境=$PROFILE, 端口=$PORT"
    
    # 查找JAR文件
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -n 1)
    
    if [ -z "$JAR_FILE" ]; then
        log_error "未找到JAR文件，请先执行构建"
        exit 1
    fi
    
    log_info "使用JAR文件: $JAR_FILE"
    
    # 设置JVM参数
    JVM_OPTS="-Xms512m -Xmx1024m"
    JVM_OPTS="$JVM_OPTS -Dspring.profiles.active=$PROFILE"
    JVM_OPTS="$JVM_OPTS -Dserver.port=$PORT"
    
    # 添加数据库相关环境变量（如果设置了的话）
    if [ -n "$DB_HOST" ]; then
        JVM_OPTS="$JVM_OPTS -DDB_HOST=$DB_HOST"
    fi
    if [ -n "$DB_PORT" ]; then
        JVM_OPTS="$JVM_OPTS -DDB_PORT=$DB_PORT"
    fi
    if [ -n "$DB_NAME" ]; then
        JVM_OPTS="$JVM_OPTS -DDB_NAME=$DB_NAME"
    fi
    if [ -n "$DB_USERNAME" ]; then
        JVM_OPTS="$JVM_OPTS -DDB_USERNAME=$DB_USERNAME"
    fi
    if [ -n "$DB_PASSWORD" ]; then
        JVM_OPTS="$JVM_OPTS -DDB_PASSWORD=$DB_PASSWORD"
    fi
    if [ -n "$DEEPSEEK_API_KEY" ]; then
        JVM_OPTS="$JVM_OPTS -DDEEPSEEK_API_KEY=$DEEPSEEK_API_KEY"
    fi
    
    log_debug "JVM参数: $JVM_OPTS"
    
    # 启动应用
    log_info "正在启动应用，请稍候..."
    java $JVM_OPTS -jar "$JAR_FILE" &
    APP_PID=$!
    
    log_info "应用已启动，PID: $APP_PID"
    
    # 等待应用启动
    wait_for_startup
    
    # 执行健康检查
    health_check
    
    log_info "应用启动完成！"
    log_info "访问地址: http://localhost:$PORT/api"
    log_info "健康检查: http://localhost:$PORT/api/health"
    log_info "按Ctrl+C停止应用"
    
    # 等待进程结束
    wait $APP_PID
}

# 等待应用启动
wait_for_startup() {
    log_info "等待应用启动完成..."
    
    MAX_WAIT=120  # 最大等待时间（秒）
    WAIT_COUNT=0
    
    while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
        if curl -s http://localhost:$PORT/api/health > /dev/null 2>&1; then
            log_info "应用启动成功！"
            return 0
        fi
        
        sleep 2
        WAIT_COUNT=$((WAIT_COUNT + 2))
        
        if [ $((WAIT_COUNT % 10)) -eq 0 ]; then
            log_info "等待中... (${WAIT_COUNT}s/${MAX_WAIT}s)"
        fi
    done
    
    log_error "应用启动超时！"
    return 1
}

# 健康检查
health_check() {
    log_info "执行健康检查..."
    
    # 系统健康检查
    HEALTH_RESPONSE=$(curl -s http://localhost:$PORT/api/health 2>/dev/null || echo "")
    
    if [ -n "$HEALTH_RESPONSE" ]; then
        STATUS=$(echo "$HEALTH_RESPONSE" | grep -o '"status":"[^"]*"' | cut -d '"' -f 4 2>/dev/null || echo "UNKNOWN")
        
        if [ "$STATUS" = "UP" ]; then
            log_info "系统健康检查: ? 正常"
        else
            log_warn "系统健康检查: ?? 异常 (状态: $STATUS)"
        fi
        
        # 显示数据库状态
        DB_HEALTHY=$(echo "$HEALTH_RESPONSE" | grep -o '"database":{"healthy":[^,}]*' | cut -d ':' -f 3 2>/dev/null || echo "unknown")
        if [ "$DB_HEALTHY" = "true" ]; then
            log_info "数据库连接: ? 正常"
        else
            log_warn "数据库连接: ?? 异常"
        fi
        
    else
        log_error "健康检查失败，无法获取响应"
    fi
}

# 显示使用帮助
show_help() {
    echo "DialogAI 后端服务启动脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 [环境] [端口]"
    echo ""
    echo "参数:"
    echo "  环境    Spring Profile (dev/test/prod)，默认: dev"
    echo "  端口    服务端口号，默认: 8080"
    echo ""
    echo "环境变量:"
    echo "  DB_HOST         数据库主机，默认: localhost"
    echo "  DB_PORT         数据库端口，默认: 3306"
    echo "  DB_NAME         数据库名称，默认: dialog_ai"
    echo "  DB_USERNAME     数据库用户名，默认: root"
    echo "  DB_PASSWORD     数据库密码"
    echo "  DEEPSEEK_API_KEY DeepSeek API密钥"
    echo ""
    echo "示例:"
    echo "  $0                    # 使用默认配置启动"
    echo "  $0 dev 8080          # 开发环境，端口8080"
    echo "  $0 prod 9090         # 生产环境，端口9090"
    echo ""
    echo "特性:"
    echo "  ? 自动检查Java和MySQL环境"
    echo "  ? 自动创建数据库和表结构"
    echo "  ? 自动初始化基础数据"
    echo "  ? 健康检查和监控"
    echo "  ? 多环境配置支持"
}

# 清理函数
cleanup() {
    log_info "正在停止应用..."
    if [ -n "$APP_PID" ]; then
        kill $APP_PID 2>/dev/null || true
        wait $APP_PID 2>/dev/null || true
    fi
    log_info "应用已停止"
}

# 注册清理函数
trap cleanup EXIT INT TERM

# 主函数
main() {
    echo "============================================"
    echo "DialogAI 后端服务启动脚本"
    echo "数据库自动初始化版本"
    echo "============================================"
    echo ""
    
    # 检查参数
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_help
        exit 0
    fi
    
    # 执行启动流程
    check_java
    check_mysql
    build_app
    start_app
}

# 执行主函数
main "$@" 