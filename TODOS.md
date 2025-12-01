# TODO List - PPG Analytics Hub

## CRÍTICOS - Autenticação e Autorização (MVP Core)

### 🔐 Implementar Sistema de Autenticação JWT
- [ ] **Criar `app/core/security.py`** com funções JWT
  - Gerar/validar tokens JWT
  - Hash/verificação de senhas (já existe no UsuarioService)
  - Configurar chaves RS256 (conforme overview.md)
  - Funções: `create_access_token()`, `verify_token()`, `get_current_user()`

- [ ] **Criar `app/api/routes/auth.py`** 
  - Endpoint `/auth/login` (POST) - autentica e retorna JWT
  - Endpoint `/auth/refresh` (POST) - renova token
  - Endpoint `/auth/logout` (POST) - invalida token
  - Endpoint `/auth/me` (GET) - dados do usuário logado

- [ ] **Atualizar `app/deps.py`** 
  - Adicionar `get_current_user()` dependency
  - Adicionar `get_current_active_user()` dependency
  - Implementar verificação de JWT em requests

### 🛡️ Implementar RBAC (Role-Based Access Control)
- [ ] **Criar sistema de permissões**
  - Definir permissões por role no modelo Role
  - Implementar decorators/dependencies para verificar permissões
  - Criar `@require_permission("read:usuarios")` decorator

- [ ] **Proteger endpoints existentes**
  - Adicionar autenticação obrigatória em todas as rotas (exceto login)
  - Implementar verificação de permissões por operação
  - Configurar hierarquia de roles (superadmin > coordenador > docente > discente)

- [ ] **Implementar multi-tenancy por programa**
  - Usuários só podem acessar dados dos programas aos quais estão vinculados
  - Validar acesso baseado em `UsuarioProgramaRole`
  - Filtrar dados automaticamente por programa do usuário

## ESTRUTURAIS - Completar Entidades do MVP

### 📝 Implementar PATCH para todas as entidades
- [ ] **Instituições** - adicionar endpoint PATCH
- [ ] **Programas** - adicionar endpoint PATCH 
- [ ] **Docentes** - adicionar endpoint PATCH
- [ ] **Usuários** - adicionar endpoint PATCH
- [ ] **Roles** - adicionar endpoint PATCH

### 🎓 Completar entidades do schema Academic
- [ ] **Discentes**
  - Corrigir ForeignKey em `app/models/discente.py` (schema auth.usuarios)
  - Criar schemas completos em `app/schemas/discente.py`
  - Implementar repository em `app/repositories/discente_repo.py`
  - Implementar service em `app/services/discente_service.py`
  - Criar rotas em `app/api/routes/discentes.py`

- [ ] **Linhas de Pesquisa** 
  - Criar schemas em `app/schemas/linha_pesquisa.py`
  - Implementar repository e service
  - Criar rotas CRUD completas
  - Implementar relacionamento com docentes/discentes

### 📚 Implementar entidades pendentes do schema Academic
- [ ] **Disciplinas**
  - Criar modelo `app/models/disciplina.py`
  - Implementar CRUD completo
  - Relacionamento com programas

- [ ] **Trabalhos de Conclusão**
  - Criar modelo `app/models/trabalho_conclusao.py`  
  - Campos: título, tipo (mestrado/doutorado), orientador, discente, data_defesa
  - Status: em_andamento, qualificado, defendido

- [ ] **Bancas**
  - Criar modelo `app/models/banca.py`
  - Modelo `app/models/membro_banca.py`
  - Relacionamento com trabalhos e docentes

### ⚙️ Infraestrutura e Integrações

- [ ] **OpenAlex Integration**
  - Instalar PyAlex: `pip install pyalex`
  - Criar `app/services/openalex_service.py`
  - Implementar sincronização de dados institucionais
  - Criar job de sync para métricas de docentes
  - Endpoint `/sync/openalex` para trigger manual

- [ ] **Health Check e Monitoring**
  - Completar endpoint `/healthz` 
  - Implementar `/readyz` (readiness probe)
  - Adicionar logging estruturado JSON
  - Configurar request_id middleware

- [ ] **Migrações de Banco**
  - Configurar Alembic adequadamente
  - Migrar de `metadata.create_all()` para migrations versionadas
  - Criar migration inicial a partir do estado atual

## TESTES - Cobertura e Qualidade

### 🧪 Testes Unitários - Camada de Repositório
- [ ] **Teste para UsuarioRepository** - `tests/unit/test_usuario_repo.py`
  - Testar CRUD operations
  - Testar filtros (ativo/inativo)
  - Testar busca por email
  - Mock da Session do SQLAlchemy

- [ ] **Teste para InstituicaoRepository** - `tests/unit/test_instituicao_repo.py`
  - Testar unique constraints (codigo, sigla)
  - Testar paginação
  - Testar update_replace

- [ ] **Teste para ProgramaRepository** - `tests/unit/test_programa_repo.py`
  - Testar relacionamento com instituições
  - Testar constraints CAPES (1-7)
  - Testar JSONB configurações

- [ ] **Teste para DocenteRepository** - `tests/unit/test_docente_repo.py`
  - Testar unique constraint (usuario_id, programa_id)
  - Testar métricas acadêmicas
  - Testar relacionamentos

### 🔧 Testes Unitários - Camada de Serviço
- [ ] **Teste para UsuarioService** - `tests/unit/test_usuario_service.py`
  - Testar hash/verificação de senhas
  - Testar criação de usuário (senha -> senha_hash)
  - Testar autenticação (authenticate method)
  - Mock do Repository

- [ ] **Teste para InstituicaoService** - `tests/unit/test_instituicao_service.py`
  - Testar regras de negócio
  - Testar tratamento de IntegrityError -> 409
  - Testar PUT operations

- [ ] **Teste para ProgramaService** - `tests/unit/test_programa_service.py`
  - Testar validações de conceito CAPES
  - Testar multi-tenancy rules
  - Mock de dependencies

### 🔐 Testes Unitários - Autenticação/Segurança
- [ ] **Teste para app/core/security.py** - `tests/unit/test_security.py`
  - Testar criação/validação de JWT tokens
  - Testar expiração de tokens
  - Testar decode com chave inválida
  - Testar diferentes tipos de payload

- [ ] **Teste para RBAC** - `tests/unit/test_rbac.py`
  - Testar verificação de permissões
  - Testar hierarquia de roles
  - Testar multi-tenancy por programa
  - Mock de current_user

### 🌐 Testes de Integração - API Endpoints
- [ ] **Teste para /auth endpoints** - `tests/integration/test_auth_api.py`
  - Testar POST /auth/login (credenciais válidas/inválidas)
  - Testar GET /auth/me (com/sem token)
  - Testar POST /auth/refresh
  - Testar POST /auth/logout
  - Testar headers Authorization

- [ ] **Teste para endpoints protegidos** - `tests/integration/test_protected_endpoints.py`
  - Testar acesso sem token (401)
  - Testar acesso com token inválido (401)
  - Testar acesso com permissões insuficientes (403)
  - Testar acesso com token expirado

- [ ] **Teste para /usuarios endpoints** - `tests/integration/test_usuarios_api.py`
  - Testar CRUD completo com autenticação
  - Testar filtros e paginação
  - Testar PATCH operations
  - Testar validações de email único

- [ ] **Teste para /instituicoes endpoints** - `tests/integration/test_instituicoes_api.py`
  - Expandir testes existentes com autenticação
  - Testar PATCH operations
  - Testar conflicts (409) com diferentes roles

- [ ] **Teste para /programas endpoints** - `tests/integration/test_programas_api.py`
  - Expandir testes existentes com autenticação
  - Testar multi-tenancy (usuário só vê programas vinculados)
  - Testar PATCH operations

- [ ] **Teste para /docentes endpoints** - `tests/integration/test_docentes_api.py`
  - Testar CRUD completo
  - Testar relacionamentos (usuario_id, programa_id)
  - Testar validações de vínculo
  - Testar métricas acadêmicas

- [ ] **Teste para /discentes endpoints** - `tests/integration/test_discentes_api.py`
  - Testar CRUD completo (quando implementado)
  - Testar relacionamentos com programas/orientadores
  - Testar status de matrícula

### 🔗 Testes de Integração - Banco de Dados
- [ ] **Teste de transações** - `tests/integration/test_database_transactions.py`
  - Testar rollback em caso de erro
  - Testar commit/rollback manual
  - Testar concurrent access
  - Testar connection pooling

- [ ] **Teste de relacionamentos** - `tests/integration/test_relationships.py`
  - Testar cascade operations
  - Testar foreign key constraints
  - Testar lazy/eager loading
  - Testar back_populates

- [ ] **Teste de constraints** - `tests/integration/test_constraints.py`
  - Testar unique constraints
  - Testar check constraints (CAPES 1-7)
  - Testar not null constraints
  - Testar violation handling

### 🌍 Testes de Integração - Serviços Externos
- [ ] **Teste para OpenAlex Integration** - `tests/integration/test_openalex.py`
  - Testar PyAlex configuration
  - Testar fetch de dados institucionais
  - Testar sync de métricas de docentes
  - Mock de API calls para testes rápidos
  - Testar rate limiting e error handling

- [ ] **Teste para Health Checks** - `tests/integration/test_health.py`
  - Testar /healthz endpoint
  - Testar /readyz endpoint  
  - Testar database connectivity
  - Testar external services status

### 🎭 Testes End-to-End (E2E)
- [ ] **Fluxo completo de usuário** - `tests/e2e/test_user_journey.py`
  - Criar usuário -> Login -> Acessar dados -> Logout
  - Coordenador criar programa -> Vincular docentes -> Gerar relatório
  - Docente acessar apenas seus programas

- [ ] **Fluxo de sincronização** - `tests/e2e/test_sync_workflow.py`
  - Trigger sync OpenAlex -> Validar dados atualizados
  - Testar sync incremental vs completo

### � Testes de Performance
- [ ] **Load Testing** - `tests/performance/test_load.py`
  - Testar concurrent requests
  - Testar performance com muitos usuários
  - Benchmark de queries pesadas
  - Usar pytest-benchmark ou locust

- [ ] **Database Performance** - `tests/performance/test_db_performance.py`
  - Testar queries com grandes datasets
  - Testar índices e performance
  - Identificar N+1 query problems

### 🔧 Testes de Infraestrutura
- [ ] **Teste de configuração** - `tests/infrastructure/test_config.py`
  - Testar loading de .env
  - Testar diferentes environments (dev/staging/prod)
  - Testar configurações obrigatórias

- [ ] **Teste de logging** - `tests/infrastructure/test_logging.py`
  - Testar structured logging (JSON)
  - Testar request_id middleware
  - Testar different log levels

- [ ] **Teste de erro handling** - `tests/infrastructure/test_error_handling.py`
  - Testar exception handlers
  - Testar IntegrityError -> 409
  - Testar ValidationError -> 400
  - Testar unhandled exceptions -> 500

### 🧹 Infraestrutura de Testes - Melhorias
- [ ] **Fixtures avançadas** - `tests/fixtures/`
  - Factory functions para todas as entidades
  - Fixtures parametrizadas para diferentes scenarios
  - Cleanup automático entre testes
  - Database seeding para testes

- [ ] **Mocks e Stubs** - `tests/mocks/`
  - Mock para PyAlex API
  - Mock para external services
  - Stub para authentication em testes
  - Mock database para unit tests

- [ ] **Test Utilities** - `tests/utils/`
  - Helper functions para autenticação em testes
  - JWT token generation para testes
  - Database assertion helpers
  - HTTP client helpers

- [ ] **Continuous Integration**
  - Configurar GitHub Actions
  - Rodar testes em multiple Python versions
  - Coverage reporting
  - Parallel test execution
  - Test result reporting

### 📊 Cobertura e Métricas
- [ ] **Coverage Setup**
  - Configurar pytest-cov
  - Meta: >90% code coverage
  - Exclude patterns para arquivos de config
  - Coverage reports em CI/CD

- [ ] **Quality Gates**
  - Configurar quality thresholds
  - Fail build se coverage < 90%
  - Lint check obrigatório
  - Type checking com mypy

### 🔄 Testes de Regressão
- [ ] **Test Suite Regression** - `tests/regression/`
  - Golden master testing para outputs complexos
  - Snapshot testing para API responses
  - Database state regression tests
  - Performance regression detection

## FUTURO - Pós MVP

### 📊 Analytics e Relatórios
- [ ] **Views SQL para KPIs CAPES**
- [ ] **Integração com Gemini AI**
- [ ] **Dashboard endpoints** 
- [ ] **Relatórios automáticos**

### 🔗 Integrações Externas
- [ ] **n8n workflows**
- [ ] **Redis para cache**
- [ ] **Integração ORCID/Lattes**

## REFATORAÇÕES - Melhorias de Código

### 🧹 Code Quality
- [ ] **Adicionar comentários em inglês** em todo código (conforme copilot-instructions.md)
- [ ] **Revisar imports** - usar `from __future__ import annotations` consistentemente
- [ ] **Padronizar error handling** - usar HTTPException consistentemente
- [ ] **Validação de dados** - melhorar constraints e validações Pydantic

### 📋 Documentação
- [ ] **OpenAPI documentation** - adicionar descriptions e examples
- [ ] **README.md** - instruções de setup e desenvolvimento
- [ ] **API documentation** - exemplos de uso para cada endpoint