package com.github.letsdrink.intellijplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

public class PsiUtils {
    public static String getContent(PsiElement value) {
        return ((StringLiteralExpression) value).getContents();
    }

    public static boolean isElementTheFirstParameterInFunctionCall(PsiElement psiElement, String functionName) {
        if (!(psiElement.getContext() instanceof ParameterList)) {
            return false;
        }

        ParameterList parameterList = (ParameterList) psiElement.getContext();

        if (parameterList == null || !(parameterList.getContext() instanceof FunctionReference)) {
            return false;
        }

        FunctionReference function = (FunctionReference) parameterList.getContext();

        if (function.getName().equals(functionName) && function.getParameters()[0].equals(psiElement)) {
            return true;
        }
        return false;
    }
}