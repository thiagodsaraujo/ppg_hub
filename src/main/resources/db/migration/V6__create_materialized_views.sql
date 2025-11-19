-- =====================================================
-- PPG Hub - FASE 4 - Sprint 4.2
-- Migration V6: Views Materializadas para Dashboards
-- =====================================================

-- =====================================================
-- VIEW 1: Estatísticas Gerais do Programa
-- =====================================================
CREATE MATERIALIZED VIEW academic.mv_programa_stats AS
SELECT
    p.id AS programa_id,
    p.nome AS programa_nome,
    p.sigla AS programa_sigla,
    COUNT(DISTINCT doc.id) AS total_docentes,
    COUNT(DISTINCT CASE WHEN doc.categoria = 'PERMANENTE' THEN doc.id END) AS docentes_permanentes,
    COUNT(DISTINCT dis.id) AS total_discentes,
    COUNT(DISTINCT CASE WHEN dis.tipo_curso = 'MESTRADO' THEN dis.id END) AS mestrandos,
    COUNT(DISTINCT CASE WHEN dis.tipo_curso = 'DOUTORADO' THEN dis.id END) AS doutorandos,
    COUNT(DISTINCT CASE WHEN dis.status = 'CURSANDO' THEN dis.id END) AS discentes_ativos,
    COUNT(DISTINCT CASE WHEN dis.status = 'TITULADO' THEN dis.id END) AS titulados,
    COUNT(DISTINCT d.id) AS total_disciplinas,
    COUNT(DISTINCT od.id) AS ofertas_ativas,
    AVG(CASE WHEN md.nota_final IS NOT NULL THEN md.nota_final END) AS media_notas
FROM core.programas p
LEFT JOIN academic.docentes doc ON doc.programa_id = p.id
LEFT JOIN academic.discentes dis ON dis.programa_id = p.id
LEFT JOIN academic.disciplinas d ON d.programa_id = p.id
LEFT JOIN academic.ofertas_disciplina od ON od.disciplina_id = d.id AND od.status = 'MATRICULAS_ABERTAS'
LEFT JOIN academic.matriculas_disciplina md ON md.discente_id = dis.id AND md.status = 'APROVADO'
GROUP BY p.id, p.nome, p.sigla;

-- Índice único para permitir REFRESH CONCURRENTLY
CREATE UNIQUE INDEX idx_mv_programa_stats_pk ON academic.mv_programa_stats(programa_id);

-- Índices adicionais para performance
CREATE INDEX idx_mv_programa_stats_sigla ON academic.mv_programa_stats(programa_sigla);
CREATE INDEX idx_mv_programa_stats_nome ON academic.mv_programa_stats(programa_nome);

COMMENT ON MATERIALIZED VIEW academic.mv_programa_stats IS 'Estatísticas consolidadas por programa de pós-graduação';
COMMENT ON COLUMN academic.mv_programa_stats.programa_id IS 'ID do programa';
COMMENT ON COLUMN academic.mv_programa_stats.total_docentes IS 'Total de docentes vinculados ao programa';
COMMENT ON COLUMN academic.mv_programa_stats.docentes_permanentes IS 'Total de docentes permanentes';
COMMENT ON COLUMN academic.mv_programa_stats.total_discentes IS 'Total de discentes no programa';
COMMENT ON COLUMN academic.mv_programa_stats.mestrandos IS 'Total de alunos de mestrado';
COMMENT ON COLUMN academic.mv_programa_stats.doutorandos IS 'Total de alunos de doutorado';
COMMENT ON COLUMN academic.mv_programa_stats.discentes_ativos IS 'Discentes com status CURSANDO';
COMMENT ON COLUMN academic.mv_programa_stats.titulados IS 'Discentes com status TITULADO';
COMMENT ON COLUMN academic.mv_programa_stats.media_notas IS 'Média das notas finais aprovadas';

-- =====================================================
-- VIEW 2: Produtividade e Métricas Docentes
-- =====================================================
CREATE MATERIALIZED VIEW academic.mv_producao_docente AS
SELECT
    doc.id AS docente_id,
    doc.programa_id,
    u.nome AS docente_nome,
    u.email AS docente_email,
    doc.categoria AS docente_categoria,
    COUNT(DISTINCT dis.id) AS total_orientandos,
    COUNT(DISTINCT CASE WHEN dis.status = 'CURSANDO' THEN dis.id END) AS orientandos_ativos,
    COUNT(DISTINCT CASE WHEN dis.status = 'TITULADO' THEN dis.id END) AS orientandos_titulados,
    COUNT(DISTINCT CASE WHEN dis.status IN ('DESLIGADO', 'CANCELADO') THEN dis.id END) AS orientandos_evadidos,
    COUNT(DISTINCT od.id) AS disciplinas_ministradas,
    COUNT(DISTINCT b.id) AS bancas_participadas,
    COUNT(DISTINCT CASE WHEN b.tipo_banca = 'QUALIFICACAO' THEN b.id END) AS bancas_qualificacao,
    COUNT(DISTINCT CASE WHEN b.tipo_banca = 'DEFESA' THEN b.id END) AS bancas_defesa,
    COALESCE(md.total_publicacoes, 0) AS total_publicacoes,
    COALESCE(md.total_citacoes, 0) AS total_citacoes,
    COALESCE(md.h_index, 0) AS h_index,
    COALESCE(md.i10_index, 0) AS i10_index
FROM academic.docentes doc
INNER JOIN auth.usuarios u ON u.id = doc.usuario_id
LEFT JOIN academic.discentes dis ON dis.orientador_id = doc.id
LEFT JOIN academic.ofertas_disciplina od ON od.docente_responsavel_id = doc.id
LEFT JOIN academic.membros_banca mb ON mb.docente_id = doc.id
LEFT JOIN academic.bancas b ON b.id = mb.banca_id
LEFT JOIN academic.metricas_docente md ON md.docente_id = doc.id
GROUP BY doc.id, doc.programa_id, doc.categoria, u.nome, u.email, md.total_publicacoes, md.total_citacoes, md.h_index, md.i10_index;

-- Índice único para permitir REFRESH CONCURRENTLY
CREATE UNIQUE INDEX idx_mv_producao_docente_pk ON academic.mv_producao_docente(docente_id);

-- Índices adicionais para performance
CREATE INDEX idx_mv_producao_docente_programa ON academic.mv_producao_docente(programa_id);
CREATE INDEX idx_mv_producao_docente_categoria ON academic.mv_producao_docente(docente_categoria);
CREATE INDEX idx_mv_producao_docente_publicacoes ON academic.mv_producao_docente(total_publicacoes DESC);
CREATE INDEX idx_mv_producao_docente_hindex ON academic.mv_producao_docente(h_index DESC);

COMMENT ON MATERIALIZED VIEW academic.mv_producao_docente IS 'Métricas consolidadas de produtividade docente';
COMMENT ON COLUMN academic.mv_producao_docente.docente_id IS 'ID do docente';
COMMENT ON COLUMN academic.mv_producao_docente.total_orientandos IS 'Total de orientandos (todos os status)';
COMMENT ON COLUMN academic.mv_producao_docente.orientandos_ativos IS 'Orientandos com status CURSANDO';
COMMENT ON COLUMN academic.mv_producao_docente.orientandos_titulados IS 'Orientandos com status TITULADO';
COMMENT ON COLUMN academic.mv_producao_docente.disciplinas_ministradas IS 'Total de ofertas de disciplinas ministradas';
COMMENT ON COLUMN academic.mv_producao_docente.bancas_participadas IS 'Total de bancas participadas';
COMMENT ON COLUMN academic.mv_producao_docente.h_index IS 'Índice H (OpenAlex)';

-- =====================================================
-- VIEW 3: Evasão e Conclusão por Coorte
-- =====================================================
CREATE MATERIALIZED VIEW academic.mv_evasao_conclusao AS
SELECT
    p.id AS programa_id,
    p.nome AS programa_nome,
    p.sigla AS programa_sigla,
    dis.tipo_curso,
    dis.ano_ingresso,
    COUNT(*) AS total_ingressantes,
    COUNT(CASE WHEN dis.status = 'TITULADO' THEN 1 END) AS total_titulados,
    COUNT(CASE WHEN dis.status IN ('DESLIGADO', 'CANCELADO') THEN 1 END) AS total_evadidos,
    COUNT(CASE WHEN dis.status = 'CURSANDO' THEN 1 END) AS total_cursando,
    COUNT(CASE WHEN dis.status = 'TRANCADO' THEN 1 END) AS total_trancados,
    ROUND(100.0 * COUNT(CASE WHEN dis.status = 'TITULADO' THEN 1 END) / NULLIF(COUNT(*), 0), 2) AS taxa_conclusao,
    ROUND(100.0 * COUNT(CASE WHEN dis.status IN ('DESLIGADO', 'CANCELADO') THEN 1 END) / NULLIF(COUNT(*), 0), 2) AS taxa_evasao,
    ROUND(100.0 * COUNT(CASE WHEN dis.status = 'CURSANDO' THEN 1 END) / NULLIF(COUNT(*), 0), 2) AS taxa_cursando,
    AVG(CASE
        WHEN dis.status = 'TITULADO' AND dis.data_titulacao IS NOT NULL
        THEN EXTRACT(YEAR FROM AGE(dis.data_titulacao, dis.data_ingresso))
    END) AS tempo_medio_titulacao
FROM core.programas p
INNER JOIN academic.discentes dis ON dis.programa_id = p.id
WHERE dis.ano_ingresso IS NOT NULL
GROUP BY p.id, p.nome, p.sigla, dis.tipo_curso, dis.ano_ingresso
ORDER BY p.id, dis.tipo_curso, dis.ano_ingresso;

-- Índice único composto para permitir REFRESH CONCURRENTLY
CREATE UNIQUE INDEX idx_mv_evasao_conclusao_pk ON academic.mv_evasao_conclusao(programa_id, tipo_curso, ano_ingresso);

-- Índices adicionais para performance
CREATE INDEX idx_mv_evasao_conclusao_programa ON academic.mv_evasao_conclusao(programa_id);
CREATE INDEX idx_mv_evasao_conclusao_ano ON academic.mv_evasao_conclusao(ano_ingresso DESC);
CREATE INDEX idx_mv_evasao_conclusao_tipo ON academic.mv_evasao_conclusao(tipo_curso);
CREATE INDEX idx_mv_evasao_conclusao_taxa_evasao ON academic.mv_evasao_conclusao(taxa_evasao DESC);

COMMENT ON MATERIALIZED VIEW academic.mv_evasao_conclusao IS 'Análise de evasão e conclusão por coorte de ingresso';
COMMENT ON COLUMN academic.mv_evasao_conclusao.programa_id IS 'ID do programa';
COMMENT ON COLUMN academic.mv_evasao_conclusao.tipo_curso IS 'Tipo de curso (MESTRADO/DOUTORADO)';
COMMENT ON COLUMN academic.mv_evasao_conclusao.ano_ingresso IS 'Ano de ingresso da coorte';
COMMENT ON COLUMN academic.mv_evasao_conclusao.total_ingressantes IS 'Total de alunos que ingressaram';
COMMENT ON COLUMN academic.mv_evasao_conclusao.taxa_conclusao IS 'Percentual de titulados';
COMMENT ON COLUMN academic.mv_evasao_conclusao.taxa_evasao IS 'Percentual de evadidos (desligados + cancelados)';
COMMENT ON COLUMN academic.mv_evasao_conclusao.tempo_medio_titulacao IS 'Tempo médio em anos para titulação';

-- =====================================================
-- Função para Refresh de Todas as Views
-- =====================================================
CREATE OR REPLACE FUNCTION academic.refresh_materialized_views()
RETURNS void AS $$
BEGIN
    -- Refresh concorrente para não bloquear leituras
    REFRESH MATERIALIZED VIEW CONCURRENTLY academic.mv_programa_stats;
    REFRESH MATERIALIZED VIEW CONCURRENTLY academic.mv_producao_docente;
    REFRESH MATERIALIZED VIEW CONCURRENTLY academic.mv_evasao_conclusao;

    RAISE NOTICE 'Materialized views refreshed successfully at %', NOW();
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION academic.refresh_materialized_views() IS 'Atualiza todas as views materializadas de forma concorrente';

-- =====================================================
-- Grants de Permissão
-- =====================================================
GRANT SELECT ON academic.mv_programa_stats TO ppg_user;
GRANT SELECT ON academic.mv_producao_docente TO ppg_user;
GRANT SELECT ON academic.mv_evasao_conclusao TO ppg_user;

-- =====================================================
-- Refresh Inicial das Views
-- =====================================================
REFRESH MATERIALIZED VIEW academic.mv_programa_stats;
REFRESH MATERIALIZED VIEW academic.mv_producao_docente;
REFRESH MATERIALIZED VIEW academic.mv_evasao_conclusao;
