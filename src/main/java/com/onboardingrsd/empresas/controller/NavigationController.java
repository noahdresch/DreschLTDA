package com.onboardingrsd.empresas.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * Destaca o item ativo do menu lateral com base no viewId da requisição atual.
 * Formulário de empresa pertence ao módulo Empresas.
 */
@Named
@RequestScoped
public class NavigationController {

    public boolean isActive(String modulo) {
        if (modulo == null || modulo.isBlank()) {
            return false;
        }

        String viewId = currentViewId();
        if (viewId == null) {
            return false;
        }

        return switch (modulo) {
            case "inicio" -> isInicio(viewId);
            case "empresas" -> isEmpresas(viewId);
            default -> false;
        };
    }

    private String currentViewId() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context == null || context.getViewRoot() == null) {
            return null;
        }
        return context.getViewRoot().getViewId();
    }

    private boolean isInicio(String viewId) {
        return "/index.xhtml".equals(viewId);
    }

    private boolean isEmpresas(String viewId) {
        return "/pages/empresas.xhtml".equals(viewId)
                || "/pages/empresa-form.xhtml".equals(viewId);
    }
}
