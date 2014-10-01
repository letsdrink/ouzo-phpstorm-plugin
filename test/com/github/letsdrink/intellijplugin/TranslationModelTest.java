package com.github.letsdrink.intellijplugin;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TranslationModelTest {
    @Test
    public void shouldTranslatePreviousPrefixKeyToExtractedTextWhenThereAreNoOtherKeys() {
        //given
        TranslationFileFacade translationFileFacade = mock(TranslationFileFacade.class);

        when(translationFileFacade.getLanguage()).thenReturn("en");

        TranslationModel translationModel = new TranslationModel(asList(translationFileFacade), asList("prev."), "text");

        //when
        Map<String, String> map = translationModel.getLangTextMap("prev.");

        //then
        assertEquals(ImmutableMap.of(
                "en", "text"
        ), map);
    }

    @Test
    public void shouldNotTranslatePreviousPrefixKeyWhenThereAreOtherKeys() {
        //given
        TranslationFileFacade translationFileFacade = mock(TranslationFileFacade.class);

        when(translationFileFacade.getLanguage()).thenReturn("en");

        TranslationModel translationModel = new TranslationModel(asList(translationFileFacade), asList("key", "prev."), "text");

        //when
        Map<String, String> map = translationModel.getLangTextMap("prev.");

        //then
        assertEquals(ImmutableMap.of(
                "en", ""
        ), map);
    }
}