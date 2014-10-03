package com.github.letsdrink.intellijplugin;

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageTargetProvider;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.Nullable;


public class TranslationUsageTargetProvider implements UsageTargetProvider {
    @Nullable
    @Override
    public UsageTarget[] getTargets(Editor editor, PsiFile psiFile) {
        final PsiElement element = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if (element != null) {
            if (!TranslationUtils.isTranslationFile(psiFile)) {
                return UsageTarget.EMPTY_ARRAY;
            }
            ArrayHashElement hashElement = PsiTreeUtil.getParentOfType(element, ArrayHashElement.class);
            if (hashElement == null) {
                return UsageTarget.EMPTY_ARRAY;
            }
            return new UsageTarget[]{new PsiElement2UsageTargetAdapter(element)};
        }
        return UsageTarget.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public UsageTarget[] getTargets(PsiElement psiElement) {
        return UsageTarget.EMPTY_ARRAY;
    }
}
