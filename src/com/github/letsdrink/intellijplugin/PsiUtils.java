package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.*;
import com.jetbrains.php.lang.psi.elements.impl.ConstantReferenceImpl;
import com.jetbrains.php.lang.psi.elements.impl.PhpExpressionImpl;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

public class PsiUtils {
    public static String getContent(PsiElement value) {
        if (value instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) value).getContents();
        }
        if (value instanceof PhpExpressionImpl && ((PhpExpressionImpl) value).getType().equals(PhpType.INT)) {
            return value.getText();
        }

        if (value instanceof ConstantReferenceImpl) {
            return value.getText(); //we cannot resolve constant value when this method is called from index because indices are not accessible then.
        }
        return null;
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

    public static boolean isElementTheFirstParameterInMethodCall(PsiElement psiElement, String methodFQN) {
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

        return isMethodCallTo(methodReference, methodFQN);
    }

    public static boolean isElementTheNthParameterInMethodCall(PsiElement psiElement, String methodFQN, int n) {
        if (!(psiElement.getContext() instanceof ParameterList)) {
            return false;
        }
        ParameterList parameterList = (ParameterList) psiElement.getContext();
        MethodReference methodReference = (MethodReference) parameterList.getContext();
        PsiElement[] parameters = methodReference.getParameters();
        if (parameters.length > n && !parameters[n].equals(psiElement)) {
            return false;
        }
        return isMethodCallTo(methodReference, methodFQN);
    }

    public static Method resolveMethod(MethodReference methodReference) {
        PsiReference psiReference = methodReference.getReference();
        if (psiReference == null) {
            return null;
        }

        PsiElement resolvedReference = psiReference.resolve();
        if (!(resolvedReference instanceof Method)) {
            return null;
        }
        return (Method) resolvedReference;
    }

    public static boolean isMethodCallTo(MethodReference methodReference, String name) {
        Method currentMethod = resolveMethod(methodReference);
        if (currentMethod == null) {
            return false;
        }
        return currentMethod.getFQN().equals(name);
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

    public static void deleteArrayElement(PsiElement element) {
        IElementType type;
        do {
            PsiElement nextSibling = element.getNextSibling();
            element.delete();
            element = nextSibling;
            type = element.getNode().getElementType();
        } while (element.isValid() && (type == PhpElementTypes.WHITE_SPACE || type == PhpTokenTypes.opCOMMA));
    }
}