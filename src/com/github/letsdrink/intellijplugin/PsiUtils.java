package com.github.letsdrink.intellijplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.*;

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

    public static boolean isElementTheFirstParameterInMethodCall(PsiElement psiElement, Method method) {
        if (!(psiElement.getContext() instanceof ParameterList)) {
            return false;
        }

        ParameterList parameterList = (ParameterList) psiElement.getContext();

        if (parameterList == null || !(parameterList.getContext() instanceof MethodReference)) {
            return false;
        }

        MethodReference methodReference = (MethodReference) parameterList.getContext();
        if (!methodReference.getParameters()[0].equals(psiElement)) {
            return false;
        }

        PsiReference psiReference = methodReference.getReference();
        if (psiReference == null) {
            return false;
        }

        PsiElement resolvedReference = psiReference.resolve();
        if (!(resolvedReference instanceof Method)) {
            return false;
        }
        Method currentMethod = (Method) resolvedReference;
        return currentMethod.equals(method);
    }

}