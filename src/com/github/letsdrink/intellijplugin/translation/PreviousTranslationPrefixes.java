package com.github.letsdrink.intellijplugin.translation;


import java.util.LinkedList;
import java.util.List;

public class PreviousTranslationPrefixes {
    private static final int MAX_SIZE = 3;
    private final LinkedList<String> previous = new LinkedList<String>();

    public void addForKey(String key) {
        String parent = TranslationUtils.getParentKey(key);
        if (parent != null) {
            add(parent + ".");
        }
    }

    private void add(String parent) {
        if (!previous.contains(parent)) {
            previous.addFirst(parent);
        }
        if (previous.size() > MAX_SIZE) {
            previous.pollLast();
        }
    }

    public List<String> getList() {
        return previous;
    }
}
