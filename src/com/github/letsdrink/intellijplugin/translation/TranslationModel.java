package com.github.letsdrink.intellijplugin.translation;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationModel {
    private static final String EMPTY_KEY = "";
    private Map<String, Map<String, String>> translations = new HashMap<String, Map<String, String>>();
    private List<String> keys;

    public TranslationModel(List<String> keys, Map<String, Map<String, String>> translations) {
        this.keys = keys;
        this.translations = translations;
    }

    public TranslationModel(List<TranslationFileFacade> translationFileFacades, List<String> keys, String text) {
        this(translationFileFacades, keys, new ArrayList<String>(), text);
    }

    public TranslationModel(List<TranslationFileFacade> translationFileFacades, List<String> foundKeys, List<String> prevKeys, String text) {
        this.keys = Lists.newArrayList(Iterables.concat(foundKeys, prevKeys));

        if (this.keys.isEmpty()) {
            addForKey(translationFileFacades, text, EMPTY_KEY);
        }

        for (String key : foundKeys) {
            addForKey(translationFileFacades, "", key);
        }

        for (String key : prevKeys) {
            addForKey(translationFileFacades, foundKeys.isEmpty() ? text : "", key);
        }
    }

    private void addForKey(List<TranslationFileFacade> translationFileFacades, String text, String key) {
        HashMap<String, String> map = new HashMap<String, String>();

        translations.put(key, map);
        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            String translation = translationFileFacade.getTranslation(key);
            translation = translation == null ? text : translation;
            map.put(translationFileFacade.getLanguage(), translation);
        }
    }

    public Map<String, String> getLangTextMap() {
        return getLangTextMap(Iterables.getFirst(keys, EMPTY_KEY));
    }

    public Map<String, String> getLangTextMap(String key) {
        return translations.get(key);
    }

    public List<String> getKeys() {
        return keys;
    }


}
