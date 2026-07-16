-- Cria o DATABASE empresas_jsf (one-shot, fora do Flyway).
-- Conectar ao database de manutenção (ex.: postgres), NÃO a empresas_jsf.
--
-- Via psql:
--   psql -U postgres -f src/main/resources/db/00-create-database.sql
--
-- Idempotente: só cria se ainda não existir (PostgreSQL / psql \gexec).

SELECT 'CREATE DATABASE empresas_jsf WITH ENCODING ''UTF8'' TEMPLATE template0'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'empresas_jsf')\gexec
