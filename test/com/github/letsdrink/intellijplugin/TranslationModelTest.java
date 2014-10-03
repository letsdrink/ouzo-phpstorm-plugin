package com.github.letsdrink.intellijplugin;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TranslationModelTest {
    @Test
    public void shouldTranslatePreviousPrefixKeyToExtractedTextWhenThereAreNoFoundKeys() {
        //given
        TranslationFileFacade translationFileFacade = mock(TranslationFileFacade.class);

        when(translationFileFacade.getLanguage()).thenReturn("en");

        TranslationModel translationModel = new TranslationModel(asList(translationFileFacade), new ArrayList<String>(), asList("prev."), "text");

        //when
        Map<String, String> map = translationModel.getLangTextMap("prev.");

        //then
        assertEquals(ImmutableMap.of(
                "en", "text"
        ), map);
    }

    @Test
    public void shouldNotTranslatePreviousPrefixKeyWhenThereAreFoundKeys() {
        //given
        TranslationFileFacade translationFileFacade = mock(TranslationFileFacade.class);

        when(translationFileFacade.getLanguage()).thenReturn("en");

        TranslationModel translationModel = new TranslationModel(asList(translationFileFacade), asList("key"), asList("prev."), "text");

        //when
        Map<String, String> map = translationModel.getLangTextMap("prev.");

        //then
        assertEquals(ImmutableMap.of(
                "en", ""
        ), map);
    }
}