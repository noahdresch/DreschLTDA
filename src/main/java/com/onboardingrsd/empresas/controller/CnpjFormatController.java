package com.onboardingrsd.empresas.controller;

import com.onboardingrsd.empresas.util.CnpjUtil;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

/**
 * Exposição CDI de {@link CnpjUtil} para uso em EL (ex.: tabelas e cards).
 */
@Named
@ApplicationScoped
public class CnpjFormatController {

    public String formatar(String cnpj) {
        return CnpjUtil.formatar(cnpj);
    }

    public String somenteDigitos(String cnpj) {
        return CnpjUtil.somenteDigitos(cnpj);
    }
}
