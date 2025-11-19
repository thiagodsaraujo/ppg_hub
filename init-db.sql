-- Script executado na inicialização do container PostgreSQL
-- Cria schemas se não existirem

CREATE SCHEMA IF NOT EXISTS core;
CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS academic;

-- Garante que o usuário ppgadmin tem permissões
GRANT ALL PRIVILEGES ON SCHEMA core TO ppgadmin;
GRANT ALL PRIVILEGES ON SCHEMA auth TO ppgadmin;
GRANT ALL PRIVILEGES ON SCHEMA academic TO ppgadmin;

-- Extensões úteis
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Comentários
COMMENT ON SCHEMA core IS 'Schema para entidades CORE (Instituição, Programa, Linha Pesquisa)';
COMMENT ON SCHEMA auth IS 'Schema para autenticação e autorização (Usuario, Role, AuditLog)';
COMMENT ON SCHEMA academic IS 'Schema para gestão acadêmica (Docente, Discente, Disciplina, etc)';
