# 🚀 Guia de Deploy - PPG Hub

Guia completo para fazer deploy da aplicação PPG Hub tanto na **AWS** quanto em uma **VPS** própria.

---

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Deploy na VPS](#deploy-na-vps)
3. [Deploy na AWS](#deploy-na-aws)
4. [Comparação VPS vs AWS](#comparação-vps-vs-aws)
5. [Troubleshooting](#troubleshooting)
6. [Manutenção](#manutenção)

---

## 🎯 Visão Geral

O PPG Hub suporta deploy em dois ambientes:

| Ambiente | Descrição | Quando Usar |
|----------|-----------|-------------|
| **VPS** | Self-hosted em servidor próprio | Menor custo, controle total, tráfego moderado |
| **AWS** | Serviços gerenciados na nuvem | Escalabilidade, alta disponibilidade, produção |

### Profiles Spring Boot

- `vps` - Para deploy em VPS com Docker Compose
- `aws` - Para deploy na AWS com RDS e ElastiCache

---

## 🖥️ Deploy na VPS

### Pré-requisitos

- VPS com Ubuntu 20.04+ ou Debian 11+
- Mínimo: 2 CPU, 4GB RAM, 40GB SSD
- Recomendado: 4 CPU, 8GB RAM, 80GB SSD
- Domínio apontando para o IP da VPS
- Acesso SSH root ou sudo

### Passo 1: Configuração Inicial da VPS

```bash
# 1. Conectar na VPS
ssh root@seu-servidor.com

# 2. Baixar o projeto
cd /app
git clone https://github.com/seu-usuario/ppg_hub.git
cd ppg_hub

# 3. Executar setup inicial
chmod +x scripts/*.sh
sudo ./scripts/setup-vps.sh
```

**O script `setup-vps.sh` irá:**
- ✅ Atualizar sistema operacional
- ✅ Instalar Docker e Docker Compose
- ✅ Configurar firewall (UFW)
- ✅ Instalar e configurar Fail2Ban
- ✅ Criar estrutura de diretórios
- ✅ Configurar Nginx, PostgreSQL e Redis
- ✅ Configurar logrotate

### Passo 2: Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar configurações
nano .env
```

**Configurações obrigatórias para VPS:**

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=vps

# Database
VPS_DB_HOST=postgres
VPS_DB_PORT=5432
VPS_DB_NAME=ppg_hub
VPS_DB_USER=ppghub
VPS_DB_PASSWORD=SENHA_FORTE_AQUI_123!

# Redis
VPS_REDIS_HOST=redis
VPS_REDIS_PORT=6379
VPS_REDIS_PASSWORD=SENHA_REDIS_FORTE_456!

# OpenAlex
OPENALEX_EMAIL=seu-email@instituicao.edu.br

# Domínio
VPS_DOMAIN=ppghub.com.br
VPS_SERVER_NAME=vps-01

# Server
SERVER_PORT=8080
```

### Passo 3: Configurar SSL (Let's Encrypt)

```bash
# Obter certificado SSL
./scripts/setup-ssl-vps.sh ppghub.com.br

# Isso irá:
# - Obter certificado para ppghub.com.br e www.ppghub.com.br
# - Configurar renovação automática
# - Atualizar Nginx com HTTPS
```

### Passo 4: Deploy da Aplicação

```bash
# Executar deploy
./scripts/deploy-vps.sh
```

**O script de deploy irá:**
1. ✅ Compilar a aplicação (Maven)
2. ✅ Criar imagem Docker
3. ✅ Fazer backup do banco (se existir)
4. ✅ Parar containers antigos
5. ✅ Iniciar novos containers
6. ✅ Verificar health check
7. ✅ Limpar imagens antigas

### Passo 5: Verificar Deploy

```bash
# Ver status dos containers
docker-compose -f docker-compose.vps.yml ps

# Ver logs da aplicação
docker-compose -f docker-compose.vps.yml logs -f app

# Testar health check
curl https://ppghub.com.br/api/management/health

# Testar API
curl https://ppghub.com.br/api/v1/instituicoes
```

### 🔄 Atualizar Aplicação na VPS

```bash
# Puxar últimas alterações
git pull origin main

# Executar deploy
./scripts/deploy-vps.sh
```

### 📦 Backup Manual na VPS

```bash
# Criar backup do banco de dados
./scripts/backup-vps.sh

# Backups ficam em: /app/ppg_hub/backups/postgres/
# Retenção: 30 dias
```

---

## ☁️ Deploy na AWS

### Pré-requisitos

- Conta AWS ativa
- AWS CLI instalado e configurado
- Permissões IAM adequadas
- Domínio (opcional, recomendado)

### Arquitetura AWS Recomendada

```
┌─────────────────────────────────────────────┐
│              Route 53 (DNS)                 │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│   Application Load Balancer (ALB)          │
│   - SSL/TLS Termination                    │
│   - Health Checks                          │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│         ECS/Fargate ou EC2                  │
│   - Auto Scaling                           │
│   - Multi-AZ                               │
└──────────────────┬──────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │                      │
┌───────▼────────┐    ┌───────▼────────┐
│ RDS PostgreSQL │    │ ElastiCache    │
│   Multi-AZ     │    │    Redis       │
└────────────────┘    └────────────────┘
```

### Opção 1: Deploy com ECS/Fargate (Recomendado)

#### Passo 1: Criar Infraestrutura AWS

**1.1. Criar RDS PostgreSQL:**

```bash
aws rds create-db-instance \
    --db-instance-identifier ppghub-db \
    --db-instance-class db.t3.medium \
    --engine postgres \
    --engine-version 16.1 \
    --master-username ppghub \
    --master-user-password SENHA_FORTE_RDS \
    --allocated-storage 100 \
    --storage-type gp3 \
    --multi-az \
    --vpc-security-group-ids sg-xxxxx \
    --db-subnet-group-name ppghub-db-subnet \
    --backup-retention-period 7 \
    --preferred-backup-window "03:00-04:00" \
    --region sa-east-1
```

**1.2. Criar ElastiCache Redis:**

```bash
aws elasticache create-cache-cluster \
    --cache-cluster-id ppghub-cache \
    --engine redis \
    --cache-node-type cache.t3.medium \
    --num-cache-nodes 1 \
    --engine-version 7.0 \
    --auth-token SENHA_FORTE_REDIS \
    --transit-encryption-enabled \
    --region sa-east-1
```

**1.3. Criar ECR Repository:**

```bash
aws ecr create-repository \
    --repository-name ppghub \
    --region sa-east-1
```

**1.4. Criar ECS Cluster:**

```bash
aws ecs create-cluster \
    --cluster-name ppghub-cluster \
    --region sa-east-1
```

#### Passo 2: Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar configurações
nano .env
```

**Configurações obrigatórias para AWS:**

```bash
# Spring Profile
SPRING_PROFILES_ACTIVE=aws

# AWS
AWS_REGION=sa-east-1
AWS_ACCOUNT_ID=123456789012

# ECR
ECR_REPOSITORY=ppghub

# Database (RDS)
AWS_RDS_ENDPOINT=ppghub-db.xxxxx.sa-east-1.rds.amazonaws.com
AWS_RDS_PORT=5432
AWS_RDS_DATABASE=ppg_hub
AWS_RDS_USERNAME=ppghub
AWS_RDS_PASSWORD=SENHA_FORTE_RDS

# Cache (ElastiCache)
AWS_ELASTICACHE_ENDPOINT=ppghub-cache.xxxxx.cache.amazonaws.com
AWS_ELASTICACHE_PORT=6379
AWS_ELASTICACHE_AUTH_TOKEN=SENHA_FORTE_REDIS

# OpenAlex
OPENALEX_EMAIL=seu-email@instituicao.edu.br

# ECS (opcional)
ECS_CLUSTER=ppghub-cluster
ECS_SERVICE=ppghub-service

# Domínio
APP_DOMAIN=ppghub.com.br
```

#### Passo 3: Deploy na AWS

```bash
# Executar deploy
./scripts/deploy-aws.sh production
```

**O script de deploy AWS irá:**
1. ✅ Compilar aplicação
2. ✅ Autenticar no ECR
3. ✅ Criar imagem Docker
4. ✅ Push para ECR
5. ✅ Atualizar Task Definition
6. ✅ Atualizar serviço ECS
7. ✅ Aguardar deployment

#### Passo 4: Configurar Application Load Balancer

```bash
# Criar Target Group
aws elbv2 create-target-group \
    --name ppghub-tg \
    --protocol HTTP \
    --port 8080 \
    --vpc-id vpc-xxxxx \
    --health-check-path /api/management/health \
    --health-check-interval-seconds 30 \
    --region sa-east-1

# Criar Load Balancer
aws elbv2 create-load-balancer \
    --name ppghub-alb \
    --subnets subnet-xxxxx subnet-yyyyy \
    --security-groups sg-xxxxx \
    --region sa-east-1

# Criar Listener HTTPS
aws elbv2 create-listener \
    --load-balancer-arn arn:aws:elasticloadbalancing:... \
    --protocol HTTPS \
    --port 443 \
    --certificates CertificateArn=arn:aws:acm:... \
    --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:...
```

### Opção 2: Deploy com Elastic Beanstalk (Mais Simples)

```bash
# Instalar EB CLI
pip install awsebcli

# Inicializar Elastic Beanstalk
eb init -p docker ppghub --region sa-east-1

# Criar ambiente
eb create ppghub-prod \
    --instance-type t3.medium \
    --database.engine postgres \
    --database.size 100 \
    --envvars SPRING_PROFILES_ACTIVE=aws

# Deploy
eb deploy
```

### 🔄 Atualizar Aplicação na AWS

```bash
# Puxar últimas alterações
git pull origin main

# Executar deploy
./scripts/deploy-aws.sh production
```

### 📊 Monitoramento na AWS

```bash
# Ver logs (CloudWatch)
aws logs tail /aws/ecs/ppghub-service --follow

# Ver métricas
aws cloudwatch get-metric-statistics \
    --namespace AWS/ECS \
    --metric-name CPUUtilization \
    --dimensions Name=ServiceName,Value=ppghub-service \
    --start-time 2024-01-01T00:00:00Z \
    --end-time 2024-01-02T00:00:00Z \
    --period 3600 \
    --statistics Average
```

---

## ⚖️ Comparação VPS vs AWS

| Aspecto | VPS | AWS |
|---------|-----|-----|
| **Custo Mensal** | $20-50 (fixo) | $100-300+ (variável) |
| **Setup Inicial** | 30 minutos | 1-2 horas |
| **Complexidade** | Baixa | Média/Alta |
| **Escalabilidade** | Manual | Automática |
| **Alta Disponibilidade** | Single point of failure | Multi-AZ, auto-recovery |
| **Backup** | Manual/Cron | Automatizado (RDS) |
| **SSL** | Let's Encrypt (grátis) | ACM (grátis) |
| **Manutenção** | Você gerencia tudo | AWS gerencia infraestrutura |
| **Desempenho** | Fixo | Escalável sob demanda |

### Recomendações

**Use VPS quando:**
- Budget limitado (<$50/mês)
- Tráfego previsível e moderado
- Equipe com conhecimento DevOps
- Precisa de controle total

**Use AWS quando:**
- Precisa de alta disponibilidade (99.9%+)
- Tráfego variável/imprevisível
- Precisa escalar rapidamente
- Produção crítica

---

## 🔧 Troubleshooting

### Problemas Comuns - VPS

**1. Aplicação não inicia:**

```bash
# Ver logs
docker-compose -f docker-compose.vps.yml logs app

# Verificar banco de dados
docker-compose -f docker-compose.vps.yml exec postgres psql -U ppghub -d ppg_hub
```

**2. Erro de conexão com banco:**

```bash
# Verificar se PostgreSQL está rodando
docker-compose -f docker-compose.vps.yml ps postgres

# Testar conexão
docker-compose -f docker-compose.vps.yml exec postgres pg_isready
```

**3. SSL não funciona:**

```bash
# Verificar certificado
sudo certbot certificates

# Renovar manualmente
sudo certbot renew

# Verificar logs do Nginx
docker-compose -f docker-compose.vps.yml logs nginx
```

**4. Falta de memória:**

```bash
# Ver uso de memória
docker stats

# Adicionar swap (se necessário)
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### Problemas Comuns - AWS

**1. Task não inicia (ECS):**

```bash
# Ver eventos do serviço
aws ecs describe-services \
    --cluster ppghub-cluster \
    --services ppghub-service

# Ver logs de container
aws logs tail /aws/ecs/ppghub-service --follow
```

**2. Erro de conexão com RDS:**

```bash
# Verificar security group
# - Porta 5432 deve estar aberta para o security group do ECS
# - Verificar em Console AWS > RDS > Security Groups
```

**3. Custo muito alto:**

```bash
# Ver custos por serviço
aws ce get-cost-and-usage \
    --time-period Start=2024-01-01,End=2024-01-31 \
    --granularity MONTHLY \
    --metrics BlendedCost \
    --group-by Type=SERVICE
```

---

## 🛠️ Manutenção

### VPS - Tarefas Regulares

**Diariamente (automático):**
- Backup do banco de dados (2h da manhã)
- Renovação SSL (se necessário)

**Semanalmente:**

```bash
# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Ver uso de disco
df -h

# Limpar logs antigos
docker system prune -f
```

**Mensalmente:**

```bash
# Verificar backups
ls -lh /app/ppg_hub/backups/postgres/

# Atualizar imagens Docker
docker-compose -f docker-compose.vps.yml pull
docker-compose -f docker-compose.vps.yml up -d
```

### AWS - Tarefas Regulares

**Diariamente (automático):**
- Backup RDS (snapshot automático)
- CloudWatch Logs rotation

**Mensalmente:**

```bash
# Revisar custos
aws ce get-cost-forecast ...

# Atualizar Task Definition com nova imagem
./scripts/deploy-aws.sh production

# Verificar snapshots RDS antigos
aws rds describe-db-snapshots
```

---

## 📞 Suporte

- **Documentação**: Este arquivo (DEPLOY.md)
- **Issues**: GitHub Issues
- **Email**: contato@ppghub.com.br

---

## 📝 Checklist de Deploy

### VPS

- [ ] VPS provisionada (mín: 2 CPU, 4GB RAM)
- [ ] Domínio apontando para IP da VPS
- [ ] Setup inicial executado (`setup-vps.sh`)
- [ ] `.env` configurado com senhas fortes
- [ ] SSL configurado (`setup-ssl-vps.sh`)
- [ ] Deploy executado (`deploy-vps.sh`)
- [ ] Health check retorna 200 OK
- [ ] HTTPS funcionando
- [ ] Backup automático configurado

### AWS

- [ ] RDS PostgreSQL criado
- [ ] ElastiCache Redis criado
- [ ] ECR Repository criado
- [ ] ECS Cluster criado
- [ ] `.env` configurado com endpoints AWS
- [ ] Deploy executado (`deploy-aws.sh`)
- [ ] ALB configurado
- [ ] Certificado SSL/ACM configurado
- [ ] Health check funcionando
- [ ] CloudWatch Logs funcionando
- [ ] Auto Scaling configurado (opcional)

---

**Última atualização**: 2024-11-16
**Versão**: 1.0.0

