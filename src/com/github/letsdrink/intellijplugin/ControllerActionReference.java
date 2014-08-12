package com.github.letsdrink.intellijplugin;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ControllerActionReference extends PsiReferenceBase<PsiElement> {
    private String controller;
    private String action;
    private PsiElement psiElement;

    public ControllerActionReference(PsiElement psiElement) {
        super(psiElement);
        this.psiElement = psiElement;
        String contents = ((StringLiteralExpression) psiElement).getContents();
        String[] strings = contents.split("#");
        String controllerName = underscoreToCamelCase(strings[0]);
        controllerName = prepareNamespace(controllerName);
        controller = "Controller\\" + controllerName + "Controller";
        action = strings[1];
    }

    private String underscoreToCamelCase(String text) {
        return StringUtils.capitalize(WordUtils.capitalizeFully(text, new char[]{'_'}).replaceAll("_", ""));
    }

    private String prepareNamespace(String s) {
        return s.replace('/', '\\');
    }

    @Nullable
    @Override
    public Method resolve() {
        PhpIndex phpIndex = PhpIndex.getInstance(psiElement.getProject());
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(controller);

        for (PhpClass phpClass : phpClasses) {
            Optional<Method> method = Iterables.tryFind(phpClass.getMethods(), new Predicate<Method>() {
                @Override
                public boolean apply(Method method) {
                    return method.getName().equals(action);
                }
            });

            if (method.isPresent()) {
                return method.get();
            }
            return null;
        }
        return null;
    }

    class ControllerActionLookupElement extends LookupElement {
        private Method controllerAction;

        public ControllerActionLookupElement(Method controllerAction) {
            this.controllerAction = controllerAction;
        }

        @NotNull
        @Override
        public String getLookupString() {
            return this.controllerAction.getName();
        }

        public void renderElement(LookupElementPresentation presentation) {
            presentation.setItemText(getLookupString());
            presentation.setTypeText(controllerAction.getFQN());
            presentation.setTypeGrayed(true);
        }
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new ControllerActionLookupElement[]{new ControllerActionLookupElement(resolve())};
    }
}