-- =====================================================
-- PPG HUB - MIGRATION V5 - SEED DATA
-- Insere dados iniciais necessários para o sistema
-- =====================================================

-- =====================================================
-- INSERIR ROLES PADRÃO
-- =====================================================
INSERT INTO auth.roles (nome, descricao, nivel_acesso, permissoes, ativo) VALUES
('SUPERADMIN', 'Administrador de sistema com acesso total', 5, '{"all": true}', true),
('ADMIN_INSTITUCIONAL', 'Administrador da instituição', 4, '{"manage_institution": true, "manage_programs": true, "view_all_data": true}', true),
('COORDENADOR', 'Coordenador de programa', 3, '{"manage_program": true, "view_analytics": true, "manage_students": true, "manage_teachers": true}', true),
('SECRETARIA', 'Secretaria acadêmica', 2, '{"manage_students": true, "manage_documents": true, "view_records": true, "register_grades": true}', true),
('DOCENTE', 'Professor do programa', 2, '{"view_students": true, "manage_advisees": true, "register_grades": true, "view_analytics": true}', true),
('DISCENTE', 'Estudante do programa', 1, '{"view_profile": true, "submit_documents": true, "enroll_courses": true, "view_grades": true}', true),
('TECNICO', 'Técnico administrativo', 2, '{"manage_data": true, "view_reports": true, "export_data": true}', true),
('VISITANTE', 'Acesso de consulta pública', 1, '{"view_public": true}', true)
ON CONFLICT (nome) DO NOTHING;

COMMENT ON TABLE auth.roles IS 'Roles inseridos:
- SUPERADMIN: Acesso total ao sistema
- ADMIN_INSTITUCIONAL: Gerencia instituição e programas
- COORDENADOR: Gerencia programa específico
- SECRETARIA: Gerencia alunos e documentos
- DOCENTE: Professor com orientandos
- DISCENTE: Estudante
- TECNICO: Suporte técnico
- VISITANTE: Consulta pública';

-- =====================================================
-- VIEWS ÚTEIS PARA REPORTS E DASHBOARDS
-- =====================================================

-- View: Histórico Acadêmico Calculado do Discente
CREATE OR REPLACE VIEW academic.historico_discente AS
SELECT
    d.id as discente_id,
    d.numero_matricula,
    d.programa_id,
    COUNT(md.id) FILTER (WHERE md.status_final = 'Aprovado') as disciplinas_aprovadas,
    COUNT(md.id) FILTER (WHERE md.status_final = 'Reprovado') as disciplinas_reprovadas,
    COUNT(md.id) FILTER (WHERE md.situacao = 'Trancado') as disciplinas_trancadas,
    SUM(disc.creditos) FILTER (WHERE md.status_final = 'Aprovado' AND disc.tipo = 'Obrigatória') as creditos_obrigatorios,
    SUM(disc.creditos) FILTER (WHERE md.status_final = 'Aprovado' AND disc.tipo = 'Eletiva') as creditos_eletivos,
    SUM(disc.creditos) FILTER (WHERE md.status_final = 'Aprovado') as total_creditos_aprovados,
    ROUND(AVG(md.nota_final) FILTER (WHERE md.status_final = 'Aprovado'), 2) as coeficiente_rendimento
FROM academic.discentes d
LEFT JOIN academic.matriculas_disciplinas md ON md.discente_id = d.id
LEFT JOIN academic.ofertas_disciplinas od ON od.id = md.oferta_disciplina_id
LEFT JOIN academic.disciplinas disc ON disc.id = od.disciplina_id
GROUP BY d.id, d.numero_matricula, d.programa_id;

COMMENT ON VIEW academic.historico_discente IS 'Histórico acadêmico calculado dos discentes';

-- View: Contagem de Orientações por Docente
CREATE OR REPLACE VIEW academic.orientacoes_docente AS
SELECT
    doc.id as docente_id,
    doc.usuario_id,
    doc.programa_id,
    COUNT(dis.id) FILTER (WHERE dis.tipo_curso = 'Mestrado' AND dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as mestrado_andamento,
    COUNT(dis.id) FILTER (WHERE dis.tipo_curso = 'Doutorado' AND dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as doutorado_andamento,
    COUNT(dis.id) FILTER (WHERE dis.tipo_curso = 'Mestrado' AND dis.status = 'Titulado') as mestrado_concluidas,
    COUNT(dis.id) FILTER (WHERE dis.tipo_curso = 'Doutorado' AND dis.status = 'Titulado') as doutorado_concluidas,
    COUNT(dis.id) FILTER (WHERE dis.coorientador_interno_id = doc.id) as coorientacoes,
    COUNT(dis.id) as total_orientacoes
FROM academic.docentes doc
LEFT JOIN academic.discentes dis ON dis.orientador_id = doc.id
GROUP BY doc.id, doc.usuario_id, doc.programa_id;

COMMENT ON VIEW academic.orientacoes_docente IS 'Contagem de orientações por docente';

-- View: Métricas Atuais dos Docentes
CREATE OR REPLACE VIEW academic.metricas_docentes_atual AS
SELECT DISTINCT ON (docente_id)
    docente_id,
    h_index,
    total_publicacoes,
    total_citacoes,
    publicacoes_ultimos_5_anos,
    fonte,
    data_coleta
FROM academic.metricas_docentes
ORDER BY docente_id, data_coleta DESC;

COMMENT ON VIEW academic.metricas_docentes_atual IS 'Métricas mais recentes de cada docente';

-- View: Dashboard do Programa
CREATE MATERIALIZED VIEW academic.dashboard_programa AS
SELECT
    p.id as programa_id,
    p.nome as programa_nome,
    p.sigla,
    p.conceito_capes,
    i.nome_abreviado as instituicao,
    -- Docentes
    COUNT(DISTINCT doc.id) FILTER (WHERE doc.status = 'Ativo') as total_docentes_ativos,
    COUNT(DISTINCT doc.id) FILTER (WHERE doc.tipo_vinculo = 'Permanente' AND doc.status = 'Ativo') as docentes_permanentes,
    COUNT(DISTINCT doc.id) FILTER (WHERE doc.tipo_vinculo IN ('Colaborador', 'Visitante') AND doc.status = 'Ativo') as docentes_colaboradores,
    -- Discentes
    COUNT(DISTINCT dis.id) FILTER (WHERE dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as total_discentes_ativos,
    COUNT(DISTINCT dis.id) FILTER (WHERE dis.tipo_curso = 'Mestrado' AND dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as mestrandos_ativos,
    COUNT(DISTINCT dis.id) FILTER (WHERE dis.tipo_curso = 'Doutorado' AND dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as doutorandos_ativos,
    COUNT(DISTINCT dis.id) FILTER (WHERE dis.status = 'Titulado') as total_egressos,
    COUNT(DISTINCT dis.id) FILTER (WHERE dis.bolsista = true AND dis.status IN ('Matriculado', 'Cursando', 'Qualificado')) as discentes_bolsistas,
    -- Disciplinas
    COUNT(DISTINCT d.id) FILTER (WHERE d.status = 'Ativa') as total_disciplinas_ativas,
    COUNT(DISTINCT d.id) FILTER (WHERE d.tipo = 'Obrigatória' AND d.status = 'Ativa') as disciplinas_obrigatorias,
    COUNT(DISTINCT d.id) FILTER (WHERE d.tipo = 'Eletiva' AND d.status = 'Ativa') as disciplinas_eletivas,
    -- Produções
    COUNT(DISTINCT tc.id) as total_trabalhos_conclusao,
    COUNT(DISTINCT tc.id) FILTER (WHERE tc.tipo = 'Dissertação') as total_dissertacoes,
    COUNT(DISTINCT tc.id) FILTER (WHERE tc.tipo = 'Tese') as total_teses,
    COUNT(DISTINCT tc.id) FILTER (WHERE EXTRACT(YEAR FROM tc.data_defesa) = EXTRACT(YEAR FROM CURRENT_DATE)) as defesas_ano_corrente,
    -- Linhas de Pesquisa
    COUNT(DISTINCT lp.id) FILTER (WHERE lp.ativa = true) as total_linhas_ativas,
    -- Timestamps
    NOW() as atualizado_em
FROM core.programas p
LEFT JOIN core.instituicoes i ON i.id = p.instituicao_id
LEFT JOIN academic.docentes doc ON doc.programa_id = p.id
LEFT JOIN academic.discentes dis ON dis.programa_id = p.id
LEFT JOIN academic.disciplinas d ON d.programa_id = p.id
LEFT JOIN academic.trabalhos_conclusao tc ON tc.programa_id = p.id
LEFT JOIN core.linhas_pesquisa lp ON lp.programa_id = p.id
WHERE p.status = 'Ativo'
GROUP BY p.id, p.nome, p.sigla, p.conceito_capes, i.nome_abreviado;

CREATE UNIQUE INDEX ON academic.dashboard_programa(programa_id);

COMMENT ON MATERIALIZED VIEW academic.dashboard_programa IS 'Dashboard consolidado de métricas do programa - refresh diário';

-- =====================================================
-- FUNÇÃO PARA REFRESH DO DASHBOARD
-- =====================================================
CREATE OR REPLACE FUNCTION refresh_dashboard_programa()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY academic.dashboard_programa;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION refresh_dashboard_programa() IS 'Atualiza o dashboard do programa - executar diariamente';
