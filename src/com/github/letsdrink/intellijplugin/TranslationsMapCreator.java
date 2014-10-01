package com.github.letsdrink.intellijplugin;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationsMapCreator {
    public Map<String, String> createTranslationsMap(List<TranslationFileFacade> translationFileFacades, String key, String text) {
        final Map<String, String> translations = new HashMap<String, String>();
        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            String translation = translationFileFacade.getTranslation(key);
            translation = translation == null ? text : translation;
            translations.put(translationFileFacade.getLanguage(), translation);
        }
        return translations;
    }
}
