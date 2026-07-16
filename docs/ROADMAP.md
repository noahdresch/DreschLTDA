# Roadmap

Evolução incremental do CRUD de Empresas (JSF). Cada etapa tem um objetivo único, mantém o projeto funcional e idealmente corresponde a um commit Git.

Status possíveis por etapa: `pendente` | `em andamento` | `concluída`

Ordem geral: docs/stack → WAR/JSF → UI/CDI → SQL/JPA → CRUD → DataTable avançado → export/relatório → polimento.

Fora deste roadmap (até nova aprovação): autenticação, testes automatizados, entity `RamoAtividade`, i18n completo, API REST.

**Progresso atual:** Etapas **01–29**, **31**, **33–36** concluídas; **Etapa 36** (destaque de campo com erro) concluída (D035). Runtime: **Apache Tomcat 10.1** (D029). Próximas: **Etapa 30** (README execução residual), **32** (fechamento docs).

Observações de evolução recentes (formalizadas em D018–D035):
- Toast global (`p:growl` + `toast.css`) para FacesMessages
- Seleção **single** por clique na linha + diálogo de detalhes (não mais múltipla/checkbox)
- Ordenação com `allowUnsorting` e restauração da ordem original no 3º clique
- Máscara/formatação de CNPJ (`CnpjConverter` + `CnpjUtil`); CSS do datePicker
- Colunas do grid reduzidas (CNPJ/fundação no card de detalhes)
- Shell: **MegaMenu vertical** fixo à esquerda (Início/Empresas) + `p:sidebar` no mobile (D031)
- Exportação unificada: um botão **Exportar** + modal (colunas + formato xlsx/pdf) (D032)
- Banco: **Flyway** (`db/migration`) + DATABASE one-shot; sem recreate no boot (D033)
- Tema: **Vela** (`primefaces.THEME=vela` no PF 13) + CSS custom adaptado ao dark (D034)
- Formulário: erro de CNPJ duplicado no campo + toast global; `p:focus` (D035)
- Runtime exclusivo Tomcat (WAR fat, RESOURCE_LOCAL)
---

## Etapa 01 — Stack e pré-requisitos no README

- **Status:** concluída
- **Objetivo:** Registrar no README as versões e pré-requisitos da stack (Java, Maven, servidor, PostgreSQL).
- **Entregável:** Seções de pré-requisitos/stack no `README.md`; eventual registro em `DECISIONS.md`.
- **Fora de escopo:** Código Maven, páginas JSF, banco.
- **Conceitos:** Baseline de ambiente; escolha de stack para estudo.
- **Commit sugerido:** `docs: registrar stack e pré-requisitos do projeto JSF`

---

## Etapa 02 — Skeleton Maven WAR

- **Status:** concluída
- **Objetivo:** Criar o esqueleto Maven com packaging WAR.
- **Entregável:** `pom.xml` mínimo + pastas `src/main/java` e `src/main/webapp`.
- **Fora de escopo:** Faces, PrimeFaces, JPA, banco.
- **Conceitos:** Maven, WAR, estrutura padrão Java Web.
- **Commit sugerido:** `chore: criar skeleton Maven WAR do projeto JSF`

---

## Etapa 03 — Configuração mínima do Faces

- **Status:** concluída
- **Objetivo:** Habilitar Jakarta Faces no projeto.
- **Entregável:** Dependência Faces + `web.xml` / `faces-config.xml` mínimos.
- **Fora de escopo:** PrimeFaces, CDI beans de negócio, persistência.
- **Conceitos:** Faces Servlet, ciclo de vida JSF (visão inicial).
- **Commit sugerido:** `feat: configurar Jakarta Faces no WAR`

---

## Etapa 04 — Primeira página XHTML

- **Status:** concluída
- **Objetivo:** Exibir uma página Hello World via Faces.
- **Entregável:** Página `.xhtml` acessível pelo Faces Servlet.
- **Fora de escopo:** Layout Facelets, PrimeFaces, CDI.
- **Conceitos:** Facelets básico, mapeamento de URL.
- **Commit sugerido:** `feat: adicionar primeira página JSF Hello World`

---

## Etapa 05 — PrimeFaces

- **Status:** concluída
- **Objetivo:** Integrar PrimeFaces e usar um componente simples.
- **Entregável:** Dependência PrimeFaces + uso em uma página.
- **Fora de escopo:** Template completo, DataTable de empresas, tema avançado.
- **Conceitos:** Biblioteca de componentes JSF; namespace `p:`.
- **Commit sugerido:** `feat: adicionar PrimeFaces ao projeto`

---

## Etapa 06 — Template Facelets

- **Status:** concluída
- **Objetivo:** Criar layout reutilizável com `ui:composition`.
- **Entregável:** Template + página de exemplo usando o layout.
- **Fora de escopo:** CRUD, persistência.
- **Conceitos:** Templates Facelets, `ui:insert` / `ui:define`.
- **Commit sugerido:** `feat: criar template Facelets de layout`

---

## Etapa 07 — CDI

- **Status:** concluída
- **Objetivo:** Habilitar CDI e expor um bean `@Named` simples na tela.
- **Entregável:** `beans.xml` + bean de demonstração ligado à view.
- **Fora de escopo:** JPA, repositórios, entidade Empresa.
- **Conceitos:** CDI, `@Named`, escopos básicos.
- **Commit sugerido:** `feat: configurar CDI e primeiro bean Named`

---

## Etapa 08 — Arquitetura e pacotes

- **Status:** concluída
- **Objetivo:** Documentar camadas e criar pacotes iniciais.
- **Entregável:** `docs/ARCHITECTURE.md` + pacotes (`model`, `repository`, `service`, `bean`).
- **Fora de escopo:** Implementar JPA/SQL completos.
- **Conceitos:** Separação de responsabilidades; arquitetura em camadas.
- **Commit sugerido:** `docs: definir arquitetura e pacotes iniciais`

---

## Etapa 09 — Script SQL (schema)

- **Status:** concluída
- **Objetivo:** Criar script de banco e tabela `empresa`.
- **Entregável:** Script SQL de schema em pasta acordada (ex.: `src/main/resources/db`).
- **Fora de escopo:** Seed, Entity JPA, datasource no servidor.
- **Conceitos:** Modelagem relacional alinhada ao domínio.
- **Commit sugerido:** `feat: adicionar script SQL de schema da tabela empresa`

---

## Etapa 10 — Script SQL (seed)

- **Status:** concluída
- **Objetivo:** Inserir dados iniciais de empresas.
- **Entregável:** Script de seed executável após o schema.
- **Fora de escopo:** Tela de cadastro, JPA.
- **Conceitos:** Dados de teste; idempotência básica de scripts.
- **Commit sugerido:** `feat: adicionar seed SQL de empresas`

---

## Etapa 11 — JPA e persistence.xml

- **Status:** concluída
- **Objetivo:** Configurar unidade de persistência e datasource.
- **Entregável:** `persistence.xml` + configuração necessária no servidor/app.
- **Fora de escopo:** Entity completa de negócio (pode ser smoke test mínimo), telas CRUD.
- **Conceitos:** JPA, persistence unit, datasource.
- **Commit sugerido:** `feat: configurar JPA e persistence unit`

---

## Etapa 12 — Entity Empresa

- **Status:** concluída
- **Objetivo:** Mapear a entidade `Empresa` conforme campos do README.
- **Entregável:** Classe `@Entity` alinhada ao schema SQL.
- **Fora de escopo:** Enum tipado (etapa seguinte), CRUD na UI.
- **Conceitos:** Anotações JPA (`@Id`, `@Column`, etc.).
- **Commit sugerido:** `feat: criar entity Empresa`

---

## Etapa 13 — Enum TipoEmpresa

- **Status:** concluída
- **Objetivo:** Tipar o campo Tipo da Empresa com enum.
- **Entregável:** `enum TipoEmpresa` + mapeamento `@Enumerated` na entity.
- **Fora de escopo:** CRUD de ramos; entity `RamoAtividade`.
- **Conceitos:** Enum Java; persistência de enums.
- **Commit sugerido:** `feat: adicionar enum TipoEmpresa na entity`

---

## Etapa 14 — Repository (leitura)

- **Status:** concluída
- **Objetivo:** Encapsular leitura JPA (`findAll` e, se útil, por id).
- **Entregável:** Classe de repositório/DAO de leitura.
- **Fora de escopo:** Create/update/delete; telas.
- **Conceitos:** Repository; `EntityManager`; JPQL básica.
- **Commit sugerido:** `feat: criar repository de leitura de Empresa`

---

## Etapa 15 — Service (listar)

- **Status:** concluída
- **Objetivo:** Orquestrar a listagem via camada de serviço.
- **Entregável:** Service que delega ao repository.
- **Fora de escopo:** Regras complexas; escrita.
- **Conceitos:** Camada de aplicação; injeção CDI entre camadas.
- **Commit sugerido:** `feat: criar service de listagem de Empresa`

---

## Etapa 16 — Listagem na UI

- **Status:** concluída
- **Objetivo:** Exibir empresas em `p:dataTable` simples.
- **Entregável:** Bean + página de listagem (sem paginação/ordenação avançadas).
- **Fora de escopo:** Filtro, edição, exclusão, Excel.
- **Conceitos:** DataTable PrimeFaces; binding com lista.
- **Commit sugerido:** `feat: listar empresas em DataTable`

---

## Etapa 17 — Formulário de cadastro

- **Status:** concluída
- **Objetivo:** Criar tela de cadastro com binding dos campos (sem persistir ainda, se mantida a divisão).
- **Entregável:** Formulário XHTML + bean de cadastro com modelo preenchível.
- **Fora de escopo:** `persist`, validações Bean Validation, edição.
- **Conceitos:** `h:form`, componentes de input, binding.
- **Commit sugerido:** `feat: criar formulário de cadastro de Empresa`

---

## Etapa 18 — Persistência create

- **Status:** concluída
- **Objetivo:** Salvar nova empresa e navegar após o save.
- **Entregável:** `persist` no repository/service + ação no bean.
- **Fora de escopo:** Update/delete; validações avançadas.
- **Conceitos:** Transação; `EntityManager.persist`; navegação JSF.
- **Commit sugerido:** `feat: persistir cadastro de Empresa`

---

## Etapa 19 — Bean Validation

- **Status:** concluída
- **Objetivo:** Validar campos da Empresa com Bean Validation.
- **Entregável:** Constraints na entity/DTO usado no form.
- **Fora de escopo:** Mensagens Faces customizadas (etapa seguinte).
- **Conceitos:** `@NotNull`, `@Size`, validação de CNPJ (formato).
- **Commit sugerido:** `feat: adicionar Bean Validation em Empresa`

---

## Etapa 20 — FacesMessages

- **Status:** concluída
- **Objetivo:** Feedback de sucesso/erro na UI.
- **Entregável:** Uso de `FacesMessage` (API Faces); apresentação evoluiu para toast global `p:growl` no layout (D025).
- **Fora de escopo:** i18n completo; novos fluxos CRUD.
- **Conceitos:** Fila de mensagens JSF; Flash + redirect; integração com validação.
- **Commit sugerido:** `feat: exibir FacesMessages no cadastro`

---

## Etapa 21 — Pesquisa / filtro

- **Status:** concluída
- **Objetivo:** Filtrar a listagem de empresas.
- **Entregável:**
  - `EmpresaRepository.findByFiltro` (JPQL `LIKE` em razão social, fantasia, CNPJ e ramo)
  - `EmpresaService.pesquisar`
  - `EmpresaListBean` com `filtro` / `pesquisar()` / `limpar()` em `@ViewScoped`
  - Painel “Pesquisar” em `empresas.xhtml`
- **Fora de escopo:** Edição/exclusão; paginação formal (Etapa 24).
- **Conceitos:** Consultas dinâmicas / parâmetros JPQL; estado do filtro na view.
- **Commit sugerido:** `feat: filtrar listagem de empresas`

---

## Etapa 22 — Edição

- **Status:** concluída
- **Objetivo:** Carregar empresa existente e atualizar.
- **Entregável:**
  - `f:viewParam` + `f:viewAction` em `empresa-form.xhtml`
  - `EmpresaFormBean.carregar` / `isEdicao` / `salvar` bifurcado (cadastrar vs atualizar)
  - `EmpresaRepository.atualizar` (`merge`) + `EmpresaService.atualizar`
  - Coluna Ações com botão editar (`?id=`)
- **Fora de escopo:** Exclusão; exportação.
- **Conceitos:** Update JPA; passagem de id entre views; reuso do formulário.
- **Commit sugerido:** `feat: permitir edição de Empresa`

---

## Etapa 23 — Exclusão

- **Status:** concluída
- **Objetivo:** Excluir empresa com confirmação.
- **Entregável:**
  - `EmpresaRepository.remover` / `removerPorId` + `EmpresaService.excluir`
  - `EmpresaListBean.excluir` com FacesMessage de sucesso
  - Botão lixeira + `p:confirm` + `p:confirmDialog` global
- **Fora de escopo:** Soft delete; Excel.
- **Conceitos:** `remove`; confirmação de ação destrutiva; hard delete.
- **Commit sugerido:** `feat: permitir exclusão de Empresa`

---

## Etapa 24 — Paginação

- **Status:** concluída
- **Objetivo:** Paginar a listagem.
- **Entregável:**
  - `p:dataTable` com `paginator="true"`, `rows` ligado a `empresaListBean.rows` (padrão 5)
  - `rowsPerPageTemplate` 5/10/20
  - `CurrentPageReport` (“Exibindo X–Y de Z”)
  - Decisão: paginação **client-side** (D022)
- **Fora de escopo:** Ordenação (Etapa 25); LazyDataModel.
- **Conceitos:** Paginação client-side vs server-side; controles do DataTable.
- **Commit sugerido:** `feat: adicionar paginação na listagem de empresas`

---

## Etapa 25 — Ordenação

- **Status:** concluída
- **Objetivo:** Ordenar colunas da listagem.
- **Entregável:**
  - `sortMode="single"` + `sortBy` nas colunas de dados (Ações sem ordenação)
  - `allowUnsorting="true"` + `onSort` no bean para recarregar a lista quando o sort volta ao neutro
  - Decisão: ordenação **client-side** alinhada à paginação (D023)
- **Fora de escopo:** `ORDER BY` dinâmico no JPQL / Lazy.
- **Conceitos:** Ordenação em grid; sort asc/desc; unsort / ordem original.
- **Commit sugerido:** `feat: adicionar ordenação na listagem de empresas`

---

## Etapa 26 — Seleção de linha

- **Status:** concluída
- **Objetivo:** Permitir seleção de linha no DataTable.
- **Entregável:**
  - `selectionMode="single"` + `selection` / `rowKey`
  - Clique na linha abre `p:dialog` com card de detalhes (`rowSelect` / `rowUnselect`)
  - `empresaSelecionada` no bean; limpeza em filtro/exclusão/fechar diálogo
- **Fora de escopo:** Excel; relatório; seleção múltipla / exclusão em lote.
- **Conceitos:** Selection mode single; eventos AJAX de linha; estado no bean.
- **Commit sugerido:** `feat: adicionar seleção de linha no DataTable`

---

## Etapa 27 — Exportação Excel

- **Status:** concluída
- **Objetivo:** Exportar dados da listagem para Excel.
- **Entregável:**
  - `p:dataExporter` (`xlsx`) + Apache POI (`poi-ooxml`) — D027
  - Modal reutilizável (`comp:exportColumnsDialog`) com checklist de todas as colunas da entidade
  - Pacote `export` (`ExportColumn` / `ExportColumnSelection`)
  - Validação: ao menos uma coluna; botões Cancelar / Exportar
- **Fora de escopo:** Relatório PDF; CSV/XML; export só da página / só da seleção.
- **Conceitos:** DataExporter; `exportable` vs `visible`; composite Facelets; download `ajax="false"`.
- **Commit sugerido:** `feat: exportar listagem de empresas para Excel`

---

## Etapa 28 — Relatório

- **Status:** concluída
- **Objetivo:** Gerar relatório simples (PDF ou abordagem mínima definida em DECISIONS).
- **Entregável:**
  - `p:dataExporter` `type="pdf"` + OpenPDF — D028
  - Botão **Relatório PDF** na listagem; mesmo modal de colunas (Excel + Gerar PDF)
  - Reuso de `exportavel` / checklist da Etapa 27
- **Fora de escopo:** JasperReports; designer visual; gráficos.
- **Conceitos:** Relatório tabular via DataExporter; dependência OpenPDF.
- **Commit sugerido:** `feat: adicionar relatório PDF simples de empresas`

---

## Etapa 29 — Tema Vela Blue

- **Status:** concluída
- **Objetivo:** Adotar o tema oficial dark **Vela Blue** e ajustar CSS custom para contraste.
- **Entregável:**
  - `primefaces.THEME=vela` em `web.xml` (substitui `nova-light`; no PF 13 a lib é `primefaces-vela`)
  - `layout.css` / `toast.css` / `datePicker.css` com variáveis e paleta dark
  - Docs: D034
- **Fora de escopo:** Troca dinâmica de tema; temas premium; mudanças de negócio.
- **Conceitos:** Temas free do PrimeFaces (`saga` / `vela` / `arya`); CSS variables do tema.
- **Commit sugerido:** `style: adotar tema PrimeFaces Vela Blue`

---

## Etapa 30 — README de execução

- **Status:** pendente
- **Objetivo:** Documentar build, deploy, PostgreSQL e URLs.
- **Entregável:** Seção “Como executar” completa no `README.md`.
- **Fora de escopo:** Refatoração de código.
- **Conceitos:** Documentação operacional de app Java Web.
- **Commit sugerido:** `docs: documentar execução e deploy do projeto JSF`

---

## Etapa 31 — Revisão pontual

- **Status:** concluída
- **Objetivo:** Ajustar nomenclatura/pacotes e organização Facelets sem mudar comportamento.
- **Entregável:**
  - Views em `webapp/pages/`; fragments em `WEB-INF/includes/empresas/`
  - Pacote `bean` → `controller` (`EmpresaListController`, `EmpresaFormController`, `CnpjFormatController`)
  - Remoção de `HelloBean` e artefato `META-INF/maven` solto na raiz
  - Docs: ARCHITECTURE + D030
- **Fora de escopo:** Features novas; i18n; pastas vazias (`dto`, `validator`, etc.).
- **Conceitos:** Organização Facelets; controller JSF/CDI; nome EL derivado de `@Named`.
- **Commit sugerido:** `refactor: organizar views e renomear bean para controller`

---

## Etapa 32 — Fechamento da documentação

- **Status:** pendente
- **Objetivo:** Consolidar ROADMAP, DECISIONS e LEARNING.
- **Entregável:** Docs alinhados ao estado final do estudo.
- **Fora de escopo:** Código de feature.
- **Conceitos:** Documentação viva; retrospectiva de aprendizado.
- **Commit sugerido:** `docs: consolidar documentação final do estudo JSF`

---

## Etapa 33 — Menu lateral (MegaMenu vertical)

- **Status:** concluída
- **Objetivo:** Substituir o `p:menubar` horizontal por sidebar fixa com `p:megaMenu orientation="vertical"`.
- **Entregável:**
  - Shell em `layout.xhtml` (sidebar + main) + `layout.css`
  - Fragmento `WEB-INF/includes/app-menu.xhtml` (Início / Empresas)
  - `NavigationController` com destaque do item ativo por viewId
  - `p:sidebar` + hamburger no mobile
  - Páginas adaptadas ao novo conteúdo (`ui-fluid`, toolbar)
  - Docs: D031
- **Fora de escopo:** Novos módulos/submenus; troca de tema (Etapa 29); autenticação.
- **Conceitos:** MegaMenu vertical; Facelets `ui:include`; viewId; layout responsivo com Sidebar PF.
- **Commit sugerido:** `feat: substituir menubar por MegaMenu vertical fixo`

---

## Etapa 34 — Toolbar horizontal e modal único de exportação

- **Status:** concluída
- **Objetivo:** Alinhar botões da listagem lado a lado e unificar Excel/PDF num único fluxo de exportação.
- **Entregável:**
  - CSS `.app-toolbar .ui-button { width: auto }` (anula `ui-fluid` na toolbar)
  - Um botão **Exportar**; composite com colunas + formato (xlsx/pdf)
  - `exportFormat` + `callbackParam` no controller; dois `dataExporter` ocultos intactos
  - Docs: D032
- **Fora de escopo:** Mudar geração dos arquivos; aplicar o modal em outras telas.
- **Conceitos:** `ui-fluid` vs toolbar; composite Facelets; AJAX → validação → download `ajax=false`.
- **Commit sugerido:** `feat: unificar exportação em modal único e corrigir toolbar`

---

## Etapa 35 — Gestão de banco (Flyway)

- **Status:** concluída
- **Objetivo:** Padronizar criação, seed e evolução do PostgreSQL sem recriar o DB no startup.
- **Entregável:**
  - `db/00-create-database.sql` + `db/migration/V1__…` + `V2__…`
  - Flyway Maven (`mvn flyway:migrate` / `info`)
  - `db/dev/reset_empresas.sql` (reset manual)
  - `db/README.md` + README raiz; D033
- **Fora de escopo:** Auto-migrate no Tomcat; Liquibase; `hbm2ddl=update`.
- **Conceitos:** Migrações versionadas; baseline; seed idempotente; validate vs migrate.
- **Commit sugerido:** `feat: adotar Flyway e reorganizar scripts SQL`

---

## Etapa 36 — Destacar campo com erro no formulário

- **Status:** concluída
- **Objetivo:** Associar erros de validação/persistência ao campo correspondente, mantendo o toast global.
- **Entregável:**
  - `existsByCnpj` / `existeCnpj` (checagem antes do persist)
  - `FacesMessage` em `formCadastro:cnpj` + toast global preservado
  - Fallback UNIQUE na `PersistenceException`
  - `p:focus context="formCadastro"` no formulário
  - Docs: D035
- **Fora de escopo:** Dígito verificador de CNPJ; AJAX no Salvar; mudança de schema.
- **Conceitos:** `clientId` vs mensagem global; estilo de erro PF; unicidade na aplicação.
- **Commit sugerido:** `feat: destacar campo com erro no formulário de empresa`
