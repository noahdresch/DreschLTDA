# CRUD Empresas JSF

Aplicação web para cadastro e gestão de empresas, construída com Java, Jakarta Faces (JSF), PrimeFaces, CDI, JPA/Hibernate e PostgreSQL, executada no Apache Tomcat 10.1.

---

## 1. Visão Geral

### Descrição

O **empresas-jsf** é um projeto Maven empacotado como WAR (`empresas-jsf.war`) que oferece um CRUD completo da entidade **Empresa**. A interface utiliza Facelets e componentes PrimeFaces; a persistência é feita via JPA (Hibernate) sobre PostgreSQL, com pool JDBC HikariCP e migrações de schema via Flyway.

### Objetivo

Permitir o gerenciamento de empresas (cadastro, consulta, edição e exclusão), com pesquisa, paginação, ordenação e exportação de dados — em um ambiente alinhado a práticas comuns de aplicações corporativas JSF em Tomcat.

### Principais funcionalidades

- CRUD completo de empresas
- Pesquisa por razão social, nome fantasia, CNPJ e ramo de atividade
- Paginação e ordenação na listagem
- Visualização de detalhes por seleção de linha
- Exportação para Excel (XLSX) e PDF com seleção de colunas
- Validação de campos (Bean Validation) e unicidade de CNPJ
- Layout com menu lateral e tema PrimeFaces Vela

---

## 2. Arquitetura

### Visão geral

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

### Fluxo da aplicação

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
| **Flyway** | 10.21.0 (plugin Maven) | Migrações de schema e seed |
| **Apache Tomcat** | 10.1 | Servidor de aplicação (Servlet 6 / Jakarta) |
| **Jakarta Servlet API** | 6.0.0 | API fornecida pelo Tomcat (`provided`) |

---

## 4. Estrutura do Projeto

```text
empresas-jsf/
├── pom.xml                          # Dependências, plugins e configuração Flyway
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
│   │   ├── db.properties            # Credenciais JDBC e pool HikariCP
│   │   └── db/
│   │       ├── 00-create-database.sql
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

Antes de executar localmente, tenha instalado:

| Requisito | Observação |
|-----------|------------|
| **JDK 17** ou superior | Compilação e runtime |
| **Apache Maven 3.9+** | Build e `flyway:migrate` |
| **Apache Tomcat 10.1** | Único servidor suportado |
| **PostgreSQL** | Banco relacional (14+ recomendado) |
| **psql** (opcional) | Facilita criação do database e reset de dados |

Porta padrão assumida: PostgreSQL `5432`, Tomcat `8080`.

---

## 6. Como executar o projeto

### 6.1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd DreschLTDA
```

### 6.2. Configurar o banco de dados

Edite as credenciais em `src/main/resources/db.properties`:

```properties
jdbc.url=jdbc:postgresql://localhost:5432/empresas_jsf
jdbc.user=postgres
jdbc.password=postgres
jdbc.driver=org.postgresql.Driver
hikari.maximumPoolSize=10
hikari.poolName=EmpresasHikariPool
```

Se as credenciais do Flyway forem diferentes, ajuste também as propriedades `flyway.url`, `flyway.user` e `flyway.password` no `pom.xml`, ou sobrescreva na linha de comando (ex.: `-Dflyway.password=...`).

### 6.3. Criar o database

Execute **uma vez** (conectado ao banco de manutenção, tipicamente `postgres`):

```bash
psql -U postgres -f src/main/resources/db/00-create-database.sql
```

Alternativa: criar manualmente o database `empresas_jsf` (encoding UTF-8) no pgAdmin.

### 6.4. Aplicar migrações (schema + seed)

```bash
mvn flyway:migrate
```

Para conferir o status:

```bash
mvn flyway:info
```

### 6.5. Gerar o WAR

```bash
mvn clean package
```

O artefato será gerado em `target/empresas-jsf.war`.

### 6.6. Deploy no Apache Tomcat 10.1

1. Copie `target/empresas-jsf.war` para o diretório `webapps/` do Tomcat.
2. Inicie o Tomcat.
3. Aguarde o deploy automático do contexto `/empresas-jsf`.

### 6.7. Acessar a aplicação

```text
http://localhost:8080/empresas-jsf/
```

A página de boas-vindas é `index.xhtml`. O módulo de empresas fica em `/pages/empresas.xhtml`.

---

## 7. Banco de Dados

### Estratégia

| Responsável | Papel |
|-------------|--------|
| `00-create-database.sql` | Cria o DATABASE `empresas_jsf` (fora do Flyway) |
| Flyway (`db/migration/`) | Schema, constraints e seed versionados |
| Hibernate (`hbm2ddl.auto=validate`) | Valida entity × schema no startup — **não** cria nem altera tabelas |
| `db.properties` | Conexão JDBC da aplicação (HikariCP) |

A aplicação **não** cria o schema ao subir no Tomcat. As migrações devem ser aplicadas com Maven antes do primeiro deploy.

### Scripts disponíveis

```text
src/main/resources/db/
├── 00-create-database.sql           # CREATE DATABASE (idempotente)
├── migration/
│   ├── V1__create_table_empresa.sql # Tabela empresa + UNIQUE CNPJ
│   └── V2__seed_empresas.sql        # Dados iniciais
├── dev/
│   └── reset_empresas.sql           # Apaga e reinsere seed (só desenvolvimento)
├── 01-schema.sql                    # Deprecado — não usar
└── 02-seed.sql                      # Deprecado — não usar
```

### Fluxo recomendado (ambiente novo)

1. Ajustar `db.properties` (e `flyway.*` se necessário).
2. Executar `00-create-database.sql`.
3. Executar `mvn flyway:migrate`.
4. Fazer o build e o deploy no Tomcat.

### Reset de dados (desenvolvimento)

Script destrutivo — remove todas as linhas de `empresa` e reinsere o seed:

```bash
psql -U postgres -d empresas_jsf -f src/main/resources/db/dev/reset_empresas.sql
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

---

## 10. Possíveis Melhorias Futuras

Sugestões de evolução a partir do estado atual da aplicação:

- **Paginação e ordenação server-side** — reduzir carga em memória e melhorar desempenho com grandes volumes
- **Autenticação e autorização** — controle de acesso às telas e operações
- **Testes automatizados** — unitários (service/repository) e de integração
- **Pipeline CI/CD** — build, migração e qualidade de código em pipeline
- **Novas entidades e relacionamentos** — expandir o domínio além de Empresa
- **Exclusão lógica (soft delete)** — preservar histórico sem remover fisicamente
- **API REST complementar** — exposição dos mesmos casos de uso para outros clientes
- **Observabilidade** — logging estruturado, métricas e health checks

---

## Licença e uso

Projeto destinado a estudo e desenvolvimento. Ajuste credenciais e configurações conforme o ambiente antes de qualquer uso além do desenvolvimento local.
