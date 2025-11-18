-- =====================================================
-- PPG HUB - MIGRATION V2 - ACADEMIC SCHEMA
-- Cria schema ACADEMIC com todas as entidades acadêmicas
-- =====================================================

CREATE SCHEMA IF NOT EXISTS academic;

-- =====================================================
-- Tabela de Docentes
-- =====================================================
CREATE TABLE academic.docentes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES auth.usuarios(id) ON DELETE CASCADE,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE RESTRICT,
    linha_pesquisa_id INTEGER REFERENCES core.linhas_pesquisa(id) ON DELETE SET NULL,
    matricula VARCHAR(50),
    categoria VARCHAR(50) CHECK (categoria IN ('Professor Titular', 'Professor Associado', 'Professor Adjunto', 'Professor Assistente')),
    regime_trabalho VARCHAR(50) CHECK (regime_trabalho IN ('DE', '40h', '20h')),
    titulacao_maxima VARCHAR(100),
    instituicao_titulacao VARCHAR(255),
    ano_titulacao INTEGER,
    pais_titulacao VARCHAR(50),
    tipo_vinculo VARCHAR(50) NOT NULL CHECK (tipo_vinculo IN ('Permanente', 'Colaborador', 'Visitante', 'Voluntário')),
    data_vinculacao DATE NOT NULL,
    data_desvinculacao DATE,
    orientacoes_mestrado_andamento INTEGER DEFAULT 0,
    orientacoes_doutorado_andamento INTEGER DEFAULT 0,
    orientacoes_mestrado_concluidas INTEGER DEFAULT 0,
    orientacoes_doutorado_concluidas INTEGER DEFAULT 0,
    coorientacoes INTEGER DEFAULT 0,
    bolsista_produtividade BOOLEAN DEFAULT FALSE,
    nivel_bolsa_produtividade VARCHAR(20),
    vigencia_bolsa_inicio DATE,
    vigencia_bolsa_fim DATE,
    areas_interesse TEXT,
    projetos_atuais TEXT,
    curriculo_resumo TEXT,
    status VARCHAR(50) DEFAULT 'Ativo' CHECK (status IN ('Ativo', 'Afastado', 'Aposentado', 'Desligado')),
    motivo_desligamento TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(usuario_id, programa_id),
    CHECK (data_desvinculacao IS NULL OR data_desvinculacao >= data_vinculacao)
);

COMMENT ON TABLE academic.docentes IS 'Docentes vinculados aos programas';
COMMENT ON COLUMN academic.docentes.tipo_vinculo IS 'Permanente, Colaborador, Visitante, Voluntário';

-- =====================================================
-- Tabela de Métricas de Docentes (histórico)
-- =====================================================
CREATE TABLE academic.metricas_docentes (
    id SERIAL PRIMARY KEY,
    docente_id INTEGER NOT NULL REFERENCES academic.docentes(id) ON DELETE CASCADE,
    h_index INTEGER,
    total_publicacoes INTEGER,
    total_citacoes INTEGER,
    publicacoes_ultimos_5_anos INTEGER,
    fonte VARCHAR(50),
    data_coleta TIMESTAMP DEFAULT NOW()
);

COMMENT ON TABLE academic.metricas_docentes IS 'Histórico de métricas acadêmicas dos docentes (OpenAlex, Lattes, etc)';

-- =====================================================
-- Tabela de Discentes
-- =====================================================
CREATE TABLE academic.discentes (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER NOT NULL REFERENCES auth.usuarios(id) ON DELETE CASCADE,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE RESTRICT,
    linha_pesquisa_id INTEGER REFERENCES core.linhas_pesquisa(id) ON DELETE SET NULL,
    orientador_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    coorientador_interno_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    numero_matricula VARCHAR(50) NOT NULL,
    tipo_curso VARCHAR(20) NOT NULL CHECK (tipo_curso IN ('Mestrado', 'Doutorado')),
    turma INTEGER NOT NULL,
    semestre_ingresso VARCHAR(10) NOT NULL,
    data_ingresso DATE NOT NULL,
    data_primeira_matricula DATE,
    titulo_projeto TEXT,
    resumo_projeto TEXT,
    palavras_chave_projeto TEXT,
    area_cnpq VARCHAR(255),
    tipo_ingresso VARCHAR(50),
    nota_processo_seletivo DECIMAL(5,2),
    classificacao_processo INTEGER,
    coorientador_externo_nome VARCHAR(255),
    coorientador_externo_instituicao VARCHAR(255),
    coorientador_externo_email VARCHAR(255),
    coorientador_externo_titulacao VARCHAR(100),
    proficiencia_idioma VARCHAR(50),
    proficiencia_status VARCHAR(50) CHECK (proficiencia_status IN ('Aprovado', 'Pendente', 'Dispensado')),
    data_proficiencia DATE,
    arquivo_proficiencia TEXT,
    bolsista BOOLEAN DEFAULT FALSE,
    tipo_bolsa VARCHAR(100),
    modalidade_bolsa VARCHAR(50),
    valor_bolsa DECIMAL(10,2),
    data_inicio_bolsa DATE,
    data_fim_bolsa DATE,
    numero_processo_bolsa VARCHAR(100),
    agencia_fomento VARCHAR(100),
    creditos_necessarios INTEGER,
    coeficiente_rendimento DECIMAL(4,2),
    qualificacao_realizada BOOLEAN DEFAULT FALSE,
    data_qualificacao DATE,
    resultado_qualificacao VARCHAR(50) CHECK (resultado_qualificacao IN ('Aprovado', 'Reprovado', 'Aprovado com Restrições')),
    prazo_original DATE,
    prorrogacoes JSONB DEFAULT '[]',
    data_limite_atual DATE,
    data_defesa DATE,
    resultado_defesa VARCHAR(50) CHECK (resultado_defesa IN ('Aprovado', 'Reprovado', 'Aprovado com Correções')),
    nota_defesa DECIMAL(4,2),
    titulo_final TEXT,
    documentos JSONB DEFAULT '{}',
    status VARCHAR(50) DEFAULT 'Matriculado' CHECK (status IN ('Matriculado', 'Cursando', 'Qualificado', 'Defendendo', 'Titulado', 'Desligado')),
    motivo_desligamento TEXT,
    data_desligamento DATE,
    destino_egresso TEXT,
    atuacao_pos_formatura TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(programa_id, numero_matricula),
    CHECK (data_defesa IS NULL OR data_defesa >= data_ingresso)
);

COMMENT ON TABLE academic.discentes IS 'Discentes (alunos de mestrado e doutorado)';
COMMENT ON COLUMN academic.discentes.tipo_curso IS 'Mestrado ou Doutorado';
COMMENT ON COLUMN academic.discentes.status IS 'Matriculado, Cursando, Qualificado, Defendendo, Titulado, Desligado';

-- =====================================================
-- Tabela de Disciplinas
-- =====================================================
CREATE TABLE academic.disciplinas (
    id SERIAL PRIMARY KEY,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE CASCADE,
    codigo VARCHAR(50) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    nome_ingles VARCHAR(255),
    ementa TEXT,
    ementa_ingles TEXT,
    objetivos TEXT,
    conteudo_programatico TEXT,
    metodologia_ensino TEXT,
    criterios_avaliacao TEXT,
    bibliografia_basica TEXT,
    bibliografia_complementar TEXT,
    carga_horaria_total INTEGER NOT NULL,
    carga_horaria_teorica INTEGER DEFAULT 0,
    carga_horaria_pratica INTEGER DEFAULT 0,
    creditos INTEGER NOT NULL,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('Obrigatória', 'Eletiva', 'Tópicos Especiais')),
    nivel VARCHAR(50) NOT NULL CHECK (nivel IN ('Mestrado', 'Doutorado', 'Ambos')),
    linha_pesquisa_id INTEGER REFERENCES core.linhas_pesquisa(id) ON DELETE SET NULL,
    pre_requisitos JSONB DEFAULT '[]',
    co_requisitos JSONB DEFAULT '[]',
    modalidade VARCHAR(50) DEFAULT 'Presencial' CHECK (modalidade IN ('Presencial', 'EAD', 'Híbrida')),
    periodicidade VARCHAR(50) CHECK (periodicidade IN ('Anual', 'Semestral', 'Eventual')),
    maximo_alunos INTEGER,
    minimo_alunos INTEGER DEFAULT 1,
    status VARCHAR(50) DEFAULT 'Ativa' CHECK (status IN ('Ativa', 'Inativa', 'Suspensa')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(programa_id, codigo),
    CHECK (carga_horaria_total = carga_horaria_teorica + carga_horaria_pratica),
    CHECK (creditos = carga_horaria_total / 15)
);

COMMENT ON TABLE academic.disciplinas IS 'Disciplinas dos programas';
COMMENT ON COLUMN academic.disciplinas.tipo IS 'Obrigatória, Eletiva ou Tópicos Especiais';

-- =====================================================
-- Tabela de Ofertas de Disciplinas
-- =====================================================
CREATE TABLE academic.ofertas_disciplinas (
    id SERIAL PRIMARY KEY,
    disciplina_id INTEGER NOT NULL REFERENCES academic.disciplinas(id) ON DELETE CASCADE,
    docente_responsavel_id INTEGER NOT NULL REFERENCES academic.docentes(id) ON DELETE RESTRICT,
    docente_colaborador_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    ano INTEGER NOT NULL,
    semestre INTEGER NOT NULL CHECK (semestre IN (1, 2)),
    periodo VARCHAR(10) NOT NULL,
    turma VARCHAR(10) DEFAULT 'A',
    horarios JSONB NOT NULL,
    sala VARCHAR(50),
    modalidade VARCHAR(50) DEFAULT 'Presencial' CHECK (modalidade IN ('Presencial', 'EAD', 'Híbrida')),
    link_virtual TEXT,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    vagas_oferecidas INTEGER NOT NULL,
    vagas_ocupadas INTEGER DEFAULT 0,
    lista_espera INTEGER DEFAULT 0,
    status VARCHAR(50) DEFAULT 'Planejada' CHECK (status IN ('Planejada', 'Aberta', 'Em_Curso', 'Concluída', 'Cancelada')),
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(disciplina_id, ano, semestre, turma),
    CHECK (data_fim > data_inicio),
    CHECK (vagas_ocupadas <= vagas_oferecidas)
);

COMMENT ON TABLE academic.ofertas_disciplinas IS 'Ofertas de disciplinas por período';
COMMENT ON COLUMN academic.ofertas_disciplinas.periodo IS 'Ex: 2024.1, 2024.2';

-- =====================================================
-- Tabela de Matrículas em Disciplinas
-- =====================================================
CREATE TABLE academic.matriculas_disciplinas (
    id SERIAL PRIMARY KEY,
    discente_id INTEGER NOT NULL REFERENCES academic.discentes(id) ON DELETE CASCADE,
    oferta_disciplina_id INTEGER NOT NULL REFERENCES academic.ofertas_disciplinas(id) ON DELETE CASCADE,
    data_matricula TIMESTAMP DEFAULT NOW(),
    situacao VARCHAR(50) DEFAULT 'Matriculado' CHECK (situacao IN ('Matriculado', 'Trancado', 'Cancelado')),
    avaliacoes JSONB DEFAULT '{}',
    frequencia_percentual DECIMAL(5,2) CHECK (frequencia_percentual >= 0 AND frequencia_percentual <= 100),
    nota_final DECIMAL(4,2) CHECK (nota_final >= 0 AND nota_final <= 10),
    conceito VARCHAR(10) CHECK (conceito IN ('A', 'B', 'C', 'D', 'E', 'Aprovado', 'Reprovado')),
    status_final VARCHAR(50) CHECK (status_final IN ('Aprovado', 'Reprovado', 'Trancado', 'Cancelado')),
    data_resultado DATE,
    observacoes TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(discente_id, oferta_disciplina_id)
);

COMMENT ON TABLE academic.matriculas_disciplinas IS 'Matrículas dos discentes em ofertas de disciplinas';

-- =====================================================
-- Tabela de Trabalhos de Conclusão
-- =====================================================
CREATE TABLE academic.trabalhos_conclusao (
    id SERIAL PRIMARY KEY,
    discente_id INTEGER NOT NULL REFERENCES academic.discentes(id) ON DELETE CASCADE,
    programa_id INTEGER NOT NULL REFERENCES core.programas(id) ON DELETE RESTRICT,
    orientador_id INTEGER NOT NULL REFERENCES academic.docentes(id) ON DELETE RESTRICT,
    coorientador_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('Dissertação', 'Tese')),
    titulo TEXT NOT NULL,
    titulo_ingles TEXT,
    subtitulo TEXT,
    resumo_portugues TEXT,
    resumo_ingles TEXT,
    abstract TEXT,
    palavras_chave_portugues TEXT,
    palavras_chave_ingles TEXT,
    keywords TEXT,
    area_cnpq TEXT,
    area_concentracao TEXT,
    linha_pesquisa TEXT,
    data_defesa DATE,
    ano_defesa INTEGER,
    semestre_defesa INTEGER CHECK (semestre_defesa IN (1, 2)),
    local_defesa TEXT,
    numero_paginas INTEGER,
    idioma VARCHAR(10) DEFAULT 'pt',
    arquivo_pdf TEXT,
    arquivo_ata_defesa TEXT,
    tamanho_arquivo_bytes BIGINT,
    handle_uri TEXT,
    uri_repositorio TEXT,
    url_download TEXT,
    tipo_acesso VARCHAR(50) DEFAULT 'Acesso Aberto' CHECK (tipo_acesso IN ('Acesso Aberto', 'Restrito', 'Embargado')),
    openalex_work_id TEXT,
    doi TEXT,
    isbn TEXT,
    downloads_count INTEGER DEFAULT 0,
    visualizacoes_count INTEGER DEFAULT 0,
    citacoes_count INTEGER DEFAULT 0,
    nota_avaliacao DECIMAL(4,2),
    premios_reconhecimentos TEXT,
    status VARCHAR(50) DEFAULT 'Em_Preparacao' CHECK (status IN ('Em_Preparacao', 'Qualificado', 'Defendido', 'Aprovado', 'Publicado')),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(discente_id)
);

COMMENT ON TABLE academic.trabalhos_conclusao IS 'Dissertações e teses dos discentes';

-- =====================================================
-- Tabela de Bancas
-- =====================================================
CREATE TABLE academic.bancas (
    id SERIAL PRIMARY KEY,
    trabalho_conclusao_id INTEGER REFERENCES academic.trabalhos_conclusao(id) ON DELETE CASCADE,
    discente_id INTEGER NOT NULL REFERENCES academic.discentes(id) ON DELETE CASCADE,
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('Qualificação', 'Defesa_Dissertacao', 'Defesa_Tese')),
    data_agendada DATE NOT NULL,
    horario_inicio TIME NOT NULL,
    horario_fim TIME,
    local_realizacao TEXT,
    modalidade VARCHAR(50) DEFAULT 'Presencial' CHECK (modalidade IN ('Presencial', 'Virtual', 'Híbrida')),
    link_virtual TEXT,
    presidente_id INTEGER NOT NULL REFERENCES academic.docentes(id) ON DELETE RESTRICT,
    secretario_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    ata_numero VARCHAR(50),
    ata_arquivo TEXT,
    resultado VARCHAR(50) CHECK (resultado IN ('Aprovado', 'Reprovado', 'Aprovado_com_Correcoes', 'Aprovado_com_Restricoes')),
    nota_final DECIMAL(4,2),
    prazo_correcoes_dias INTEGER,
    correcoes_exigidas TEXT,
    observacoes_banca TEXT,
    recomendacoes TEXT,
    sugestao_publicacao BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'Agendada' CHECK (status IN ('Agendada', 'Realizada', 'Cancelada', 'Adiada')),
    data_realizacao DATE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CHECK (data_agendada >= CURRENT_DATE - INTERVAL '1 year')
);

COMMENT ON TABLE academic.bancas IS 'Bancas de qualificação e defesa';

-- =====================================================
-- Tabela de Membros de Banca
-- =====================================================
CREATE TABLE academic.membros_banca (
    id SERIAL PRIMARY KEY,
    banca_id INTEGER NOT NULL REFERENCES academic.bancas(id) ON DELETE CASCADE,
    docente_id INTEGER REFERENCES academic.docentes(id) ON DELETE SET NULL,
    nome_completo VARCHAR(255),
    instituicao VARCHAR(255),
    titulacao VARCHAR(100),
    email VARCHAR(255),
    curriculo_resumo TEXT,
    funcao VARCHAR(50) NOT NULL CHECK (funcao IN ('Presidente', 'Examinador_Interno', 'Examinador_Externo', 'Suplente')),
    tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('Interno', 'Externo')),
    ordem_apresentacao INTEGER CHECK (ordem_apresentacao > 0),
    confirmado BOOLEAN DEFAULT FALSE,
    presente BOOLEAN,
    justificativa_ausencia TEXT,
    parecer_individual TEXT,
    nota_individual DECIMAL(4,2),
    created_at TIMESTAMP DEFAULT NOW(),
    CHECK ((docente_id IS NOT NULL AND tipo = 'Interno') OR
           (docente_id IS NULL AND tipo = 'Externo' AND nome_completo IS NOT NULL))
);

COMMENT ON TABLE academic.membros_banca IS 'Membros das bancas (internos e externos)';

-- =====================================================
-- Adicionar FKs de linha_pesquisa para docente
-- =====================================================
ALTER TABLE core.linhas_pesquisa
    ADD CONSTRAINT fk_coordenador_linha
        FOREIGN KEY (coordenador_id) REFERENCES academic.docentes(id) ON DELETE SET NULL;

-- =====================================================
-- Garantir presidente único em cada banca
-- =====================================================
CREATE UNIQUE INDEX idx_banca_presidente_unico
    ON academic.membros_banca(banca_id)
    WHERE funcao = 'Presidente';
