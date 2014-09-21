package com.github.letsdrink.intellijplugin;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class TranslationCodeBuilderTest {
    TranslationCodeBuilder translationCodeBuilder = new TranslationCodeBuilder();

    @Test
    public void shouldBuildInsertionString() {
        String insertionText = translationCodeBuilder.getInsertionText("value", asList("key1", "key2", "key3"), new ArrayList<String>(), true);

        Assert.assertEquals("\n\t\t\t'key3' => 'value',", insertionText);
    }

    @Test
    public void shouldBuildInsertionStringWhenMissingKeys() {
        String insertionText = translationCodeBuilder.getInsertionText("value", asList("key1", "key2", "key3", "key4"), asList("key2", "key3"), false);

        Assert.assertEquals("\n\t\t'key2' => array(\n\t\t\t'key3' => array(\n\t\t\t\t'key4' => 'value'\n\t\t\t)\n\t\t)", insertionText);
    }
}

