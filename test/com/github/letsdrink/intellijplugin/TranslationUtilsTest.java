package com.github.letsdrink.intellijplugin;

import com.github.letsdrink.intellijplugin.translation.TranslationUtils;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class TranslationUtilsTest {
    @Test
    public void shouldReturnTrueIfPrefixIsParent() {
        //given
        String key = "user.table.header";

        //when then
        assertTrue(TranslationUtils.isParent("user.table", key));
        assertTrue(TranslationUtils.isParent("user", key));

        assertFalse(TranslationUtils.isParent("user.table.he", key));
        assertFalse(TranslationUtils.isParent("user.table.", key));
    }
}
