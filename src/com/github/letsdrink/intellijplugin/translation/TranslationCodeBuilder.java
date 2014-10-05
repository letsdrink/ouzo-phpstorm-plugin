package com.github.letsdrink.intellijplugin.translation;

import com.google.common.collect.Iterables;

import java.util.List;

public class TranslationCodeBuilder {
    public String getInsertionText(String text, List<String> keys, List<String> missingKeys, boolean empty) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        int level = keys.size() - missingKeys.size();
        for (String missingKey : missingKeys) {
            appendTabs(sb, level);
            appendKey(sb, missingKey);
            sb.append("array(\n");
            level++;
        }

        appendTabs(sb, level);
        appendKey(sb, Iterables.getLast(keys));

        sb.append("'");
        sb.append(text.replaceAll("'", "\\\\'"));
        sb.append("'");

        level = keys.size() - 1;
        for (String missingKey : missingKeys) {
            sb.append("\n");

            appendTabs(sb, level);
            sb.append(")");
            level--;
        }

        if (empty) {
            sb.append(',');
        }
        return sb.toString();
    }

    private void appendKey(StringBuilder sb, String key) {
        sb.append("'");
        sb.append(key);
        sb.append("' => ");
    }

    private void appendTabs(StringBuilder sb, int count) {
        for (int i = 0; i < count; ++i) {
            sb.append("\t");
        }
    }

}