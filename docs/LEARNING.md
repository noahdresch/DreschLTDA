# Diário de Aprendizado

Este documento registra os principais conceitos estudados durante o desenvolvimento.

Cada etapa concluída deve ganhar uma seção no formato abaixo (após aprovação da atualização).

---

## Modelo de entrada

```markdown
## Etapa NN — Título da etapa

- **Data:** AAAA-MM-DD
- **Conceitos:** lista dos conceitos praticados
- **O que ficou claro:** síntese em poucas linhas
- **O que eu praticaria de novo:** ponto frágil ou que merece revisão
```

---

## Registro

## Etapa 01 — Stack e pré-requisitos no README

- **Data:** 2026-07-13
- **Conceitos:** LTS (Java 17); Jakarta EE vs `javax.*`; app server (WildFly) vs servlet container; baseline de versões antes do código
- **O que ficou claro:** Sem stack registrada, as próximas etapas ficam ambíguas; a D001 em DECISIONS.md documenta o “porquê” das escolhas
- **O que eu praticaria de novo:** Comparar na prática WildFly vs Payara só para sentir diferença de setup

## Etapa 02 — Skeleton Maven WAR

- **Data:** 2026-07-13
- **Conceitos:** Maven; packaging WAR; `src/main/java` e `src/main/webapp`; `maven.compiler.release`; `groupId` / `artifactId`
- **O que ficou claro:** O WAR é o artefato deployável; pastas padrão do Maven bastam para crescer o projeto sem reinventar estrutura
- **O que eu praticaria de novo:** Rodar `mvn package` com Maven no PATH e inspecionar o conteúdo do `.war`

## Etapa 03 — Configuração mínima do Faces

- **Data:** 2026-07-13
- **Conceitos:** `FacesServlet`; `web.xml` (Servlet 6); `faces-config.xml` (Faces 4); scope `provided`; `PROJECT_STAGE`
- **O que ficou claro:** Faces é o front controller das views `.xhtml`; a API vai `provided` porque o WildFly traz a implementação (Mojarra)
- **O que eu praticaria de novo:** Contrastar mapeamento `*.xhtml` com `/faces/*` em um projeto de teste

## Etapa 04 — Primeira página XHTML

- **Data:** 2026-07-13
- **Conceitos:** Facelets; namespace `jakarta.faces.html`; `h:head` / `h:body` / `h:outputText`; `welcome-file`
- **O que ficou claro:** A página só é processada pelo Faces se passar pelo `FacesServlet`; HTML puro sozinho não ativa o ciclo JSF
- **O que eu praticaria de novo:** Deploy no WildFly e validar a URL raiz vs `/index.xhtml`

## Etapa 05 — PrimeFaces

- **Data:** 2026-07-13
- **Conceitos:** Biblioteca de componentes; namespace `p:`; classifier Maven `jakarta`; `p:panel`; dependência no WAR (não `provided`)
- **O que ficou claro:** Sem classifier `jakarta`, o artefato usa `javax.*` e quebra no Jakarta EE; PrimeFaces complementa o Faces, não o substitui
- **O que eu praticaria de novo:** Inspecionar o WAR e confirmar que o JAR `primefaces-*-jakarta.jar` está em `WEB-INF/lib`

## Etapa 06 — Template Facelets

- **Data:** 2026-07-13
- **Conceitos:** Templates Facelets; `ui:composition`; `ui:insert` / `ui:define`; namespace `jakarta.faces.facelets`; template sob `WEB-INF`
- **O que ficou claro:** O layout fica em um único arquivo; as páginas só preenchem os slots. Colocar o template em `WEB-INF` evita acesso direto por URL
- **O que eu praticaria de novo:** Criar uma segunda página só para praticar outro `ui:define` no mesmo layout

## Etapa 07 — CDI

- **Data:** 2026-07-13
- **Conceitos:** CDI; `beans.xml`; `bean-discovery-mode="annotated"`; `@Named`; `@RequestScoped`; EL `#{helloBean.message}`; API CDI `provided`
- **O que ficou claro:** CDI liga a view ao Java sem managed beans legados do JSF; o nome EL padrão decapitaliza a classe (`HelloBean` → `helloBean`)
- **O que eu praticaria de novo:** Trocar mentalmente `@RequestScoped` por `@ViewScoped` (Jakarta Faces) e entender quando cada um faz sentido no CRUD

## Etapa 08 — Arquitetura e pacotes

- **Data:** 2026-07-13
- **Conceitos:** Arquitetura em camadas; `model` / `repository` / `service` / `bean`; `package-info.java`; separação de responsabilidades; adiar `util`/`converter` até necessidade real (vieram depois, D026)
- **O que ficou claro:** A view fala com `bean`; regras ficam em `service`; persistência em `repository`; domínio em `model`. Documentar isso cedo evita “tudo no bean”
- **O que eu praticaria de novo:** Desenhar no papel o fluxo listagem → bean → service → repository antes da Etapa 16

## Etapa 09 — Script SQL (schema)

- **Data:** 2026-07-13
- **Conceitos:** `CREATE DATABASE` / `CREATE TABLE`; PK auto increment (`BIGSERIAL` no PostgreSQL); UNIQUE em CNPJ; VARCHAR para enum futuro
- **O que ficou claro:** O contrato do banco nasce antes da `@Entity`; nomes snake_case no SQL vs camelCase no Java
- **O que eu praticaria de novo:** Executar `01-schema.sql` no PostgreSQL/pgAdmin e conferir `\d empresa`

## Etapa 10 — Script SQL (seed)

- **Data:** 2026-07-13
- **Conceitos:** Seed / dados de teste; `ON CONFLICT DO NOTHING` (PostgreSQL); idempotência básica via UNIQUE
- **O que ficou claro:** Seed permite testar listagem depois sem cadastrar na mão; reexecutar o script não duplica CNPJs
- **O que eu praticaria de novo:** Rodar `02-seed.sql` duas vezes e validar que continuam 4 linhas

## Etapa 11 — JPA e persistence.xml

- **Data:** 2026-07-13
- **Conceitos:** Persistence Unit; JTA; JNDI datasource; `hibernate.hbm2ddl.auto=validate`; API JPA `provided`; setup WildFly CLI
- **O que ficou claro:** No WildFly o datasource fica no servidor; o app só referencia o JNDI. `validate` protege o schema dos scripts SQL
- **O que eu praticaria de novo:** Criar `EmpresaDS` com o CLI e testar o deploy sem falha de JNDI

## Etapa 12 — Entity Empresa

- **Data:** 2026-07-13
- **Conceitos:** `@Entity`; `@Table`; `@Id`; `@GeneratedValue(IDENTITY)`; `@Column`; `LocalDate`; `equals`/`hashCode` por id
- **O que ficou claro:** A entity espelha a tabela; mapeamento explícito evita surpresa do Hibernate com nomes default
- **O que eu praticaria de novo:** Conferir cada `@Column` contra o `01-schema.sql` linha a linha

## Etapa 13 — Enum TipoEmpresa

- **Data:** 2026-07-13
- **Conceitos:** Enum Java; `@Enumerated(EnumType.STRING)`; valores `MEI`, `EIRELI`, `LTDA`, `SA`
- **O que ficou claro:** `STRING` grava o nome legível no banco e sobrevive a reordenação do enum; `ORDINAL` é frágil
- **O que eu praticaria de novo:** Comparar mentalmente o que aconteceria se alguém usasse `ORDINAL` e inserisse um valor no meio do enum

## Etapa 14 — Repository (leitura)

- **Data:** 2026-07-13
- **Conceitos:** Repository; `@PersistenceContext`; `EntityManager`; JPQL; `Optional`; `@ApplicationScoped`
- **O que ficou claro:** Persistência fica isolada do bean de tela; `findAll` / `findById` bastam para começar a listagem
- **O que eu praticaria de novo:** Escrever à mão a JPQL `SELECT e FROM Empresa e ORDER BY e.razaoSocial` e explicar cada parte

## Etapa 15 — Service (listar)

- **Data:** 2026-07-13
- **Conceitos:** Camada de serviço; `@Inject`; `@Transactional(SUPPORTS)`; nomes de caso de uso (`listar`) vs persistência (`findAll`)
- **O que ficou claro:** O bean não fala com o repository; o service orquestra e concentra o ponto de transação da aplicação
- **O que eu praticaria de novo:** Comparar `SUPPORTS` (leitura) com `REQUIRED` (escrita futura no cadastro)

## Etapa 16 — Listagem na UI

- **Data:** 2026-07-13
- **Conceitos:** `EmpresaListBean`; `p:dataTable`; `var`; `f:convertDateTime` (`localDate`); `h:link`; fluxo view → bean → service → repository
- **O que ficou claro:** A listagem é o primeiro valor de negócio completo na tela; HelloBean permanece só como demo CDI
- **O que eu praticaria de novo:** Tracear no debugger/logs o SQL gerado ao abrir `empresas.xhtml`

## Etapa 17 — Formulário de cadastro

- **Data:** 2026-07-14
- **Conceitos:** `EmpresaFormBean`; `@ViewScoped`; binding `#{bean.empresa.campo}`; `p:inputText`; `p:datepicker`; `p:selectOneMenu` + enum; `f:selectItems`
- **O que ficou claro:** ViewScoped preserva o formulário entre postbacks; o formulário usa escopo de view (a listagem também passou a ViewScoped na Etapa 21 por causa do filtro)
- **O que eu praticaria de novo:** Preencher o form, disparar validação parcial e ver o estado do bean sobreviver ao AJAX

## Etapa 18 — Persistência create

- **Data:** 2026-07-14
- **Conceitos:** `EntityManager.persist`; `@Transactional(REQUIRED)`; Post-Redirect-Get; `faces-redirect=true`
- **O que ficou claro:** Escrita exige transação `REQUIRED` no service; redirect evita reenvio do POST ao atualizar a listagem
- **O que eu praticaria de novo:** Cadastrar e conferir no PostgreSQL (pgAdmin) + na listagem na mesma sequência

## Etapa 19 — Bean Validation

- **Data:** 2026-07-14
- **Conceitos:** `@NotBlank`; `@Size`; `@NotNull`; `@Pattern` (CNPJ 14 dígitos); integração BV + Faces; `p:message` por campo
- **O que ficou claro:** Constraints na entity valem como contrato do domínio; formato de CNPJ sem dígito verificador basta neste estudo
- **O que eu praticaria de novo:** Salvar vazio e com CNPJ inválido para ver cada mensagem

## Etapa 20 — FacesMessages

- **Data:** 2026-07-14
- **Conceitos:** `FacesMessage`; severidades INFO/ERROR; Flash `keepMessages`; summary/detail (`SUCESSO`/`ERRO`); tratamento de `PersistenceException`; presentation via toast (ver D025)
- **O que ficou claro:** Sucesso usa flash + redirect; erro de persistência fica no formulário (`return null`); o display visual passou a ser `p:growl` no layout
- **O que eu praticaria de novo:** Cadastrar OK e depois repetir CNPJ para contrastar toast de sucesso vs erro

## Etapa 21 — Pesquisa / filtro

- **Data:** 2026-07-14
- **Conceitos:** Filtro livre; JPQL com `LIKE` + `LOWER`; parâmetro `:termo`; `EmpresaListBean` `@ViewScoped`; painel Pesquisar/Limpar; `update` AJAX na tabela
- **O que ficou claro:** A pesquisa fica na listagem; termo vazio equivale a listar tudo; ViewScoped mantém filtro e resultado entre postbacks
- **O que eu praticaria de novo:** Filtrar por trecho de razão social, CNPJ e ramo e usar Limpar

## Etapa 22 — Edição

- **Data:** 2026-07-14
- **Conceitos:** `f:viewParam`; `f:viewAction`; `EntityManager.merge`; mesmo formulário create/edit; título dinâmico (`isEdicao`); FacesMessage “atualizada”
- **O que ficou claro:** A edição reutiliza `empresa-form.xhtml?id=`; `carregar()` popula o bean ou redireciona se o id não existir
- **O que eu praticaria de novo:** Clicar no lápis na coluna Ações, alterar um campo e salvar

## Etapa 23 — Exclusão

- **Data:** 2026-07-14
- **Conceitos:** `EntityManager.remove` (com `merge` se detach); `p:confirm` + `p:confirmDialog` global; botão `ui-button-danger`; refresh da lista com filtro ativo
- **O que ficou claro:** Hard delete na listagem, sempre com confirmação antes de apagar; mensagem INFO após sucesso
- **O que eu praticaria de novo:** Excluir uma empresa do seed (Sim/Não no diálogo) e validar no pgAdmin

## Ajustes visuais e de layout (durante as etapas 21–23 e refinamentos seguintes)

- **Data:** 2026-07-14 / 2026-07-15
- **Conceitos:** `p:menubar` (Início/Empresas) — **substituído na Etapa 33 / D031** por MegaMenu vertical; content full-width; painéis fluidos; `p:datePicker` + `datePicker.css`; botões secondary/danger; DataTable `bordered`/`stripedRows`; toast global `p:growl` + `toast.css`
- **O que ficou claro:** Feedback de negócio migrou de `p:messages` na página para toast no canto; o grid ficou mais enxuto (detalhes no diálogo)
- **O que eu praticaria de novo:** Disparar cadastro/exclusão e observar o growl; abrir detalhe clicando na linha

## Etapa 24 — Paginação

- **Data:** 2026-07-14
- **Conceitos:** `paginator`; `rows` / `rowsPerPageTemplate`; `CurrentPageReport`; paginação client-side (lista inteira no bean)
- **O que ficou claro:** Com volume pequeno, o PrimeFaces pagina em memória; Lazy/`LIMIT` ficam para escala maior
- **O que eu praticaria de novo:** Cadastrar >5 empresas e navegar páginas / trocar 5↔10↔20 linhas

## Etapa 25 — Ordenação

- **Data:** 2026-07-14 / 2026-07-15
- **Conceitos:** `sortMode="single"`; `sortBy` nas colunas; `allowUnsorting`; listener `onSort` / `SortEvent` para restaurar ordem do serviço no unsort
- **O que ficou claro:** No 3º clique o ícone fica neutro, mas a lista precisava ser recarregada do serviço para voltar à ordem original
- **O que eu praticaria de novo:** Asc → desc → unsort e conferir se a ordem volta à do backend/filtro

## Etapa 26 — Seleção de linha

- **Data:** 2026-07-14 / 2026-07-15
- **Conceitos:** `selectionMode="single"`; `rowSelect`/`rowUnselect`; `p:dialog` + `p:card` de detalhes; `empresaSelecionada`; CNPJ formatado no detalhe via `cnpjConverter`
- **O que ficou claro:** Clique na linha mostra o card (CNPJ, fundação, etc.) sem poluir o grid; seleção limpa ao filtrar/excluir/fechar o diálogo
- **O que eu praticaria de novo:** Abrir detalhe, fechar com Escape/X, e ver a seleção zerar

## Notificações toast + Formatação CNPJ (refinos de UX)

- **Data:** 2026-07-15
- **Conceitos:** `p:growl` global (`globalOnly`) + `p:autoUpdate`; FacesMessage com summary `SUCESSO`/`ERRO` e detail; `CnpjConverter` / `CnpjUtil` (máscara na UI, dígitos no model)
- **O que ficou claro:** A API `addMessage(null, …)` continua a mesma; só o presentation layer mudou para toast. Converter não altera o contrato do banco (`VARCHAR(14)` só dígitos)
- **O que eu praticaria de novo:** Digitar CNPJ mascarado no form e ver só dígitos persistidos; observar toast após salvar

## Migração — MySQL → PostgreSQL (D017)

- **Data:** 2026-07-14
- **Conceitos:** Dialect SQL (MySQL vs PostgreSQL); `BIGSERIAL`; `ON CONFLICT`; driver JDBC `org.postgresql`; JNDI estável (`EmpresaDS`); pgAdmin
- **O que ficou claro:** Trocar o SGBD muda scripts e datasource no WildFly; a camada JPA/entity quase não muda se o mapeamento for portável
- **O que eu praticaria de novo:** Conferir dados no pgAdmin e a listagem JSF apontando para o mesmo database `empresas_jsf`

## Etapa 27 — Exportação Excel

- **Data:** 2026-07-15
- **Conceitos:** `p:dataExporter` + Apache POI (`poi-ooxml`); `exportable` vs `visible` em `p:column`; composite Facelets; download com `ajax="false"`; `FacesContext.validationFailed()` para bloquear o fluxo
- **O que ficou claro:** O PrimeFaces exporta as colunas do DataTable; o modal só escolhe o que entra (`exportable`). Validar em AJAX e só então clicar no botão oculto evita baixar arquivo vazio de colunas
- **O que eu praticaria de novo:** Desmarcar todas as colunas e ver o toast; exportar só CNPJ + razão; abrir o `.xlsx` e conferir a ordem

## Etapa 28 — Relatório PDF

- **Data:** 2026-07-15
- **Conceitos:** `p:dataExporter` `type="pdf"`; OpenPDF no WAR; reuso do modal de colunas para Excel e PDF
- **O que ficou claro:** PDF e Excel compartilham o mesmo `exportable` do DataTable; só muda o tipo do exporter e a lib (POI vs OpenPDF)
- **O que eu praticaria de novo:** Filtrar a lista, gerar PDF só com razão + CNPJ, e conferir o arquivo

## Migração — WildFly → Apache Tomcat (D029)

- **Data:** 2026-07-15
- **Conceitos:** Servlet container vs app server; WAR fat (Mojarra, Weld, Hibernate); `RESOURCE_LOCAL` + HikariCP; interceptor CDI para `@Transactional`; `db.properties`
- **O que ficou claro:** No Tomcat nada de EE vem “de graça” — tudo que era `provided` no WildFly entrou no WAR. JNDI `java:jboss` deixou de existir; o pool é da aplicação
- **O que eu praticaria de novo:** Deploy do WAR no Tomcat 10.1 sem WildFly instalado e validar CRUD + export

## Etapa 31 — Revisão estrutural (D030)

- **Data:** 2026-07-16
- **Conceitos:** `webapp/pages/` vs raiz; `WEB-INF/templates` vs `WEB-INF/includes` vs `resources/components`; pacote `controller` + `@Named` (nome EL = simple name); util estático ≠ controller CDI
- **O que ficou claro:** Mover `.xhtml` exige atualizar outcomes e redirects na mesma mudança. Renomear `*Bean` para `*Controller` muda o EL automaticamente se `@Named` não tiver valor explícito
- **O que eu praticaria de novo:** Grep por paths/EL antigos após o move; smoke test de navegação listagem ↔ formulário e dos includes do diálogo

## Etapa 33 — Menu lateral MegaMenu (D031)

- **Data:** 2026-07-16
- **Conceitos:** `p:megaMenu orientation="vertical"`; Facelets `ui:include` + `ui:param` (dois clientIds); `NavigationController` + `viewId`; shell CSS (sidebar fixa); `p:sidebar` no mobile
- **O que ficou claro:** O menu fica no template; as páginas só preenchem `content`. O item ativo depende do viewId — formulário de empresa continua destacando Empresas. No mobile o mesmo fragmento entra no overlay
- **O que eu praticaria de novo:** Navegar Início ↔ Empresas ↔ formulário e conferir o destaque; redimensionar a janela e abrir o menu pelo hamburger

## Etapa 34 — Toolbar e export unificado (D032)

- **Data:** 2026-07-16
- **Conceitos:** `ui-fluid` vs `.app-toolbar .ui-button { width: auto }`; composite com formato; `PrimeFaces.ajax().addCallbackParam`; um dialog / dois exporters ocultos
- **O que ficou claro:** O painel fluido esticava os botões; a toolbar precisa “escapar” do fluid. O formato escolhido no modal só chega confiável no JS via callbackParam (não via EL no oncomplete)
- **O que eu praticaria de novo:** Exportar Excel e PDF pelo mesmo modal; desmarcar todas as colunas e ver o toast de atenção

## Etapa 35 — Flyway e scripts de banco (D033)

- **Data:** 2026-07-16
- **Conceitos:** Separar CREATE DATABASE do DDL; Flyway `V1`/`V2`; `mvn flyway:migrate` / `baseline`; Hibernate `validate`; reset só em `dev/`
- **O que ficou claro:** A app não deve criar schema no boot. Migrações já aplicadas não se editam — só se acrescenta `V{n}`. Banco antigo precisa de baseline antes do migrate
- **O que eu praticaria de novo:** Em DB limpo: `00-create-database` → `flyway:migrate` → `flyway:info`; depois um reset com `dev/reset_empresas.sql`

## Etapa 29 — Tema Vela Blue (D034)

- **Data:** 2026-07-16
- **Conceitos:** `primefaces.THEME`; no PF 13 free: `saga` / `vela` / `arya` (não `*-blue`); CSS variables (`--surface-ground`, etc.); adaptar CSS custom ao dark
- **O que ficou claro:** Trocar o tema no `web.xml` basta para os componentes PF; o id deve bater com a pasta no JAR (`primefaces-vela`). `vela-blue` é nome de versões mais novas e quebra no 13.0.10
- **O que eu praticaria de novo:** Ctrl+F5 após o deploy; percorrer listagem, form, dialogs e toasts conferindo contraste

## Etapa 36 — Destaque de campo com erro (D035)

- **Data:** 2026-07-16
- **Conceitos:** `addMessage(clientId, …)` vs global; `p:message` / `p:focus`; checagem de unicidade antes do persist; SQLState `23505`
- **O que ficou claro:** Toast global informa o usuário; a mensagem no `clientId` do campo é o que ativa a borda vermelha do PrimeFaces. `p:focus` cobre required e converter também
- **O que eu praticaria de novo:** Cadastrar CNPJ do seed; ver toast + CNPJ vermelho; editar uma empresa sem mudar o CNPJ e salvar com sucesso
