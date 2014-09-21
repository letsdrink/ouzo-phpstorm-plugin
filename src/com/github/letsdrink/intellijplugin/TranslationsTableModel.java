package com.github.letsdrink.intellijplugin;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;
import java.util.Map;

public class TranslationsTableModel extends AbstractTableModel {
    private final Map<String, String> translations;
    private final Map<Integer, String> translationsIndices = new HashMap<Integer, String>();

    public TranslationsTableModel(Map<String, String> translations) {
        this.translations = translations;
        int index = 0;
        for (String key : translations.keySet()) {
            translationsIndices.put(index, key);
            index++;
        }
    }

    public boolean isCellEditable(int row, int col) {
        return col == 1;
    }

    public void setValueAt(Object value, int row, int col) {
        translations.put(getLanguage(row), value.toString());
        fireTableCellUpdated(row, col);
    }

    @Override
    public int getRowCount() {
        return translations.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String language = getLanguage(rowIndex);
        if (columnIndex == 0) {
            return language;
        }
        return translations.get(language);
    }

    private String getLanguage(int rowIndex) {
        return translationsIndices.get(rowIndex);
    }
}
