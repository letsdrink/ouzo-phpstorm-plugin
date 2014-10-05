package com.github.letsdrink.intellijplugin.rename;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewBundle;
import com.intellij.usageView.UsageViewDescriptor;
import org.jetbrains.annotations.NotNull;


class TranslationKeyUsageViewDescriptor implements UsageViewDescriptor {
    private final String oldKey;
    private final String newKey;

    public TranslationKeyUsageViewDescriptor(String oldKey, String newKey) {
        this.oldKey = oldKey;
        this.newKey = newKey;
    }

    @Override
    @NotNull
    public PsiElement[] getElements() {
        return new PsiElement[0];
    }

    @Override
    public String getProcessedElementsHeader() {
        return StringUtil.capitalize(RefactoringBundle.message("0.to.be.renamed.to.1.2", oldKey, "", newKey));
    }

    @Override
    public String getCodeReferencesText(int usagesCount, int filesCount) {
        return RefactoringBundle.message("references.in.code.to.0", oldKey) + UsageViewBundle.getReferencesString(usagesCount, filesCount);
    }

    @Override
    public String getCommentReferencesText(int usagesCount, int filesCount) {
        return RefactoringBundle.message("comments.elements.header",
                UsageViewBundle.getOccurencesString(usagesCount, filesCount));
    }
}
