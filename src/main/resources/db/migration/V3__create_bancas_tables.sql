-- PPG Hub - Tabelas para Gestão de Bancas de Defesa
-- Tabelas para armazenar discentes, bancas, membros e professores externos

-- ============================================================================
-- TABELA: DISCENTES
-- ============================================================================
CREATE TABLE discentes (
    id BIGSERIAL PRIMARY KEY,

    -- Dados de Matrícula
    matricula VARCHAR(50) UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    data_nascimento DATE,

    -- Vínculo Acadêmico
    programa_id BIGINT NOT NULL,
    orientador_id BIGINT,
    data_ingresso DATE,
    data_conclusao DATE,
    status_matricula VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    nivel_formacao VARCHAR(20) NOT NULL,

    -- Dados da Tese/Dissertação
    titulo_tese VARCHAR(500),
    data_defesa DATE,

    -- Identificadores Acadêmicos
    lattes_id VARCHAR(50) UNIQUE,
    orcid VARCHAR(19) UNIQUE,
    openalex_author_id VARCHAR(50) UNIQUE,

    -- OpenAlex Metrics
    openalex_works_count INTEGER DEFAULT 0,
    openalex_cited_by_count INTEGER DEFAULT 0,
    openalex_last_sync_at TIMESTAMP,

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT fk_discente_programa FOREIGN KEY (programa_id)
        REFERENCES programas(id) ON DELETE RESTRICT,
    CONSTRAINT fk_discente_orientador FOREIGN KEY (orientador_id)
        REFERENCES docentes(id) ON DELETE SET NULL,
    CONSTRAINT check_status_matricula CHECK (
        status_matricula IN ('ATIVO', 'INATIVO', 'EGRESSO', 'TRANCADO', 'DESLIGADO')
    ),
    CONSTRAINT check_nivel_formacao CHECK (
        nivel_formacao IN ('MESTRADO', 'DOUTORADO', 'DOUTORADO_DIRETO')
    )
);

-- Índices para discentes
CREATE INDEX idx_discentes_matricula ON discentes(matricula);
CREATE INDEX idx_discentes_programa ON discentes(programa_id);
CREATE INDEX idx_discentes_orientador ON discentes(orientador_id);
CREATE INDEX idx_discentes_nome ON discentes(nome);
CREATE INDEX idx_discentes_email ON discentes(email);
CREATE INDEX idx_discentes_status ON discentes(status_matricula);
CREATE INDEX idx_discentes_cpf ON discentes(cpf);
CREATE INDEX idx_discentes_lattes ON discentes(lattes_id);
CREATE INDEX idx_discentes_orcid ON discentes(orcid);

-- Comentários
COMMENT ON TABLE discentes IS 'Estudantes de pós-graduação (mestrado e doutorado)';
COMMENT ON COLUMN discentes.status_matricula IS 'Status atual da matrícula: ATIVO, INATIVO, EGRESSO, TRANCADO, DESLIGADO';
COMMENT ON COLUMN discentes.nivel_formacao IS 'Nível de formação: MESTRADO, DOUTORADO, DOUTORADO_DIRETO';

-- ============================================================================
-- TABELA: PROFESSORES EXTERNOS
-- ============================================================================
CREATE TABLE professores_externos (
    id BIGSERIAL PRIMARY KEY,

    -- Dados Básicos
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    instituicao_origem VARCHAR(255),
    titulacao VARCHAR(100),
    especialidade VARCHAR(255),
    telefone VARCHAR(20),

    -- Identificadores Acadêmicos
    lattes_id VARCHAR(50) UNIQUE,
    orcid VARCHAR(19) UNIQUE,
    openalex_author_id VARCHAR(50) UNIQUE,
    scopus_id VARCHAR(50),

    -- OpenAlex Metrics
    openalex_works_count INTEGER DEFAULT 0,
    openalex_cited_by_count INTEGER DEFAULT 0,
    openalex_h_index INTEGER DEFAULT 0,
    openalex_last_sync_at TIMESTAMP,

    -- Status
    observacoes TEXT,
    validado BOOLEAN DEFAULT FALSE NOT NULL,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

-- Índices para professores externos
CREATE INDEX idx_prof_ext_nome ON professores_externos(nome);
CREATE INDEX idx_prof_ext_email ON professores_externos(email);
CREATE INDEX idx_prof_ext_instituicao ON professores_externos(instituicao_origem);
CREATE INDEX idx_prof_ext_orcid ON professores_externos(orcid);
CREATE INDEX idx_prof_ext_lattes ON professores_externos(lattes_id);
CREATE INDEX idx_prof_ext_validado ON professores_externos(validado);

-- Comentários
COMMENT ON TABLE professores_externos IS 'Professores externos que participam de bancas mas não são docentes cadastrados';
COMMENT ON COLUMN professores_externos.validado IS 'Indica se os dados foram validados via fontes externas (OpenAlex, ORCID, Lattes)';
COMMENT ON COLUMN professores_externos.ativo IS 'Indica se o professor está ativo para participar de bancas';

-- ============================================================================
-- TABELA: BANCAS
-- ============================================================================
CREATE TABLE bancas (
    id BIGSERIAL PRIMARY KEY,

    -- Relacionamentos
    discente_id BIGINT NOT NULL,
    programa_id BIGINT NOT NULL,

    -- Dados da Banca
    tipo_banca VARCHAR(30) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    local_defesa VARCHAR(255),
    titulo_trabalho VARCHAR(500),

    -- Status e Resultado
    status_banca VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    resultado_banca VARCHAR(30),
    data_realizacao TIMESTAMP,

    -- Observações e Documentos
    observacoes TEXT,
    documento_ata VARCHAR(500),
    documento_tese VARCHAR(500),

    -- Configurações
    orientador_participa BOOLEAN DEFAULT TRUE NOT NULL,
    defesa_remota BOOLEAN DEFAULT FALSE NOT NULL,
    link_videoconferencia VARCHAR(500),

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT fk_banca_discente FOREIGN KEY (discente_id)
        REFERENCES discentes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_banca_programa FOREIGN KEY (programa_id)
        REFERENCES programas(id) ON DELETE RESTRICT,
    CONSTRAINT uk_banca_discente_tipo_data UNIQUE (discente_id, tipo_banca, data_hora),
    CONSTRAINT check_tipo_banca CHECK (
        tipo_banca IN (
            'QUALIFICACAO_MESTRADO',
            'QUALIFICACAO_DOUTORADO',
            'DEFESA_MESTRADO',
            'DEFESA_DOUTORADO',
            'DEFESA_DOUTORADO_DIRETO',
            'EXAME_PROFICIENCIA'
        )
    ),
    CONSTRAINT check_status_banca CHECK (
        status_banca IN ('AGENDADA', 'CONFIRMADA', 'REALIZADA', 'CANCELADA', 'REAGENDADA')
    ),
    CONSTRAINT check_resultado_banca CHECK (
        resultado_banca IS NULL OR
        resultado_banca IN ('APROVADO', 'APROVADO_COM_RESTRICOES', 'APROVADO_COM_CORRECOES', 'REPROVADO')
    )
);

-- Índices para bancas
CREATE INDEX idx_bancas_discente ON bancas(discente_id);
CREATE INDEX idx_bancas_programa ON bancas(programa_id);
CREATE INDEX idx_bancas_tipo ON bancas(tipo_banca);
CREATE INDEX idx_bancas_status ON bancas(status_banca);
CREATE INDEX idx_bancas_data ON bancas(data_hora);
CREATE INDEX idx_bancas_resultado ON bancas(resultado_banca);

-- Comentários
COMMENT ON TABLE bancas IS 'Bancas de defesa e qualificação de dissertações e teses';
COMMENT ON COLUMN bancas.tipo_banca IS 'Tipo: QUALIFICACAO_MESTRADO, QUALIFICACAO_DOUTORADO, DEFESA_MESTRADO, DEFESA_DOUTORADO, etc';
COMMENT ON COLUMN bancas.status_banca IS 'Status: AGENDADA, CONFIRMADA, REALIZADA, CANCELADA, REAGENDADA';
COMMENT ON COLUMN bancas.resultado_banca IS 'Resultado: APROVADO, APROVADO_COM_RESTRICOES, APROVADO_COM_CORRECOES, REPROVADO';

-- ============================================================================
-- TABELA: MEMBROS DE BANCA
-- ============================================================================
CREATE TABLE membros_banca (
    id BIGSERIAL PRIMARY KEY,

    -- Relacionamentos
    banca_id BIGINT NOT NULL,
    docente_id BIGINT,
    professor_externo_id BIGINT,

    -- Tipo e Função
    tipo_membro VARCHAR(20) NOT NULL,
    funcao VARCHAR(30) NOT NULL,

    -- Status do Convite
    status_convite VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    data_convite TIMESTAMP,
    data_resposta TIMESTAMP,

    -- Observações
    observacoes TEXT,
    ordem_apresentacao INTEGER,

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT fk_membro_banca FOREIGN KEY (banca_id)
        REFERENCES bancas(id) ON DELETE CASCADE,
    CONSTRAINT fk_membro_docente FOREIGN KEY (docente_id)
        REFERENCES docentes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_membro_prof_externo FOREIGN KEY (professor_externo_id)
        REFERENCES professores_externos(id) ON DELETE RESTRICT,
    CONSTRAINT uk_membro_banca_docente UNIQUE (banca_id, docente_id),
    CONSTRAINT uk_membro_banca_prof_externo UNIQUE (banca_id, professor_externo_id),
    CONSTRAINT check_tipo_membro CHECK (
        tipo_membro IN ('TITULAR', 'SUPLENTE')
    ),
    CONSTRAINT check_funcao CHECK (
        funcao IN ('PRESIDENTE', 'MEMBRO_INTERNO', 'MEMBRO_EXTERNO', 'ORIENTADOR', 'COORIENTADOR')
    ),
    CONSTRAINT check_status_convite CHECK (
        status_convite IN ('PENDENTE', 'ENVIADO', 'CONFIRMADO', 'RECUSADO', 'CANCELADO')
    ),
    CONSTRAINT check_membro_exclusivo CHECK (
        (docente_id IS NOT NULL AND professor_externo_id IS NULL) OR
        (docente_id IS NULL AND professor_externo_id IS NOT NULL)
    )
);

-- Índices para membros de banca
CREATE INDEX idx_membros_banca_banca ON membros_banca(banca_id);
CREATE INDEX idx_membros_banca_docente ON membros_banca(docente_id);
CREATE INDEX idx_membros_banca_prof_ext ON membros_banca(professor_externo_id);
CREATE INDEX idx_membros_banca_tipo ON membros_banca(tipo_membro);
CREATE INDEX idx_membros_banca_funcao ON membros_banca(funcao);
CREATE INDEX idx_membros_banca_status ON membros_banca(status_convite);

-- Comentários
COMMENT ON TABLE membros_banca IS 'Membros que compõem uma banca de defesa';
COMMENT ON COLUMN membros_banca.tipo_membro IS 'Tipo: TITULAR, SUPLENTE';
COMMENT ON COLUMN membros_banca.funcao IS 'Função: PRESIDENTE, MEMBRO_INTERNO, MEMBRO_EXTERNO, ORIENTADOR, COORIENTADOR';
COMMENT ON COLUMN membros_banca.status_convite IS 'Status: PENDENTE, ENVIADO, CONFIRMADO, RECUSADO, CANCELADO';
COMMENT ON CONSTRAINT check_membro_exclusivo ON membros_banca IS 'Garante que apenas um entre docente_id ou professor_externo_id esteja preenchido';

-- ============================================================================
-- TRIGGERS PARA UPDATED_AT
-- ============================================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_discentes_updated_at
    BEFORE UPDATE ON discentes
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_professores_externos_updated_at
    BEFORE UPDATE ON professores_externos
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_bancas_updated_at
    BEFORE UPDATE ON bancas
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_membros_banca_updated_at
    BEFORE UPDATE ON membros_banca
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
