#!/bin/bash
# =============================================================================
# Script de Setup Inicial para VPS
# =============================================================================
# Autor: PPG Hub Team
# Descrição: Configuração inicial da VPS para rodar PPG Hub
# Uso: sudo ./scripts/setup-vps.sh
# =============================================================================

set -e

# Cores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar se é root
if [ "$EUID" -ne 0 ]; then
    log_error "Execute como root: sudo ./scripts/setup-vps.sh"
    exit 1
fi

echo "========================================"
echo "  PPG Hub - Setup VPS"
echo "========================================"
echo ""

# Atualizar sistema
log_info "Atualizando sistema..."
apt-get update
apt-get upgrade -y
log_success "Sistema atualizado"

# Instalar dependências básicas
log_info "Instalando dependências..."
apt-get install -y \
    curl \
    wget \
    git \
    vim \
    htop \
    ufw \
    fail2ban \
    certbot \
    python3-certbot-nginx

log_success "Dependências instaladas"

# Instalar Docker
if ! command -v docker &> /dev/null; then
    log_info "Instalando Docker..."
    curl -fsSL https://get.docker.com -o get-docker.sh
    sh get-docker.sh
    rm get-docker.sh
    systemctl enable docker
    systemctl start docker
    log_success "Docker instalado"
else
    log_success "Docker já está instalado"
fi

# Instalar Docker Compose
if ! command -v docker-compose &> /dev/null; then
    log_info "Instalando Docker Compose..."
    DOCKER_COMPOSE_VERSION="2.24.0"
    curl -L "https://github.com/docker/compose/releases/download/v${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" \
        -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    log_success "Docker Compose instalado"
else
    log_success "Docker Compose já está instalado"
fi

# Configurar Firewall (UFW)
log_info "Configurando firewall..."
ufw --force reset
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable
log_success "Firewall configurado"

# Configurar Fail2Ban
log_info "Configurando Fail2Ban..."
cat > /etc/fail2ban/jail.local <<EOF
[DEFAULT]
bantime = 3600
findtime = 600
maxretry = 5

[sshd]
enabled = true
port = ssh
logpath = /var/log/auth.log

[nginx-http-auth]
enabled = true
port = http,https
logpath = /var/log/nginx/error.log
EOF

systemctl enable fail2ban
systemctl restart fail2ban
log_success "Fail2Ban configurado"

# Criar diretórios necessários
log_info "Criando estrutura de diretórios..."
mkdir -p /app/ppg-hub
mkdir -p /var/log/ppg-hub
mkdir -p /backup/ppg-hub
mkdir -p /app/ppg-hub/config/nginx/vps/conf.d
mkdir -p /app/ppg-hub/config/postgres/vps
mkdir -p /app/ppg-hub/config/redis/vps
mkdir -p /app/ppg-hub/ssl

log_success "Diretórios criados"

# Criar configuração do Nginx
log_info "Criando configuração do Nginx..."
cat > /app/ppg-hub/config/nginx/vps/conf.d/ppghub.conf <<'EOF'
upstream ppghub_backend {
    server app:8080;
}

server {
    listen 80;
    server_name _;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        return 301 https://$host$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name _;

    ssl_certificate /etc/nginx/ssl/fullchain.pem;
    ssl_certificate_key /etc/nginx/ssl/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    client_max_body_size 50M;

    location / {
        proxy_pass http://ppghub_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    location /api/management/health {
        access_log off;
        proxy_pass http://ppghub_backend;
    }

    location /health {
        access_log off;
        return 200 "OK";
        add_header Content-Type text/plain;
    }
}
EOF

log_success "Configuração do Nginx criada"

# Criar configuração do PostgreSQL
cat > /app/ppg-hub/config/postgres/vps/postgresql.conf <<EOF
# Performance
max_connections = 100
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
work_mem = 4MB

# WAL
wal_buffers = 8MB
max_wal_size = 1GB
min_wal_size = 80MB

# Checkpoint
checkpoint_completion_target = 0.9

# Logging
logging_collector = on
log_directory = 'pg_log'
log_filename = 'postgresql-%Y-%m-%d.log'
log_statement = 'all'
log_duration = on
EOF

log_success "Configuração do PostgreSQL criada"

# Criar configuração do Redis
cat > /app/ppg-hub/config/redis/vps/redis.conf <<EOF
# Bind
bind 127.0.0.1

# Persistence
save 900 1
save 300 10
save 60 10000

# Logging
loglevel notice

# Limits
maxmemory 512mb
maxmemory-policy allkeys-lru

# Append Only File
appendonly yes
appendfsync everysec
EOF

log_success "Configuração do Redis criada"

# Configurar logrotate
log_info "Configurando logrotate..."
cat > /etc/logrotate.d/ppghub <<EOF
/var/log/ppg-hub/*.log {
    daily
    rotate 30
    compress
    delaycompress
    notifempty
    create 0640 root root
    sharedscripts
    postrotate
        docker-compose -f /app/ppg-hub/docker-compose.vps.yml exec -T app kill -USR1 1
    endscript
}
EOF

log_success "Logrotate configurado"

# Permissões
log_info "Ajustando permissões..."
chown -R 1001:1001 /var/log/ppg-hub
chmod +x /app/ppg-hub/scripts/*.sh 2>/dev/null || true

log_success "Permissões ajustadas"

# Resumo
echo ""
echo "========================================"
echo "  Setup Concluído!"
echo "========================================"
echo ""
log_success "VPS configurada com sucesso!"
echo ""
log_info "Próximos passos:"
echo "  1. Clone o repositório em /app/ppg-hub"
echo "  2. Copie .env.example para .env e configure"
echo "  3. Configure SSL: ./scripts/setup-ssl-vps.sh"
echo "  4. Execute deploy: ./scripts/deploy-vps.sh"
echo ""
log_info "Documentação completa: DEPLOY.md"
echo ""
