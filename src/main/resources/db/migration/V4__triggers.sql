-- =====================================================
-- PPG HUB - MIGRATION V4 - TRIGGERS
-- Cria triggers para atualização automática de timestamps
-- =====================================================

-- =====================================================
-- FUNÇÃO GENÉRICA PARA ATUALIZAR updated_at
-- =====================================================
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION update_timestamp() IS 'Atualiza automaticamente o campo updated_at';

-- =====================================================
-- TRIGGERS: CORE SCHEMA
-- =====================================================

CREATE TRIGGER instituicoes_updated_at
    BEFORE UPDATE ON core.instituicoes
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER programas_updated_at
    BEFORE UPDATE ON core.programas
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER linhas_pesquisa_updated_at
    BEFORE UPDATE ON core.linhas_pesquisa
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- =====================================================
-- TRIGGERS: AUTH SCHEMA
-- =====================================================

CREATE TRIGGER usuarios_updated_at
    BEFORE UPDATE ON auth.usuarios
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- =====================================================
-- TRIGGERS: ACADEMIC SCHEMA
-- =====================================================

CREATE TRIGGER docentes_updated_at
    BEFORE UPDATE ON academic.docentes
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER discentes_updated_at
    BEFORE UPDATE ON academic.discentes
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER disciplinas_updated_at
    BEFORE UPDATE ON academic.disciplinas
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER ofertas_disciplinas_updated_at
    BEFORE UPDATE ON academic.ofertas_disciplinas
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER matriculas_disciplinas_updated_at
    BEFORE UPDATE ON academic.matriculas_disciplinas
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trabalhos_conclusao_updated_at
    BEFORE UPDATE ON academic.trabalhos_conclusao
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER bancas_updated_at
    BEFORE UPDATE ON academic.bancas
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- =====================================================
-- TRIGGER: ATUALIZAR VAGAS OCUPADAS AUTOMATICAMENTE
-- =====================================================
CREATE OR REPLACE FUNCTION atualizar_vagas_oferta()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' AND NEW.situacao = 'Matriculado' THEN
        -- Incrementar vagas ocupadas
        UPDATE academic.ofertas_disciplinas
        SET vagas_ocupadas = vagas_ocupadas + 1
        WHERE id = NEW.oferta_disciplina_id;

        -- Verificar se ainda há vagas
        IF (SELECT vagas_ocupadas >= vagas_oferecidas
            FROM academic.ofertas_disciplinas
            WHERE id = NEW.oferta_disciplina_id) THEN
            RAISE NOTICE 'Oferta % está com todas as vagas ocupadas', NEW.oferta_disciplina_id;
        END IF;

    ELSIF TG_OP = 'DELETE' AND OLD.situacao = 'Matriculado' THEN
        -- Decrementar vagas ocupadas
        UPDATE academic.ofertas_disciplinas
        SET vagas_ocupadas = GREATEST(0, vagas_ocupadas - 1)
        WHERE id = OLD.oferta_disciplina_id;

    ELSIF TG_OP = 'UPDATE' THEN
        -- Se mudou de Matriculado para outro status
        IF OLD.situacao = 'Matriculado' AND NEW.situacao != 'Matriculado' THEN
            UPDATE academic.ofertas_disciplinas
            SET vagas_ocupadas = GREATEST(0, vagas_ocupadas - 1)
            WHERE id = NEW.oferta_disciplina_id;
        -- Se mudou de outro status para Matriculado
        ELSIF OLD.situacao != 'Matriculado' AND NEW.situacao = 'Matriculado' THEN
            UPDATE academic.ofertas_disciplinas
            SET vagas_ocupadas = vagas_ocupadas + 1
            WHERE id = NEW.oferta_disciplina_id;
        END IF;
    END IF;

    RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER matricula_atualiza_vagas
    AFTER INSERT OR UPDATE OR DELETE ON academic.matriculas_disciplinas
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_vagas_oferta();

COMMENT ON TRIGGER matricula_atualiza_vagas ON academic.matriculas_disciplinas
    IS 'Atualiza automaticamente o contador de vagas ocupadas na oferta';

-- =====================================================
-- TRIGGER: VALIDAR LIMITE DE ORIENTANDOS
-- =====================================================
CREATE OR REPLACE FUNCTION validar_limite_orientandos()
RETURNS TRIGGER AS $$
DECLARE
    orientandos_atuais INTEGER;
    limite_orientandos INTEGER := 10; -- 5 mestrado + 5 doutorado
BEGIN
    -- Contar orientandos ativos do orientador
    SELECT COUNT(*)
    INTO orientandos_atuais
    FROM academic.discentes
    WHERE orientador_id = NEW.orientador_id
      AND status IN ('Matriculado', 'Cursando', 'Qualificado', 'Defendendo');

    -- Verificar limite
    IF orientandos_atuais >= limite_orientandos THEN
        RAISE EXCEPTION 'Docente já atingiu o limite máximo de % orientandos', limite_orientandos;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER discente_valida_orientador
    BEFORE INSERT OR UPDATE OF orientador_id ON academic.discentes
    FOR EACH ROW
    WHEN (NEW.orientador_id IS NOT NULL)
    EXECUTE FUNCTION validar_limite_orientandos();

COMMENT ON TRIGGER discente_valida_orientador ON academic.discentes
    IS 'Valida se o orientador não excedeu o limite de orientandos';

-- =====================================================
-- TRIGGER: RESETAR TENTATIVAS DE LOGIN APÓS SUCESSO
-- =====================================================
CREATE OR REPLACE FUNCTION resetar_tentativas_login()
RETURNS TRIGGER AS $$
BEGIN
    -- Se o último login foi atualizado, resetar tentativas
    IF NEW.ultimo_login IS DISTINCT FROM OLD.ultimo_login THEN
        NEW.tentativas_login := 0;
        NEW.conta_bloqueada := FALSE;
        NEW.bloqueada_ate := NULL;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER usuario_reset_tentativas
    BEFORE UPDATE OF ultimo_login ON auth.usuarios
    FOR EACH ROW
    EXECUTE FUNCTION resetar_tentativas_login();

COMMENT ON TRIGGER usuario_reset_tentativas ON auth.usuarios
    IS 'Reseta tentativas de login após login bem-sucedido';

-- =====================================================
-- TRIGGER: BLOQUEAR CONTA APÓS 5 TENTATIVAS FALHAS
-- =====================================================
CREATE OR REPLACE FUNCTION bloquear_conta_tentativas()
RETURNS TRIGGER AS $$
BEGIN
    -- Se atingiu 5 tentativas, bloquear por 30 minutos
    IF NEW.tentativas_login >= 5 THEN
        NEW.conta_bloqueada := TRUE;
        NEW.bloqueada_ate := NOW() + INTERVAL '30 minutes';

        -- Inserir log de auditoria
        INSERT INTO auth.audit_logs (usuario_id, acao, entidade, entidade_id)
        VALUES (NEW.id, 'CONTA_BLOQUEADA', 'usuarios', NEW.id);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER usuario_bloquear_conta
    BEFORE UPDATE OF tentativas_login ON auth.usuarios
    FOR EACH ROW
    EXECUTE FUNCTION bloquear_conta_tentativas();

COMMENT ON TRIGGER usuario_bloquear_conta ON auth.usuarios
    IS 'Bloqueia conta após 5 tentativas de login falhadas';

-- =====================================================
-- TRIGGER: LIMPAR SESSÕES EXPIRADAS (executar via scheduler)
-- =====================================================
CREATE OR REPLACE FUNCTION limpar_sessoes_expiradas()
RETURNS INTEGER AS $$
DECLARE
    linhas_deletadas INTEGER;
BEGIN
    DELETE FROM auth.sessoes
    WHERE expires_at < NOW() - INTERVAL '7 days';

    GET DIAGNOSTICS linhas_deletadas = ROW_COUNT;

    RETURN linhas_deletadas;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION limpar_sessoes_expiradas()
    IS 'Remove sessões expiradas há mais de 7 dias - executar diariamente via cron';
