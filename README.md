# PPG Hub - Sistema de Gestão de Programas de Pós-Graduação

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Sistema completo para gestão de programas de pós-graduação stricto sensu (mestrado e doutorado), integrando dados acadêmicos, bibliométricos e gestão de bancas de defesa.

---

## 📋 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Execução](#-execução)
- [Documentação da API](#-documentação-da-api)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Módulos](#-módulos)
- [Deploy](#-deploy)
- [Testes](#-testes)
- [Contribuindo](#-contribuindo)
- [Licença](#-licença)

---

## 🎯 Sobre o Projeto

O **PPG Hub** é uma plataforma completa para gerenciamento de programas de pós-graduação que oferece:

- 📊 **Gestão Acadêmica**: Instituições, programas, docentes e discentes
- 📚 **Integração Bibliométrica**: Sincronização com OpenAlex para métricas de publicações
- 🎓 **Bancas de Defesa**: Sistema completo para agendamento e gestão de bancas
- 🔍 **Busca Avançada**: Filtros e consultas complexas por múltiplos critérios
- 📈 **Relatórios e Analytics**: Estatísticas e indicadores de desempenho

### Problema que Resolve

Programas de pós-graduação enfrentam desafios na:
- Consolidação de dados de múltiplas fontes (Lattes, ORCID, OpenAlex)
- Gestão manual de bancas de defesa com professores internos e externos
- Geração de relatórios para CAPES e avaliações internas
- Acompanhamento de métricas bibliométricas dos docentes

O PPG Hub centraliza e automatiza esses processos.

---

## ✨ Funcionalidades

### Gestão de Entidades Acadêmicas

#### 🏛️ Instituições
- CRUD completo de instituições de ensino superior
- Busca por CNPJ, sigla, nome
- Tipos: FEDERAL, ESTADUAL, MUNICIPAL, PRIVADA, CONFESSIONAL
- Sincronização com OpenAlex para métricas institucionais

#### 📖 Programas
- Gestão de programas de pós-graduação
- Código CAPES e áreas de conhecimento
- Níveis: MESTRADO, DOUTORADO, MESTRADO_PROFISSIONAL
- Vinculação com instituições

#### 👨‍🏫 Docentes
- Cadastro completo de docentes/pesquisadores
- Identificadores acadêmicos: CPF, Lattes, ORCID, OpenAlex
- Métricas bibliométricas (h-index, citações, publicações)
- Vínculos com programas e instituições
- Sincronização automática com OpenAlex

#### 🎓 Discentes
- Gestão de estudantes de pós-graduação
- Matrícula, status (ATIVO, EGRESSO, TRANCADO, etc.)
- Vínculo com programa e orientador
- Níveis: MESTRADO, DOUTORADO, DOUTORADO_DIRETO
- Rastreamento de defesas

### Gestão de Publicações

#### 📄 Publicações
- Importação de publicações via OpenAlex
- DOI, PMID, tipos de publicação
- Métricas de citações e impacto
- Relacionamento com autores (docentes)

#### ✍️ Autorias
- Ordem de autoria (primeiro, último, etc.)
- Autor correspondente
- Afiliações institucionais

### Gestão de Bancas de Defesa (Novo!)

#### 👥 Professores Externos
- Cadastro de professores de outras instituições
- **Padrão Find-or-Create**: busca ou cria automaticamente
- Validação de dados via OpenAlex/ORCID
- Rastreamento de participações em bancas

#### 🎯 Bancas
- Tipos: QUALIFICACAO_MESTRADO, DEFESA_MESTRADO, DEFESA_DOUTORADO
- Workflow de status: AGENDADA → CONFIRMADA → REALIZADA
- Resultados: APROVADO, APROVADO_COM_RESTRICOES, REPROVADO
- Validação automática de composição (3-5 membros, mínimo 1 externo)
- Detecção de conflitos de horário
- Suporte a defesas remotas
- Gestão de documentos (ata, tese)

#### 🤝 Membros de Banca
- Tipos: TITULAR, SUPLENTE
- Funções: PRESIDENTE, MEMBRO_INTERNO, MEMBRO_EXTERNO, ORIENTADOR
- Ciclo de vida de convites: PENDENTE → ENVIADO → CONFIRMADO/RECUSADO
- Suporte a docentes internos e professores externos
- Histórico de participações

---

## 🏗️ Arquitetura

### Arquitetura em Camadas (Clean Architecture)

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│  (Controllers REST, Exception Handlers, Validations)    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                  Application Layer                       │
│         (DTOs, Mappers, Use Cases Orchestration)        │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                    Domain Layer                          │
│  (Services, Business Logic, Domain Models, Validations) │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                Infrastructure Layer                      │
│  (Repositories, Database, External APIs, Caching)       │
└─────────────────────────────────────────────────────────┘
```

### Princípios Aplicados

- **SOLID**: Responsabilidade única, inversão de dependência
- **DDD (Domain-Driven Design)**: Entidades, agregados, repositórios
- **RESTful API**: Design orientado a recursos, HTTP semântico
- **Separation of Concerns**: Camadas bem definidas e isoladas
- **Dependency Injection**: Spring IoC container

### Fluxo de Requisição

```
HTTP Request (Cliente)
    ↓
┌─────────────────────┐
│   Controller        │  ← Validação de entrada (Jakarta Validation)
│   (@RestController) │  ← Documentação (Swagger)
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│   Mapper            │  ← Conversão DTO → Entity
│   (MapStruct)       │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│   Service           │  ← Regras de negócio
│   (@Service)        │  ← Transações (@Transactional)
│                     │  ← Validações de domínio
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│   Repository        │  ← Acesso a dados (Spring Data JPA)
│   (JpaRepository)   │  ← Queries customizadas (@Query)
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│   Database          │  ← PostgreSQL
│   (PostgreSQL)      │  ← Flyway migrations
└─────────────────────┘
           ↓
Response (JSON)
```

---

## 🛠️ Tecnologias

### Backend

- **Java 17** - LTS com recursos modernos (Records, Pattern Matching)
- **Spring Boot 3.2** - Framework principal
  - Spring Web - REST APIs
  - Spring Data JPA - Persistência
  - Spring Cache - Cache distribuído
  - Spring Validation - Validações
  - Spring Boot Actuator - Métricas e health checks
- **Hibernate 6** - ORM
- **Flyway** - Migração de banco de dados
- **MapStruct** - Mapeamento objeto-objeto
- **Lombok** - Redução de boilerplate

### Banco de Dados

- **PostgreSQL 16** - Banco relacional principal
- **Redis 7** - Cache distribuído
- **H2** - Testes (opcional)

### Documentação

- **Swagger/OpenAPI 3** - Documentação interativa de API
- **SpringDoc** - Geração automática de docs

### Observabilidade

- **SLF4J + Logback** - Logging estruturado
- **Spring Boot Actuator** - Métricas e health
- **Prometheus** (futuro) - Métricas
- **Grafana** (futuro) - Dashboards

### Build e Deploy

- **Maven 3.9** - Gerenciamento de dependências
- **Docker** - Containerização
- **Docker Compose** - Orquestração local
- **AWS ECS/Fargate** - Deploy em nuvem
- **GitHub Actions** (futuro) - CI/CD

---

## 📦 Pré-requisitos

### Desenvolvimento

- **Java 17** ou superior ([AdoptOpenJDK](https://adoptopenjdk.net/))
- **Maven 3.8+** ([Apache Maven](https://maven.apache.org/))
- **Docker** e **Docker Compose** ([Docker Desktop](https://www.docker.com/products/docker-desktop))
- **Git** ([Git SCM](https://git-scm.com/))
- **IDE** recomendada: IntelliJ IDEA, Eclipse ou VS Code

### Produção

- **PostgreSQL 16**
- **Redis 7** (opcional, para cache)
- **Nginx** (para reverse proxy)
- **SSL/TLS** certificado

---

## 🚀 Instalação

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/ppg_hub.git
cd ppg_hub
```

### 2. Configure as Variáveis de Ambiente

Copie o arquivo de exemplo e edite conforme necessário:

```bash
cp .env.example .env
```

Edite `.env`:

```env
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=ppg_hub
POSTGRES_USER=ppghub
POSTGRES_PASSWORD=your_secure_password

# Redis (opcional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Application
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# OpenAlex (opcional)
OPENALEX_API_EMAIL=seu-email@example.com
```

### 3. Inicie o Banco de Dados (Docker)

```bash
docker-compose up -d postgres redis
```

### 4. Execute as Migrações

O Flyway executa automaticamente na inicialização, mas você pode rodar manualmente:

```bash
mvn flyway:migrate
```

### 5. Compile o Projeto
# PPG Hub - Sistema de Gestão para Programas de Pós-Graduação

Sistema completo para gerenciamento de programas de pós-graduação, desenvolvido com **Java Spring Boot**.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM
- **PostgreSQL** - Banco de dados
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação da API (Swagger)
- **MapStruct** - Mapeamento de DTOs
- **Maven** - Gerenciamento de dependências

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Instalação e Configuração

### 1. Clone o repositório

```bash
git clone <repository-url>
cd ppg_hub
```

### 2. Configure o banco de dados

Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE ppg_hub;
```

### 3. Configure as variáveis de ambiente

Edite o arquivo `src/main/resources/application.yml` com suas configurações:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ppg_hub
    username: seu_usuario
    password: sua_senha
```

Ou use variáveis de ambiente:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/ppg_hub
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha
```

### 4. Compile o projeto

```bash
mvn clean install
```

---

## ⚙️ Configuração

### Perfis de Ambiente

O projeto suporta múltiplos perfis Spring:

| Perfil | Uso | Características |
|--------|-----|-----------------|
| `dev` | Desenvolvimento | Logs DEBUG, Swagger habilitado, cache curto |
| `test` | Testes | TestContainers, H2 em memória |
| `preprod` | Pré-produção | Logs INFO, cache médio, simulação de produção |
| `prod` | Produção | Logs WARN/ERROR, cache longo, Swagger desabilitado |
| `aws` | AWS Cloud | RDS, ElastiCache, CloudWatch |
| `vps` | VPS Self-hosted | PostgreSQL/Redis locais, logs em arquivo |

### Ativar Perfil

```bash
# Via variável de ambiente
export SPRING_PROFILES_ACTIVE=dev

# Via argumento Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Via argumento JAR
java -jar target/ppg-hub.jar --spring.profiles.active=dev
```

Consulte a [documentação completa de configuração](docs/CONFIGURATION.md) para mais detalhes.

---

## ▶️ Execução

### Modo Desenvolvimento (Maven)
### 5. Execute a aplicação

```bash
mvn spring-boot:run
```

Aplicação disponível em: `http://localhost:8080`

### Modo Produção (JAR)

```bash
mvn clean package -DskipTests
java -jar target/ppg-hub.jar
```

### Usando Docker Compose

#### Desenvolvimento

```bash
docker-compose -f docker-compose.dev.yml up
```

#### Produção

```bash
docker-compose -f docker-compose.prod.yml up -d
```

#### VPS

```bash
docker-compose -f docker-compose.vps.yml up -d
```

---

## 📚 Documentação da API

### Swagger UI (Interativo)

Acesse a documentação interativa em:

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Spec (JSON)

```
http://localhost:8080/v3/api-docs
```

### Principais Endpoints

Veja a [documentação completa da API](docs/API.md).

---

## 📁 Estrutura do Projeto

```
ppg_hub/
├── docs/                                    # Documentação
│   ├── BANCAS_REQUIREMENTS.md               # Requisitos bancas
│   ├── DEPLOY.md                            # Guia de deploy
│   └── API.md                               # Doc da API
│
├── scripts/                                 # Scripts de deploy
│   ├── setup-vps.sh
│   ├── deploy-vps.sh
│   └── deploy-aws.sh
│
├── src/
│   ├── main/
│   │   ├── java/com/ppghub/
│   │   │   ├── config/                      # Configurações
│   │   │   ├── domain/                      # Camada de domínio
│   │   │   │   └── service/                 # Serviços
│   │   │   ├── application/                 # Camada de aplicação
│   │   │   │   ├── dto/                     # DTOs
│   │   │   │   └── mapper/                  # Mappers
│   │   │   ├── infrastructure/              # Infraestrutura
│   │   │   │   └── persistence/             # Persistência
│   │   │   │       ├── entity/              # Entidades JPA
│   │   │   │       └── repository/          # Repositórios
│   │   │   └── presentation/                # Apresentação
│   │   │       └── controller/              # Controllers REST
│   │   └── resources/
│   │       ├── db/migration/                # Migrações Flyway
│   │       ├── application*.yml             # Configs
│   │       └── messages.properties          # i18n
│   └── test/                                # Testes
│
├── docker-compose*.yml                      # Docker Compose
├── Dockerfile.prod                          # Dockerfile produção
├── pom.xml                                  # Maven POM
└── README.md                                # Este arquivo
```

### Documentação por Camada

- [Domain Layer](src/main/java/com/ppghub/domain/README.md)
- [Application Layer](src/main/java/com/ppghub/application/README.md)
- [Infrastructure Layer](src/main/java/com/ppghub/infrastructure/README.md)
- [Presentation Layer](src/main/java/com/ppghub/presentation/README.md)
- [Database Migrations](src/main/resources/db/migration/README.md)

---

## 📦 Módulos

### 1. Módulo Base (Core)
- Instituições
- Programas
- Docentes
- Publicações

### 2. Módulo Bancas (Novo!)
- Discentes
- Professores Externos
- Bancas de Defesa
- Membros de Banca

### 3. Módulo OpenAlex (Integração)
- Sincronização de publicações
- Métricas bibliométricas
- Enriquecimento de dados

---

## 🌐 Deploy

### Deploy em VPS

Veja o guia completo em [DEPLOY.md](docs/DEPLOY.md)

```bash
# 1. Setup inicial
./scripts/setup-vps.sh

# 2. Configurar SSL
./scripts/setup-ssl-vps.sh yourdomain.com your@email.com

# 3. Deploy
./scripts/deploy-vps.sh
```

### Deploy em AWS

```bash
# 1. Configurar credenciais AWS
aws configure

# 2. Deploy
./scripts/deploy-aws.sh production
```

---

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Coverage com JaCoCo

```bash
mvn clean test jacoco:report
```

Relatório disponível em: `target/site/jacoco/index.html`

---
Ou execute o JAR compilado:

```bash
java -jar target/ppg-hub-0.1.0.jar
```

## 📚 Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **API Docs (JSON)**: http://localhost:8000/api-docs

## 🛠️ Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── br/edu/ppg/hub/
│   │       ├── config/          # Configurações
│   │       ├── controller/      # Controllers REST
│   │       ├── dto/             # Data Transfer Objects
│   │       │   └── mapper/      # Mappers DTO ↔ Entity
│   │       ├── exception/       # Exceções customizadas
│   │       ├── model/           # Entidades JPA
│   │       ├── repository/      # Repositories Spring Data
│   │       ├── service/         # Lógica de negócio
│   │       ├── validation/      # Validadores customizados
│   │       └── PpgHubApplication.java  # Classe principal
│   └── resources/
│       └── application.yml      # Configurações da aplicação
└── test/                        # Testes unitários e integração
```

## 🔌 Endpoints Principais

### Instituições

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET    | `/api/v1/instituicoes` | Lista todas as instituições |
| GET    | `/api/v1/instituicoes/{id}` | Busca por ID |
| GET    | `/api/v1/instituicoes/codigo/{codigo}` | Busca por código |
| GET    | `/api/v1/instituicoes/cnpj/{cnpj}` | Busca por CNPJ |
| GET    | `/api/v1/instituicoes/ativas` | Lista instituições ativas |
| GET    | `/api/v1/instituicoes/search?termo={termo}` | Busca por termo |
| GET    | `/api/v1/instituicoes/tipo/{tipo}` | Lista por tipo |
| GET    | `/api/v1/instituicoes/stats` | Estatísticas |
| POST   | `/api/v1/instituicoes` | Cria nova instituição |
| PUT    | `/api/v1/instituicoes/{id}` | Atualiza instituição |
| DELETE | `/api/v1/instituicoes/{id}` | Remove instituição |
| PATCH  | `/api/v1/instituicoes/{id}/activate` | Ativa instituição |
| PATCH  | `/api/v1/instituicoes/{id}/deactivate` | Desativa instituição |

## 📝 Exemplo de Uso

### Criar Instituição

```bash
curl -X POST http://localhost:8000/api/v1/instituicoes \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "UEPB",
    "nome_completo": "Universidade Estadual da Paraíba",
    "nome_abreviado": "UEPB",
    "sigla": "UEPB",
    "tipo": "Estadual",
    "cnpj": "12.345.678/0001-90",
    "website": "https://uepb.edu.br",
    "ativo": true
  }'
```

### Listar Instituições

```bash
curl http://localhost:8000/api/v1/instituicoes?page=0&size=20
```

### Buscar por Código

```bash
curl http://localhost:8000/api/v1/instituicoes/codigo/UEPB
```

## 🧪 Testes

Execute os testes com:

```bash
mvn test
```

Para gerar relatório de cobertura:

```bash
mvn test jacoco:report
```

## 🏗️ Build para Produção

```bash
mvn clean package -DskipTests
```

O JAR será gerado em `target/ppg-hub-0.1.0.jar`

## 🐳 Docker

### Build da imagem

```bash
docker build -t ppg-hub:latest .
```

### Executar com Docker Compose

```bash
docker-compose up -d
```

## 🔐 Segurança

- Validação de entrada com Bean Validation
- Validadores customizados (CNPJ, Código)
- Tratamento de exceções globalizado
- CORS configurável
- Prepared statements (proteção contra SQL Injection)

## 📊 Features

- ✅ CRUD completo de instituições
- ✅ Busca avançada e filtros
- ✅ Paginação e ordenação
- ✅ Validação de dados
- ✅ Tratamento de erros
- ✅ Documentação Swagger
- ✅ Suporte a JSON para campos complexos
- ✅ Soft delete (ativar/desativar)
- ✅ Auditoria (created_at, updated_at)
- ✅ Estatísticas e relatórios

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 🗺️ Roadmap

### Fase 1 - MVP ✅ (Completo)
- [x] Gestão de instituições e programas
- [x] Gestão de docentes
- [x] Gestão de publicações
- [x] Integração OpenAlex
- [x] **Gestão de bancas de defesa**
- [x] **Gestão de discentes**
- [x] **Professores externos**

### Fase 2 - Notificações e Relatórios
- [ ] Sistema de notificações por email
- [ ] Relatórios gerenciais
- [ ] Dashboard com métricas
- [ ] Exportação de dados (PDF, Excel)

### Fase 3 - Integração Avançada
- [ ] Integração com Lattes
- [ ] Integração com ORCID
- [ ] Sincronização automática
- [ ] API pública

---

**Desenvolvido com ❤️ para a comunidade acadêmica**
## 👥 Autores

- PPG Team

## 📧 Contato

- Email: admin@ppg.edu.br

## 🔄 Changelog

### v0.1.0 (2024-01-15)
- Implementação inicial do sistema
- CRUD de instituições
- Documentação Swagger
- Validações customizadas
