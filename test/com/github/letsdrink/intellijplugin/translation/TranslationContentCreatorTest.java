package com.github.letsdrink.intellijplugin.translation;

import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.twig.TwigLanguage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TranslationContentCreatorTest {
    PsiElement psiElement = mock(PsiElement.class);

    ElementTypeResolver typeResolver = mock(ElementTypeResolver.class);

    TranslationContentCreator contentCreator = new TranslationContentCreator(typeResolver);

    @Test
    public void shouldBuildTranslationForJavascriptStringInPhpFile() {
        //given
        when(typeResolver.isJsLiteral(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("'<?= I18n::t('key')?>'", content);
    }

    @Test
    public void shouldBuildTranslationForHtmlInPhpFile() {
        //given
        when(typeResolver.isXmlText(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(PhpLanguage.INSTANCE);

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
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(PhpLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("<?= t('key')?>", content);
    }

    @Test
    public void shouldBuildTranslationForJavascriptStringInJsFile() {
        //given
        when(typeResolver.isJsLiteral(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(JavascriptLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("t('key')", content);
    }

    @Test
    public void shouldBuildTranslationForPhpString() {
        //given
        when(typeResolver.isPhpString(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(PhpLanguage.INSTANCE);

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
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(PhpLanguage.INSTANCE);


        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("t('key')", content);
    }

    @Test
    public void shouldBuildTranslationForTwigStringInViewFile() {
        //given
        when(typeResolver.isTwigLiteral(psiElement)).thenReturn(true);
        when(typeResolver.isInView(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(TwigLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("t('key')", content);
    }

    @Test
    public void shouldBuildTranslationForTwigHtmlInViewFile() {
        //given
        when(typeResolver.isTwigLiteral(psiElement)).thenReturn(false);
        when(typeResolver.isInView(psiElement)).thenReturn(true);
        when(typeResolver.getFileLanguage(psiElement)).thenReturn(TwigLanguage.INSTANCE);

        //when
        String content = contentCreator.buildTranslation("key", psiElement);

        //then
        assertEquals("{{ t('key') }}", content);
    }
}