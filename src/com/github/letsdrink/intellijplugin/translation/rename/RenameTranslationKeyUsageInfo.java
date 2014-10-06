package com.github.letsdrink.intellijplugin.translation.rename;


import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;

public class RenameTranslationKeyUsageInfo extends RenameTranslationUsageInfo {
    private boolean refactor = true;

    public RenameTranslationKeyUsageInfo(@NotNull PsiElement element) {
        super(element);

        if (!(element.getContext() instanceof ParameterList)) {
            setDynamicUsage(true);
            refactor = false;
        }
    }

    @Override
    void performRefactoring(String newKey) {
        if (refactor) {
            PsiElement element = getElement();
            PsiElement psiElement = PhpPsiElementFactory.createFromText(element.getProject(), PhpElementTypes.STRING, "'" + newKey + "'");
            element.replace(psiElement);
        }
    }

    @Override
    public void performPsiSpoilingRefactoring(String newKey) {

    }
}
