package com.onboardingrsd.empresas.model;

/**
 * Tipo societário / jurídica da empresa.
 * Persistido como nome do enum ({@code STRING}) na coluna {@code tipo_empresa}.
 */
public enum TipoEmpresa {

    MEI,
    EIRELI,
    LTDA,
    SA
}
