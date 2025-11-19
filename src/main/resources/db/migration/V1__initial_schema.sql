-- =====================================================
-- PPG HUB - MIGRATION V1 - INITIAL SCHEMA
-- Cria extensões, schemas CORE e AUTH
-- =====================================================

-- =====================================================
-- EXTENSÕES NECESSÁRIAS
-- =====================================================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "unaccent";
CREATE EXTENSION IF NOT EXISTS "btree_gin";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================
-- SCHEMA: CORE (Estrutura base multi-tenant)
-- =====================================================
CREATE SCHEMA IF NOT EXISTS core;

-- =====================================================
-- Tabela de Instituições
-- =====================================================
CREATE TABLE core.instituicoes (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nome_completo VARCHAR(500) NOT NULL,
    nome_abreviado VARCHAR(50) NOT NULL,
    sigla VARCHAR(10) NOT NULL,
    cnpj VARCHAR(18) UNIQUE,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('Federal', 'Estadual', 'Municipal', 'Privada')),
    natureza_juridica VARCHAR(100),
    endereco JSONB,
    contatos JSONB,
    redes_sociais JSONB,
    logo_url TEXT,
    website TEXT,
    fundacao DATE,
    openalex_institution_id TEXT,
    ror_id TEXT,
    ativo BOOLEAN DEFAULT TRUE,
    configuracoes JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE core.instituicoes IS 'Instituições de ensino superior (universidades, faculdades)';
COMMENT ON COLUMN core.instituicoes.codigo IS 'Código único da instituição (ex: UEPB, UFCG)';
COMMENT ON COLUMN core.instituicoes.tipo IS 'Tipo: Federal, Estadual, Municipal, Privada';

-- =====================================================
-- Tabela de Programas de Pós-Graduação
-- =====================================================
CREATE TABLE core.programas (
    id SERIAL PRIMARY KEY,
    instituicao_id INTEGER NOT NULL REFERENCES core.instituicoes(id) ON DELETE RESTRICT,
    codigo_capes VARCHAR(20) UNIQUE,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(20) NOT NULL,
    area_concentracao VARCHAR(255),
    nivel VARCHAR(50) NOT NULL CHECK (nivel IN ('Mestrado', 'Doutorado', 'Mestrado/Doutorado')),
    modalidade VARCHAR(50) DEFAULT 'Presencial' CHECK (modalidade IN ('Presencial', 'EAD', 'Semipresencial')),
    inicio_funcionamento DATE,
    conceito_capes INTEGER CHECK (conceito_capes >= 1 AND conceito_capes <= 7),
    data_ultima_avaliacao DATE,
    trienio_avaliacao VARCHAR(20),
    coordenador_id INTEGER,
    coordenador_adjunto_id INTEGER,
    mandato_inicio DATE,
    mandato_fim DATE,
    creditos_minimos_mestrado INTEGER DEFAULT 24,
    creditos_minimos_doutorado INTEGER DEFAULT 48,
    prazo_maximo_mestrado INTEGER DEFAULT 24,
    prazo_maximo_doutorado INTEGER DEFAULT 48,
    openalex_institution_id TEXT,
    status VARCHAR(50) DEFAULT 'Ativo' CHECK (status IN ('Ativo', 'Suspenso', 'Descredenciado')),
    configuracoes JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(instituicao_id, sigla),
    CHECK (mandato_fim IS NULL OR mandato_fim > mandato_inicio)
);

COMMENT ON TABLE core.programas IS 'Programas de pós-graduação (mestrado e/ou doutorado)';
COMMENT ON COLUMN core.programas.codigo_capes IS 'Código oficial da CAPES';
COMMENT ON COLUMN core.programas.conceito_capes IS 'Nota CAPES de 1 a 7';
COMMENT ON COLUMN core.programas.nivel IS 'Mestrado, Doutorado ou Mestrado/Doutorado';

-- =====================================================
-- Tabela de Linhas de Pesquisa
-- =====================================================
CREATE TABLE core.linhas_pesquisa (
    id SERIAL PRIMARY KEY,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE CASCADE,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    palavras_chave TEXT,
    coordenador_id INTEGER,
    ativa BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(programa_id, nome)
);

COMMENT ON TABLE core.linhas_pesquisa IS 'Linhas de pesquisa dos programas';

-- =====================================================
-- SCHEMA: AUTH (Autenticação e Autorização)
-- =====================================================
CREATE SCHEMA IF NOT EXISTS auth;

-- =====================================================
-- Tabela de Roles (Papéis no sistema)
-- =====================================================
CREATE TABLE auth.roles (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL,
    descricao TEXT,
    nivel_acesso INTEGER NOT NULL CHECK (nivel_acesso >= 1 AND nivel_acesso <= 5),
    permissoes JSONB DEFAULT '{}',
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE auth.roles IS 'Papéis/perfis de usuário no sistema';
COMMENT ON COLUMN auth.roles.nivel_acesso IS '1=baixo, 5=admin total';

-- =====================================================
-- Tabela principal de Usuários
-- =====================================================
CREATE TABLE auth.usuarios (
    id SERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE,
    nome_completo VARCHAR(255) NOT NULL,
    nome_preferido VARCHAR(100),
    email VARCHAR(255) UNIQUE NOT NULL,
    email_alternativo VARCHAR(255),
    telefone VARCHAR(20),
    cpf VARCHAR(14) UNIQUE,
    rg VARCHAR(20),
    passaporte VARCHAR(50),
    password_hash VARCHAR(500) NOT NULL,
    email_verificado BOOLEAN DEFAULT FALSE,
    email_verificado_em TIMESTAMP,
    data_nascimento DATE,
    genero VARCHAR(20),
    nacionalidade VARCHAR(50) DEFAULT 'Brasileira',
    naturalidade VARCHAR(100),
    endereco JSONB,
    orcid VARCHAR(100) UNIQUE,
    lattes_id VARCHAR(100),
    google_scholar_id VARCHAR(100),
    researchgate_id VARCHAR(100),
    linkedin VARCHAR(100),
    openalex_author_id TEXT,
    ultimo_sync_openalex TIMESTAMP,
    configuracoes JSONB DEFAULT '{}',
    preferencias JSONB DEFAULT '{}',
    avatar_url TEXT,
    biografia TEXT,
    ultimo_login TIMESTAMP,
    tentativas_login INTEGER DEFAULT 0,
    conta_bloqueada BOOLEAN DEFAULT FALSE,
    bloqueada_ate TIMESTAMP,
    reset_token VARCHAR(255),
    reset_token_expira TIMESTAMP,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    created_by INTEGER REFERENCES auth.usuarios(id),
    updated_by INTEGER REFERENCES auth.usuarios(id),
    CHECK (cpf IS NOT NULL OR passaporte IS NOT NULL)
);

COMMENT ON TABLE auth.usuarios IS 'Usuários do sistema (todos os perfis)';
COMMENT ON COLUMN auth.usuarios.password_hash IS 'Hash bcrypt da senha';
COMMENT ON COLUMN auth.usuarios.tentativas_login IS 'Contador de tentativas falhas (bloqueio após 5)';

-- =====================================================
-- Tabela de vinculações usuário-programa-role
-- =====================================================
CREATE TABLE auth.usuario_programa_roles (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES auth.usuarios(id) ON DELETE CASCADE,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
    data_vinculacao DATE DEFAULT CURRENT_DATE,
    data_desvinculacao DATE,
    status VARCHAR(50) DEFAULT 'Ativo' CHECK (status IN ('Ativo', 'Suspenso', 'Desligado')),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    created_by INTEGER REFERENCES auth.usuarios(id),
    UNIQUE(usuario_id, programa_id, role_id),
    CHECK (data_desvinculacao IS NULL OR data_desvinculacao >= data_vinculacao)
);

COMMENT ON TABLE auth.usuario_programa_roles IS 'Vinculação de usuários a programas com papéis específicos';

-- =====================================================
-- Tabela de Sessões (JWT tokens)
-- =====================================================
CREATE TABLE auth.sessoes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES auth.usuarios(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    refresh_token VARCHAR(500) UNIQUE,
    ip_address INET,
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE auth.sessoes IS 'Sessões ativas dos usuários (JWT tokens)';

-- =====================================================
-- Tabela de Logs de Auditoria
-- =====================================================
CREATE TABLE auth.audit_logs (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER REFERENCES auth.usuarios(id),
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(100),
    entidade_id INTEGER,
    dados_anteriores JSONB,
    dados_novos JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE auth.audit_logs IS 'Log de auditoria de todas as ações no sistema';

-- =====================================================
-- Adicionar FKs que referenciam auth.usuarios
-- =====================================================
ALTER TABLE core.programas
    ADD CONSTRAINT fk_coordenador
        FOREIGN KEY (coordenador_id) REFERENCES auth.usuarios(id) ON DELETE SET NULL;

ALTER TABLE core.programas
    ADD CONSTRAINT fk_coordenador_adjunto
        FOREIGN KEY (coordenador_adjunto_id) REFERENCES auth.usuarios(id) ON DELETE SET NULL;
