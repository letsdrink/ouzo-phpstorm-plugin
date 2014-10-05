package com.github.letsdrink.intellijplugin.rename;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.github.letsdrink.intellijplugin.TranslationFileFacade;
import com.github.letsdrink.intellijplugin.TranslationRemoveUtils;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

public class RenameTranslationInLocaleUsageInfo extends RenameTranslationUsageInfo {
    private final TranslationFileFacade translationFileFacade;
    private String text;

    public RenameTranslationInLocaleUsageInfo(TranslationFileFacade translationFileFacade, @NotNull PsiElement element) {
        super(element);
        text = PsiUtils.getContent(((ArrayHashElement)element).getValue());
        this.translationFileFacade = translationFileFacade;
    }

    @Override
    void performRefactoring(String newKey) {
        ArrayHashElement element = getArrayHashElement();
        final PsiElement elementToRemove = TranslationRemoveUtils.findElementToRemove(element);
        PsiUtils.deleteArrayElement(elementToRemove);
    }

    @Override
    public void performPsiSpoilingRefactoring(String newKey) {
        translationFileFacade.addTranslation(newKey, text);
    }

    private ArrayHashElement getArrayHashElement() {
        return (ArrayHashElement) getElement();
    }
}
