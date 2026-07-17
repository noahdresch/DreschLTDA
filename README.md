# CRUD Empresas JSF

Aplicação web para cadastro e gestão de empresas, construída com Java, Jakarta Faces (JSF), PrimeFaces, CDI, JPA/Hibernate e PostgreSQL. Pode ser executada no Apache Tomcat localmente ou via **Docker Compose** (PostgreSQL + Flyway + aplicação).

---

## 1. Visão Geral

### Descrição

O **empresas-jsf** é um projeto Maven empacotado como WAR que oferece um CRUD completo da entidade **Empresa**. A interface utiliza Facelets e componentes PrimeFaces; a persistência é feita via JPA (Hibernate) sobre PostgreSQL, com pool JDBC HikariCP e migrações de schema via Flyway.

O repositório inclui empacotamento **Docker multi-stage** e orquestração com **Docker Compose**, permitindo subir banco, migrações e aplicação com um único comando.

### Objetivo

Permitir o gerenciamento de empresas (cadastro, consulta, edição e exclusão), com pesquisa, paginação, ordenação e exportação de dados — em um ambiente alinhado a práticas comuns de aplicações corporativas JSF, inclusive em containers.

### Principais funcionalidades

- CRUD completo de empresas
- Pesquisa por razão social, nome fantasia, CNPJ e ramo de atividade
- Paginação e ordenação na listagem
- Visualização de detalhes por seleção de linha
- Exportação para Excel (XLSX) e PDF com seleção de colunas
- Validação de campos (Bean Validation) e unicidade de CNPJ
- Layout com menu lateral e tema PrimeFaces Vela
- Execução local tradicional (Maven + Tomcat + PostgreSQL) ou via Docker Compose

---

## 2. Arquitetura

### Visão geral da aplicação

A aplicação segue uma arquitetura em camadas, com responsabilidades bem definidas e sem acoplamento desnecessário entre UI e persistência.

```
XHTML / PrimeFaces
        │
        ▼
   Controller (CDI)
        │
        ▼
     Service
        │
        ├──► TransactionalInterceptor (@Transactional)
        │
        ▼
    Repository
        │
        ▼
  EntityManager (JPA/Hibernate)
        │
        ▼
     PostgreSQL
```

### Arquitetura com Docker Compose

Em ambiente containerizado, três serviços colaboram na ordem abaixo:

```
┌──────────────────────────────────────────────────────────────────────┐
│                     Docker Compose                           │       │
│                                                              │       │
│  ┌──────────┐    healthy     ┌───────────┐   migrate OK   ┌──┴───┐   │
│  │ postgres │ ─────────────► │  flyway   │ ─────────────► │ app  │   │
│  │ (PG 17)  │                │ (one-shot)│                │Tomcat│   │
│  └────┬─────┘                └───────────┘                └──┬───┘   │
│       │                      volume:                         │       │
│       │                      db/migration                    │       │
│       ▼                                                      │       │
│  volume postgres_data                                 porta APP_PORT │
└──────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
                     http://localhost:APP_PORT/
```

| Serviço | Imagem / build | Papel |
|---------|----------------|--------|
| **postgres** | `postgres:17` | Banco persistente (`POSTGRES_DB`); healthcheck com `pg_isready` |
| **flyway** | `flyway/flyway:10` | Aplica `src/main/resources/db/migration` e encerra |
| **app** | Build do `Dockerfile` | Build Maven + Tomcat com o WAR em `ROOT.war` |

Ordem de subida: PostgreSQL saudável → Flyway conclui `migrate` → aplicação sobe. A rede interna do Compose resolve o hostname `postgres` (usado em `db.properties` / JDBC da app).

### Fluxo da aplicação (requisição)

1. O navegador solicita uma página `*.xhtml`.
2. O **Faces Servlet** processa a requisição e resolve os beans CDI.
3. O **Controller** (`@ViewScoped` / `@RequestScoped`) trata eventos da tela e chama o **Service**.
4. O **Service** aplica o caso de uso e delimita a transação (`@Transactional`).
5. O **Repository** executa JPQL/operações JPA via `EntityManager`.
6. O **Hibernate** acessa o PostgreSQL através do DataSource HikariCP.
7. A resposta é renderizada em Facelets/PrimeFaces e devolvida ao cliente.

A persistência usa `transaction-type="RESOURCE_LOCAL"` (adequado ao Tomcat). O suporte a `@Transactional` é feito por um interceptor CDI próprio, registrado em `WEB-INF/beans.xml` — não há JTA de application server.

### Organização das camadas e pacotes

Base: `com.onboardingrsd.empresas`

| Camada | Pacote / localização | Responsabilidade |
|--------|----------------------|------------------|
| **View** | `src/main/webapp` | Páginas XHTML, templates Facelets, includes e recursos CSS |
| **Controller** | `controller` | Orquestra a UI: listagem, formulário, navegação e formatação de CNPJ |
| **Service** | `service` | Casos de uso de Empresa e limites transacionais |
| **Repository** | `repository` | Acesso a dados via JPA/JPQL |
| **Model** | `model` | Entidade `Empresa` e enum `TipoEmpresa` |
| **Persistence** | `persistence` | Producers de DataSource, `EntityManagerFactory` e `EntityManager`; interceptor de transação |
| **Export** | `export` | Seleção de colunas para exportação tabular |
| **Converter** | `converter` | Conversão de CNPJ (máscara ↔ dígitos) |
| **Util** | `util` | Utilitários de suporte (ex.: CNPJ) |

### Responsabilidades por camada

- **Controller** — recebe ações da view, mantém estado de tela, exibe mensagens Faces e delega regras ao service. Não acessa `EntityManager` diretamente.
- **Service** — concentra operações de negócio (listar, pesquisar, cadastrar, atualizar, excluir, verificar CNPJ). Anotado com `@Transactional`.
- **Repository** — encapsula consultas e operações de persistência (`persist`, `merge`, `remove`, JPQL).
- **Entity (Model)** — mapeamento JPA + anotações de Bean Validation.
- **Persistence** — infraestrutura Tomcat: pool JDBC, fábrica de EM e controle transacional RESOURCE_LOCAL.

---

## 3. Tecnologias Utilizadas

### Aplicação

| Tecnologia | Versão / detalhe | Uso |
|------------|------------------|-----|
| **Java** | 17 | Linguagem e bytecode alvo (`maven.compiler.release`) |
| **Maven** | 3.x | Build e empacotamento WAR |
| **Jakarta Faces (Mojarra)** | 4.0.7 | Framework MVC web (JSF) |
| **PrimeFaces** | 13.0.10 (classifier `jakarta`) | Componentes de UI; tema **Vela** |
| **CDI (Weld Servlet)** | 5.1.2.Final | Injeção de dependências no Tomcat |
| **Jakarta Persistence (Hibernate ORM)** | 6.4.10.Final | Mapeamento objeto-relacional |
| **Hibernate Validator** | 8.0.1.Final | Bean Validation |
| **Jakarta Transactions API** | 2.0.1 | Anotação `@Transactional` (com interceptor próprio) |
| **HikariCP** | 5.1.0 | Pool de conexões JDBC |
| **PostgreSQL JDBC** | 42.7.4 | Driver do banco |
| **Apache POI** | 5.2.5 | Exportação Excel (XLSX) |
| **OpenPDF** | 1.3.39 | Exportação PDF |
| **Flyway** | 10.x (plugin Maven e imagem Docker) | Migrações de schema e seed |
| **Apache Tomcat** | 10.1 (local) / **11.0** (imagem Docker) | Servidor de aplicação (Jakarta) |
| **Jakarta Servlet API** | 6.0.0 | API fornecida pelo Tomcat (`provided`) |

### Infraestrutura e containers

| Tecnologia | Versão / detalhe | Uso |
|------------|------------------|-----|
| **Docker** | Engine + CLI | Build e execução de imagens |
| **Docker Compose** | formato `3.9` | Orquestração dos serviços |
| **PostgreSQL** | imagem `postgres:17` | Banco no Compose |
| **Flyway** | imagem `flyway/flyway:10` | Migrações one-shot no Compose |
| **Maven (build image)** | `maven:3.9.9-eclipse-temurin-17` | Stage de build do `Dockerfile` |
| **Tomcat (runtime image)** | `tomcat:11.0-jdk17-temurin` | Stage de runtime do `Dockerfile` |

---

## 4. Estrutura do Projeto

```text
empresas-jsf/
├── pom.xml                          # Dependências, plugins e Flyway (Maven)
├── Dockerfile                       # Build multi-stage (Maven → Tomcat)
├── docker-compose.yml               # Serviços: postgres, flyway, app
├── .dockerignore                    # Exclusões do contexto de build
├── .env.example                     # Modelo de variáveis de ambiente
├── .env                             # Variáveis locais (não versionado)
├── src/main/
│   ├── java/com/onboardingrsd/empresas/
│   │   ├── controller/              # Beans CDI das telas e navegação
│   │   ├── converter/               # Conversores JSF (CNPJ)
│   │   ├── export/                  # Modelo de colunas para exportação
│   │   ├── model/                   # Entidades e enums de domínio
│   │   ├── persistence/             # DataSource, EMF/EM e interceptor transacional
│   │   ├── repository/              # Acesso a dados
│   │   ├── service/                 # Casos de uso
│   │   └── util/                    # Utilitários
│   ├── resources/
│   │   ├── META-INF/persistence.xml # Unidade de persistência empresasPU
│   │   ├── db.properties            # JDBC HikariCP (host postgres no Docker)
│   │   └── db/
│   │       ├── 00-create-database.sql
│   │       ├── db-docker.properties # Referência JDBC para rede Docker
│   │       ├── migration/           # Scripts Flyway (V1, V2, …)
│   │       ├── dev/                 # Scripts manuais de desenvolvimento
│   │       └── README.md            # Detalhes dos scripts de banco
│   └── webapp/
│       ├── index.xhtml              # Página inicial
│       ├── pages/                   # Telas de listagem e formulário
│       ├── resources/               # CSS e componentes compostos
│       ├── META-INF/context.xml     # Resource BeanManager (Weld)
│       └── WEB-INF/
│           ├── web.xml              # Faces Servlet, tema, CDI listener
│           ├── beans.xml            # CDI + interceptor @Transactional
│           ├── faces-config.xml
│           ├── templates/           # Layout Facelets
│           └── includes/            # Fragmentos reutilizáveis (menu, diálogos)
└── target/
    └── empresas-jsf.war             # Artefato gerado pelo Maven
```

---

## 5. Pré-requisitos

### Opção A — Docker (recomendada para subir tudo junto)

| Requisito | Observação |
|-----------|------------|
| **Docker** | Docker Desktop (Windows/macOS) ou Engine + Compose no Linux |
| **Arquivo `.env`** | Copiar de `.env.example` e preencher |

Não é necessário instalar JDK, Maven, Tomcat ou PostgreSQL no host para essa opção (ficam nas imagens).

### Opção B — Execução local (sem containers)

| Requisito | Observação |
|-----------|------------|
| **JDK 17** ou superior | Compilação e runtime |
| **Apache Maven 3.9+** | Build e `flyway:migrate` |
| **Apache Tomcat 10.1** | Deploy manual do WAR |
| **PostgreSQL** | Banco relacional (14+ recomendado; Compose usa 17) |
| **psql** (opcional) | Facilita criação do database e reset de dados |

Portas típicas no host: PostgreSQL `5432` (local) ou `5433` (mapeamento Compose no `.env`), aplicação `8080`.

---

## 6. Como executar o projeto

### 6.1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd DreschLTDA
```

---

### 6.2. Executar com Docker Compose (recomendado)

#### 6.2.1. Configurar variáveis de ambiente

```bash
cp .env.example .env
```

Preencha o `.env`. Exemplo alinhado ao uso atual:

```env
# PostgreSQL
POSTGRES_DB=empresas_jsf
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_PORT=5433

# Aplicação
APP_PORT=8080
```

- `POSTGRES_PORT` — porta no **host** mapeada para `5432` do container (útil para acessar o banco com um cliente externo).
- `APP_PORT` — porta no **host** mapeada para `8080` do Tomcat.

O arquivo `.env` não é versionado (está no `.gitignore`).

#### 6.2.2. Subir os serviços

```bash
docker compose up --build
```

O que acontece:

1. **postgres** sobe e cria o database definido em `POSTGRES_DB`.
2. Após o healthcheck, **flyway** aplica as migrações em `src/main/resources/db/migration`.
3. Com o Flyway concluído com sucesso, **app** faz o build (Maven na imagem) e sobe o Tomcat com o WAR como `ROOT.war`.

#### 6.2.3. Acessar a aplicação

Com `APP_PORT=8080`:

```text
http://localhost:8080/
```

No Docker o WAR é publicado como **ROOT**, portanto o contexto é `/` (não `/empresas-jsf`).

Páginas principais:

- Início: `http://localhost:8080/`
- Empresas: `http://localhost:8080/pages/empresas.xhtml`

#### 6.2.4. Comandos úteis

```bash
# Subir em segundo plano
docker compose up --build -d

# Logs da aplicação
docker compose logs -f app

# Parar e remover containers (mantém o volume do Postgres)
docker compose down

# Parar e apagar também os dados do banco
docker compose down -v
```

#### 6.2.5. JDBC na rede Docker

Dentro da rede do Compose, o host do banco é o nome do serviço (`postgres`), não `localhost`. Isso está refletido em `src/main/resources/db.properties` (e na referência `db/db-docker.properties`):

```properties
jdbc.url=jdbc:postgresql://postgres:5432/empresas_jsf
jdbc.user=postgres
jdbc.password=postgres
```

---

### 6.3. Executar localmente (sem Docker)

Use esta opção se preferir Tomcat e PostgreSQL instalados no host.

#### 6.3.1. Configurar o banco de dados

Ajuste `src/main/resources/db.properties` para o host local (exemplo):

```properties
jdbc.url=jdbc:postgresql://localhost:5432/empresas_jsf
jdbc.user=postgres
jdbc.password=postgres
jdbc.driver=org.postgresql.Driver
hikari.maximumPoolSize=10
hikari.poolName=EmpresasHikariPool
```

Se as credenciais do Flyway Maven forem diferentes, ajuste `flyway.*` no `pom.xml` ou use `-Dflyway.password=...`.

#### 6.3.2. Criar o database

```bash
psql -U postgres -f src/main/resources/db/00-create-database.sql
```

Ou crie manualmente o database `empresas_jsf` (UTF-8) no pgAdmin.

#### 6.3.3. Aplicar migrações

```bash
mvn flyway:migrate
mvn flyway:info
```

#### 6.3.4. Gerar o WAR e fazer deploy

```bash
mvn clean package
```

1. Copie `target/empresas-jsf.war` para `webapps/` do Tomcat 10.1.
2. Inicie o Tomcat.
3. Acesse:

```text
http://localhost:8080/empresas-jsf/
```

Neste modo o contexto costuma ser `/empresas-jsf` (nome do WAR), diferente do deploy Docker como `ROOT`.

---

## 7. Banco de Dados

### Estratégia

| Responsável | Papel |
|-------------|--------|
| `POSTGRES_DB` (Compose) / `00-create-database.sql` (local) | Existência do DATABASE |
| Flyway (`db/migration/` via Maven ou container `flyway`) | Schema, constraints e seed versionados |
| Hibernate (`hbm2ddl.auto=validate`) | Valida entity × schema no startup — **não** cria nem altera tabelas |
| `db.properties` | Conexão JDBC da aplicação (HikariCP) |

A aplicação **não** cria o schema ao subir no Tomcat. As migrações devem existir antes do primeiro uso (Maven local ou serviço Flyway no Compose).

### Scripts disponíveis

```text
src/main/resources/db/
├── 00-create-database.sql           # CREATE DATABASE (ambiente local; no Docker o DB vem do POSTGRES_DB)
├── db-docker.properties             # Referência JDBC para hostname postgres
├── migration/
│   ├── V1__create_table_empresa.sql # Tabela empresa + UNIQUE CNPJ
│   └── V2__seed_empresas.sql        # Dados iniciais
├── dev/
│   └── reset_empresas.sql           # Apaga e reinsere seed (só desenvolvimento)
├── 01-schema.sql                    # Deprecado — não usar
└── 02-seed.sql                      # Deprecado — não usar
```

### Fluxo com Docker

1. Preencher `.env`.
2. `docker compose up --build`.
3. O container PostgreSQL cria o database; o container Flyway aplica V1/V2; a app sobe.

Dados persistem no volume nomeado `postgres_data` até um `docker compose down -v`.

### Fluxo local (sem Docker)

1. Ajustar `db.properties` (host `localhost`).
2. Executar `00-create-database.sql`.
3. Executar `mvn flyway:migrate`.
4. Build e deploy no Tomcat.

### Reset de dados (desenvolvimento)

Script destrutivo — remove todas as linhas de `empresa` e reinsere o seed:

```bash
# Postgres no host
psql -U postgres -d empresas_jsf -f src/main/resources/db/dev/reset_empresas.sql

# Postgres exposto pelo Compose (ex.: porta 5433 no .env)
psql -h localhost -p 5433 -U postgres -d empresas_jsf -f src/main/resources/db/dev/reset_empresas.sql
```

Não utilize em produção. Não execute `flyway clean` fora de ambientes descartáveis.

### Modelo de dados (entidade Empresa)

| Campo | Tipo | Observação |
|-------|------|------------|
| `id` | `BIGSERIAL` | Chave primária |
| `razao_social` | `VARCHAR(120)` | Obrigatório |
| `nome_fantasia` | `VARCHAR(120)` | Obrigatório |
| `cnpj` | `VARCHAR(14)` | Obrigatório, único (`uk_empresa_cnpj`) |
| `ramo_atividade` | `VARCHAR(80)` | Obrigatório |
| `data_fundacao` | `DATE` | Obrigatório |
| `tipo_empresa` | `VARCHAR(30)` | Enum: `MEI`, `EIRELI`, `LTDA`, `SA` |

Mais detalhes sobre os scripts: `src/main/resources/db/README.md`.

---

## 8. Funcionalidades

### Gestão de empresas

- **Listar** empresas ordenadas por razão social
- **Cadastrar** nova empresa
- **Editar** empresa existente
- **Excluir** com diálogo de confirmação
- **Detalhes** em diálogo ao selecionar uma linha na tabela

### Pesquisa

- Filtro textual em razão social, nome fantasia, CNPJ e ramo de atividade
- Busca de CNPJ também por dígitos (ignora máscara na digitação)

### Paginação e ordenação

- Paginação client-side via `p:dataTable` (5, 10 ou 20 linhas por página)
- Ordenação por coluna com opção de remover a ordenação (restaura ordem original da consulta)

### Exportação

- Formatos **XLSX** e **PDF**
- Diálogo para escolher quais colunas exportar
- Validação: exige ao menos uma coluna selecionada

### Validações e regras

- Bean Validation na entidade (`@NotBlank`, `@Size`, `@Pattern`, `@NotNull`)
- CNPJ armazenado com 14 dígitos; formatação na UI via converter/utilitário
- Verificação de unicidade de CNPJ no cadastro/edição (serviço + constraint no banco)

### Interface

- Template Facelets com menu lateral
- Tema PrimeFaces **Vela** (dark)
- Feedback ao usuário via mensagens Faces (toasts)

### Empacotamento e execução

- Build multi-stage no `Dockerfile` (compilação Maven + runtime Tomcat)
- Orquestração Compose com dependências e healthcheck
- Migrações aplicadas automaticamente pelo serviço Flyway antes da app

---

## 9. Padrões e Boas Práticas

- **Separação em camadas** — UI, casos de uso, acesso a dados e infraestrutura isolados.
- **CDI anotado** (`bean-discovery-mode="annotated"`) — beans descobertos por anotações (`@Named`, `@ApplicationScoped`, etc.).
- **Escopos adequados** — `@ViewScoped` nas telas de listagem/formulário; `@RequestScoped` na navegação; `@ApplicationScoped` em service/repository.
- **Repository sem regra de negócio** — consultas e CRUD de persistência apenas.
- **Transações no service** — `@Transactional` com interceptor RESOURCE_LOCAL compatível com Tomcat.
- **Schema versionado** — Flyway; migrações já aplicadas não devem ser editadas.
- **Hibernate em modo `validate`** — o schema é fonte de verdade dos scripts SQL, não do `hbm2ddl`.
- **Bean Validation na entidade** — regras de campo próximas ao modelo de domínio.
- **Facelets** — template central + includes para menu e diálogos reutilizáveis.
- **Componentes compostos** — diálogo de exportação reutilizável sob `resources/components`.
- **Docker multi-stage** — imagem final enxuta (apenas Tomcat + WAR), build isolado no stage Maven.
- **Compose com ordem garantida** — healthcheck do Postgres + `service_completed_successfully` do Flyway antes da app.
- **Segredos fora do Git** — `.env` ignorado; `.env.example` documenta as chaves necessárias.
- **Volume persistente** — dados do PostgreSQL sobrevivem a reinícios dos containers (até `down -v`).

---

## 10. Possíveis Melhorias Futuras

Sugestões de evolução a partir do estado atual da aplicação:

- **Paginação e ordenação server-side** — reduzir carga em memória e melhorar desempenho com grandes volumes
- **Autenticação e autorização** — controle de acesso às telas e operações
- **Testes automatizados** — unitários (service/repository) e de integração
- **Pipeline CI/CD** — build, migração e qualidade de código em pipeline (incluindo imagens Docker)
- **Perfis de configuração JDBC** — separar claramente `db.properties` local vs Docker sem editar o arquivo à mão
- **Novas entidades e relacionamentos** — expandir o domínio além de Empresa
- **Exclusão lógica (soft delete)** — preservar histórico sem remover fisicamente
- **API REST complementar** — exposição dos mesmos casos de uso para outros clientes
- **Observabilidade** — logging estruturado, métricas e health checks nos containers

---

## Licença e uso

Projeto destinado a estudo e desenvolvimento. Ajuste credenciais (`.env`, `db.properties`) conforme o ambiente antes de qualquer uso além do desenvolvimento local.
