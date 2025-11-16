-- PPG Hub - Tabelas para integração OpenAlex
-- Tabelas para armazenar publicações, autores e dados bibliométricos

-- Tabela de Publicações (Works no OpenAlex)
CREATE TABLE publicacoes (
    id BIGSERIAL PRIMARY KEY,

    -- Identificadores
    openalex_work_id VARCHAR(50) UNIQUE NOT NULL,
    doi VARCHAR(255) UNIQUE,
    pmid VARCHAR(50),
    pmcid VARCHAR(50),

    -- Dados Básicos
    titulo TEXT NOT NULL,
    titulo_original TEXT,
    abstract TEXT,

    -- Publicação
    ano_publicacao INTEGER NOT NULL,
    data_publicacao DATE,
    tipo VARCHAR(50), -- article, book-chapter, dissertation, etc
    idioma VARCHAR(10),

    -- Fonte (Journal/Conference)
    fonte_nome VARCHAR(500),
    fonte_issn VARCHAR(20),
    fonte_openalex_id VARCHAR(50),
    volume VARCHAR(50),
    issue VARCHAR(50),
    pagina_inicial VARCHAR(20),
    pagina_final VARCHAR(20),

    -- Métricas
    cited_by_count INTEGER DEFAULT 0,
    is_retracted BOOLEAN DEFAULT FALSE,
    is_paratext BOOLEAN DEFAULT FALSE,
    is_oa BOOLEAN DEFAULT FALSE,

    -- OpenAlex Concepts/Topics
    concepts JSONB,
    topics JSONB,
    keywords JSONB,

    -- URLs
    landing_page_url VARCHAR(1000),
    pdf_url VARCHAR(1000),

    -- Metadata
    raw_openalex_data JSONB,
    last_sync_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT check_ano_publicacao CHECK (ano_publicacao >= 1900 AND ano_publicacao <= 2100)
);

-- Índices
CREATE INDEX idx_publicacoes_openalex_id ON publicacoes(openalex_work_id);
CREATE INDEX idx_publicacoes_doi ON publicacoes(doi);
CREATE INDEX idx_publicacoes_ano ON publicacoes(ano_publicacao);
CREATE INDEX idx_publicacoes_tipo ON publicacoes(tipo);
CREATE INDEX idx_publicacoes_cited_by ON publicacoes(cited_by_count DESC);
CREATE INDEX idx_publicacoes_concepts ON publicacoes USING gin(concepts);
CREATE INDEX idx_publicacoes_topics ON publicacoes USING gin(topics);

-- Tabela de Autoria (relacionamento Publicação-Docente)
CREATE TABLE autorias (
    id BIGSERIAL PRIMARY KEY,
    publicacao_id BIGINT NOT NULL,
    docente_id BIGINT,

    -- Informações do autor
    author_position VARCHAR(20), -- first, middle, last
    position_order INTEGER NOT NULL,
    is_corresponding BOOLEAN DEFAULT FALSE,

    -- Dados brutos (caso não tenha docente associado)
    raw_author_name VARCHAR(255),
    raw_author_openalex_id VARCHAR(50),
    raw_author_orcid VARCHAR(19),

    -- Afiliação conforme publicação
    instituicao_ids BIGINT[],
    raw_affiliations JSONB,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_autoria_publicacao FOREIGN KEY (publicacao_id)
        REFERENCES publicacoes(id) ON DELETE CASCADE,
    CONSTRAINT fk_autoria_docente FOREIGN KEY (docente_id)
        REFERENCES docentes(id) ON DELETE SET NULL
);

-- Índices
CREATE INDEX idx_autorias_publicacao ON autorias(publicacao_id);
CREATE INDEX idx_autorias_docente ON autorias(docente_id);
CREATE INDEX idx_autorias_position ON autorias(position_order);
CREATE INDEX idx_autorias_raw_openalex ON autorias(raw_author_openalex_id);

-- Tabela de log de sincronização OpenAlex
CREATE TABLE openalex_sync_log (
    id BIGSERIAL PRIMARY KEY,

    -- Identificação
    entity_type VARCHAR(50) NOT NULL, -- INSTITUTION, AUTHOR, WORK
    entity_id BIGINT,
    openalex_id VARCHAR(50) NOT NULL,

    -- Sincronização
    sync_type VARCHAR(50) NOT NULL, -- FULL, INCREMENTAL, RETRY
    sync_status VARCHAR(50) NOT NULL, -- PENDING, RUNNING, SUCCESS, FAILED, PARTIAL
    started_at TIMESTAMP NOT NULL,
    finished_at TIMESTAMP,
    duration_ms BIGINT,

    -- Resultados
    records_processed INTEGER DEFAULT 0,
    records_created INTEGER DEFAULT 0,
    records_updated INTEGER DEFAULT 0,
    records_failed INTEGER DEFAULT 0,

    -- Erros
    error_message TEXT,
    error_details JSONB,

    -- Metadata
    triggered_by VARCHAR(100), -- USER, SCHEDULER, API
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_sync_log_entity ON openalex_sync_log(entity_type, entity_id);
CREATE INDEX idx_sync_log_openalex_id ON openalex_sync_log(openalex_id);
CREATE INDEX idx_sync_log_status ON openalex_sync_log(sync_status);
CREATE INDEX idx_sync_log_started ON openalex_sync_log(started_at DESC);

-- Tabela de cache de respostas OpenAlex
CREATE TABLE openalex_cache (
    id BIGSERIAL PRIMARY KEY,

    -- Chave de cache
    cache_key VARCHAR(500) UNIQUE NOT NULL,
    endpoint_type VARCHAR(50) NOT NULL, -- INSTITUTION, AUTHOR, WORK, SEARCH

    -- Dados
    response_data JSONB NOT NULL,
    http_status INTEGER NOT NULL,

    -- Controle
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    hit_count INTEGER DEFAULT 0,
    last_hit_at TIMESTAMP
);

-- Índices
CREATE INDEX idx_cache_key ON openalex_cache(cache_key);
CREATE INDEX idx_cache_endpoint ON openalex_cache(endpoint_type);
CREATE INDEX idx_cache_expires ON openalex_cache(expires_at);

-- Tabela de métricas agregadas por instituição
CREATE TABLE instituicao_metricas (
    id BIGSERIAL PRIMARY KEY,
    instituicao_id BIGINT NOT NULL UNIQUE,

    -- Período de referência
    ano_referencia INTEGER NOT NULL,

    -- Métricas de publicação
    total_publicacoes INTEGER DEFAULT 0,
    publicacoes_com_doi INTEGER DEFAULT 0,
    publicacoes_open_access INTEGER DEFAULT 0,

    -- Métricas de citação
    total_citacoes INTEGER DEFAULT 0,
    h_index INTEGER DEFAULT 0,
    i10_index INTEGER DEFAULT 0,

    -- Colaborações
    total_coautores_externos INTEGER DEFAULT 0,
    total_instituicoes_colaboradoras INTEGER DEFAULT 0,
    total_paises_colaboradores INTEGER DEFAULT 0,

    -- Distribuição por tipo
    artigos_periodico INTEGER DEFAULT 0,
    livros_capitulos INTEGER DEFAULT 0,
    trabalhos_eventos INTEGER DEFAULT 0,
    outros INTEGER DEFAULT 0,

    -- Atualização
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_metricas_instituicao FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id) ON DELETE CASCADE,
    CONSTRAINT check_ano_referencia CHECK (ano_referencia >= 1900 AND ano_referencia <= 2100)
);

-- Índices
CREATE INDEX idx_metricas_instituicao ON instituicao_metricas(instituicao_id);
CREATE INDEX idx_metricas_ano ON instituicao_metricas(ano_referencia);

-- Trigger para updated_at
CREATE TRIGGER update_publicacoes_updated_at BEFORE UPDATE ON publicacoes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_metricas_updated_at BEFORE UPDATE ON instituicao_metricas
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comentários
COMMENT ON TABLE publicacoes IS 'Publicações científicas importadas do OpenAlex';
COMMENT ON TABLE autorias IS 'Relacionamento entre publicações e autores (docentes)';
COMMENT ON TABLE openalex_sync_log IS 'Log de todas as sincronizações com a API do OpenAlex';
COMMENT ON TABLE openalex_cache IS 'Cache de respostas da API do OpenAlex para otimizar requests';
COMMENT ON TABLE instituicao_metricas IS 'Métricas bibliométricas agregadas por instituição e ano';
