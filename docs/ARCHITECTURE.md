# Arquitetura

Visão das camadas do CRUD de Empresas (JSF) no **Apache Tomcat 10.1**.

Base do código: `com.onboardingrsd.empresas`

---

## Camadas

```text
View (XHTML / PrimeFaces)
        │  EL / ações
        ▼
controller    → controladores de tela (@Named, escopos CDI)
        │
        ▼
service       → regras de aplicação / @Transactional (interceptor RESOURCE_LOCAL)
        │
        ▼
repository    → acesso a dados (@Inject EntityManager)
        │
        ▼
persistence   → HikariCP, EMF/EM, interceptor TX (infra Tomcat)
        │
        ▼
model         → entidades e enums de domínio
        │
        ▼
PostgreSQL
```

| Pacote | Responsabilidade | O que NÃO deve fazer |
|--------|------------------|----------------------|
| `model` | Entidades, enums, tipos de domínio | Acessar `EntityManager`, conhecer Faces |
| `repository` | Persistência e consultas | Regras de negócio, mensagens JSF |
| `service` | Casos de uso, orquestração, fronteira transacional | Montar componentes de UI |
| `controller` | Ligação view ↔ service, navegação, estado de tela | SQL/JPQL direto (salvo exceção justificada) |
| `converter` | Conversores JSF (ex.: CNPJ máscara ↔ dígitos) | Regras de negócio / acesso a dados |
| `util` | Helpers transversais **só com necessidade real** (ex.: `CnpjUtil`) | Virar “god package”; beans `@Named` |
| `export` | Modelos de seleção de colunas para exportação tabular | Gerar bytes do arquivo; conhecer Empresa |
| `persistence` | DataSource, EMF/EM producers, interceptor `@Transactional` | Regras de negócio / UI |

Pacote `persistence` nasceu na migração Tomcat (D029): o servlet container não fornece JTA/JNDI EE.  
Pacote `controller` substitui o antigo `bean` (D030): mesma responsabilidade, nomenclatura alinhada a projetos corporativos JSF.

---

## Organização Facelets (`webapp`)

```text
src/main/webapp/
  index.xhtml                 # welcome-file (raiz)
  pages/                      # páginas de negócio
    empresas.xhtml
    empresa-form.xhtml
  WEB-INF/
    templates/layout.xhtml    # shell: sidebar MegaMenu + conteúdo
    includes/
      app-menu.xhtml          # fragmento do menu (desktop + mobile)
      empresas/               # fragments específicos da listagem
    web.xml / faces-config.xml / beans.xml
  resources/
    css/                      # toast, datePicker, layout (shell)
    components/               # composite components Facelets
  META-INF/context.xml        # BeanManager Weld no Tomcat
```

| Local | Uso |
|-------|-----|
| `pages/` | Views públicas processadas pelo Faces |
| `WEB-INF/templates/` | Layout Facelets (`ui:composition`) — shell com sidebar |
| `WEB-INF/includes/` | Fragments (`ui:include`): menu global e por tela |
| `resources/components/` | Composites reutilizáveis entre páginas |

---

## Organização do banco (`resources/db`)

```text
src/main/resources/db/
  00-create-database.sql      # CREATE DATABASE (fora do Flyway)
  migration/                  # Flyway: V1 tabela, V2 seed, …
  dev/reset_empresas.sql      # TRUNCATE + seed (manual, destrutivo)
  README.md                   # fluxo, baseline, comandos
```

| Responsabilidade | Dono |
|------------------|------|
| Existência do DATABASE | `00-create-database.sql` (uma vez) |
| Tabelas / constraints / seed baseline | Flyway (`mvn flyway:migrate`) |
| Conferência entity × schema | Hibernate `validate` |
| JDBC da aplicação | `db.properties` + HikariCP |

A aplicação **não** dropa nem recria o banco no startup (D033).

---

## Fluxo típico (CRUD atual)

1. Usuário interage com a página Facelets/PrimeFaces.
2. O `controller` chama o `service` (CDI / Weld no WAR).
3. O interceptor RESOURCE_LOCAL abre/confirma `EntityTransaction` conforme `@Transactional`.
4. O `repository` usa o `EntityManager` da requisição.
5. HikariCP fala com o PostgreSQL (`db.properties`).

---

## Decisões relacionadas

- Runtime atual: [DECISIONS.md](./DECISIONS.md) **D029** (Tomcat); histórico D001/D008/D017
- Faces / template / CDI / UX: D002–D004, D018, D025, **D031** (MegaMenu lateral), **D032** (export)
- Pacotes: D005, D026, D027, D029 (`persistence`), **D030** (`controller` + `pages/`)
- Domínio / CRUD / grid / export / relatório: D006–D016, D019–D024, D027–D028
- Banco / migrações: D007 (histórico), **D033** (Flyway)

---

## Evolução

Camadas preenchidas até a Etapa 28 + runtime Tomcat (D029) + reorganização estrutural (D030) + menu lateral (D031) + export unificado (D032) + Flyway (D033). Próximas no roadmap: polimento residual e docs finais.
