package com.github.letsdrink.intellijplugin;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;


class ControllerActionReferenceProvider extends PsiReferenceProvider {
    private boolean isApplicable(PsiElement psiElement) {
        if (!OuzoUtils.isExpectedFile(psiElement, "routes.php")) {
            return false;
        }

        return PsiUtils.isElementTheFirstParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_RESOURCE_FQN) ||
                PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_GET_FQN, 1) ||
                PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_POST_FQN, 1) ||
                PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_DELETE_FQN, 1) ||
                PsiUtils.isElementTheNthParameterInMethodCall(psiElement, OuzoUtils.OUZO_ROUTE_PUT_FQN, 1);
    }

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        if (!isApplicable(psiElement)) {
            return new PsiReference[]{};
        }
        String controller = extractController(PsiUtils.getContent(psiElement));
        String action = extractAction(PsiUtils.getContent(psiElement));

        PsiReferenceBase.Immediate reference = new PsiReferenceBase.Immediate(psiElement, OuzoUtils.getControllerAction(psiElement.getProject(), controller, action));
        return new PsiReference[]{reference};
    }

    private String extractController(String controller) {
        if (controller.contains("#")) {
            controller = controller.split("#")[0];
        }
        return prepareNamespace(underscoreToCamelCase(controller)) + "Controller";
    }

    private String underscoreToCamelCase(String text) {
        return StringUtils.capitalize(WordUtils.capitalizeFully(text, new char[]{'_'}).replaceAll("_", ""));
    }

    private String prepareNamespace(String s) {
        return s.replace('/', '\\');
    }

    private String extractAction(String action) {
        if (action.contains("#")) {
            return action.split("#")[1];
        }
        return "";
    }
}
