# Scripts de Deploy - PPG Hub

Este diretório contém scripts automatizados para deploy e manutenção do PPG Hub.

## 📜 Scripts Disponíveis

### 🖥️ VPS Scripts

#### `setup-vps.sh`
Configuração inicial da VPS (executar apenas uma vez).

```bash
sudo ./scripts/setup-vps.sh
```

**O que faz:**
- Instala Docker e Docker Compose
- Configura firewall (UFW)
- Instala Fail2Ban
- Cria estrutura de diretórios
- Configura Nginx, PostgreSQL e Redis

**Requisitos:** Ubuntu 20.04+ ou Debian 11+

---

#### `setup-ssl-vps.sh`
Configura SSL com Let's Encrypt.

```bash
./scripts/setup-ssl-vps.sh seu-dominio.com.br
```

**O que faz:**
- Obtém certificado SSL do Let's Encrypt
- Configura renovação automática
- Atualiza configuração do Nginx

---

#### `deploy-vps.sh`
Deploy da aplicação na VPS.

```bash
./scripts/deploy-vps.sh
```

**O que faz:**
1. Compila a aplicação
2. Cria imagem Docker
3. Faz backup do banco de dados
4. Para containers antigos
5. Inicia novos containers
6. Verifica health check

**Tempo estimado:** 5-10 minutos

---

#### `backup-vps.sh`
Backup manual do banco de dados.

```bash
./scripts/backup-vps.sh
```

**O que faz:**
- Cria backup comprimido do PostgreSQL
- Armazena em `/backups/`
- Remove backups com mais de 30 dias

**Execução automática:** Diariamente às 2h (via cron)

---

### ☁️ AWS Scripts

#### `deploy-aws.sh`
Deploy da aplicação na AWS.

```bash
./scripts/deploy-aws.sh production
```

**O que faz:**
1. Compila a aplicação
2. Autentica no ECR
3. Cria e faz push da imagem Docker
4. Atualiza Task Definition (ECS)
5. Faz deployment
6. Aguarda estabilização

**Requisitos:**
- AWS CLI instalado e configurado
- Permissões IAM adequadas
- ECR Repository criado

**Tempo estimado:** 10-15 minutos

---

## 🚀 Fluxo de Deploy

### VPS - Primeira Vez

```bash
# 1. Setup inicial (uma vez)
sudo ./scripts/setup-vps.sh

# 2. Configurar .env
cp .env.vps.example .env
nano .env

# 3. Configurar SSL
./scripts/setup-ssl-vps.sh meu-dominio.com.br

# 4. Deploy
./scripts/deploy-vps.sh
```

### VPS - Atualizações

```bash
# 1. Puxar código
git pull origin main

# 2. Deploy
./scripts/deploy-vps.sh
```

### AWS - Primeira Vez

```bash
# 1. Criar infraestrutura AWS (Console ou CLI)
# - RDS PostgreSQL
# - ElastiCache Redis
# - ECR Repository
# - ECS Cluster

# 2. Configurar .env
cp .env.aws.example .env
nano .env

# 3. Deploy
./scripts/deploy-aws.sh production
```

### AWS - Atualizações

```bash
# 1. Puxar código
git pull origin main

# 2. Deploy
./scripts/deploy-aws.sh production
```

---

## 🔧 Troubleshooting

### Script não executa

```bash
# Dar permissão de execução
chmod +x scripts/*.sh
```

### Erro no deploy VPS

```bash
# Ver logs
docker-compose -f docker-compose.vps.yml logs -f app

# Reiniciar containers
docker-compose -f docker-compose.vps.yml restart
```

### Erro no deploy AWS

```bash
# Ver logs ECS
aws logs tail /aws/ecs/ppghub-service --follow

# Ver status do serviço
aws ecs describe-services \
    --cluster ppghub-cluster \
    --services ppghub-service
```

---

## 📝 Variáveis de Ambiente

Cada script espera variáveis específicas no arquivo `.env`:

### VPS
- `VPS_DB_PASSWORD`
- `VPS_REDIS_PASSWORD`
- `OPENALEX_EMAIL`
- `VPS_DOMAIN`

### AWS
- `AWS_REGION`
- `AWS_RDS_ENDPOINT`
- `AWS_RDS_PASSWORD`
- `AWS_ELASTICACHE_ENDPOINT`
- `AWS_ELASTICACHE_AUTH_TOKEN`
- `OPENALEX_EMAIL`

---

## 🔐 Segurança

**IMPORTANTE:**
- ⚠️ **NUNCA** commite o arquivo `.env`
- ⚠️ Use senhas fortes (mínimo 16 caracteres)
- ⚠️ Mude senhas padrão imediatamente
- ✅ Use `.env.vps.example` ou `.env.aws.example` como base

---

## 📞 Ajuda

Para mais informações, consulte:
- [DEPLOY.md](../DEPLOY.md) - Guia completo de deploy
- [README.md](../README.md) - Documentação principal
