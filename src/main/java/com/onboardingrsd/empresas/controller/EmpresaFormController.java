package com.onboardingrsd.empresas.controller;

import java.io.Serializable;
import java.sql.SQLException;

import com.onboardingrsd.empresas.model.Empresa;
import com.onboardingrsd.empresas.model.TipoEmpresa;
import com.onboardingrsd.empresas.service.EmpresaService;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.PersistenceException;

@Named
@ViewScoped
public class EmpresaFormController implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String CLIENT_ID_CNPJ = "formCadastro:cnpj";

    private static final String MSG_ERRO_PERSISTENCIA =
            "Não foi possível salvar a empresa. Verifique os dados.";

    private static final String MSG_CNPJ_DUPLICADO = "CNPJ já cadastrado.";

    @Inject
    private EmpresaService empresaService;

    private Long id;
    private Empresa empresa = new Empresa();

    public void carregar() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (id == null) {
            empresa = new Empresa();
            return;
        }

        empresaService.buscarPorId(id).ifPresentOrElse(
                encontrada -> empresa = encontrada,
                () -> {
                    context.getExternalContext().getFlash().setKeepMessages(true);
                    context.addMessage(null, new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "ERRO",
                            "Empresa não encontrada."));
                    context.getApplication()
                            .getNavigationHandler()
                            .handleNavigation(context, null, "/pages/empresas?faces-redirect=true");
                });
    }

    public boolean isEdicao() {
        return empresa != null && empresa.getId() != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public TipoEmpresa[] getTiposEmpresa() {
        return TipoEmpresa.values();
    }

    public String salvar() {
        FacesContext context = FacesContext.getCurrentInstance();

        Long excludeId = isEdicao() ? empresa.getId() : null;
        if (empresaService.existeCnpj(empresa.getCnpj(), excludeId)) {
            return mensagemErroCnpjDuplicado(context);
        }

        try {
            if (isEdicao()) {
                empresaService.atualizar(empresa);
                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "SUCESSO",
                        "Empresa atualizada com sucesso."));
            } else {
                empresaService.cadastrar(empresa);
                context.getExternalContext().getFlash().setKeepMessages(true);
                context.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "SUCESSO",
                        "Empresa cadastrada com sucesso."));
            }
            return "/pages/empresas?faces-redirect=true";
        } catch (PersistenceException e) {
            return tratarErroPersistencia(context, e);
        } catch (RuntimeException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof PersistenceException persistenceException) {
                    return tratarErroPersistencia(context, persistenceException);
                }
                cause = cause.getCause();
            }
            context.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "ERRO",
                    "Erro inesperado ao salvar a empresa."));
            return null;
        }
    }

    private String tratarErroPersistencia(FacesContext context, PersistenceException e) {
        if (isViolacaoUnicidadeCnpj(e)) {
            return mensagemErroCnpjDuplicado(context);
        }
        return mensagemErroPersistencia(context);
    }

    private String mensagemErroCnpjDuplicado(FacesContext context) {
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "ERRO",
                MSG_ERRO_PERSISTENCIA));
        context.addMessage(CLIENT_ID_CNPJ, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                MSG_CNPJ_DUPLICADO,
                MSG_CNPJ_DUPLICADO));
        context.validationFailed();
        return null;
    }

    private String mensagemErroPersistencia(FacesContext context) {
        context.addMessage(null, new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "ERRO",
                MSG_ERRO_PERSISTENCIA));
        return null;
    }

    private boolean isViolacaoUnicidadeCnpj(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String lower = message.toLowerCase();
                if (lower.contains("uk_empresa_cnpj")
                        || lower.contains("empresa_cnpj")
                        || (lower.contains("cnpj") && lower.contains("unique"))) {
                    return true;
                }
            }
            if (current instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
