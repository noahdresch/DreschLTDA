package com.onboardingrsd.empresas.export;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExportColumnSelection implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<ExportColumn> columns;

    public ExportColumnSelection(List<ExportColumn> columns) {
        this.columns = new ArrayList<>(columns);
    }

    public static ExportColumnSelection of(ExportColumn... columns) {
        return new ExportColumnSelection(Arrays.asList(columns));
    }

    public List<ExportColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public List<String> getSelectedKeys() {
        List<String> keys = new ArrayList<>();
        for (ExportColumn column : columns) {
            if (column.isSelected()) {
                keys.add(column.getKey());
            }
        }
        return keys;
    }

    public void setSelectedKeys(List<String> keys) {
        Set<String> selected = keys == null ? Set.of() : new HashSet<>(keys);
        for (ExportColumn column : columns) {
            column.setSelected(selected.contains(column.getKey()));
        }
    }

    public boolean isSelected(String key) {
        return columns.stream()
                .filter(c -> c.getKey().equals(key))
                .findFirst()
                .map(ExportColumn::isSelected)
                .orElse(false);
    }

    public boolean hasSelection() {
        return columns.stream().anyMatch(ExportColumn::isSelected);
    }

    public void selectAll() {
        columns.forEach(c -> c.setSelected(true));
    }
}
