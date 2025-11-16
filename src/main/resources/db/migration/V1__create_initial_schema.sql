-- PPG Hub - Schema Inicial
-- Criação das tabelas base para instituições, programas e docentes

-- Tabela de Instituições
CREATE TABLE instituicoes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(50) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- PUBLICA, PRIVADA, ESPECIAL
    categoria VARCHAR(50), -- FEDERAL, ESTADUAL, MUNICIPAL, PARTICULAR, COMUNITARIA
    cnpj VARCHAR(18) UNIQUE,

    -- Endereço
    logradouro VARCHAR(255),
    numero VARCHAR(20),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(9),
    pais VARCHAR(100) DEFAULT 'Brasil',

    -- Contato
    telefone VARCHAR(20),
    email VARCHAR(255),
    website VARCHAR(500),

    -- OpenAlex Integration
    openalex_institution_id VARCHAR(50) UNIQUE,
    ror_id VARCHAR(50) UNIQUE,
    openalex_display_name VARCHAR(255),
    openalex_last_sync_at TIMESTAMP,
    openalex_works_count INTEGER DEFAULT 0,
    openalex_cited_by_count INTEGER DEFAULT 0,

    -- Metadata
    ativo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT check_estado CHECK (LENGTH(estado) = 2)
);

-- Índices para buscas eficientes
CREATE INDEX idx_instituicoes_nome ON instituicoes(nome);
CREATE INDEX idx_instituicoes_sigla ON instituicoes(sigla);
CREATE INDEX idx_instituicoes_cidade_estado ON instituicoes(cidade, estado);
CREATE INDEX idx_instituicoes_openalex_id ON instituicoes(openalex_institution_id);
CREATE INDEX idx_instituicoes_ror_id ON instituicoes(ror_id);
CREATE INDEX idx_instituicoes_ativo ON instituicoes(ativo);

-- Tabela de Programas de Pós-Graduação
CREATE TABLE programas (
    id BIGSERIAL PRIMARY KEY,
    instituicao_id BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    sigla VARCHAR(50),
    codigo_capes VARCHAR(20) UNIQUE,

    -- Classificação
    area_conhecimento VARCHAR(100),
    area_avaliacao VARCHAR(100),
    modalidade VARCHAR(50), -- ACADEMICO, PROFISSIONAL
    nivel VARCHAR(50), -- MESTRADO, DOUTORADO, MESTRADO_DOUTORADO

    -- Avaliação CAPES
    conceito_capes INTEGER,
    ano_avaliacao INTEGER,

    -- Contato
    coordenador VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(20),
    website VARCHAR(500),

    -- Status
    status VARCHAR(50) DEFAULT 'ATIVO', -- ATIVO, INATIVO, EM_IMPLANTACAO
    data_inicio DATE,
    data_fim DATE,

    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT fk_programa_instituicao FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id) ON DELETE CASCADE,
    CONSTRAINT check_conceito_capes CHECK (conceito_capes BETWEEN 1 AND 7)
);

-- Índices
CREATE INDEX idx_programas_instituicao ON programas(instituicao_id);
CREATE INDEX idx_programas_codigo_capes ON programas(codigo_capes);
CREATE INDEX idx_programas_area_conhecimento ON programas(area_conhecimento);
CREATE INDEX idx_programas_status ON programas(status);

-- Tabela de Docentes/Pesquisadores
CREATE TABLE docentes (
    id BIGSERIAL PRIMARY KEY,
    instituicao_id BIGINT NOT NULL,

    -- Dados Pessoais
    nome_completo VARCHAR(255) NOT NULL,
    nome_citacao VARCHAR(255),
    cpf VARCHAR(14) UNIQUE,
    email VARCHAR(255),
    telefone VARCHAR(20),

    -- Identificadores Acadêmicos
    lattes_id VARCHAR(50) UNIQUE,
    orcid VARCHAR(19) UNIQUE,
    openalex_author_id VARCHAR(50) UNIQUE,
    scopus_id VARCHAR(50),
    researcher_id VARCHAR(50),

    -- Formação
    titulacao VARCHAR(50), -- GRADUACAO, ESPECIALIZACAO, MESTRADO, DOUTORADO, POS_DOUTORADO
    area_atuacao VARCHAR(255),

    -- Vínculo
    tipo_vinculo VARCHAR(50), -- PERMANENTE, COLABORADOR, VISITANTE
    regime_trabalho VARCHAR(50), -- DEDICACAO_EXCLUSIVA, TEMPO_INTEGRAL, TEMPO_PARCIAL
    data_ingresso DATE,
    data_saida DATE,
    ativo BOOLEAN DEFAULT TRUE,

    -- OpenAlex Metrics
    openalex_works_count INTEGER DEFAULT 0,
    openalex_cited_by_count INTEGER DEFAULT 0,
    openalex_h_index INTEGER DEFAULT 0,
    openalex_last_sync_at TIMESTAMP,

    -- Metadata
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT fk_docente_instituicao FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id) ON DELETE CASCADE
);

-- Índices
CREATE INDEX idx_docentes_instituicao ON docentes(instituicao_id);
CREATE INDEX idx_docentes_nome ON docentes(nome_completo);
CREATE INDEX idx_docentes_lattes ON docentes(lattes_id);
CREATE INDEX idx_docentes_orcid ON docentes(orcid);
CREATE INDEX idx_docentes_openalex ON docentes(openalex_author_id);
CREATE INDEX idx_docentes_ativo ON docentes(ativo);

-- Tabela de associação Docente-Programa
CREATE TABLE docente_programa (
    docente_id BIGINT NOT NULL,
    programa_id BIGINT NOT NULL,
    categoria VARCHAR(50) NOT NULL, -- PERMANENTE, COLABORADOR, VISITANTE
    data_inicio DATE NOT NULL,
    data_fim DATE,

    PRIMARY KEY (docente_id, programa_id),

    CONSTRAINT fk_dp_docente FOREIGN KEY (docente_id)
        REFERENCES docentes(id) ON DELETE CASCADE,
    CONSTRAINT fk_dp_programa FOREIGN KEY (programa_id)
        REFERENCES programas(id) ON DELETE CASCADE
);

CREATE INDEX idx_dp_programa ON docente_programa(programa_id);

-- Função para atualizar updated_at automaticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers para updated_at
CREATE TRIGGER update_instituicoes_updated_at BEFORE UPDATE ON instituicoes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_programas_updated_at BEFORE UPDATE ON programas
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_docentes_updated_at BEFORE UPDATE ON docentes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Comentários nas tabelas
COMMENT ON TABLE instituicoes IS 'Instituições de ensino superior';
COMMENT ON TABLE programas IS 'Programas de pós-graduação';
COMMENT ON TABLE docentes IS 'Docentes e pesquisadores vinculados às instituições';
COMMENT ON TABLE docente_programa IS 'Relacionamento many-to-many entre docentes e programas';

COMMENT ON COLUMN instituicoes.openalex_institution_id IS 'ID da instituição no OpenAlex (formato: I123456789)';
COMMENT ON COLUMN instituicoes.ror_id IS 'Research Organization Registry ID';
COMMENT ON COLUMN docentes.openalex_author_id IS 'ID do autor no OpenAlex (formato: A123456789)';
