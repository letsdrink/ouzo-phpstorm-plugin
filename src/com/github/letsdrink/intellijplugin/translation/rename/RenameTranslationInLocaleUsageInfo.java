package com.github.letsdrink.intellijplugin.translation.rename;


import com.github.letsdrink.intellijplugin.PsiUtils;
import com.github.letsdrink.intellijplugin.translation.TranslationFileFacade;
import com.github.letsdrink.intellijplugin.translation.TranslationRemoveUtils;
import com.github.letsdrink.intellijplugin.translation.TranslationUtils;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RenameTranslationInLocaleUsageInfo extends RenameTranslationUsageInfo {
    private final TranslationFileFacade translationFileFacade;
    private final String oldKey;
    private String text;

    public RenameTranslationInLocaleUsageInfo(TranslationFileFacade translationFileFacade, @NotNull PsiElement element, String oldKey) {
        super(element);
        this.oldKey = oldKey;
        text = PsiUtils.getContent(((ArrayHashElement) element).getValue());
        this.translationFileFacade = translationFileFacade;
    }

    @Override
    void performRefactoring(String newKey) {
        if (parentChanged(newKey)) {
            ArrayHashElement element = getArrayHashElement();
            final PsiElement elementToRemove = TranslationRemoveUtils.findElementToRemove(element);
            PsiUtils.deleteArrayElement(elementToRemove);
        } else {
            PsiElement keyElement = getArrayHashElement().getKey();
            String key = TranslationUtils.getLastKeyPart(newKey);
            PsiElement psiElement = PhpPsiElementFactory.createFromText(keyElement.getProject(), PhpElementTypes.STRING, "'" + key + "'");
            keyElement.replace(psiElement);
        }
    }

    private boolean parentChanged(String newKey) {
        return !Objects.equals(TranslationUtils.getParentKey(this.oldKey), TranslationUtils.getParentKey(newKey));
    }

    @Override
    public void performPsiSpoilingRefactoring(String newKey) {
        translationFileFacade.addTranslation(newKey, text);
    }

    private ArrayHashElement getArrayHashElement() {
        return (ArrayHashElement) getElement();
    }
}
