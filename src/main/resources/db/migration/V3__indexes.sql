-- =====================================================
-- PPG HUB - MIGRATION V3 - INDEXES
-- Cria todos os índices para otimização de consultas
-- =====================================================

-- =====================================================
-- ÍNDICES: CORE SCHEMA
-- =====================================================

-- Instituições
CREATE INDEX idx_instituicoes_codigo ON core.instituicoes(codigo);
CREATE INDEX idx_instituicoes_cnpj ON core.instituicoes(cnpj);
CREATE INDEX idx_instituicoes_tipo ON core.instituicoes(tipo);
CREATE INDEX idx_instituicoes_ativo ON core.instituicoes(ativo);
CREATE INDEX idx_instituicoes_nome_abreviado ON core.instituicoes(nome_abreviado);
CREATE INDEX idx_instituicoes_nome_trgm ON core.instituicoes USING gin(nome_completo gin_trgm_ops);
CREATE INDEX idx_instituicoes_openalex ON core.instituicoes(openalex_institution_id) WHERE openalex_institution_id IS NOT NULL;

-- Programas
CREATE INDEX idx_programas_instituicao ON core.programas(instituicao_id);
CREATE INDEX idx_programas_codigo_capes ON core.programas(codigo_capes);
CREATE INDEX idx_programas_sigla ON core.programas(sigla);
CREATE INDEX idx_programas_status ON core.programas(status);
CREATE INDEX idx_programas_conceito ON core.programas(conceito_capes);
CREATE INDEX idx_programas_coordenador ON core.programas(coordenador_id) WHERE coordenador_id IS NOT NULL;
CREATE INDEX idx_programas_nivel ON core.programas(nivel);
CREATE INDEX idx_programas_nome_trgm ON core.programas USING gin(nome gin_trgm_ops);

-- Linhas de Pesquisa
CREATE INDEX idx_linhas_programa ON core.linhas_pesquisa(programa_id);
CREATE INDEX idx_linhas_coordenador ON core.linhas_pesquisa(coordenador_id) WHERE coordenador_id IS NOT NULL;
CREATE INDEX idx_linhas_ativa ON core.linhas_pesquisa(ativa);
CREATE INDEX idx_linhas_nome_trgm ON core.linhas_pesquisa USING gin(nome gin_trgm_ops);

-- =====================================================
-- ÍNDICES: AUTH SCHEMA
-- =====================================================

-- Roles
CREATE INDEX idx_roles_nome ON auth.roles(nome);
CREATE INDEX idx_roles_nivel_acesso ON auth.roles(nivel_acesso);
CREATE INDEX idx_roles_ativo ON auth.roles(ativo);

-- Usuários
CREATE INDEX idx_usuarios_email ON auth.usuarios(email);
CREATE INDEX idx_usuarios_cpf ON auth.usuarios(cpf) WHERE cpf IS NOT NULL;
CREATE INDEX idx_usuarios_uuid ON auth.usuarios(uuid);
CREATE INDEX idx_usuarios_ativo ON auth.usuarios(ativo);
CREATE INDEX idx_usuarios_orcid ON auth.usuarios(orcid) WHERE orcid IS NOT NULL;
CREATE INDEX idx_usuarios_lattes ON auth.usuarios(lattes_id) WHERE lattes_id IS NOT NULL;
CREATE INDEX idx_usuarios_openalex ON auth.usuarios(openalex_author_id) WHERE openalex_author_id IS NOT NULL;
CREATE INDEX idx_usuarios_nome_trgm ON auth.usuarios USING gin(nome_completo gin_trgm_ops);
CREATE INDEX idx_usuarios_conta_bloqueada ON auth.usuarios(conta_bloqueada) WHERE conta_bloqueada = TRUE;

-- Vinculações usuário-programa-role
CREATE INDEX idx_usuario_roles_usuario ON auth.usuario_programa_roles(usuario_id);
CREATE INDEX idx_usuario_roles_programa ON auth.usuario_programa_roles(programa_id);
CREATE INDEX idx_usuario_roles_role ON auth.usuario_programa_roles(role_id);
CREATE INDEX idx_usuario_roles_status ON auth.usuario_programa_roles(status);
CREATE INDEX idx_usuario_roles_lookup ON auth.usuario_programa_roles(usuario_id, programa_id, role_id);

-- Sessões
CREATE INDEX idx_sessoes_token ON auth.sessoes(token);
CREATE INDEX idx_sessoes_refresh_token ON auth.sessoes(refresh_token) WHERE refresh_token IS NOT NULL;
CREATE INDEX idx_sessoes_usuario ON auth.sessoes(usuario_id);
CREATE INDEX idx_sessoes_ativo ON auth.sessoes(ativo) WHERE ativo = TRUE;
CREATE INDEX idx_sessoes_expires ON auth.sessoes(expires_at) WHERE ativo = TRUE;

-- Audit Logs
CREATE INDEX idx_audit_usuario ON auth.audit_logs(usuario_id) WHERE usuario_id IS NOT NULL;
CREATE INDEX idx_audit_acao ON auth.audit_logs(acao);
CREATE INDEX idx_audit_entidade ON auth.audit_logs(entidade, entidade_id);
CREATE INDEX idx_audit_created ON auth.audit_logs(created_at DESC);
CREATE INDEX idx_audit_dados_novos ON auth.audit_logs USING gin(dados_novos);

-- =====================================================
-- ÍNDICES: ACADEMIC SCHEMA
-- =====================================================

-- Docentes
CREATE INDEX idx_docentes_usuario ON academic.docentes(usuario_id);
CREATE INDEX idx_docentes_programa ON academic.docentes(programa_id);
CREATE INDEX idx_docentes_linha ON academic.docentes(linha_pesquisa_id) WHERE linha_pesquisa_id IS NOT NULL;
CREATE INDEX idx_docentes_matricula ON academic.docentes(matricula) WHERE matricula IS NOT NULL;
CREATE INDEX idx_docentes_status ON academic.docentes(status);
CREATE INDEX idx_docentes_tipo_vinculo ON academic.docentes(tipo_vinculo);
CREATE INDEX idx_docentes_categoria ON academic.docentes(categoria);
CREATE INDEX idx_docentes_lookup ON academic.docentes(usuario_id, programa_id);

-- Métricas Docentes
CREATE INDEX idx_metricas_docente ON academic.metricas_docentes(docente_id);
CREATE INDEX idx_metricas_data ON academic.metricas_docentes(data_coleta DESC);
CREATE INDEX idx_metricas_fonte ON academic.metricas_docentes(fonte);

-- Discentes
CREATE INDEX idx_discentes_usuario ON academic.discentes(usuario_id);
CREATE INDEX idx_discentes_programa ON academic.discentes(programa_id);
CREATE INDEX idx_discentes_linha ON academic.discentes(linha_pesquisa_id) WHERE linha_pesquisa_id IS NOT NULL;
CREATE INDEX idx_discentes_orientador ON academic.discentes(orientador_id) WHERE orientador_id IS NOT NULL;
CREATE INDEX idx_discentes_coorientador ON academic.discentes(coorientador_interno_id) WHERE coorientador_interno_id IS NOT NULL;
CREATE INDEX idx_discentes_matricula ON academic.discentes(numero_matricula);
CREATE INDEX idx_discentes_status ON academic.discentes(status);
CREATE INDEX idx_discentes_tipo_curso ON academic.discentes(tipo_curso);
CREATE INDEX idx_discentes_turma ON academic.discentes(turma);
CREATE INDEX idx_discentes_bolsista ON academic.discentes(bolsista) WHERE bolsista = TRUE;
CREATE INDEX idx_discentes_data_ingresso ON academic.discentes(data_ingresso);
CREATE INDEX idx_discentes_lookup ON academic.discentes(programa_id, numero_matricula);

-- Disciplinas
CREATE INDEX idx_disciplinas_programa ON academic.disciplinas(programa_id);
CREATE INDEX idx_disciplinas_codigo ON academic.disciplinas(codigo);
CREATE INDEX idx_disciplinas_linha ON academic.disciplinas(linha_pesquisa_id) WHERE linha_pesquisa_id IS NOT NULL;
CREATE INDEX idx_disciplinas_tipo ON academic.disciplinas(tipo);
CREATE INDEX idx_disciplinas_nivel ON academic.disciplinas(nivel);
CREATE INDEX idx_disciplinas_status ON academic.disciplinas(status);
CREATE INDEX idx_disciplinas_nome_trgm ON academic.disciplinas USING gin(nome gin_trgm_ops);

-- Ofertas de Disciplinas
CREATE INDEX idx_ofertas_disciplina ON academic.ofertas_disciplinas(disciplina_id);
CREATE INDEX idx_ofertas_docente ON academic.ofertas_disciplinas(docente_responsavel_id);
CREATE INDEX idx_ofertas_colaborador ON academic.ofertas_disciplinas(docente_colaborador_id) WHERE docente_colaborador_id IS NOT NULL;
CREATE INDEX idx_ofertas_periodo ON academic.ofertas_disciplinas(ano, semestre);
CREATE INDEX idx_ofertas_status ON academic.ofertas_disciplinas(status);
CREATE INDEX idx_ofertas_data_inicio ON academic.ofertas_disciplinas(data_inicio);
CREATE INDEX idx_ofertas_lookup ON academic.ofertas_disciplinas(disciplina_id, ano, semestre, turma);

-- Matrículas em Disciplinas
CREATE INDEX idx_matriculas_discente ON academic.matriculas_disciplinas(discente_id);
CREATE INDEX idx_matriculas_oferta ON academic.matriculas_disciplinas(oferta_disciplina_id);
CREATE INDEX idx_matriculas_situacao ON academic.matriculas_disciplinas(situacao);
CREATE INDEX idx_matriculas_status_final ON academic.matriculas_disciplinas(status_final);
CREATE INDEX idx_matriculas_lookup ON academic.matriculas_disciplinas(discente_id, oferta_disciplina_id);

-- Trabalhos de Conclusão
CREATE INDEX idx_trabalhos_discente ON academic.trabalhos_conclusao(discente_id);
CREATE INDEX idx_trabalhos_programa ON academic.trabalhos_conclusao(programa_id);
CREATE INDEX idx_trabalhos_orientador ON academic.trabalhos_conclusao(orientador_id);
CREATE INDEX idx_trabalhos_coorientador ON academic.trabalhos_conclusao(coorientador_id) WHERE coorientador_id IS NOT NULL;
CREATE INDEX idx_trabalhos_tipo ON academic.trabalhos_conclusao(tipo);
CREATE INDEX idx_trabalhos_status ON academic.trabalhos_conclusao(status);
CREATE INDEX idx_trabalhos_ano ON academic.trabalhos_conclusao(ano_defesa) WHERE ano_defesa IS NOT NULL;
CREATE INDEX idx_trabalhos_openalex ON academic.trabalhos_conclusao(openalex_work_id) WHERE openalex_work_id IS NOT NULL;
CREATE INDEX idx_trabalhos_doi ON academic.trabalhos_conclusao(doi) WHERE doi IS NOT NULL;
CREATE INDEX idx_trabalhos_titulo_trgm ON academic.trabalhos_conclusao USING gin(titulo gin_trgm_ops);

-- Bancas
CREATE INDEX idx_bancas_trabalho ON academic.bancas(trabalho_conclusao_id) WHERE trabalho_conclusao_id IS NOT NULL;
CREATE INDEX idx_bancas_discente ON academic.bancas(discente_id);
CREATE INDEX idx_bancas_tipo ON academic.bancas(tipo);
CREATE INDEX idx_bancas_presidente ON academic.bancas(presidente_id);
CREATE INDEX idx_bancas_status ON academic.bancas(status);
CREATE INDEX idx_bancas_data_agendada ON academic.bancas(data_agendada);
CREATE INDEX idx_bancas_data_realizacao ON academic.bancas(data_realizacao) WHERE data_realizacao IS NOT NULL;

-- Membros de Banca
CREATE INDEX idx_membros_banca ON academic.membros_banca(banca_id);
CREATE INDEX idx_membros_docente ON academic.membros_banca(docente_id) WHERE docente_id IS NOT NULL;
CREATE INDEX idx_membros_funcao ON academic.membros_banca(funcao);
CREATE INDEX idx_membros_tipo ON academic.membros_banca(tipo);
CREATE INDEX idx_membros_confirmado ON academic.membros_banca(confirmado);

-- =====================================================
-- ÍNDICES COMPOSTOS PARA QUERIES COMPLEXAS
-- =====================================================

-- Discentes ativos de um programa por tipo de curso
CREATE INDEX idx_discentes_programa_curso_status
    ON academic.discentes(programa_id, tipo_curso, status);

-- Ofertas abertas de um período
CREATE INDEX idx_ofertas_periodo_status
    ON academic.ofertas_disciplinas(ano, semestre, status)
    WHERE status IN ('Aberta', 'Em_Curso');

-- Trabalhos defendidos por ano e programa
CREATE INDEX idx_trabalhos_programa_ano_status
    ON academic.trabalhos_conclusao(programa_id, ano_defesa, status)
    WHERE status IN ('Defendido', 'Aprovado', 'Publicado');

-- Docentes ativos por programa
CREATE INDEX idx_docentes_programa_status
    ON academic.docentes(programa_id, status)
    WHERE status = 'Ativo';

-- =====================================================
-- ÍNDICES JSONB (para campos flexíveis)
-- =====================================================

-- Endereços (busca por cidade, estado)
CREATE INDEX idx_instituicoes_endereco ON core.instituicoes USING gin(endereco);
CREATE INDEX idx_usuarios_endereco ON auth.usuarios USING gin(endereco);

-- Contatos
CREATE INDEX idx_instituicoes_contatos ON core.instituicoes USING gin(contatos);

-- Configurações
CREATE INDEX idx_instituicoes_config ON core.instituicoes USING gin(configuracoes);
CREATE INDEX idx_programas_config ON core.programas USING gin(configuracoes);
CREATE INDEX idx_usuarios_config ON auth.usuarios USING gin(configuracoes);

-- Horários de ofertas
CREATE INDEX idx_ofertas_horarios ON academic.ofertas_disciplinas USING gin(horarios);

-- Prorrogações de discentes
CREATE INDEX idx_discentes_prorrogacoes ON academic.discentes USING gin(prorrogacoes);

-- Documentos de discentes
CREATE INDEX idx_discentes_documentos ON academic.discentes USING gin(documentos);
