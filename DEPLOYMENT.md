# Guia de Deploy - PPG Hub

## 🚀 Deploy com Docker Compose (Recomendado)

### Pré-requisitos
- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM disponível
- 10GB espaço em disco

### Passo a Passo

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/ppg-hub.git
cd ppg-hub
```

2. **Configure variáveis de ambiente:**
```bash
cp .env.example .env
nano .env  # Edite os valores
```

**IMPORTANTE:** Altere as seguintes variáveis em produção:
- `DB_PASSWORD`: Senha do PostgreSQL
- `JWT_SECRET`: Chave secreta JWT (mínimo 256 bits)
- `GRAFANA_PASSWORD`: Senha do Grafana

3. **Inicie os containers:**
```bash
docker-compose up -d
```

4. **Verifique o status:**
```bash
docker-compose ps
docker-compose logs -f app
```

5. **Acesse a aplicação:**
- API: http://localhost:8000
- Swagger: http://localhost:8000/swagger-ui.html
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### Comandos Úteis

```bash
# Parar containers
docker-compose down

# Parar e remover volumes (CUIDADO: apaga dados)
docker-compose down -v

# Ver logs em tempo real
docker-compose logs -f app

# Ver logs de todos os serviços
docker-compose logs -f

# Rebuild da aplicação
docker-compose up -d --build app

# Executar migrations manualmente
docker-compose exec app java -jar app.jar --flyway.migrate

# Acessar banco de dados
docker-compose exec postgres psql -U ppgadmin -d ppg_hub

# Acessar shell do container da aplicação
docker-compose exec app sh

# Reiniciar apenas um serviço
docker-compose restart app

# Ver uso de recursos
docker stats
```

## 🔧 Deploy Manual (Sem Docker)

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 12+

### Passo a Passo

1. **Configurar banco de dados:**
```sql
CREATE DATABASE ppg_hub;
CREATE USER ppgadmin WITH PASSWORD 'sua_senha';
GRANT ALL PRIVILEGES ON DATABASE ppg_hub TO ppgadmin;

-- Conectar ao banco ppg_hub
\c ppg_hub

-- Criar schemas
CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS academic;

-- Conceder permissões
GRANT ALL PRIVILEGES ON SCHEMA core TO ppgadmin;
GRANT ALL PRIVILEGES ON SCHEMA auth TO ppgadmin;
GRANT ALL PRIVILEGES ON SCHEMA academic TO ppgadmin;

-- Criar extensões
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
```

2. **Configurar application.yml:**

Edite `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ppg_hub
    username: ppgadmin
    password: sua_senha_aqui
```

3. **Build:**
```bash
mvn clean package -DskipTests
```

4. **Executar:**
```bash
java -jar target/ppg-hub-0.1.0.jar
```

5. **Verificar:**
```bash
curl http://localhost:8000/actuator/health
```

## 📊 Monitoramento

### Health Check
```bash
curl http://localhost:8000/actuator/health
```

Resposta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### Métricas Prometheus
```bash
curl http://localhost:8000/actuator/prometheus
```

### Dashboards Grafana

1. Acesse: http://localhost:3000
2. Login: `admin` / `admin` (ou senha configurada em `.env`)
3. Adicione data source Prometheus:
   - URL: `http://prometheus:9090`
4. Importe dashboards prontos:
   - Spring Boot 2.1 Statistics (ID: 10280)
   - JVM Micrometer (ID: 4701)

## 🔒 Segurança em Produção

### 1. Altere Senhas Padrão

Edite `.env`:
```bash
DB_PASSWORD=SenhaForteESegura123!
JWT_SECRET=ChaveSecretaComMinimoDE256BitsPorFavorAlterarEmProducao
GRAFANA_PASSWORD=SenhaDoGrafanaSegura123!
```

### 2. Configure HTTPS

#### Opção A: Nginx como Reverse Proxy

Crie `/etc/nginx/sites-available/ppg-hub`:

```nginx
server {
    listen 80;
    server_name ppg-hub.suainstituicao.edu.br;

    location / {
        return 301 https://$server_name$request_uri;
    }
}

server {
    listen 443 ssl http2;
    server_name ppg-hub.suainstituicao.edu.br;

    ssl_certificate /etc/letsencrypt/live/ppg-hub.suainstituicao.edu.br/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/ppg-hub.suainstituicao.edu.br/privkey.pem;

    location / {
        proxy_pass http://localhost:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### Opção B: Traefik (Docker)

Adicione ao `docker-compose.yml`:

```yaml
services:
  traefik:
    image: traefik:v2.10
    command:
      - "--providers.docker=true"
      - "--entrypoints.web.address=:80"
      - "--entrypoints.websecure.address=:443"
      - "--certificatesresolvers.myresolver.acme.tlschallenge=true"
      - "--certificatesresolvers.myresolver.acme.email=admin@suainstituicao.edu.br"
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - ./letsencrypt:/letsencrypt

  app:
    labels:
      - "traefik.enable=true"
      - "traefik.http.routers.app.rule=Host(`ppg-hub.suainstituicao.edu.br`)"
      - "traefik.http.routers.app.entrypoints=websecure"
      - "traefik.http.routers.app.tls.certresolver=myresolver"
```

### 3. Firewall

```bash
# UFW (Ubuntu/Debian)
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw enable

# Iptables (CentOS/RHEL)
sudo firewall-cmd --permanent --add-service=http
sudo firewall-cmd --permanent --add-service=https
sudo firewall-cmd --reload
```

### 4. Backups Automáticos

Crie script `/usr/local/bin/backup-ppg-hub.sh`:

```bash
#!/bin/bash
set -e

BACKUP_DIR="/backup/ppg-hub"
DATE=$(date +%Y%m%d_%H%M%S)
DB_CONTAINER="ppg-hub-db"

# Criar diretório de backup
mkdir -p $BACKUP_DIR

# Backup do banco de dados
docker exec $DB_CONTAINER pg_dump -U ppgadmin ppg_hub | gzip > $BACKUP_DIR/ppg_hub_$DATE.sql.gz

# Manter apenas últimos 30 dias
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete

echo "Backup completo: $BACKUP_DIR/ppg_hub_$DATE.sql.gz"
```

Configure cron:
```bash
sudo chmod +x /usr/local/bin/backup-ppg-hub.sh
crontab -e
```

Adicione:
```
0 2 * * * /usr/local/bin/backup-ppg-hub.sh >> /var/log/ppg-hub-backup.log 2>&1
```

## 🐛 Troubleshooting

### App não inicia

**Verificar logs:**
```bash
docker-compose logs app
```

**Problemas comuns:**
- Porta 8000 já em uso: `lsof -i :8000` ou `netstat -tlnp | grep 8000`
- Falta de memória: `docker stats`
- Erro de conexão com banco: verificar se PostgreSQL está rodando

### Erro de conexão com banco

**Verificar se PostgreSQL está saudável:**
```bash
docker-compose exec postgres pg_isready -U ppgadmin
```

**Verificar conexão:**
```bash
docker-compose exec postgres psql -U ppgadmin -d ppg_hub -c "SELECT 1"
```

**Verificar logs do banco:**
```bash
docker-compose logs postgres
```

### Migrations Flyway falharam

**Ver histórico de migrations:**
```bash
docker-compose exec postgres psql -U ppgadmin -d ppg_hub -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5"
```

**Reparar Flyway (último recurso):**
```bash
docker-compose exec app java -jar app.jar --flyway.repair
```

### Limpar tudo e recomeçar

**ATENÇÃO: Isso irá APAGAR TODOS OS DADOS!**

```bash
# Parar e remover containers
docker-compose down -v

# Remover imagens
docker rmi ppg-hub:latest

# Limpar sistema Docker
docker system prune -a

# Reiniciar
docker-compose up -d
```

## 🚀 Deploy em Produção (VPS/Cloud)

### DigitalOcean / Linode / Vultr

1. **Criar Droplet:**
   - Ubuntu 22.04 LTS
   - Mínimo: 2GB RAM, 2 vCPUs, 50GB SSD
   - Recomendado: 4GB RAM, 2 vCPUs, 80GB SSD

2. **Conectar via SSH:**
```bash
ssh root@seu-servidor-ip
```

3. **Instalar Docker e Docker Compose:**
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
apt install docker-compose-plugin -y
```

4. **Clonar repositório:**
```bash
cd /opt
git clone https://github.com/seu-usuario/ppg-hub.git
cd ppg-hub
```

5. **Configurar e iniciar:**
```bash
cp .env.example .env
nano .env  # Editar variáveis
docker compose up -d
```

### AWS EC2

Similar ao VPS, mas use:
- AMI: Ubuntu Server 22.04 LTS
- Instance Type: t3.medium (2 vCPUs, 4GB RAM)
- Security Group: Liberar portas 22, 80, 443

### Google Cloud Run (Serverless)

```bash
# Build
docker build -t gcr.io/seu-projeto/ppg-hub:latest .

# Push
docker push gcr.io/seu-projeto/ppg-hub:latest

# Deploy
gcloud run deploy ppg-hub \
  --image gcr.io/seu-projeto/ppg-hub:latest \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

## 📈 Otimizações de Performance

### 1. Aumentar Pool de Conexões

Edite `application-prod.yml`:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 10
```

### 2. Cache Redis (Opcional)

Adicione ao `docker-compose.yml`:

```yaml
services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - ppg-network

volumes:
  redis_data:
```

Adicione ao `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 3. Tuning PostgreSQL

Edite `docker-compose.yml`:

```yaml
postgres:
  command: postgres -c 'max_connections=200' -c 'shared_buffers=256MB' -c 'effective_cache_size=1GB'
```

## 📞 Suporte

Para problemas ou dúvidas:
- Issues: https://github.com/seu-usuario/ppg-hub/issues
- Email: suporte@suainstituicao.edu.br
- Documentação: https://ppg-hub.suainstituicao.edu.br/docs

---

**Versão:** 0.1.0
**Última atualização:** 2024
**Licença:** MIT
