package com.onboardingrsd.empresas.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.PrimeFaces;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.SortMeta;

import com.onboardingrsd.empresas.export.ExportColumn;
import com.onboardingrsd.empresas.export.ExportColumnSelection;
import com.onboardingrsd.empresas.model.Empresa;
import com.onboardingrsd.empresas.service.EmpresaService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Controller da tela de listagem/pesquisa de empresas.
 * Paginação e ordenação: client-side via {@code p:dataTable}.
 * Seleção simples por clique na linha, com card de detalhes.
 * Exportação: checklist de colunas + formato (xlsx/pdf) + {@code p:dataExporter}.
 */
@Named
@ViewScoped
public class EmpresaListController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int ROWS_PADRAO = 5;
    private static final String FORMATO_XLSX = "xlsx";

    @Inject
    private EmpresaService empresaService;

    private String filtro;
    private List<Empresa> empresas;
    private Empresa empresaSelecionada;
    private int rows = ROWS_PADRAO;
    private ExportColumnSelection exportSelection;
    private String exportFormat = FORMATO_XLSX;

    @PostConstruct
    public void init() {
        empresas = empresaService.listar();
        exportSelection = ExportColumnSelection.of(
                new ExportColumn("id", "ID"),
                new ExportColumn("razaoSocial", "Razão social"),
                new ExportColumn("nomeFantasia", "Nome fantasia"),
                new ExportColumn("tipoEmpresa", "Tipo empresa"),
                new ExportColumn("ramoAtividade", "Ramo atividade"),
                new ExportColumn("cnpj", "CNPJ"),
                new ExportColumn("dataFundacao", "Data de fundação"));
    }

    public void pesquisar() {
        limparSelecao();
        empresas = empresaService.pesquisar(filtro);
    }

    public void limpar() {
        filtro = null;
        limparSelecao();
        empresas = empresaService.listar();
    }

    public void excluir(Empresa empresa) {
        FacesContext context = FacesContext.getCurrentInstance();
        empresaService.excluir(empresa.getId());
        if (empresaSelecionada != null && empresa.getId().equals(empresaSelecionada.getId())) {
            limparSelecao();
        }
        empresas = empresaService.pesquisar(filtro);
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_INFO,
                "SUCESSO",
                "Empresa excluída com sucesso."));
    }

    public void onRowSelect() {
        // seleção já vinculada em empresaSelecionada via selection do DataTable
    }

    public void onRowUnselect() {
        limparSelecao();
    }

    /**
     * No 3º clique ({@code allowUnsorting}), o ícone volta ao neutro,
     * mas a lista permanece na última ordem. Recarrega do serviço
     * para restaurar a ordem original (sem ordenação aplicada no grid).
     */
    public void onSort(SortEvent event) {
        if (estaSemOrdenacao(event.getSortBy())) {
            empresas = empresaService.pesquisar(filtro);
        }
    }

    private boolean estaSemOrdenacao(Map<String, SortMeta> sortBy) {
        if (sortBy == null || sortBy.isEmpty()) {
            return true;
        }
        return sortBy.values().stream()
                .noneMatch(meta -> meta != null
                        && meta.isActive()
                        && meta.getOrder() != null
                        && !meta.getOrder().isUnsorted());
    }

    public void limparSelecao() {
        empresaSelecionada = null;
    }

    /**
     * Marca todas as colunas e redefine o formato ao abrir o modal de exportação.
     */
    public void prepararDialogoExportacao() {
        exportSelection.selectAll();
        exportFormat = FORMATO_XLSX;
    }

    /**
     * Impede o download se nenhuma coluna estiver marcada.
     * Envia {@code exportFormat} via callbackParam para o oncomplete do composite.
     */
    public void validarSelecaoExportacao() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!exportSelection.hasSelection()) {
            context.validationFailed();
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_WARN,
                    "ATENÇÃO",
                    "Selecione pelo menos uma coluna para exportar."));
            return;
        }
        PrimeFaces.current().ajax().addCallbackParam("exportFormat", exportFormat);
    }

    /** Usado em {@code exportable="#{empresaListController.exportavel('chave')}"}. */
    public boolean exportavel(String key) {
        return exportSelection.isSelected(key);
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<Empresa> getEmpresas() {
        return empresas;
    }

    public Empresa getEmpresaSelecionada() {
        return empresaSelecionada;
    }

    public void setEmpresaSelecionada(Empresa empresaSelecionada) {
        this.empresaSelecionada = empresaSelecionada;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public ExportColumnSelection getExportSelection() {
        return exportSelection;
    }

    public String getExportFormat() {
        return exportFormat;
    }

    public void setExportFormat(String exportFormat) {
        this.exportFormat = exportFormat;
    }
}
