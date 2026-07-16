package com.onboardingrsd.empresas.converter;

import com.onboardingrsd.empresas.util.CnpjUtil;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.convert.FacesConverter;

@FacesConverter(value = "cnpjConverter", managed = true)
public class CnpjConverter implements Converter<String> {

    @Override
    public String getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String digitos = CnpjUtil.somenteDigitos(value);
        if (digitos != null && !digitos.isEmpty() && digitos.length() != 14) {
            throw new ConverterException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "CNPJ deve estar no formato 00.000.000/0000-00",
                    null));
        }
        return digitos;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return CnpjUtil.formatar(value);
    }
}
