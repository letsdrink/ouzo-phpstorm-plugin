package com.github.letsdrink.intellijplugin.translation.rename;


import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import org.jetbrains.annotations.NotNull;

public class RenameTranslationKeyUsageInfo extends RenameTranslationUsageInfo {
    public RenameTranslationKeyUsageInfo(@NotNull PsiElement element) {
        super(element);
    }

    @Override
    void performRefactoring(String newKey) {
        PsiElement element = getElement();
        PsiElement psiElement = PhpPsiElementFactory.createFromText(element.getProject(), PhpElementTypes.STRING, "'" + newKey + "'");
        element.replace(psiElement);
    }

    @Override
    public void performPsiSpoilingRefactoring(String newKey) {

    }
}
