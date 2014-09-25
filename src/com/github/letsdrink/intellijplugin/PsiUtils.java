package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.jetbrains.php.lang.psi.elements.*;

public class PsiUtils {
    public static String getContent(PsiElement value) {
        if (!(value instanceof StringLiteralExpression)) {
            return null;
        }
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

        return isMethodCallTo(methodReference, method);
    }

    public static boolean isElementTheNthParameterInMethodCall(PsiElement psiElement, Method method, int n) {
        ParameterList parameterList = (ParameterList) psiElement.getContext();
        MethodReference methodReference = (MethodReference) parameterList.getContext();
        if (!methodReference.getParameters()[n].equals(psiElement)) {
            return false;
        }
        return isMethodCallTo(methodReference, method);
    }

    public static boolean isMethodCallTo(MethodReference methodReference, Method method) {
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

    public static PsiElement getCurrentElement(Editor editor, PsiFile psiFile) {
        if (psiFile == null || editor == null)
            return null;
        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = psiFile.findElementAt(offset);

        return psiElement;
    }

    public static String getContainingFilename(PsiElement psiElement) {
        return psiElement.getContainingFile().getName();
    }
}