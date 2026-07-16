# Decisões de Arquitetura

Este documento registra decisões importantes tomadas durante o desenvolvimento (ADR leve).

Cada decisão deve usar o modelo abaixo. Não apague decisões antigas: o histórico faz parte do aprendizado.

---

## Modelo

```markdown
### D00N — Título

- **Data:** AAAA-MM-DD
- **Contexto:** Qual problema ou dúvida motivou a decisão?
- **Decisão:** O que foi escolhido?
- **Motivo:** Por que essa opção é a mais adequada a este projeto de estudo?
- **Alternativas consideradas:** Opção A (prós/contras); Opção B (prós/contras)
- **Consequências:** Impactos positivos e trade-offs aceitos
```

---

## Registro

### D001 — Stack do projeto JSF

- **Data:** 2026-07-13
- **Contexto:** Era necessário fixar versões e runtime antes de criar o WAR Maven e as próximas etapas (Faces, PrimeFaces, JPA).
- **Decisão:** Java 17 LTS, Maven WAR, Jakarta Faces 4, PrimeFaces 13.x (classifier `jakarta`), CDI, JPA (provedor do WildFly), **PostgreSQL 16** (ver D017; originalmente MySQL 8), WildFly 31+ (Jakarta EE 10).
- **Motivo:** Combinação LTS moderna, alinhada a projetos corporativos atuais, sem depender do ecossistema legado `javax.*`, e com CDI/JPA disponíveis no servidor (menos configuração manual no início do estudo).
- **Alternativas consideradas:**
  - Java 21 LTS — prós: mais recente; contras: menos material didático JSF ainda cita 17.
  - JSF 2.3 (`javax.*`) + Tomcat — prós: muitos tutoriais antigos; contras: API descontinuada e CDI/JPA não vêm “de fábrica” no Tomcat.
  - Payara Community — prós: também Jakarta EE completo; contras: WildFly é referência comum em ambientes corporativos brasileiros.
  - MySQL 8 / MariaDB — prós: previstos no plano inicial; contras: preferência do estudante por PostgreSQL + pgAdmin (formalizado na D017).
- **Consequências:** Namespaces `jakarta.*`; deploy alvo WildFly; APIs Jakarta EE no `pom.xml` com scope `provided`; PrimeFaces embutido no WAR com classifier `jakarta`.

### D002 — Mapeamento do FacesServlet em `*.xhtml`

- **Data:** 2026-07-13
- **Contexto:** Era preciso expor as views Facelets de forma simples e didática.
- **Decisão:** Mapear o `FacesServlet` com `url-pattern` `*.xhtml`.
- **Motivo:** URL previsível (`/index.xhtml`), alinhada à extensão real do arquivo; facilita o aprendizado inicial.
- **Alternativas consideradas:**
  - `/faces/*` — prós: padrão clássico em vários tutoriais; contras: URL diferente do caminho do arquivo, mais indirection no início.
  - Extensionless mapping — prós: URLs limpas; contras: configuração extra desnecessária nesta fase.
- **Consequências:** Toda view processada pelo Faces deve ser `.xhtml` sob o mapeamento; `welcome-file` aponta para `index.xhtml`.

### D003 — Template Facelets em `WEB-INF/templates`

- **Data:** 2026-07-13
- **Contexto:** Várias páginas futuras compartilhariam cabeçalho/estrutura; o template não deve ser acessível como página pública.
- **Decisão:** Layout em `/WEB-INF/templates/layout.xhtml`, consumido via `ui:composition`; slots `title` e `content`.
- **Motivo:** `WEB-INF` bloqueia acesso HTTP direto; `ui:insert`/`ui:define` evitam duplicar chrome HTML.
- **Alternativas consideradas:**
  - Template na raiz de `webapp` — prós: caminho curto; contras: risco de acesso direto.
  - Incluir só com `ui:include` sem template — prós: simples; contras: menos claro para layout completo.
- **Consequências:** Novas páginas devem usar `ui:composition` com esse template; navegação/menu podem ser adicionados depois no layout.

### D004 — CDI com discovery `annotated` e bean de demonstração em request

- **Data:** 2026-07-13
- **Contexto:** Era preciso ligar a view a uma classe Java sem JPA ainda.
- **Decisão:** `beans.xml` CDI 4.0 com `bean-discovery-mode="annotated"`; `HelloBean` com `@Named` + `@RequestScoped` no pacote `bean`.
- **Motivo:** Discovery anotado é explícito e previsível; request scope basta para uma mensagem de demonstração.
- **Alternativas consideradas:**
  - `bean-discovery-mode="all"` — prós: menos anotações; contras: mais classes viram bean sem querer.
  - `@ViewScoped` já na demo — prós: antecipa CRUD; contras: complexidade e dependência Faces no primeiro bean sem necessidade.
- **Consequências:** Só classes anotadas são beans; beans de tela futuros podem migrar para `@ViewScoped` quando o estado de formulário exigir.

### D005 — Arquitetura em camadas e pacotes iniciais

- **Data:** 2026-07-13
- **Contexto:** Antes de SQL/JPA/CRUD, o projeto precisava de um mapa claro de responsabilidades.
- **Decisão:** Pacotes iniciais `com.onboardingrsd.empresas.{model,repository,service,bean}`; documentados em `ARCHITECTURE.md` e `package-info.java`. Pacotes `util`/`converter` **adiados** até necessidade real (vieram na D026).
- **Motivo:** Espelha arquitetura JSF corporativa comum; separa UI, regras e persistência; evita overengineering.
- **Alternativas consideradas:**
  - Tudo em um único pacote — prós: menos arquivos; contras: mistura responsabilidades cedo.
  - Criar `util` desde já — prós: lugar para helpers; contras: vira depósito sem critério.
  - Camada `dao` com nome diferente de `repository` — prós: vocabulário clássico; contras: `repository` é igualmente claro e alinhado ao plano do estudo.
- **Consequências:** Fluxo alvo view → bean → service → repository → model → PostgreSQL; `converter`/`util` entraram depois só para CNPJ (D026).

### D006 — Modelagem inicial do domínio Empresa (antecipação à persistência)

- **Data:** 2026-07-13
- **Contexto:** O README define os campos de Empresa; a Etapa 09 (SQL) precisa de um contrato de modelagem coerente.
- **Decisão:** Tipo da Empresa como `enum TipoEmpresa` (`MEI`, `EIRELI`, `LTDA`, `SA`); Ramo de Atividade como `String`; CNPJ como `String` (14 dígitos) com Bean Validation depois. Entity `RamoAtividade` fica fora do roadmap atual.
- **Motivo:** Ensina Enum + JPA sem CRUD auxiliar; adia complexidade de relacionamento; CNPJ não exige API externa no estudo.
- **Alternativas consideradas:**
  - `RamoAtividade` como entity desde o schema — prós: modelo mais normalizado; contras: segundo CRUD cedo demais.
  - Tipo como `String` livre — prós: flexível; contras: perde tipagem e aprendizado de `@Enumerated`.
- **Consequências:** Schema, seed, `@Entity` e enum (Etapas 09–13) seguem esse desenho.

### D007 — Database `empresas_jsf` e scripts em `src/main/resources/db`

- **Data:** 2026-07-13
- **Contexto:** Era preciso um lugar versionado para schema/seed e um nome de database estável.
- **Decisão:** Database PostgreSQL `empresas_jsf`; scripts `01-schema.sql`, `02-seed.sql` e `03-wildfly-datasource.cli` em `src/main/resources/db/`. CNPJ persistido só com dígitos (`VARCHAR(14)`). *(Originalmente MySQL; migrado na D017.)*
- **Motivo:** Scripts próximos do código facilitam o estudo; nome do DB deixa claro o projeto; CNPJ sem máscara simplifica UNIQUE e validação futura.
- **Alternativas consideradas:**
  - Scripts soltos fora do módulo Maven — prós: independentes; contras: mais fáceis de perder no clone.
  - CNPJ com máscara no banco — prós: igual à UI; contras: formatação polui constraint e consultas.
- **Consequências:** Datasource e connection URL apontam para `empresas_jsf`; seed usa `ON CONFLICT (cnpj) DO NOTHING`.

### D008 — Persistence Unit JTA com datasource WildFly e `validate`

- **Data:** 2026-07-13
- **Contexto:** Configurar JPA sem recriar o schema pelo Hibernate e alinhado ao runtime corporativo.
- **Decisão:** PU `empresasPU`, `transaction-type="JTA"`, `jta-data-source=java:jboss/datasources/EmpresaDS`, `hibernate.hbm2ddl.auto=validate`. Setup do driver/datasource documentado em CLI (não embutir JDBC no `persistence.xml`).
- **Motivo:** Padrão WildFly/Jakarta EE; schema permanece responsabilidade dos scripts SQL; falhas de mapeamento aparecem no deploy.
- **Alternativas consideradas:**
  - `RESOURCE_LOCAL` + URL JDBC no `persistence.xml` — prós: sobe sem CLI; contras: menos didático para app server e pior encaixe com transações JTA depois.
  - `hbm2ddl.auto=update` — prós: conveniência; contras: diverge dos scripts e esconde erros de modelagem.
- **Consequências:** Deploy exige `EmpresaDS` criado no WildFly; entity `Empresa` registrada na PU.

### D009 — Enum persistido com `EnumType.STRING`

- **Data:** 2026-07-13
- **Contexto:** Na Etapa 13, `tipoEmpresa` passou de `String` para `TipoEmpresa`.
- **Decisão:** `@Enumerated(EnumType.STRING)` na coluna `tipo_empresa`.
- **Motivo:** Valores legíveis no banco; compatível com o seed; reordenar constantes do enum não corrompe dados.
- **Alternativas consideradas:**
  - `EnumType.ORDINAL` — prós: coluna numérica menor; contras: frágil se a ordem do enum mudar.
- **Consequências:** Novos tipos exigem valor novo no enum **e** dado coerente no banco; UI/forms usarão as constantes do enum.

### D010 — `EmpresaRepository` só leitura com CDI + `EntityManager`

- **Data:** 2026-07-13
- **Contexto:** Isolar acesso a dados antes da UI de listagem.
- **Decisão:** Classe `EmpresaRepository` `@ApplicationScoped` com `@PersistenceContext(unitName = "empresasPU")`; métodos `findAll()` e `findById(Long)` (`Optional`). Sem escrita nesta etapa.
- **Motivo:** Ensina Repository/JPQL sem misturar com bean de tela; `Optional` deixa ausência de id explícita.
- **Alternativas consideradas:**
  - JPQL direto no `@Named` bean — prós: menos classes; contras: quebra a arquitetura (D005).
  - Interface + implementação — prós: mais “enterprise”; contras: overengineering no estudo atual.
- **Consequências:** A Etapa 15 (service) e a 16 (listagem) consomem este repository; `salvar`/`persist` entrou na Etapa 18.

### D011 — Transação e orquestração no `EmpresaService`

- **Data:** 2026-07-13
- **Contexto:** Montar a camada de aplicação entre bean e repository.
- **Decisão:** `EmpresaService` `@ApplicationScoped` com `@Inject EmpresaRepository`; métodos `listar()` / `buscarPorId()` anotados com `@Transactional(SUPPORTS)`. API de transação `provided` no Maven.
- **Motivo:** Service é o lugar natural das transações e dos casos de uso; `SUPPORTS` serve leitura sem forçar TX nova; escrita futura usará `REQUIRED`.
- **Alternativas consideradas:**
  - `@Transactional` no repository — prós: centraliza EM+TX; contras: mistura infraestrutura com API de casos de uso e dificulta regras compostas.
  - Sem `@Transactional` na leitura — prós: menos anotações; contras: com JTA/`EntityManager` container-managed pode falhar fora de contexto transacional.
- **Consequências:** Beans de tela chamam só o service; repository permanece sem anotação de transação; `cadastrar` usa `REQUIRED` (D014).

### D012 — Bean e página dedicados à listagem

- **Data:** 2026-07-13
- **Contexto:** Exibir empresas do PostgreSQL sem misturar com o Hello World CDI.
- **Decisão:** `EmpresaListBean` (`@Named`) + `empresas.xhtml` com `p:dataTable`; navegação no layout; `HelloBean` mantido só como demo. *(Escopo evoluiu para `@ViewScoped` na D019, quando entrou o filtro.)*
- **Motivo:** Separar responsabilidades de tela; DataTable demonstra o fluxo completo das camadas.
- **Alternativas consideradas:**
  - Listar na `index.xhtml` via `HelloBean` — prós: uma página só; contras: mistura demonstração e negócio.
- **Consequências:** URL principal de negócio: `/empresas.xhtml`; cadastro/edição em `empresa-form.xhtml`.

### D013 — Formulário de cadastro em `@ViewScoped`

- **Data:** 2026-07-14
- **Contexto:** Criar tela de entrada de dados sem misturar com a listagem.
- **Decisão:** `EmpresaFormBean` (`@Named` + `@ViewScoped`) + `empresa-form.xhtml`; modelo `Empresa` no bean; `TipoEmpresa.values()` para o select.
- **Motivo:** View scope preserva o formulário entre postbacks/AJAX do PrimeFaces; separação listagem × cadastro mantém um job por tela.
- **Alternativas consideradas:**
  - Reusar `EmpresaListBean` — prós: menos classes; contras: mistura responsabilidades e escopos.
  - `@RequestScoped` no form — prós: mais simples; contras: perde estado em postbacks parciais.
- **Consequências:** Persistência (Etapa 18) preenche `salvar()`; na Etapa 22 o mesmo bean passou a atender edição via `?id=`.

### D014 — Create com `persist`, TX `REQUIRED` e redirect PRG

- **Data:** 2026-07-14
- **Contexto:** Fechar o “C” do CRUD após o formulário existir.
- **Decisão:** `EmpresaRepository.salvar` → `persist`; `EmpresaService.cadastrar` com `@Transactional(REQUIRED)`; bean redireciona para `/empresas?faces-redirect=true`.
- **Motivo:** Escrita JTA precisa de TX ativa; PRG evita reenvio do formulário e recarrega a listagem limpa.
- **Alternativas consideradas:**
  - Ficar na mesma página após save — prós: menos navegação; contras: risco de resubmit e form “sujo”.
  - `merge` no create — prós: genérico; contras: menos preciso semanticamente para entidade nova.
- **Consequências:** Fluxo feliz vai à listagem; feedback amigável exigiu FacesMessages (D016).

### D015 — Bean Validation na entity `Empresa`

- **Data:** 2026-07-14
- **Contexto:** Impedir cadastro incompleto/inválido antes da persistência.
- **Decisão:** Constraints na entity (`@NotBlank`, `@Size`, `@NotNull`, `@Pattern` CNPJ `\\d{14}`); API validation `provided`; formulário com `required` + `p:message` por campo.
- **Motivo:** Um único contrato de domínio; formato de CNPJ alinhado ao schema (14 dígitos); sem dígito verificador (fora do escopo didático).
- **Alternativas consideradas:**
  - DTO só para o form — prós: desacopla JPA; contras: camada a mais cedo demais neste estudo.
  - Validar só no Faces (`required`) — prós: rápido; contras: regras não viajam com a entity.
- **Consequências:** Validação roda no ciclo JSF; mensagens de sucesso/erro de negócio ficam no bean (D016).

### D016 — FacesMessages com Flash no sucesso e erro no formulário

- **Data:** 2026-07-14
- **Contexto:** Dar feedback claro após salvar (sucesso ou falha de persistência).
- **Decisão:** Sucesso: `FacesMessage` INFO + `Flash.setKeepMessages(true)` + redirect para listagem. Erro (`PersistenceException` / causa): ERROR e `return null`. Mensagens usam summary curto (`SUCESSO` / `ERRO`) e detail descritivo. Presentation: toast global (D025), não `p:messages` nas páginas.
- **Motivo:** Flash preserva a mensagem no PRG; erro no form permite correção (ex.: CNPJ duplicado) sem perder o preenchimento.
- **Alternativas consideradas:**
  - Só log no servidor — prós: simples; contras: ruído ruim para o usuário.
  - Sucesso sem flash na mesma view — prós: menos API; contras: incompatível com o redirect escolhido (D014).
- **Consequências:** API `addMessage(null, …)` permanece estável; UX visual evoluída no layout (growl).

### D017 — Migração do banco de MySQL para PostgreSQL

- **Data:** 2026-07-14
- **Contexto:** Após as etapas iniciais com MySQL, a preferência do estudo passou a ser PostgreSQL com pgAdmin.
- **Decisão:** Banco **PostgreSQL 16**; database `empresas_jsf`; usuário de desenvolvimento `postgres`; JNDI permanece `java:jboss/datasources/EmpresaDS`; scripts SQL e CLI WildFly reescritos para PostgreSQL (`BIGSERIAL`, `ON CONFLICT`, driver `org.postgresql`).
- **Motivo:** Alinha o projeto à ferramenta que o estudante já usa (pgAdmin); PostgreSQL é padrão corporativo frequente; a API JPA/JPQL quase não muda — o impacto fica em SQL/datasource.
- **Alternativas consideradas:**
  - Manter MySQL — prós: ambiente já validado; contras: diverge da preferência e do fluxo com pgAdmin.
  - Trocar também o JNDI/nome da PU — prós: nome “postgres” explícito; contras: diff desnecessário em `persistence.xml` e beans já estáveis.
- **Consequências:** Recriar schema/seed no PostgreSQL; reinstalar driver/módulo no WildFly; MySQL deixa de ser requisito do projeto; documentações atualizadas.

### D018 — Shell visual PrimeFaces (layout e formulário)

- **Data:** 2026-07-14
- **Contexto:** Com o CRUD avançando, a UI básica (header HTML + links) ficou frágil e o formulário pouco confortável.
- **Decisão:**
  - Template com `p:menubar` (Início / Empresas), `viewport` e área de conteúdo full-width
  - Listagem: painéis `width:100%`, toolbar com Nova empresa, pesquisa fluida, DataTable `bordered` + `stripedRows`
  - Formulário: `p:panelGrid` fluido, `p:datePicker` (+ `datePicker.css`), Salvar/Voltar padronizados
- **Motivo:** Melhorar usabilidade sem abrir a Etapa 29 completa; componentes PrimeFaces padronizam navegação e formulário.
- **Alternativas consideradas:**
  - Esperar Etapa 29 para qualquer polish — prós: roadmap “puro”; contras: má experiência ao testar CRUD.
  - CSS custom pesado — prós: visual único; contras: foge do objetivo de aprender PrimeFaces.
- **Consequências:** “Nova empresa” fica na toolbar da listagem (não no menubar); toast/growl e refinamentos de grid vieram depois (D025–D026).

### D019 — Listagem `@ViewScoped` com filtro textual único

- **Data:** 2026-07-14
- **Contexto:** Etapa 21 — pesquisar sem perder o termo entre AJAX.
- **Decisão:** `EmpresaListBean` `@ViewScoped`; um campo `filtro`; JPQL `LIKE` em quatro colunas textuais; `pesquisar()` / `limpar()`.
- **Motivo:** View scope adequada a estado de tela; um termo livre ensina parâmetro JPQL sem Criteria API ainda.
- **Alternativas consideradas:**
  - Filtros por coluna/tipo/data — prós: mais preciso; contras: UI e query mais complexas nesta etapa.
  - Filtrar só no cliente (PrimeFaces filter) — prós: zero JPQL; contras: não pratica consulta no servidor.
- **Consequências:** Exclusão e seleção limpam/atualizam estado junto com o filtro.

### D020 — Edição no mesmo formulário via `viewParam` + `merge`

- **Data:** 2026-07-14
- **Contexto:** Etapa 22 — atualizar empresa sem segunda página.
- **Decisão:** Reusar `empresa-form.xhtml` com `?id=`; `f:viewParam`/`f:viewAction`; `merge` no repository; mensagens distintas cadastrada/atualizada.
- **Motivo:** Menos duplicação de XHTML; ensina passagem de id e diferença `persist`/`merge`.
- **Alternativas consideradas:**
  - Diálogo `p:dialog` na listagem — prós: sem navegação; contras: form mais difícil de estudar e validar.
  - Página `empresa-edit.xhtml` separada — prós: URLs explícitas; contras: código duplicado.
- **Consequências:** Id inexistente redireciona à listagem com erro; botão lápis na coluna Ações.

### D021 — Exclusão física com confirmação PrimeFaces

- **Data:** 2026-07-14
- **Contexto:** Etapa 23 — completar o “D” do CRUD com segurança de UI.
- **Decisão:** Hard delete (`remove`); `p:confirm` no botão + `p:confirmDialog` global; FacesMessage de sucesso (toast) na listagem.
- **Motivo:** Sem soft delete no roadmap; confirmação evita clique acidental na lixeira.
- **Alternativas consideradas:**
  - Soft delete (coluna `ativo`) — prós: recuperável; contras: fora do escopo e muda schema.
  - Exclusão sem diálogo — prós: menos código; contras: risco alto de erro do usuário.
- **Consequências:** CRUD completo (C/R/U/D + filtro); grid avançado nas Etapas 24–26.

### D022 — Paginação client-side no DataTable

- **Data:** 2026-07-14
- **Contexto:** Etapa 24 — paginar a listagem sem overengineering.
- **Decisão:** Paginador nativo do `p:dataTable` sobre a lista completa em memória; `rows` padrão 5; dropdown 5/10/20; relatório “Exibindo X–Y de Z”. Sem `LazyDataModel`.
- **Motivo:** Volume do estudo (seed + cadastros manuais) é pequeno; ensina o componente antes de paginação SQL.
- **Alternativas consideradas:**
  - Lazy / server-side (`LIMIT`/`OFFSET`) — prós: escala; contras: bem mais código para o ganho atual.
- **Consequências:** Ordenação (D023) também fica client-side para manter consistência; Lazy fica como evolução futura se o volume crescer.

### D023 — Ordenação client-side com unsort restaurando ordem original

- **Data:** 2026-07-14
- **Contexto:** Etapa 25 — ordenar o grid após a paginação existir; depois refinado para o 3º clique (unsort).
- **Decisão:** `sortMode="single"`; `sortBy` nas colunas de dados; `allowUnsorting="true"`; listener `onSort(SortEvent)` recarrega `pesquisar(filtro)` quando não há sort ativo, porque o DataTable mantinha a última ordem visual.
- **Motivo:** Alinha à paginação client-side; unsort previsível evita lista “presa” na última ordenação.
- **Alternativas consideradas:**
  - `ORDER BY` dinâmico no JPQL — prós: ordena no banco; contras: exige lazy e parâmetros de sort.
  - Ignorar unsort — prós: zero código extra; contras: UX confusa no 3º clique.
- **Consequências:** Combina com filtro e paginação; colunas CNPJ/fundação saíram do grid (ficam no detalhe) sem perder sort nas colunas restantes.

### D024 — Seleção single por clique + diálogo de detalhes

- **Data:** 2026-07-15
- **Contexto:** Etapa 26 — pós-revisão da seleção múltipla inicial (“Detalhar seleção”).
- **Decisão:** `selectionMode="single"`; clique na linha (`rowSelect`) abre `p:dialog` com `p:card`/painel de detalhes; `rowUnselect`/fechar diálogo limpa `empresaSelecionada`. Sem coluna checkbox / botão Detalhar.
- **Motivo:** Detalhe enriquecido (CNPJ mascarado, fundação, etc.) sem poluir o grid; single é mais natural para “inspect row”.
- **Alternativas consideradas:**
  - Seleção múltipla + FacesMessage — prós: lote; contras: não mostra o registro completo e conflita com o novo card.
  - Página de detalhe separada — prós: URL própria; contras: navegação a mais para um read-only.
- **Consequências:** Grid mostra Razão, Fantasia, Tipo, Ramo + Ações; toast (D025) cobre feedback; export futuro pode revisitar múltipla se necessário.

### D025 — Toast global com `p:growl`

- **Data:** 2026-07-15
- **Contexto:** `p:messages` nas páginas competia com o layout e sumia no fluxo PRG de forma menos perceptível.
- **Decisão:** `p:growl` global no `layout.xhtml` (`globalOnly`, `life=4500`, `p:autoUpdate`) + `resources/css/toast.css` (estilo StaticMessage). Beans continuam com `addMessage(null, …)` e Flash quando há redirect.
- **Motivo:** Feedback consistente em todas as telas sem alterar a API Faces; visual alinhado ao showcase PrimeFaces.
- **Alternativas consideradas:**
  - Manter só `p:messages` — prós: padrão Faces puro; contras: menos visível e repetido em cada page.
  - Biblioteca JS externa de toast — prós: animação rica; contras: fora do stack JSF/PrimeFaces do estudo.
- **Consequências:** Summary curto (`SUCESSO`/`ERRO`) + detail; páginas de negócio não precisam de bloco `p:messages` local para global.

### D026 — CNPJ mascarado na UI (`CnpjConverter` + `CnpjUtil`)

- **Data:** 2026-07-15
- **Contexto:** Usuário digita/vê CNPJ formatado, mas o banco permanece só dígitos (D007/D015).
- **Decisão:** Pacotes `converter` e `util`; `@FacesConverter("cnpjConverter")` no form e no diálogo de detalhes; `CnpjUtil` para strip/format.
- **Motivo:** Separar apresentação de armazenamento; ensina Converter JSF sem mudar o schema.
- **Alternativas consideradas:**
  - Guardar máscara no banco — prós: igual à tela; contras: piora UNIQUE/consultas.
  - Só `p:inputMask` sem converter — prós: menos classes; contras: model pode receber formato inconsistente.
- **Consequências:** Pacote `util` passou a existir com necessidade real (exceção consciente à regra de adiar util); validação de 14 dígitos continua no model após conversão.

### D027 — Export Excel com `p:dataExporter` + modal de colunas reutilizável

- **Data:** 2026-07-15
- **Contexto:** Etapa 27 — exportar listagem; requisito de escolher colunas antes do download.
- **Decisão:**
  - Excel via `p:dataExporter` `type="xlsx"` + dependência `poi-ooxml` (POI não vem no JAR do PrimeFaces).
  - Pacote `export` com `ExportColumn` / `ExportColumnSelection` (sem depender de Empresa).
  - Composite `resources/components/exportColumnsDialog.xhtml` (checklist + Cancelar/Exportar).
  - Fluxo em dois passos: AJAX valida seleção → botão oculto `ajax="false"` dispara o download.
  - `exportable` dinâmico por coluna; campos fora do grid (`id`, CNPJ, fundação) com `visible="false"`.
- **Motivo:** Pouco código alinhado ao stack; modal reaproveitável em outras listagens; evita POI “na mão” nesta etapa.
- **Alternativas consideradas:**
  - Apache POI custom no service — prós: layout total; contras: mais classes e fora do padrão PF.
  - Export imediato sem modal — prós: simples; contras: não atende escolha de colunas.
- **Consequências:** Ordem do Excel = ordem das `p:column` / checklist; Ações permanece `exportable="false"`; PDF na Etapa 28 (D028) reusa o mesmo modal.

### D028 — Relatório PDF com `p:dataExporter` + OpenPDF

- **Data:** 2026-07-15
- **Contexto:** Etapa 28 — relatório simples a partir da listagem.
- **Decisão:**
  - PDF via `p:dataExporter` `type="pdf"` + dependência `com.github.librepdf:openpdf`.
  - Mesmo modal de colunas da Etapa 27: botões **Exportar Excel** e **Gerar PDF**.
  - Entrada na toolbar: **Relatório PDF** (abre o modal; igual ao Excel).
- **Motivo:** Mesmo padrão do Excel; pouco código; adequado a “relatório simples” de estudo.
- **Alternativas consideradas:**
  - JasperReports — prós: layout rico; contras: stack extra e overengineering nesta etapa.
  - PDFBox / iText “na mão” — prós: controle total; contras: mais classes sem ganho didático agora.
- **Consequências:** Listagem filtrada vira PDF com as colunas escolhidas; pasta de salvamento continua a cargo do navegador.

### D029 — Runtime exclusivo Apache Tomcat 10.1 (WAR fat + RESOURCE_LOCAL)

- **Data:** 2026-07-15
- **Contexto:** Substituir totalmente o WildFly; a aplicação não deve depender de app server EE.
- **Decisão:**
  - Único servidor suportado: **Tomcat 10.1.x**.
  - WAR “fat”: Mojarra, Weld Servlet, Hibernate ORM, Hibernate Validator, HikariCP, driver PostgreSQL.
  - JPA `transaction-type="RESOURCE_LOCAL"`; DataSource via `db.properties` (sem `java:jboss/...`).
  - Pacote `persistence`: producers CDI + interceptor que honra `@Transactional` com `EntityTransaction`.
  - Removido `03-wildfly-datasource.cli` e o modelo `provided` das implementações EE.
- **Motivo:** Tomcat é servlet container previsível para estudo; evita dual-stack; RESOURCE_LOCAL é mais simples que Narayana no Tomcat.
- **Alternativas consideradas:**
  - Manter WildFly — prós: JTA/JNDI nativos; contras: foge do pedido de exclusividade Tomcat.
  - Narayana + JTA no Tomcat — prós: perto do modelo EE; contras: overengineering para o escopo.
  - Compatibilidade dual WildFly+Tomcat — prós: flexibilidade; contras: proibido pelo objetivo da migração.
- **Consequências:** Deploy = copiar WAR no `webapps` do Tomcat; credenciais JDBC em `db.properties`; D001/D008/D017 permanecem como histórico (runtime atual = D029).
- **Nota:** Weld (`org.jboss.weld`) é a implementação de CDI embutida no WAR — não é o servidor WildFly.

### D030 — Organização Facelets (`pages/`) e pacote `controller`

- **Data:** 2026-07-16
- **Contexto:** Revisão estrutural (Etapa 31): alinhar pastas e nomenclatura a convenções JSF corporativas sem mudar comportamento.
- **Decisão:**
  - Páginas de negócio em `webapp/pages/`; `index.xhtml` permanece na raiz (`welcome-file`).
  - Fragments da listagem em `WEB-INF/includes/empresas/` (`ui:include`); composites continuam em `resources/components/`.
  - Pacote `bean` renomeado para `controller`; classes `*Bean` → `*Controller` (EL: `empresaListController`, `empresaFormController`).
  - `CnpjFormatBean` moveu para `controller.CnpjFormatController`; `util` fica só com helpers estáticos.
  - Removidos `HelloBean` (demo residual) e artefato solto `META-INF/maven` na raiz do repo.
- **Motivo:** `pages/` escala melhor; `controller` deixa clara a camada de apresentação CDI; includes vs composites evitam misturar reuso local com reuso global.
- **Alternativas consideradas:**
  - Manter páginas na raiz do `webapp` — prós: URLs curtas; contras: mistura com recursos e dificulta crescimento.
  - Manter nome `bean` — prós: zero churn de EL; contras: menos alinhado ao vocabulário corporativo pedido no estudo.
  - `@Named("empresaListBean")` preservando EL antigo — prós: menos diff XHTML; contras: nome EL divergente da classe.
- **Consequências:** Outcomes `/pages/empresas` e `/pages/empresa-form`; D005 permanece como histórico da criação dos pacotes; nomenclatura atual da UI = `controller` (esta decisão).

### D031 — Menu lateral com MegaMenu vertical

- **Data:** 2026-07-16
- **Contexto:** Etapa 33 — o `p:menubar` horizontal (D018) não escalava bem para novos módulos e divergia do padrão visual de apps com sidebar.
- **Decisão:**
  - Shell em `layout.xhtml`: sidebar fixa (~260px) + área `.app-main` / `.app-content`
  - Navegação com `p:megaMenu orientation="vertical"` (Início / Empresas) no fragmento `WEB-INF/includes/app-menu.xhtml`
  - `NavigationController` (`@RequestScoped`) destaca o item ativo via `viewId` (`empresa-form` conta como Empresas)
  - Mobile: `.app-sidebar` oculto; hamburger + `p:sidebar` com o mesmo `ui:include`
  - Estilos em `resources/css/layout.css` (sem reinventar o componente PF)
- **Motivo:** Fiel ao showcase oficial do MegaMenu vertical; componentes nativos; um único fragmento facilita novos itens depois.
- **Alternativas consideradas:**
  - Manter `p:menubar` — prós: zero churn; contras: não atende o pedido de sidebar.
  - `p:menu` + `DefaultMenuModel` — prós: padrão corporativo clássico; contras: menos alinhado à referência visual do showcase.
  - CSS-only sem `p:sidebar` no mobile — prós: menos markup; contras: perde overlay nativo do PF.
- **Consequências:** D018 permanece como histórico do menubar; navegação atual = MegaMenu lateral (esta decisão). Novos módulos = novo `p:menuitem` no fragmento + regra em `isActive`.

### D032 — Modal único de exportação (colunas + formato)

- **Data:** 2026-07-16
- **Contexto:** Etapa 34 — dois botões (Excel / PDF) e dois dialogs duplicavam UI; `ui-fluid` no painel esticava a toolbar em coluna full-width.
- **Decisão:**
  - Toolbar: `.app-toolbar` com `width: auto` nos botões (Nova empresa + Exportar lado a lado)
  - Um composite `exportColumnsDialog`: checklist de colunas + `p:selectOneRadio` (xlsx/pdf) + Cancelar / Exportar
  - Dois `p:dataExporter` ocultos permanecem; `oncomplete` escolhe o trigger via `callbackParam exportFormat`
  - Ao abrir: `selectAll()` + formato padrão `xlsx`
- **Motivo:** Fluxo único e reutilizável; geração de arquivo inalterada; evita `rendered` dinâmico no exporter.
- **Alternativas consideradas:**
  - Manter dois dialogs (D027/D028) — prós: zero churn no composite; contras: UI pedida unificada.
  - SplitButton / menu no botão Exportar — prós: menos cliques; contras: não permite escolher colunas antes do formato no mesmo fluxo.
  - EL do formato no `oncomplete` — prós: simples; contras: valor stale após AJAX (por isso `callbackParam`).
- **Consequências:** D027/D028 continuam válidas para a geração; a UI de entrada é esta decisão.

### D033 — Banco persistente + Flyway (schema versionado)

- **Data:** 2026-07-16
- **Contexto:** Scripts `01-schema.sql` / `02-seed.sql` misturavam CREATE DATABASE com DDL; evolução futura sem histórico; risco de recriar DB no boot.
- **Decisão:**
  - DATABASE criado **uma vez** (`db/00-create-database.sql`, idempotente); nunca no startup do Tomcat
  - Schema/seed via **Flyway** (`db/migration/V1__…`, `V2__…`); comando `mvn flyway:migrate`
  - Hibernate permanece `validate` (fonte da verdade = SQL)
  - Dados **persistem** entre deploys; reset só com `db/dev/reset_empresas.sql` (manual, destrutivo)
  - Banco já existente sem histórico: `mvn flyway:baseline -Dflyway.baselineVersion=2`
- **Motivo:** Evolução auditável, SQL didático, alinhado a Java/Maven/PostgreSQL; evita perda de dados de estudo.
- **Alternativas consideradas:**
  - Continuar só SQL manual — prós: zero tooling; contras: frágil quando o schema crescer.
  - Liquibase — prós: poderoso; contras: overkill neste porte.
  - `hbm2ddl=update` / create no boot — prós: conveniência; contras: diverge dos scripts e já rejeitado (D008).
  - Drop/recreate a cada start — prós: ambiente limpo; contras: perde CRUD real e é perigoso.
- **Consequências:** D007 permanece como histórico dos scripts iniciais; fluxo operacional atual = esta decisão + `db/README.md`.

### D034 — Tema oficial Vela Blue

- **Data:** 2026-07-16
- **Contexto:** Etapa 29 — UI ainda em `nova-light` (claro); CSS custom (`layout`, `toast`, `datePicker`) com cores claras hardcoded.
- **Decisão:**
  - `primefaces.THEME=vela` no `web.xml` (tema free dark bundled no JAR 13.0.10 como `primefaces-vela`; o showcase recente chama “Vela Blue”, mas o id no PF 13 é `vela`, não `vela-blue`)
  - Shell e toasts passam a usar variáveis do tema (`--surface-*`, `--text-color`, `--primary-color`) com fallbacks escuros
  - Toast/mensagem: fundos translúcidos escuros + barra lateral colorida (legibilidade no dark)
  - DatePicker: selects de mês/ano sem fundo branco fixo
- **Motivo:** Tema oficial dark moderno; configuração nativa; sem SASS custom nem JAR extra.
- **Alternativas consideradas:**
  - Manter `nova-light` — prós: zero churn; contras: não atende o pedido de Vela.
  - `vela-blue` (nome do showcase PF 15+) — prós: nome marketing; contras: **não existe** no JAR 13.0.10 (`theme.css` ausente → FacesException).
  - `saga` / `arya` — prós: também oficiais no JAR 13; contras: fora do escopo pedido.
  - Tema SASS custom — prós: identidade própria; contras: overengineering neste estudo.
- **Consequências:** Após deploy, fazer Ctrl+F5 para limpar cache de tema/CSS. Componentes PF herdam o tema automaticamente.

### D035 — Erro de formulário associado ao campo (CNPJ)

- **Data:** 2026-07-16
- **Contexto:** Etapa 36 — CNPJ duplicado só gerava toast global; o usuário não via qual campo corrigir.
- **Decisão:**
  - Checagem `existeCnpj` antes de persist/merge (repository COUNT + excludeId na edição)
  - Toast global (`clientId null`) com a mensagem de erro de persistência já usada
  - Mensagem de campo em `formCadastro:cnpj` (“CNPJ já cadastrado.”) → `p:message` + `ui-state-error`
  - Fallback se a UNIQUE do banco ainda disparar (`uk_empresa_cnpj` / SQLState `23505`)
  - `p:focus context="formCadastro"` para o primeiro campo inválido (também cobre `required`/converter)
- **Motivo:** Boas práticas JSF: mensagem global + mensagem no componente; feedback imediato sem mudar regra de negócio.
- **Alternativas consideradas:**
  - Só toast global — prós: simples; contras: UX fraca (cenário do bug).
  - Só mensagem de campo — prós: preciso; contras: perde o padrão de toast do projeto.
  - Confiar só na PersistenceException — prós: zero query extra; contras: menos previsível e pior para mapear o campo.
- **Consequências:** Cadastro/edição com CNPJ repetido permanece na tela com destaque no CNPJ; edição com o próprio CNPJ continua válida.

