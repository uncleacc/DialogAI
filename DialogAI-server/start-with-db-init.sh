#!/bin/bash

# DialogAI ��˷��������ű������ݿ��Զ���ʼ���汾��
# 
# ���ܣ�
# 1. ���Java����
# 2. ���MySQL����״̬
# 3. ����Ӧ�ò��Զ���ʼ�����ݿ�
# 4. �ṩ�������
#
# ʹ�÷�����
# ./start-with-db-init.sh [����] [�˿�]
# 
# ʾ����
# ./start-with-db-init.sh dev 8080
# ./start-with-db-init.sh prod 8080

set -e

# Ĭ������
DEFAULT_PROFILE="dev"
DEFAULT_PORT="8080"
DEFAULT_DB_HOST="localhost"
DEFAULT_DB_PORT="3306"
DEFAULT_DB_NAME="dialog_ai"
DEFAULT_DB_USER="root"

# ��ȡ����
PROFILE=${1:-$DEFAULT_PROFILE}
PORT=${2:-$DEFAULT_PORT}

# ��ɫ����
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ��־����
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

# ���Java����
check_java() {
    log_info "���Java����..."
    
    if ! command -v java &> /dev/null; then
        log_error "Javaδ��װ��δ���õ�PATH"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d '"' -f 2)
    log_info "Java�汾: $JAVA_VERSION"
    
    # ���Java�汾�Ƿ�֧�֣���ҪJava 8+��
    JAVA_MAJOR_VERSION=$(echo $JAVA_VERSION | cut -d '.' -f 1)
    if [ "$JAVA_MAJOR_VERSION" -lt "8" ] && [ "$JAVA_MAJOR_VERSION" != "1" ]; then
        log_error "��ҪJava 8����߰汾����ǰ�汾: $JAVA_VERSION"
        exit 1
    fi
    
    log_info "Java�������ͨ��"
}

# ���MySQL����
check_mysql() {
    log_info "���MySQL����״̬..."
    
    # �ӻ���������ʹ��Ĭ��ֵ��ȡ���ݿ�����
    DB_HOST=${DB_HOST:-$DEFAULT_DB_HOST}
    DB_PORT=${DB_PORT:-$DEFAULT_DB_PORT}
    DB_NAME=${DB_NAME:-$DEFAULT_DB_NAME}
    DB_USER=${DB_USERNAME:-$DEFAULT_DB_USER}
    
    log_info "���ݿ�����: $DB_USER@$DB_HOST:$DB_PORT/$DB_NAME"
    
    # ���MySQL�����Ƿ�����
    if ! command -v mysql &> /dev/null; then
        log_warn "MySQL�ͻ���δ��װ���������Ӳ���"
        return 0
    fi
    
    # ��������MySQL
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"${DB_PASSWORD:-}" -e "SELECT 1;" &> /dev/null; then
        log_info "MySQL���Ӳ��Գɹ�"
    else
        log_warn "MySQL���Ӳ���ʧ�ܣ�Ӧ������ʱ�������Զ�����"
    fi
    
    # ���Ŀ�����ݿ��Ƿ����
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"${DB_PASSWORD:-}" -e "USE $DB_NAME;" &> /dev/null; then
        log_info "���ݿ� '$DB_NAME' �Ѵ���"
    else
        log_info "���ݿ� '$DB_NAME' �����ڣ�Ӧ������ʱ���Զ�����"
    fi
}

# ����Ӧ��
build_app() {
    log_info "����Ӧ��..."
    
    if [ ! -f "pom.xml" ]; then
        log_error "δ�ҵ�pom.xml�ļ�����ȷ������Ŀ��Ŀ¼ִ�нű�"
        exit 1
    fi
    
    if command -v mvn &> /dev/null; then
        log_info "ʹ��Maven����Ӧ��..."
        mvn clean package -DskipTests -q
        
        if [ $? -eq 0 ]; then
            log_info "Ӧ�ù����ɹ�"
        else
            log_error "Ӧ�ù���ʧ��"
            exit 1
        fi
    else
        log_warn "Mavenδ��װ��������������"
    fi
}

# ����Ӧ��
start_app() {
    log_info "����DialogAI��˷���..."
    log_info "����: ����=$PROFILE, �˿�=$PORT"
    
    # ����JAR�ļ�
    JAR_FILE=$(find target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" | head -n 1)
    
    if [ -z "$JAR_FILE" ]; then
        log_error "δ�ҵ�JAR�ļ�������ִ�й���"
        exit 1
    fi
    
    log_info "ʹ��JAR�ļ�: $JAR_FILE"
    
    # ����JVM����
    JVM_OPTS="-Xms512m -Xmx1024m"
    JVM_OPTS="$JVM_OPTS -Dspring.profiles.active=$PROFILE"
    JVM_OPTS="$JVM_OPTS -Dserver.port=$PORT"
    
    # ������ݿ���ػ�����������������˵Ļ���
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
    
    log_debug "JVM����: $JVM_OPTS"
    
    # ����Ӧ��
    log_info "��������Ӧ�ã����Ժ�..."
    java $JVM_OPTS -jar "$JAR_FILE" &
    APP_PID=$!
    
    log_info "Ӧ����������PID: $APP_PID"
    
    # �ȴ�Ӧ������
    wait_for_startup
    
    # ִ�н������
    health_check
    
    log_info "Ӧ��������ɣ�"
    log_info "���ʵ�ַ: http://localhost:$PORT/api"
    log_info "�������: http://localhost:$PORT/api/health"
    log_info "��Ctrl+CֹͣӦ��"
    
    # �ȴ����̽���
    wait $APP_PID
}

# �ȴ�Ӧ������
wait_for_startup() {
    log_info "�ȴ�Ӧ���������..."
    
    MAX_WAIT=120  # ���ȴ�ʱ�䣨�룩
    WAIT_COUNT=0
    
    while [ $WAIT_COUNT -lt $MAX_WAIT ]; do
        if curl -s http://localhost:$PORT/api/health > /dev/null 2>&1; then
            log_info "Ӧ�������ɹ���"
            return 0
        fi
        
        sleep 2
        WAIT_COUNT=$((WAIT_COUNT + 2))
        
        if [ $((WAIT_COUNT % 10)) -eq 0 ]; then
            log_info "�ȴ���... (${WAIT_COUNT}s/${MAX_WAIT}s)"
        fi
    done
    
    log_error "Ӧ��������ʱ��"
    return 1
}

# �������
health_check() {
    log_info "ִ�н������..."
    
    # ϵͳ�������
    HEALTH_RESPONSE=$(curl -s http://localhost:$PORT/api/health 2>/dev/null || echo "")
    
    if [ -n "$HEALTH_RESPONSE" ]; then
        STATUS=$(echo "$HEALTH_RESPONSE" | grep -o '"status":"[^"]*"' | cut -d '"' -f 4 2>/dev/null || echo "UNKNOWN")
        
        if [ "$STATUS" = "UP" ]; then
            log_info "ϵͳ�������: ? ����"
        else
            log_warn "ϵͳ�������: ?? �쳣 (״̬: $STATUS)"
        fi
        
        # ��ʾ���ݿ�״̬
        DB_HEALTHY=$(echo "$HEALTH_RESPONSE" | grep -o '"database":{"healthy":[^,}]*' | cut -d ':' -f 3 2>/dev/null || echo "unknown")
        if [ "$DB_HEALTHY" = "true" ]; then
            log_info "���ݿ�����: ? ����"
        else
            log_warn "���ݿ�����: ?? �쳣"
        fi
        
    else
        log_error "�������ʧ�ܣ��޷���ȡ��Ӧ"
    fi
}

# ��ʾʹ�ð���
show_help() {
    echo "DialogAI ��˷��������ű�"
    echo ""
    echo "ʹ�÷���:"
    echo "  $0 [����] [�˿�]"
    echo ""
    echo "����:"
    echo "  ����    Spring Profile (dev/test/prod)��Ĭ��: dev"
    echo "  �˿�    ����˿ںţ�Ĭ��: 8080"
    echo ""
    echo "��������:"
    echo "  DB_HOST         ���ݿ�������Ĭ��: localhost"
    echo "  DB_PORT         ���ݿ�˿ڣ�Ĭ��: 3306"
    echo "  DB_NAME         ���ݿ����ƣ�Ĭ��: dialog_ai"
    echo "  DB_USERNAME     ���ݿ��û�����Ĭ��: root"
    echo "  DB_PASSWORD     ���ݿ�����"
    echo "  DEEPSEEK_API_KEY DeepSeek API��Կ"
    echo ""
    echo "ʾ��:"
    echo "  $0                    # ʹ��Ĭ����������"
    echo "  $0 dev 8080          # �����������˿�8080"
    echo "  $0 prod 9090         # �����������˿�9090"
    echo ""
    echo "����:"
    echo "  ? �Զ����Java��MySQL����"
    echo "  ? �Զ��������ݿ�ͱ�ṹ"
    echo "  ? �Զ���ʼ����������"
    echo "  ? �������ͼ��"
    echo "  ? �໷������֧��"
}

# ������
cleanup() {
    log_info "����ֹͣӦ��..."
    if [ -n "$APP_PID" ]; then
        kill $APP_PID 2>/dev/null || true
        wait $APP_PID 2>/dev/null || true
    fi
    log_info "Ӧ����ֹͣ"
}

# ע��������
trap cleanup EXIT INT TERM

# ������
main() {
    echo "============================================"
    echo "DialogAI ��˷��������ű�"
    echo "���ݿ��Զ���ʼ���汾"
    echo "============================================"
    echo ""
    
    # ������
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        show_help
        exit 0
    fi
    
    # ִ����������
    check_java
    check_mysql
    build_app
    start_app
}

# ִ��������
main "$@" 