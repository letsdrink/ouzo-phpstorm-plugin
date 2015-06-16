package com.github.letsdrink.intellijplugin.translation;


import com.github.letsdrink.intellijplugin.OuzoUtils;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.twig.TwigTokenTypes;

public class ElementTypeResolver {
    public boolean isPhpString(PsiElement psiElement) {
        return PlatformPatterns.psiElement(PhpElementTypes.STRING).accepts(psiElement);
    }

    public Language getFileLanguage(PsiElement psiElement) {
        FileType fileType= FileTypeManager.getInstance().getFileTypeByExtension(psiElement.getContainingFile().getName());
        if (fileType instanceof LanguageFileType) {
            return ((LanguageFileType) fileType).getLanguage();
        }
        return null;
    }

    public boolean isInView(PsiElement psiElement) {
        return OuzoUtils.isInViewDir(psiElement.getContainingFile());
    }

    public boolean isXmlText(PsiElement element) {
        return isXmlText(element.getNode().getElementType());
    }

    public boolean isJsLiteral(PsiElement element) {
        return isJsLiteral(element.getNode().getElementType());
    }

    public boolean isXmlText(IElementType elementType) {
        return elementType == XmlTokenType.XML_DATA_CHARACTERS || elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN || elementType.toString().equals("XML_TEXT");
    }

    public boolean isJsLiteral(IElementType elementType) {
        return elementType.toString().equals("JS:STRING_LITERAL") || "JS:LITERAL_EXPRESSION".equals(elementType.toString());
    }

    public boolean isTwigLiteral(PsiElement element) {
        return PlatformPatterns.psiElement(TwigTokenTypes.STRING_TEXT).accepts(element);
    }
}
