#!/bin/bash
# =============================================================================
# Script de Configuração SSL com Let's Encrypt
# =============================================================================
# Autor: PPG Hub Team
# Descrição: Configura certificado SSL usando Let's Encrypt/Certbot
# Uso: ./scripts/setup-ssl-vps.sh seu-dominio.com.br
# =============================================================================

set -e

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar argumento
if [ -z "$1" ]; then
    log_error "Uso: ./scripts/setup-ssl-vps.sh seu-dominio.com.br"
    exit 1
fi

DOMAIN=$1
EMAIL=${2:-admin@$DOMAIN}

echo "========================================"
echo "  Configuração SSL - Let's Encrypt"
echo "========================================"
echo ""
log_info "Domínio: $DOMAIN"
log_info "Email: $EMAIL"
echo ""

# Verificar se certbot está instalado
if ! command -v certbot &> /dev/null; then
    log_error "Certbot não está instalado"
    log_info "Execute primeiro: sudo ./scripts/setup-vps.sh"
    exit 1
fi

# Parar nginx se estiver rodando
log_info "Parando Nginx..."
docker-compose -f docker-compose.vps.yml stop nginx 2>/dev/null || true

# Obter certificado
log_info "Obtendo certificado SSL..."
certbot certonly --standalone \
    --preferred-challenges http \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN \
    -d www.$DOMAIN

if [ $? -eq 0 ]; then
    log_success "Certificado obtido com sucesso"
else
    log_error "Falha ao obter certificado"
    exit 1
fi

# Copiar certificados para o diretório do projeto
log_info "Copiando certificados..."
mkdir -p ssl
cp /etc/letsencrypt/live/$DOMAIN/fullchain.pem ssl/
cp /etc/letsencrypt/live/$DOMAIN/privkey.pem ssl/
chmod 644 ssl/*.pem

log_success "Certificados copiados"

# Criar cron para renovação automática
log_info "Configurando renovação automática..."
(crontab -l 2>/dev/null; echo "0 3 * * * certbot renew --quiet --post-hook 'docker-compose -f /app/ppg-hub/docker-compose.vps.yml restart nginx'") | crontab -

log_success "Renovação automática configurada"

# Atualizar configuração do Nginx com o domínio correto
log_info "Atualizando configuração do Nginx..."
sed -i "s/server_name _;/server_name $DOMAIN www.$DOMAIN;/g" config/nginx/vps/conf.d/ppghub.conf

# Reiniciar nginx
log_info "Reiniciando Nginx..."
docker-compose -f docker-compose.vps.yml up -d nginx

if [ $? -eq 0 ]; then
    log_success "Nginx reiniciado"
else
    log_error "Falha ao reiniciar Nginx"
    exit 1
fi

# Testar configuração SSL
log_info "Testando SSL..."
sleep 5

if curl -s -o /dev/null -w "%{http_code}" https://$DOMAIN | grep -q "200\|301\|302"; then
    log_success "SSL configurado corretamente!"
else
    log_error "Falha ao acessar HTTPS"
fi

# Resumo
echo ""
echo "========================================"
echo "  SSL Configurado!"
echo "========================================"
echo ""
log_success "Certificado SSL instalado para:"
echo "  - $DOMAIN"
echo "  - www.$DOMAIN"
echo ""
log_info "Acesse: https://$DOMAIN"
log_info "Renovação automática: Diariamente às 3h"
echo ""
