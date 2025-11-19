# PPG HUB - PLANO DE IMPLEMENTAÇÃO COMPLETO
## Sistema Multi-Programa de Pós-Graduação

**Versão:** 1.0.0
**Data:** 2025-11-18
**Arquitetura:** Clean Architecture + SOLID + RESTful API
**Stack:** Java 17, Spring Boot 3.2.0, PostgreSQL 12+

---

## 📋 ÍNDICE

1. [Visão Geral](#1-visão-geral)
2. [Arquitetura Proposta](#2-arquitetura-proposta)
3. [Estrutura de Pastas](#3-estrutura-de-pastas)
4. [Módulos e Domínios](#4-módulos-e-domínios)
5. [Ordem de Implementação](#5-ordem-de-implementação)
6. [Dependências](#6-dependências)
7. [Padrões e Convenções](#7-padrões-e-convenções)
8. [Cronograma](#8-cronograma)
9. [Checklist de Implementação](#9-checklist-de-implementação)

---

## 1. VISÃO GERAL

### 1.1 Objetivo
Implementar uma REST API completa para gerenciar múltiplos programas de pós-graduação, seguindo:
- **Clean Architecture** (camadas independentes e testáveis)
- **SOLID Principles** (código manutenível e extensível)
- **RESTful Best Practices** (HTTP semântico, HATEOAS opcional)
- **Security First** (Spring Security + JWT)
- **DDD** (Domain-Driven Design) para organização de domínios

### 1.2 Escopo Funcional
O sistema cobrirá 3 schemas principais:

```
┌─────────────────────────────────────────────────┐
│  CORE (Estrutura Multi-Tenant)                  │
│  - Instituições                                 │
│  - Programas de Pós-Graduação                   │
│  - Linhas de Pesquisa                           │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│  AUTH (Autenticação e Autorização)              │
│  - Usuários                                     │
│  - Roles (Papéis)                               │
│  - Vinculações (Usuário-Programa-Role)          │
│  - Sessões (JWT)                                │
│  - Audit Logs                                   │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│  ACADEMIC (Gestão Acadêmica)                    │
│  - Docentes                                     │
│  - Discentes                                    │
│  - Disciplinas                                  │
│  - Ofertas de Disciplinas                       │
│  - Matrículas                                   │
│  - Trabalhos de Conclusão                       │
│  - Bancas                                       │
└─────────────────────────────────────────────────┘
```

### 1.3 Estado Atual
**Já Implementado:**
- ✅ Módulo `Instituicao` completo (CRUD)
- ✅ Estrutura básica do Spring Boot
- ✅ PostgreSQL configurado
- ✅ Swagger/OpenAPI
- ✅ Validações customizadas (CNPJ, Código)
- ✅ Exception handling global

**Pendente:**
- ❌ 2 domínios do CORE (Programa, Linha Pesquisa)
- ❌ 5 domínios do AUTH (Usuario, Role, etc.)
- ❌ 8 domínios do ACADEMIC (Docente, Discente, etc.)
- ❌ Spring Security + JWT
- ❌ Testes automatizados
- ❌ CI/CD pipeline

---

## 2. ARQUITETURA PROPOSTA

### 2.1 Clean Architecture - Camadas

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│  (Controllers, DTOs, Exception Handlers, Validators)    │
│  - RestControllers com @RequestMapping                  │
│  - DTOs (CreateDTO, UpdateDTO, ResponseDTO)             │
│  - Mappers (DTO ↔ Entity)                               │
│  - GlobalExceptionHandler                               │
│  - Custom Validators                                    │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                      │
│  (Services, Use Cases, Business Logic)                  │
│  - Services com @Service                                │
│  - Business Rules                                       │
│  - Orchestration entre domínios                         │
│  - Transaction Management (@Transactional)              │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER                         │
│  (Entities, Value Objects, Domain Services)             │
│  - Entities JPA com @Entity                             │
│  - Value Objects (Email, CPF, etc.)                     │
│  - Enums                                                │
│  - Domain Exceptions                                    │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                     │
│  (Repositories, External Services, Security)            │
│  - Repositories (Spring Data JPA)                       │
│  - Database Configuration                               │
│  - Security Configuration                               │
│  - External APIs (OpenAlex, CAPES)                      │
└─────────────────────────────────────────────────────────┘
                           ↓ ↑
┌─────────────────────────────────────────────────────────┐
│                      DATABASE                           │
│  PostgreSQL 12+ (schemas: core, auth, academic)         │
└─────────────────────────────────────────────────────────┘
```

### 2.2 SOLID Principles - Aplicação

**S - Single Responsibility Principle**
- Cada Service tem UMA responsabilidade
- Controllers apenas delegam para Services
- Repositories apenas acessam dados

**O - Open/Closed Principle**
- Interfaces para Services e Repositories
- Strategy pattern para validadores
- Plugin architecture para integrações externas

**L - Liskov Substitution Principle**
- Interfaces bem definidas
- Implementations podem ser substituídas

**I - Interface Segregation Principle**
- Interfaces específicas por domínio
- Não forçar implementações desnecessárias

**D - Dependency Inversion Principle**
- Depender de abstrações (interfaces), não implementações
- Injeção de dependências via @Autowired/@RequiredArgsConstructor

### 2.3 Comunicação Entre Camadas

```java
// FLUXO DE UMA REQUISIÇÃO
HTTP Request → Controller → DTO → Validator → Service → Repository → Entity → Database
                    ↓           ↓           ↓           ↓           ↓
HTTP Response ← Controller ← DTO ← Mapper ← Service ← Repository ← Entity
```

---

## 3. ESTRUTURA DE PASTAS

### 3.1 Estrutura Proposta (Package by Feature + Layer)

```
src/main/java/br/edu/ppg/hub/
│
├── PpgHubApplication.java (Main class)
│
├── config/                              # Configurações globais
│   ├── CorsConfig.java
│   ├── SecurityConfig.java              # [NOVO] Spring Security
│   ├── JwtConfig.java                   # [NOVO] JWT Configuration
│   ├── OpenApiConfig.java               # [NOVO] Swagger config
│   ├── DatabaseConfig.java              # [NOVO] DataSource config
│   └── AsyncConfig.java                 # [NOVO] Async processing
│
├── core/                                # DOMAIN: Core (multi-tenant)
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Instituicao.java         # [EXISTENTE]
│   │   │   ├── Programa.java            # [NOVO]
│   │   │   └── LinhaPesquisa.java       # [NOVO]
│   │   ├── enums/
│   │   │   ├── TipoInstituicao.java     # [NOVO]
│   │   │   ├── NivelPrograma.java       # [NOVO]
│   │   │   └── StatusPrograma.java      # [NOVO]
│   │   └── valueobject/
│   │       ├── CNPJ.java                # [NOVO]
│   │       └── CodigoCapes.java         # [NOVO]
│   │
│   ├── application/
│   │   ├── service/
│   │   │   ├── InstituicaoService.java  # [EXISTENTE]
│   │   │   ├── ProgramaService.java     # [NOVO]
│   │   │   └── LinhaPesquisaService.java # [NOVO]
│   │   └── dto/
│   │       ├── instituicao/
│   │       │   ├── InstituicaoCreateDTO.java
│   │       │   ├── InstituicaoUpdateDTO.java
│   │       │   ├── InstituicaoResponseDTO.java
│   │       │   └── InstituicaoMapper.java
│   │       ├── programa/                # [NOVO]
│   │       │   ├── ProgramaCreateDTO.java
│   │       │   ├── ProgramaUpdateDTO.java
│   │       │   ├── ProgramaResponseDTO.java
│   │       │   └── ProgramaMapper.java
│   │       └── linha/                   # [NOVO]
│   │           └── ...
│   │
│   ├── infrastructure/
│   │   └── repository/
│   │       ├── InstituicaoRepository.java  # [EXISTENTE]
│   │       ├── ProgramaRepository.java     # [NOVO]
│   │       └── LinhaPesquisaRepository.java # [NOVO]
│   │
│   └── presentation/
│       └── controller/
│           ├── InstituicaoController.java  # [EXISTENTE]
│           ├── ProgramaController.java     # [NOVO]
│           └── LinhaPesquisaController.java # [NOVO]
│
├── auth/                                # DOMAIN: Authentication
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Usuario.java             # [NOVO]
│   │   │   ├── Role.java                # [NOVO]
│   │   │   ├── UsuarioProgramaRole.java # [NOVO]
│   │   │   ├── Sessao.java              # [NOVO]
│   │   │   └── AuditLog.java            # [NOVO]
│   │   ├── enums/
│   │   │   ├── TipoRole.java            # [NOVO]
│   │   │   └── StatusUsuario.java       # [NOVO]
│   │   └── valueobject/
│   │       ├── Email.java               # [NOVO]
│   │       ├── CPF.java                 # [NOVO]
│   │       └── Password.java            # [NOVO]
│   │
│   ├── application/
│   │   ├── service/
│   │   │   ├── UsuarioService.java      # [NOVO]
│   │   │   ├── AuthService.java         # [NOVO]
│   │   │   ├── JwtService.java          # [NOVO]
│   │   │   ├── RoleService.java         # [NOVO]
│   │   │   └── AuditService.java        # [NOVO]
│   │   └── dto/
│   │       ├── auth/
│   │       │   ├── LoginRequestDTO.java # [NOVO]
│   │       │   ├── LoginResponseDTO.java # [NOVO]
│   │       │   ├── RegisterRequestDTO.java # [NOVO]
│   │       │   └── TokenRefreshDTO.java # [NOVO]
│   │       └── usuario/
│   │           └── ...
│   │
│   ├── infrastructure/
│   │   ├── repository/
│   │   │   ├── UsuarioRepository.java   # [NOVO]
│   │   │   ├── RoleRepository.java      # [NOVO]
│   │   │   ├── SessaoRepository.java    # [NOVO]
│   │   │   └── AuditLogRepository.java  # [NOVO]
│   │   └── security/
│   │       ├── JwtAuthenticationFilter.java # [NOVO]
│   │       ├── JwtTokenProvider.java    # [NOVO]
│   │       ├── UserDetailsServiceImpl.java # [NOVO]
│   │       └── SecurityUtils.java       # [NOVO]
│   │
│   └── presentation/
│       └── controller/
│           ├── AuthController.java      # [NOVO] /auth/login, /auth/register
│           ├── UsuarioController.java   # [NOVO]
│           └── RoleController.java      # [NOVO]
│
├── academic/                            # DOMAIN: Academic Management
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Docente.java             # [NOVO]
│   │   │   ├── Discente.java            # [NOVO]
│   │   │   ├── Disciplina.java          # [NOVO]
│   │   │   ├── OfertaDisciplina.java    # [NOVO]
│   │   │   ├── MatriculaDisciplina.java # [NOVO]
│   │   │   ├── TrabalhoConclusao.java   # [NOVO]
│   │   │   ├── Banca.java               # [NOVO]
│   │   │   └── MembroBanca.java         # [NOVO]
│   │   ├── enums/
│   │   │   ├── TipoCurso.java           # [NOVO]
│   │   │   ├── StatusDiscente.java      # [NOVO]
│   │   │   ├── TipoDisciplina.java      # [NOVO]
│   │   │   └── ResultadoBanca.java      # [NOVO]
│   │   └── valueobject/
│   │       ├── Matricula.java           # [NOVO]
│   │       └── Nota.java                # [NOVO]
│   │
│   ├── application/
│   │   ├── service/
│   │   │   ├── DocenteService.java      # [NOVO]
│   │   │   ├── DiscenteService.java     # [NOVO]
│   │   │   ├── DisciplinaService.java   # [NOVO]
│   │   │   ├── OfertaService.java       # [NOVO]
│   │   │   ├── MatriculaService.java    # [NOVO]
│   │   │   ├── TrabalhoService.java     # [NOVO]
│   │   │   └── BancaService.java        # [NOVO]
│   │   └── dto/
│   │       ├── docente/
│   │       ├── discente/
│   │       ├── disciplina/
│   │       └── ...
│   │
│   ├── infrastructure/
│   │   └── repository/
│   │       ├── DocenteRepository.java   # [NOVO]
│   │       ├── DiscenteRepository.java  # [NOVO]
│   │       └── ...
│   │
│   └── presentation/
│       └── controller/
│           ├── DocenteController.java   # [NOVO]
│           ├── DiscenteController.java  # [NOVO]
│           └── ...
│
├── shared/                              # Código compartilhado
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java  # [EXISTENTE]
│   │   ├── ResourceNotFoundException.java # [EXISTENTE]
│   │   ├── DuplicateResourceException.java # [EXISTENTE]
│   │   ├── BusinessException.java       # [NOVO]
│   │   ├── UnauthorizedException.java   # [NOVO]
│   │   └── ValidationException.java     # [NOVO]
│   │
│   ├── validation/
│   │   ├── CNPJValidator.java           # [EXISTENTE]
│   │   ├── ValidCNPJ.java               # [EXISTENTE]
│   │   ├── CodigoValidator.java         # [EXISTENTE]
│   │   ├── ValidCodigo.java             # [EXISTENTE]
│   │   ├── CPFValidator.java            # [NOVO]
│   │   ├── ValidCPF.java                # [NOVO]
│   │   ├── EmailValidator.java          # [NOVO]
│   │   └── PasswordValidator.java       # [NOVO]
│   │
│   ├── util/
│   │   ├── DateUtils.java               # [NOVO]
│   │   ├── StringUtils.java             # [NOVO]
│   │   ├── JsonUtils.java               # [NOVO]
│   │   └── PaginationUtils.java         # [NOVO]
│   │
│   └── constant/
│       ├── ApiConstants.java            # [NOVO]
│       ├── ErrorMessages.java           # [NOVO]
│       └── RegexPatterns.java           # [NOVO]
│
└── integration/                         # Integrações externas
    ├── openalex/
    │   ├── OpenAlexClient.java          # [NOVO]
    │   ├── OpenAlexService.java         # [NOVO]
    │   └── dto/
    │       └── OpenAlexResponseDTO.java # [NOVO]
    └── capes/
        ├── CapesClient.java             # [NOVO]
        └── CapesService.java            # [NOVO]
```

### 3.2 Estrutura de Resources

```
src/main/resources/
├── application.yml                      # [EXISTENTE]
├── application-dev.yml                  # [NOVO]
├── application-prod.yml                 # [NOVO]
├── application-test.yml                 # [NOVO]
├── db/
│   ├── migration/                       # Flyway migrations
│   │   ├── V1__initial_schema.sql       # [NOVO]
│   │   ├── V2__add_indexes.sql          # [NOVO]
│   │   ├── V3__add_triggers.sql         # [NOVO]
│   │   └── V4__insert_roles.sql         # [NOVO]
│   └── seed/                            # Dados iniciais
│       ├── roles.sql                    # [NOVO]
│       └── test_data.sql                # [NOVO]
├── messages/                            # i18n
│   ├── messages_pt_BR.properties        # [NOVO]
│   └── messages_en_US.properties        # [NOVO]
└── static/
    └── api-docs/                        # Documentação extra
```

### 3.3 Estrutura de Testes

```
src/test/java/br/edu/ppg/hub/
├── core/
│   ├── service/
│   │   ├── InstituicaoServiceTest.java
│   │   └── ProgramaServiceTest.java
│   ├── repository/
│   │   └── InstituicaoRepositoryTest.java
│   └── controller/
│       └── InstituicaoControllerTest.java
├── auth/
│   └── ...
├── academic/
│   └── ...
└── integration/
    ├── CoreIntegrationTest.java
    ├── AuthIntegrationTest.java
    └── AcademicIntegrationTest.java
```

---

## 4. MÓDULOS E DOMÍNIOS

### 4.1 CORE Module (Estrutura Multi-Tenant)

#### 4.1.1 Instituicao [✅ COMPLETO]
**Endpoints:**
- `POST /api/v1/instituicoes` - Criar
- `GET /api/v1/instituicoes` - Listar (paginado)
- `GET /api/v1/instituicoes/{id}` - Buscar por ID
- `GET /api/v1/instituicoes/codigo/{codigo}` - Buscar por código
- `PUT /api/v1/instituicoes/{id}` - Atualizar
- `DELETE /api/v1/instituicoes/{id}` - Deletar

**Status:** ✅ Implementado

#### 4.1.2 Programa [❌ PENDENTE]
**Entidade:** `Programa`
**Relacionamentos:**
- N:1 com `Instituicao`
- 1:N com `LinhaPesquisa`
- N:1 com `Usuario` (coordenador)

**Endpoints:**
- `POST /api/v1/programas` - Criar programa
- `GET /api/v1/programas` - Listar todos
- `GET /api/v1/programas/{id}` - Buscar por ID
- `GET /api/v1/programas/codigo-capes/{codigo}` - Buscar por código CAPES
- `GET /api/v1/instituicoes/{instituicaoId}/programas` - Programas de uma instituição
- `PUT /api/v1/programas/{id}` - Atualizar
- `PATCH /api/v1/programas/{id}/coordenador` - Alterar coordenador
- `DELETE /api/v1/programas/{id}` - Deletar

**Business Rules:**
- Código CAPES único
- Conceito CAPES entre 1 e 7
- Instituição deve existir
- Coordenador deve ser docente do programa

#### 4.1.3 LinhaPesquisa [❌ PENDENTE]
**Entidade:** `LinhaPesquisa`
**Relacionamentos:**
- N:1 com `Programa`
- 1:N com `Docente`
- 1:N com `Discente`

**Endpoints:**
- `POST /api/v1/linhas-pesquisa` - Criar
- `GET /api/v1/linhas-pesquisa` - Listar todas
- `GET /api/v1/programas/{programaId}/linhas-pesquisa` - Linhas de um programa
- `PUT /api/v1/linhas-pesquisa/{id}` - Atualizar
- `DELETE /api/v1/linhas-pesquisa/{id}` - Deletar

### 4.2 AUTH Module (Autenticação e Autorização)

#### 4.2.1 Usuario [❌ PENDENTE]
**Entidade:** `Usuario`
**Relacionamentos:**
- N:N com `Programa` via `UsuarioProgramaRole`
- N:N com `Role` via `UsuarioProgramaRole`
- 1:N com `Sessao`

**Endpoints:**
- `POST /api/v1/usuarios` - Criar usuário
- `GET /api/v1/usuarios` - Listar todos
- `GET /api/v1/usuarios/{id}` - Buscar por ID
- `GET /api/v1/usuarios/email/{email}` - Buscar por email
- `GET /api/v1/usuarios/cpf/{cpf}` - Buscar por CPF
- `PUT /api/v1/usuarios/{id}` - Atualizar
- `PATCH /api/v1/usuarios/{id}/senha` - Alterar senha
- `PATCH /api/v1/usuarios/{id}/ativar` - Ativar
- `PATCH /api/v1/usuarios/{id}/desativar` - Desativar

**Business Rules:**
- Email único
- CPF único (se brasileiro)
- Senha forte (mín 8 chars, maiúscula, minúscula, número, especial)
- Email deve ser verificado
- Bloquear após 5 tentativas de login

#### 4.2.2 Auth [❌ PENDENTE - CRÍTICO]
**Endpoints:**
- `POST /api/v1/auth/register` - Registrar novo usuário
- `POST /api/v1/auth/login` - Login (retorna JWT)
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/auth/logout` - Logout
- `POST /api/v1/auth/verify-email` - Verificar email
- `POST /api/v1/auth/forgot-password` - Recuperar senha
- `POST /api/v1/auth/reset-password` - Resetar senha

**Security:**
- JWT com expiração (15 min access, 7 dias refresh)
- Bcrypt para hash de senhas
- HTTPS obrigatório em produção
- Rate limiting (10 req/min no login)

#### 4.2.3 Role [❌ PENDENTE]
**Entidade:** `Role`
**Roles Padrão:**
- `SUPERADMIN` - Acesso total
- `ADMIN_INSTITUCIONAL` - Admin da instituição
- `COORDENADOR` - Coordenador de programa
- `SECRETARIA` - Secretaria acadêmica
- `DOCENTE` - Professor
- `DISCENTE` - Aluno
- `TECNICO` - Técnico administrativo
- `VISITANTE` - Consulta pública

**Endpoints:**
- `GET /api/v1/roles` - Listar roles
- `GET /api/v1/roles/{id}` - Buscar role

#### 4.2.4 AuditLog [❌ PENDENTE]
**Entidade:** `AuditLog`
**Funcionalidades:**
- Log automático de todas as operações CUD (Create, Update, Delete)
- Captura de dados anteriores e novos (JSONB)
- IP e User Agent
- Consulta de logs por usuário, entidade, ação

**Endpoints:**
- `GET /api/v1/audit-logs` - Listar logs (admin only)
- `GET /api/v1/audit-logs/usuario/{usuarioId}` - Logs de um usuário
- `GET /api/v1/audit-logs/entidade/{tipo}/{id}` - Logs de uma entidade

### 4.3 ACADEMIC Module (Gestão Acadêmica)

#### 4.3.1 Docente [❌ PENDENTE]
**Entidade:** `Docente`
**Relacionamentos:**
- 1:1 com `Usuario`
- N:1 com `Programa`
- N:1 com `LinhaPesquisa`
- 1:N com `Discente` (orientações)

**Endpoints:**
- `POST /api/v1/docentes` - Cadastrar docente
- `GET /api/v1/docentes` - Listar todos
- `GET /api/v1/docentes/{id}` - Buscar por ID
- `GET /api/v1/programas/{programaId}/docentes` - Docentes de um programa
- `GET /api/v1/docentes/{id}/orientandos` - Orientandos do docente
- `PUT /api/v1/docentes/{id}` - Atualizar
- `PATCH /api/v1/docentes/{id}/desvincular` - Desvincular do programa

**Business Rules:**
- Usuário deve existir
- Um usuário pode ser docente em múltiplos programas
- Validar limite de orientandos (5 mestrado + 5 doutorado)

#### 4.3.2 Discente [❌ PENDENTE]
**Entidade:** `Discente`
**Relacionamentos:**
- 1:1 com `Usuario`
- N:1 com `Programa`
- N:1 com `Docente` (orientador)
- 1:1 com `TrabalhoConclusao`
- N:N com `Disciplina` via `MatriculaDisciplina`

**Endpoints:**
- `POST /api/v1/discentes` - Matricular discente
- `GET /api/v1/discentes` - Listar todos
- `GET /api/v1/discentes/{id}` - Buscar por ID
- `GET /api/v1/discentes/matricula/{matricula}` - Buscar por matrícula
- `GET /api/v1/programas/{programaId}/discentes` - Discentes de um programa
- `GET /api/v1/discentes/{id}/historico` - Histórico acadêmico
- `PUT /api/v1/discentes/{id}` - Atualizar
- `PATCH /api/v1/discentes/{id}/orientador` - Alterar orientador
- `PATCH /api/v1/discentes/{id}/status` - Alterar status

**Business Rules:**
- Matrícula única por programa
- Orientador deve ser docente do programa
- Validar prazo máximo (24 meses mestrado, 48 doutorado)
- Créditos mínimos (24 mestrado, 48 doutorado)

#### 4.3.3 Disciplina [❌ PENDENTE]
**Entidade:** `Disciplina`
**Relacionamentos:**
- N:1 com `Programa`
- 1:N com `OfertaDisciplina`

**Endpoints:**
- `POST /api/v1/disciplinas` - Criar disciplina
- `GET /api/v1/disciplinas` - Listar todas
- `GET /api/v1/disciplinas/{id}` - Buscar por ID
- `GET /api/v1/disciplinas/codigo/{codigo}` - Buscar por código
- `GET /api/v1/programas/{programaId}/disciplinas` - Disciplinas de um programa
- `PUT /api/v1/disciplinas/{id}` - Atualizar
- `DELETE /api/v1/disciplinas/{id}` - Deletar

**Business Rules:**
- Código único por programa
- Créditos = carga horária / 15
- Validar pré-requisitos (disciplinas devem existir)

#### 4.3.4 OfertaDisciplina [❌ PENDENTE]
**Entidade:** `OfertaDisciplina`
**Relacionamentos:**
- N:1 com `Disciplina`
- N:1 com `Docente` (responsável)
- 1:N com `MatriculaDisciplina`

**Endpoints:**
- `POST /api/v1/ofertas` - Criar oferta
- `GET /api/v1/ofertas` - Listar ofertas
- `GET /api/v1/ofertas/{id}` - Buscar por ID
- `GET /api/v1/ofertas/periodo/{ano}/{semestre}` - Ofertas de um período
- `GET /api/v1/disciplinas/{disciplinaId}/ofertas` - Ofertas de uma disciplina
- `PUT /api/v1/ofertas/{id}` - Atualizar
- `PATCH /api/v1/ofertas/{id}/abrir` - Abrir matrículas
- `PATCH /api/v1/ofertas/{id}/fechar` - Fechar matrículas
- `DELETE /api/v1/ofertas/{id}` - Cancelar oferta

**Business Rules:**
- Disciplina + Ano + Semestre + Turma = único
- Vagas mínimas e máximas
- Docente deve estar ativo no programa
- Data fim > data início

#### 4.3.5 MatriculaDisciplina [❌ PENDENTE]
**Entidade:** `MatriculaDisciplina`
**Relacionamentos:**
- N:1 com `Discente`
- N:1 com `OfertaDisciplina`

**Endpoints:**
- `POST /api/v1/matriculas` - Matricular em disciplina
- `GET /api/v1/matriculas` - Listar matrículas
- `GET /api/v1/matriculas/{id}` - Buscar por ID
- `GET /api/v1/discentes/{discenteId}/matriculas` - Matrículas de um discente
- `GET /api/v1/ofertas/{ofertaId}/matriculas` - Matrículas em uma oferta
- `PATCH /api/v1/matriculas/{id}/avaliacoes` - Lançar avaliações
- `PATCH /api/v1/matriculas/{id}/nota-final` - Lançar nota final
- `PATCH /api/v1/matriculas/{id}/trancar` - Trancar disciplina
- `DELETE /api/v1/matriculas/{id}` - Cancelar matrícula

**Business Rules:**
- Discente não pode matricular 2x na mesma oferta
- Validar vagas disponíveis (lock pessimista)
- Frequência mínima 75%
- Nota mínima 7.0 para aprovação

#### 4.3.6 TrabalhoConclusao [❌ PENDENTE]
**Entidade:** `TrabalhoConclusao`
**Relacionamentos:**
- 1:1 com `Discente`
- N:1 com `Docente` (orientador)
- 1:N com `Banca`

**Endpoints:**
- `POST /api/v1/trabalhos` - Cadastrar trabalho
- `GET /api/v1/trabalhos` - Listar trabalhos
- `GET /api/v1/trabalhos/{id}` - Buscar por ID
- `GET /api/v1/discentes/{discenteId}/trabalho` - Trabalho de um discente
- `GET /api/v1/trabalhos/search?q={termo}` - Buscar por termo
- `PUT /api/v1/trabalhos/{id}` - Atualizar
- `POST /api/v1/trabalhos/{id}/upload` - Upload do PDF
- `GET /api/v1/trabalhos/{id}/download` - Download do PDF

**Business Rules:**
- Um discente tem 1 trabalho
- Validar qualificação antes de defesa
- Upload de PDF obrigatório para defesa

#### 4.3.7 Banca [❌ PENDENTE]
**Entidade:** `Banca`
**Relacionamentos:**
- N:1 com `TrabalhoConclusao`
- N:1 com `Discente`
- N:1 com `Docente` (presidente)
- 1:N com `MembroBanca`

**Endpoints:**
- `POST /api/v1/bancas` - Agendar banca
- `GET /api/v1/bancas` - Listar bancas
- `GET /api/v1/bancas/{id}` - Buscar por ID
- `GET /api/v1/trabalhos/{trabalhoId}/bancas` - Bancas de um trabalho
- `PUT /api/v1/bancas/{id}` - Atualizar
- `PATCH /api/v1/bancas/{id}/resultado` - Registrar resultado
- `DELETE /api/v1/bancas/{id}` - Cancelar banca

**Business Rules:**
- Qualificação: mínimo 3 membros (1 externo)
- Defesa: mínimo 3 membros (1 externo)
- Presidente deve ser orientador
- Data agendada > hoje + 15 dias

#### 4.3.8 MembroBanca [❌ PENDENTE]
**Entidade:** `MembroBanca`
**Relacionamentos:**
- N:1 com `Banca`
- N:1 com `Docente` (se interno)

**Endpoints:**
- `POST /api/v1/bancas/{bancaId}/membros` - Adicionar membro
- `GET /api/v1/bancas/{bancaId}/membros` - Listar membros
- `PUT /api/v1/membros-banca/{id}` - Atualizar membro
- `DELETE /api/v1/membros-banca/{id}` - Remover membro

**Business Rules:**
- Mínimo 3 membros
- Máximo 5 membros
- Pelo menos 1 externo
- Presidente é sempre o primeiro

---

## 5. ORDEM DE IMPLEMENTAÇÃO

### FASE 1 - FUNDAÇÃO (Semana 1-2) 🔴 ALTA PRIORIDADE

#### Sprint 1.1 - Infraestrutura Base
**Duração:** 3 dias
**Objetivos:**
- ✅ Reestruturar packages (package by feature)
- ✅ Adicionar dependências necessárias
- ✅ Configurar Spring Security
- ✅ Configurar Flyway (migrations)
- ✅ Criar scripts SQL de criação do schema
- ✅ Criar índices e triggers

**Entregas:**
- [ ] `pom.xml` atualizado
- [ ] Estrutura de packages reorganizada
- [ ] `SecurityConfig.java`
- [ ] `V1__initial_schema.sql`
- [ ] `V2__add_indexes.sql`
- [ ] `V3__add_triggers.sql`

#### Sprint 1.2 - Módulo AUTH (Core)
**Duração:** 4 dias
**Objetivos:**
- ✅ Implementar `Usuario` (model, dto, repository, service)
- ✅ Implementar `Role` (model, repository, service)
- ✅ Implementar `AuthService` (login, register, JWT)
- ✅ Implementar `JwtTokenProvider`
- ✅ Implementar `UserDetailsServiceImpl`
- ✅ Implementar `AuthController`

**Entregas:**
- [ ] Endpoints de autenticação funcionando
- [ ] JWT gerado e validado
- [ ] Testes unitários do AuthService
- [ ] Documentação Swagger do /auth

### FASE 2 - CORE COMPLETO (Semana 3) 🟡 MÉDIA PRIORIDADE

#### Sprint 2.1 - Programa e Linha Pesquisa
**Duração:** 3 dias
**Objetivos:**
- ✅ Implementar `Programa` completo (CRUD)
- ✅ Implementar `LinhaPesquisa` completo (CRUD)
- ✅ Implementar relacionamentos com `Instituicao`
- ✅ Implementar relacionamentos entre eles

**Entregas:**
- [ ] Endpoints de Programa funcionando
- [ ] Endpoints de Linha Pesquisa funcionando
- [ ] Testes de integração
- [ ] Validações de negócio

#### Sprint 2.2 - Vinculações e Permissões
**Duração:** 2 dias
**Objetivos:**
- ✅ Implementar `UsuarioProgramaRole`
- ✅ Implementar verificação de permissões por endpoint
- ✅ Implementar `@PreAuthorize` nos controllers
- ✅ Implementar filtros de dados por programa

**Entregas:**
- [ ] Permissões funcionando
- [ ] Usuário vê apenas dados do seu programa
- [ ] Testes de autorização

#### Sprint 2.3 - Auditoria
**Duração:** 2 dias
**Objetivos:**
- ✅ Implementar `AuditLog` (model, repository)
- ✅ Implementar `AuditService`
- ✅ Implementar `@Aspect` para captura automática
- ✅ Implementar endpoints de consulta de logs

**Entregas:**
- [ ] Logs automáticos funcionando
- [ ] Endpoints de consulta de logs
- [ ] Testes

### FASE 3 - ACADEMIC (Semana 4-5) 🟢 MÉDIA-BAIXA PRIORIDADE

#### Sprint 3.1 - Docentes e Discentes
**Duração:** 4 dias
**Objetivos:**
- ✅ Implementar `Docente` completo
- ✅ Implementar `Discente` completo
- ✅ Implementar relacionamentos com `Usuario`
- ✅ Implementar regras de negócio (orientações, prazos)

**Entregas:**
- [ ] CRUD de Docentes
- [ ] CRUD de Discentes
- [ ] Histórico acadêmico (VIEW)
- [ ] Testes

#### Sprint 3.2 - Disciplinas e Ofertas
**Duração:** 3 dias
**Objetivos:**
- ✅ Implementar `Disciplina` completo
- ✅ Implementar `OfertaDisciplina` completo
- ✅ Implementar `MatriculaDisciplina` completo
- ✅ Implementar regras de vagas e lock pessimista

**Entregas:**
- [ ] CRUD de Disciplinas
- [ ] CRUD de Ofertas
- [ ] Sistema de matrículas funcionando
- [ ] Controle de vagas
- [ ] Testes

#### Sprint 3.3 - Trabalhos e Bancas
**Duração:** 4 dias
**Objetivos:**
- ✅ Implementar `TrabalhoConclusao` completo
- ✅ Implementar `Banca` completo
- ✅ Implementar `MembroBanca` completo
- ✅ Implementar upload/download de PDFs
- ✅ Implementar workflow de qualificação → defesa

**Entregas:**
- [ ] CRUD de Trabalhos
- [ ] CRUD de Bancas
- [ ] Upload de arquivos
- [ ] Workflow completo
- [ ] Testes

### FASE 4 - INTEGRAÇÕES E MELHORIAS (Semana 6) 🔵 BAIXA PRIORIDADE

#### Sprint 4.1 - Integração OpenAlex
**Duração:** 2 dias
**Objetivos:**
- ✅ Implementar `OpenAlexClient`
- ✅ Implementar sincronização de métricas de docentes
- ✅ Implementar busca de trabalhos por DOI
- ✅ Implementar cache de respostas

**Entregas:**
- [ ] Integração funcionando
- [ ] Endpoint de sincronização manual
- [ ] Job agendado (semanal)
- [ ] Testes com mocks

#### Sprint 4.2 - Dashboards e Relatórios
**Duração:** 2 dias
**Objetivos:**
- ✅ Implementar VIEWs materializadas
- ✅ Implementar endpoints de estatísticas
- ✅ Implementar relatórios em PDF
- ✅ Implementar export CSV/Excel

**Entregas:**
- [ ] Dashboard do programa
- [ ] Relatórios de produção
- [ ] Export de dados
- [ ] Testes

#### Sprint 4.3 - Testes e CI/CD
**Duração:** 3 dias
**Objetivos:**
- ✅ Completar cobertura de testes (>80%)
- ✅ Configurar GitHub Actions
- ✅ Configurar SonarQube
- ✅ Configurar Docker Compose
- ✅ Documentar deployment

**Entregas:**
- [ ] Testes unitários completos
- [ ] Testes de integração completos
- [ ] Pipeline CI/CD funcionando
- [ ] Docker Compose pronto
- [ ] Documentação de deploy

---

## 6. DEPENDÊNCIAS

### 6.1 Dependências Maven a Adicionar

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JSON Web Token) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Flyway Database Migrations -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Spring AOP (para Auditoria) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- Apache POI (para export Excel) -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>

<!-- iText (para PDF) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>8.0.2</version>
    <type>pom</type>
</dependency>

<!-- Feign Client (para integrações) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
    <version>4.1.0</version>
</dependency>

<!-- Cache (Caffeine) -->
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- Actuator (monitoramento) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer (métricas) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 6.2 Dependências de Desenvolvimento

```xml
<!-- REST Assured (testes de API) -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers (testes com PostgreSQL) -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

---

## 7. PADRÕES E CONVENÇÕES

### 7.1 Nomenclatura

**Packages:**
```
br.edu.ppg.hub.{domain}.{layer}.{feature}
```

**Classes:**
- Entities: `{Nome}.java` (ex: `Programa.java`)
- DTOs: `{Nome}{Tipo}DTO.java` (ex: `ProgramaCreateDTO.java`)
- Services: `{Nome}Service.java` (ex: `ProgramaService.java`)
- Controllers: `{Nome}Controller.java` (ex: `ProgramaController.java`)
- Repositories: `{Nome}Repository.java` (ex: `ProgramaRepository.java`)
- Mappers: `{Nome}Mapper.java` (ex: `ProgramaMapper.java`)

**Métodos:**
- CRUD: `create()`, `findById()`, `findAll()`, `update()`, `delete()`
- Queries: `findBy{Campo}()`, `existsBy{Campo}()`, `countBy{Campo}()`
- Business: verbo + substantivo (ex: `alterarOrientador()`, `matricularEmDisciplina()`)

**Endpoints:**
- Base: `/api/v1/{recurso-plural}`
- CRUD: `GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`
- Ações: `PATCH /{id}/{acao}` (ex: `/api/v1/programas/1/ativar`)
- Relacionamentos: `GET /{id}/{recurso}` (ex: `/api/v1/programas/1/docentes`)

### 7.2 Status HTTP

| Operação | Sucesso | Erro |
|----------|---------|------|
| GET (existente) | 200 OK | 404 Not Found |
| GET (lista vazia) | 200 OK (array vazio) | - |
| POST | 201 Created | 400 Bad Request, 409 Conflict |
| PUT | 200 OK | 400 Bad Request, 404 Not Found |
| PATCH | 200 OK | 400 Bad Request, 404 Not Found |
| DELETE | 204 No Content | 404 Not Found |
| Login | 200 OK | 401 Unauthorized |
| Sem permissão | - | 403 Forbidden |
| Erro servidor | - | 500 Internal Server Error |

### 7.3 Estrutura de Response

**Sucesso (Objeto):**
```json
{
  "id": 1,
  "nome": "Programa de Pós-Graduação em Ciência da Computação",
  "sigla": "PPGCC",
  "instituicao": {
    "id": 1,
    "nome": "UEPB"
  },
  "created_at": "2024-01-15T10:30:00",
  "updated_at": "2024-01-15T10:30:00"
}
```

**Sucesso (Lista Paginada):**
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

**Erro (Validação):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Erro de validação nos campos fornecidos",
  "path": "/api/v1/programas",
  "errors": {
    "codigoCapes": "Código CAPES é obrigatório",
    "nome": "Nome deve ter entre 5 e 255 caracteres"
  }
}
```

**Erro (Negócio):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Business Rule Violation",
  "message": "Docente já atingiu o limite máximo de orientandos",
  "path": "/api/v1/discentes"
}
```

### 7.4 Validações

**Bean Validation:**
- `@NotNull` - Campo obrigatório
- `@NotBlank` - String não vazia
- `@Size(min, max)` - Tamanho
- `@Email` - Email válido
- `@Pattern(regex)` - Regex
- `@Min/@Max` - Valores numéricos
- `@Past/@Future` - Datas

**Validadores Customizados:**
- `@ValidCNPJ` - Valida CNPJ
- `@ValidCPF` - Valida CPF
- `@ValidCodigo` - Valida código alfanumérico
- `@ValidPassword` - Valida senha forte

### 7.5 Transações

```java
@Service
@Transactional(readOnly = true) // Padrão: leitura
public class ProgramaService {

    @Transactional // Escrita: sobrescreve o padrão
    public ProgramaResponseDTO create(ProgramaCreateDTO dto) {
        // ...
    }

    public ProgramaResponseDTO findById(Long id) {
        // readonly transaction
    }
}
```

### 7.6 Logs

**Níveis:**
- `ERROR` - Erros críticos (exceções não tratadas)
- `WARN` - Avisos (validações de negócio falhadas)
- `INFO` - Eventos importantes (login, criação de entidades)
- `DEBUG` - Informações de debug (queries SQL)
- `TRACE` - Detalhes extremos (não usar em produção)

**Formato:**
```java
log.info("Criando programa: {} para instituição: {}", dto.getNome(), dto.getInstituicaoId());
log.warn("Tentativa de login com email inexistente: {}", email);
log.error("Erro ao processar matrícula: {}", e.getMessage(), e);
```

### 7.7 Testes

**Nomenclatura:**
- Unit tests: `{Class}Test.java`
- Integration tests: `{Feature}IntegrationTest.java`
- Métodos: `should{Action}When{Condition}()` ou `{method}_{scenario}_{expectedResult}()`

**Exemplo:**
```java
@Test
void shouldCreateProgramaWhenValidData() {
    // given
    ProgramaCreateDTO dto = ...;

    // when
    ProgramaResponseDTO result = service.create(dto);

    // then
    assertThat(result.getId()).isNotNull();
    assertThat(result.getNome()).isEqualTo(dto.getNome());
}
```

**Cobertura Mínima:**
- Services: 90%
- Controllers: 80%
- Repositories: 70%
- Global: 80%

---

## 8. CRONOGRAMA

### Timeline Geral (6 semanas)

```
SEMANA 1-2: FASE 1 - FUNDAÇÃO
├── Sprint 1.1: Infraestrutura Base (3 dias)
└── Sprint 1.2: Módulo AUTH (4 dias)

SEMANA 3: FASE 2 - CORE COMPLETO
├── Sprint 2.1: Programa e Linha Pesquisa (3 dias)
├── Sprint 2.2: Vinculações e Permissões (2 dias)
└── Sprint 2.3: Auditoria (2 dias)

SEMANA 4-5: FASE 3 - ACADEMIC
├── Sprint 3.1: Docentes e Discentes (4 dias)
├── Sprint 3.2: Disciplinas e Ofertas (3 dias)
└── Sprint 3.3: Trabalhos e Bancas (4 dias)

SEMANA 6: FASE 4 - INTEGRAÇÕES E MELHORIAS
├── Sprint 4.1: Integração OpenAlex (2 dias)
├── Sprint 4.2: Dashboards e Relatórios (2 dias)
└── Sprint 4.3: Testes e CI/CD (3 dias)
```

### Estimativas por Módulo

| Módulo | Entidades | Endpoints | Esforço | Prioridade |
|--------|-----------|-----------|---------|------------|
| AUTH | 5 | ~20 | 4 dias | 🔴 ALTA |
| CORE (Programa) | 1 | ~8 | 2 dias | 🟡 MÉDIA |
| CORE (Linha) | 1 | ~5 | 1 dia | 🟡 MÉDIA |
| ACADEMIC (Docente) | 1 | ~8 | 2 dias | 🟢 MÉDIA |
| ACADEMIC (Discente) | 1 | ~10 | 2 dias | 🟢 MÉDIA |
| ACADEMIC (Disciplina) | 1 | ~7 | 1.5 dias | 🟢 MÉDIA |
| ACADEMIC (Oferta) | 1 | ~9 | 1.5 dias | 🟢 MÉDIA |
| ACADEMIC (Matrícula) | 1 | ~8 | 2 dias | 🟢 MÉDIA |
| ACADEMIC (Trabalho) | 1 | ~8 | 2 dias | 🟢 MÉDIA |
| ACADEMIC (Banca) | 2 | ~8 | 2 dias | 🟢 MÉDIA |
| Integrações | - | ~5 | 2 dias | 🔵 BAIXA |
| **TOTAL** | **16** | **~100** | **~30 dias** | |

---

## 9. CHECKLIST DE IMPLEMENTAÇÃO

### 9.1 Por Módulo

Cada módulo deve seguir este checklist:

#### Domínio (Domain Layer)
- [ ] Criar entity `{Nome}.java` com JPA annotations
- [ ] Criar enums necessários
- [ ] Criar value objects (se necessário)
- [ ] Adicionar validações Bean Validation
- [ ] Adicionar timestamps e auditoria
- [ ] Documentar com JavaDoc

#### Aplicação (Application Layer)
- [ ] Criar `{Nome}CreateDTO.java`
- [ ] Criar `{Nome}UpdateDTO.java`
- [ ] Criar `{Nome}ResponseDTO.java`
- [ ] Criar `{Nome}Mapper.java` com MapStruct
- [ ] Criar `{Nome}Service.java` com business logic
- [ ] Adicionar `@Transactional` adequadamente
- [ ] Implementar validações de negócio
- [ ] Adicionar logs

#### Infraestrutura (Infrastructure Layer)
- [ ] Criar `{Nome}Repository.java`
- [ ] Adicionar queries customizadas (se necessário)
- [ ] Criar índices no banco (migration)
- [ ] Adicionar foreign keys e constraints

#### Apresentação (Presentation Layer)
- [ ] Criar `{Nome}Controller.java`
- [ ] Adicionar annotations OpenAPI
- [ ] Implementar todos os endpoints
- [ ] Adicionar `@PreAuthorize` para segurança
- [ ] Validar DTOs com `@Valid`
- [ ] Retornar status HTTP corretos

#### Testes
- [ ] Criar `{Nome}ServiceTest.java` (unit)
- [ ] Criar `{Nome}RepositoryTest.java` (integration)
- [ ] Criar `{Nome}ControllerTest.java` (integration)
- [ ] Cobertura > 80%
- [ ] Testar casos de erro
- [ ] Testar validações

#### Documentação
- [ ] Atualizar README.md
- [ ] Documentar endpoints no Swagger
- [ ] Adicionar exemplos de request/response
- [ ] Documentar regras de negócio

### 9.2 Checklist Geral do Projeto

#### Fundação
- [ ] Reestruturar packages
- [ ] Adicionar todas as dependências necessárias
- [ ] Configurar Spring Security
- [ ] Configurar JWT
- [ ] Configurar Flyway
- [ ] Criar migrations do schema completo
- [ ] Adicionar índices e triggers
- [ ] Configurar application-{env}.yml

#### Segurança
- [ ] Implementar login/register
- [ ] Implementar refresh token
- [ ] Implementar recuperação de senha
- [ ] Implementar verificação de email
- [ ] Implementar rate limiting
- [ ] Implementar CORS adequadamente
- [ ] Configurar HTTPS (produção)
- [ ] Implementar auditoria automática

#### Core Modules
- [ ] Implementar Programa (completo)
- [ ] Implementar Linha Pesquisa (completo)
- [ ] Implementar vinculações (usuário-programa-role)
- [ ] Implementar controle de permissões
- [ ] Implementar filtros por programa

#### Academic Modules
- [ ] Implementar Docente (completo)
- [ ] Implementar Discente (completo)
- [ ] Implementar Disciplina (completo)
- [ ] Implementar Oferta Disciplina (completo)
- [ ] Implementar Matrícula (completo)
- [ ] Implementar Trabalho Conclusão (completo)
- [ ] Implementar Banca (completo)
- [ ] Implementar upload/download de arquivos

#### Integrações
- [ ] Implementar OpenAlex client
- [ ] Implementar sincronização de métricas
- [ ] Implementar cache de respostas
- [ ] Implementar job agendado

#### Relatórios
- [ ] Implementar dashboard do programa
- [ ] Implementar relatórios em PDF
- [ ] Implementar export CSV/Excel
- [ ] Criar VIEWs materializadas

#### Testes
- [ ] Testes unitários completos (>90% services)
- [ ] Testes de integração (>80% controllers)
- [ ] Testes de repositórios
- [ ] Testes de segurança
- [ ] Testes de performance (básicos)

#### CI/CD
- [ ] Configurar GitHub Actions
- [ ] Configurar SonarQube
- [ ] Configurar Docker
- [ ] Configurar Docker Compose
- [ ] Criar pipeline de deploy

#### Documentação
- [ ] README.md completo
- [ ] DOCUMENTATION.md técnico
- [ ] API.md com endpoints
- [ ] DEPLOYMENT.md
- [ ] Swagger UI completo
- [ ] Postman collection

#### Produção
- [ ] Configurar variáveis de ambiente
- [ ] Configurar backup do banco
- [ ] Configurar monitoramento (Actuator + Prometheus)
- [ ] Configurar logs centralizados
- [ ] Configurar alertas
- [ ] Testes de carga
- [ ] Security scan
- [ ] Review de código completo

---

## 10. MÉTRICAS DE SUCESSO

### 10.1 Métricas Técnicas
- ✅ Cobertura de testes > 80%
- ✅ Tempo de resposta médio < 200ms
- ✅ Zero vulnerabilidades críticas (SonarQube)
- ✅ Code smells < 50
- ✅ Technical debt < 5%
- ✅ Documentação completa (100% dos endpoints)

### 10.2 Métricas Funcionais
- ✅ 100% dos endpoints implementados
- ✅ 100% das regras de negócio validadas
- ✅ Autenticação e autorização funcionando
- ✅ Auditoria completa
- ✅ Upload/Download funcionando
- ✅ Integrações externas estáveis

### 10.3 Métricas de Qualidade
- ✅ Zero bugs críticos
- ✅ Zero bugs de segurança
- ✅ Code review aprovado
- ✅ Padrões seguidos (SOLID, Clean Code)
- ✅ Performance aceitável (< 200ms)

---

## 11. RISCOS E MITIGAÇÕES

| Risco | Probabilidade | Impacto | Mitigação |
|-------|---------------|---------|-----------|
| Complexidade do schema | Alta | Alto | Implementação incremental, foco no MVP |
| Problemas de performance | Média | Alto | Índices adequados, queries otimizadas, cache |
| Segurança (JWT, senhas) | Baixa | Crítico | Usar bibliotecas consolidadas, security review |
| Integrações externas instáveis | Média | Médio | Circuit breaker, fallbacks, cache |
| Falta de testes | Média | Alto | TDD, cobertura obrigatória > 80% |
| Prazo apertado | Alta | Médio | Priorização (MVP primeiro), cortes de escopo |
| Bugs em produção | Média | Alto | Testes abrangentes, staging environment |

---

## 12. PRÓXIMOS PASSOS IMEDIATOS

### Para Começar AGORA:

1. **Atualizar pom.xml**
   - Adicionar dependências de Spring Security, JWT, Flyway, etc.
   - Executar `mvn clean install`

2. **Reestruturar packages**
   - Mover classes existentes para `core/`
   - Criar estrutura de `auth/` e `academic/`

3. **Criar migrations do banco**
   - Criar `V1__initial_schema.sql` com todo o schema
   - Criar `V2__add_indexes.sql`
   - Criar `V3__add_triggers.sql`

4. **Implementar Spring Security básico**
   - Criar `SecurityConfig.java`
   - Criar `JwtTokenProvider.java`
   - Criar filtro de autenticação

5. **Implementar entidade Usuario**
   - Model, DTO, Repository, Service
   - Hash de senha com BCrypt
   - Validações

6. **Implementar AuthController**
   - Endpoint de login
   - Endpoint de register
   - Geração de JWT

---

## 13. CONCLUSÃO

Este plano cobre a implementação completa de uma REST API robusta, escalável e segura para gestão de múltiplos programas de pós-graduação. A arquitetura proposta segue as melhores práticas de Clean Architecture e SOLID, garantindo um código manutenível e testável.

**Estimativa Total:** 30 dias úteis (6 semanas)
**Entregas Principais:** 16 entidades, ~100 endpoints, sistema completo de autenticação, auditoria e relatórios
**Próximo Passo:** Começar pela FASE 1 (Fundação e AUTH)

---

**Documento vivo - atualizar conforme o progresso do projeto**
