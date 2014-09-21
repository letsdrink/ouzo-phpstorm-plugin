package com.github.letsdrink.intellijplugin;

import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class PsiUtils {
    public static String getContent(PsiElement value) {
        return ((StringLiteralExpression) value).getContents();
    }
}