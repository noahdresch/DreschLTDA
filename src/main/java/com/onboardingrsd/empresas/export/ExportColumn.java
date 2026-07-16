package com.onboardingrsd.empresas.export;

import java.io.Serializable;
import java.util.Objects;

/**
 * Coluna disponível para exportação tabular.
 * Reutilizável por qualquer tela que ofereça checklist de colunas.
 */
public class ExportColumn implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String key;
    private final String label;
    private boolean selected;

    public ExportColumn(String key, String label) {
        this(key, label, true);
    }

    public ExportColumn(String key, String label, boolean selected) {
        this.key = Objects.requireNonNull(key, "key");
        this.label = Objects.requireNonNull(label, "label");
        this.selected = selected;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
