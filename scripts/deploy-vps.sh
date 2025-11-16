#!/bin/bash
# =============================================================================
# Script de Deploy para VPS
# =============================================================================
# Autor: PPG Hub Team
# Descrição: Deploy automatizado da aplicação PPG Hub em VPS
# Uso: ./scripts/deploy-vps.sh
# =============================================================================

set -e  # Exit on error
set -u  # Exit on undefined variable

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funções auxiliares
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

# Banner
echo "========================================"
echo "  PPG Hub - Deploy VPS"
echo "========================================"
echo ""

# Verificar se o arquivo .env existe
if [ ! -f .env ]; then
    log_error "Arquivo .env não encontrado!"
    log_info "Copie .env.example para .env e configure as variáveis"
    exit 1
fi

# Carregar variáveis de ambiente
log_info "Carregando variáveis de ambiente..."
export $(cat .env | grep -v '^#' | xargs)

# Verificar variáveis obrigatórias
REQUIRED_VARS=("VPS_DB_PASSWORD" "VPS_REDIS_PASSWORD" "OPENALEX_EMAIL" "VPS_DOMAIN")
for var in "${REQUIRED_VARS[@]}"; do
    if [ -z "${!var:-}" ]; then
        log_error "Variável $var não está definida no .env"
        exit 1
    fi
done

log_success "Variáveis de ambiente carregadas"

# 1. Build da aplicação
log_info "Compilando aplicação..."
mvn clean package -DskipTests -Dspring.profiles.active=vps

if [ $? -eq 0 ]; then
    log_success "Build concluído com sucesso"
else
    log_error "Falha no build"
    exit 1
fi

# 2. Build da imagem Docker
log_info "Construindo imagem Docker..."
docker build -f Dockerfile.prod -t ppghub:vps-latest .

if [ $? -eq 0 ]; then
    log_success "Imagem Docker criada"
else
    log_error "Falha na criação da imagem"
    exit 1
fi

# 3. Parar containers antigos
log_info "Parando containers existentes..."
docker-compose -f docker-compose.vps.yml down

# 4. Backup do banco de dados (se existir)
if docker ps -a | grep -q ppghub_postgres_vps; then
    log_info "Criando backup do banco de dados..."
    ./scripts/backup-vps.sh
    log_success "Backup criado"
fi

# 5. Iniciar novos containers
log_info "Iniciando containers..."
docker-compose -f docker-compose.vps.yml up -d

if [ $? -eq 0 ]; then
    log_success "Containers iniciados"
else
    log_error "Falha ao iniciar containers"
    exit 1
fi

# 6. Aguardar aplicação ficar pronta
log_info "Aguardando aplicação inicializar..."
sleep 30

# 7. Health check
HEALTH_URL="http://localhost:8080/api/management/health"
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -f -s "$HEALTH_URL" > /dev/null 2>&1; then
        log_success "Aplicação está rodando e saudável!"
        break
    fi

    ATTEMPT=$((ATTEMPT + 1))
    log_info "Tentativa $ATTEMPT/$MAX_ATTEMPTS - Aguardando aplicação..."
    sleep 10
done

if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
    log_error "Aplicação não respondeu ao health check"
    log_info "Verificando logs..."
    docker-compose -f docker-compose.vps.yml logs app
    exit 1
fi

# 8. Verificar SSL
log_info "Verificando certificado SSL..."
if [ -d "ssl" ] && [ -f "ssl/fullchain.pem" ]; then
    log_success "Certificado SSL encontrado"
else
    log_warning "Certificado SSL não encontrado"
    log_info "Execute: ./scripts/setup-ssl-vps.sh"
fi

# 9. Limpar imagens antigas
log_info "Limpando imagens antigas..."
docker image prune -f

# Resumo do deploy
echo ""
echo "========================================"
echo "  Deploy Concluído!"
echo "========================================"
echo ""
log_info "Aplicação: http://${VPS_DOMAIN}"
log_info "Health Check: http://${VPS_DOMAIN}/api/management/health"
log_info "Logs: docker-compose -f docker-compose.vps.yml logs -f app"
echo ""
log_info "Para verificar o status:"
log_info "  docker-compose -f docker-compose.vps.yml ps"
echo ""
