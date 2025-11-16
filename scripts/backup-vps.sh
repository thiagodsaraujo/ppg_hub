#!/bin/bash
# =============================================================================
# Script de Backup para VPS
# =============================================================================
# Autor: PPG Hub Team
# Descrição: Backup automatizado do banco de dados PostgreSQL
# Uso: ./scripts/backup-vps.sh
# =============================================================================

set -e

# Configurações
BACKUP_DIR="/backups"
DATE=$(date +%Y%m%d_%H%M%S)
RETENTION_DAYS=30

# Criar diretório de backup se não existir
mkdir -p $BACKUP_DIR

# Nome do arquivo de backup
BACKUP_FILE="$BACKUP_DIR/ppghub_backup_$DATE.sql.gz"

echo "[$(date)] Iniciando backup..."

# Fazer backup do banco de dados
pg_dump -h ${PGHOST:-localhost} \
        -p ${PGPORT:-5432} \
        -U ${PGUSER:-ppghub} \
        -d ${PGDATABASE:-ppg_hub} \
        -F p | gzip > $BACKUP_FILE

if [ $? -eq 0 ]; then
    echo "[$(date)] Backup criado com sucesso: $BACKUP_FILE"

    # Mostrar tamanho do backup
    SIZE=$(du -h $BACKUP_FILE | cut -f1)
    echo "[$(date)] Tamanho do backup: $SIZE"

    # Remover backups antigos
    find $BACKUP_DIR -name "ppghub_backup_*.sql.gz" -mtime +$RETENTION_DAYS -delete
    echo "[$(date)] Backups antigos removidos (> $RETENTION_DAYS dias)"
else
    echo "[$(date)] ERRO: Falha no backup"
    exit 1
fi

echo "[$(date)] Backup concluído"
