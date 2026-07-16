package com.onboardingrsd.empresas.util;

public final class CnpjUtil {

    private static final int TAMANHO_DIGITOS = 14;

    private CnpjUtil() {
    }

    public static String somenteDigitos(String valor) {
        if (valor == null) {
            return null;
        }
        String digitos = valor.replaceAll("\\D", "");
        return digitos.isEmpty() ? "" : digitos;
    }

    public static String formatar(String valor) {
        String digitos = somenteDigitos(valor);
        if (digitos == null || digitos.length() != TAMANHO_DIGITOS) {
            return valor == null ? "" : valor;
        }
        return digitos.substring(0, 2) + "."
                + digitos.substring(2, 5) + "."
                + digitos.substring(5, 8) + "/"
                + digitos.substring(8, 12) + "-"
                + digitos.substring(12, 14);
    }

    public static boolean isCompleto(String valor) {
        String digitos = somenteDigitos(valor);
        return digitos != null && digitos.length() == TAMANHO_DIGITOS;
    }
}
