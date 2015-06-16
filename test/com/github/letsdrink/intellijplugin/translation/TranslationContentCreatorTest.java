package com.github.letsdrink.intellijplugin.translation;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.PhpLanguage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TranslationContentCreatorTest {
    PsiElement psiElement = mock(PsiElement.class);
    PsiFile psiFile = mock(PsiFile.class);

    ElementTypeResolver typeResolver = mock(ElementTypeResolver.class);

    TranslationContentCreator contentCreator = new TranslationContentCreator(typeResolver);

    @Before
    public void setUp() throws Exception {
        when(psiElement.getContainingFile()).thenReturn(psiFile);
    }

    @Test
    public void shouldBuildTranslationForJavascriptStringInPhpFile() {
        //given
        when(typeResolver.isJsLiteral(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("'<?= I18n::t('key')?>'", content);
    }

    @Test
    public void shouldBuildTranslationForHtmlInPhpFile() {
        //given
        when(typeResolver.isXmlText(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("<?= I18n::t('key')?>", content);
    }

    @Test
    public void shouldBuildTranslationForHtmlInPhpViewFile() {
        //given
        when(typeResolver.isXmlText(psiElement)).thenReturn(true);
        when(typeResolver.isInView(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("<?= t('key')?>", content);
    }

    @Test
    public void shouldBuildTranslationForJavascriptStringInJsFile() {
        //given
        when(typeResolver.isJsLiteral(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(JavascriptLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("t('key')", content);
    }

    @Test
    public void shouldBuildTranslationForPhpString() {
        //given
        when(typeResolver.isPhpString(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("I18n::t('key')", content);
    }

    @Test
    public void shouldBuildTranslationForPhpStringInViewFile() {
        //given
        when(typeResolver.isPhpString(psiElement)).thenReturn(true);
        when(typeResolver.isInView(psiElement)).thenReturn(true);
        when(psiFile.getLanguage()).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("t('key')", content);
    }
}