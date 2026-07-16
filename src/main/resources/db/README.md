# Scripts de banco (PostgreSQL)

Estratégia: banco **persistente** + **Flyway** + seed versionado.  
A aplicação Tomcat **não** cria nem dropa o database no startup (`hibernate.hbm2ddl.auto=validate`).

## Estrutura

```text
db/
  00-create-database.sql     # só CREATE DATABASE (fora do Flyway)
  migration/                 # migrações Flyway (V1, V2, …)
  dev/                       # scripts manuais destrutivos (não rodados pelo Flyway)
  01-schema.sql / 02-seed.sql  # stubs deprecados (não usar)
```

## Fluxo (ambiente novo)

1. Ajustar credenciais em `src/main/resources/db.properties` (e, se necessário, propriedades `flyway.*` no `pom.xml`).
2. Criar o database (uma vez):
   ```bash
   psql -U postgres -f src/main/resources/db/00-create-database.sql
   ```
   Ou criar `empresas_jsf` no pgAdmin e pular este passo.
3. Aplicar schema + seed:
   ```bash
   mvn flyway:migrate
   ```
4. Conferir:
   ```bash
   mvn flyway:info
   ```

## Dia a dia

- Subir o Tomcat: dados e schema **permanecem**.
- Nova mudança de schema: criar `migration/V{n}__descricao.sql` → `mvn flyway:migrate`.
- **Nunca** editar uma migração já aplicada.

## Banco já existente (antes do Flyway)

Se a tabela `empresa` já existe e ainda não há `flyway_schema_history`:

```bash
mvn flyway:baseline -Dflyway.baselineVersion=2 -Dflyway.baselineDescription="Existing schema+seed"
mvn flyway:info
```

Isso marca V1 e V2 como já aplicadas (baseline na versão 2). Novas migrações começam em `V3__...`.

## Reset de dados (só desenvolvimento local)

**Destrutivo.** Apaga todas as linhas de `empresa` e reinsere o seed:

```bash
psql -U postgres -d empresas_jsf -f src/main/resources/db/dev/reset_empresas.sql
```

Não use em produção. Não rode `flyway clean` fora de lab descartável.

## Relação com a aplicação

| Camada | Responsabilidade |
|--------|------------------|
| `00-create-database.sql` | Existência do DATABASE |
| Flyway (`migration/`) | Tabelas, constraints, seed baseline |
| Hibernate `validate` | Conferir entity × schema no deploy |
| `db.properties` | JDBC da aplicação (HikariCP) |
