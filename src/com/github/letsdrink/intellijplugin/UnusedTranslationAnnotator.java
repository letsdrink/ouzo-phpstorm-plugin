package com.github.letsdrink.intellijplugin;


import com.github.letsdrink.intellijplugin.index.TranslationCallIndex;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class UnusedTranslationAnnotator extends ExternalAnnotator<List<PsiElement>, List<PsiElement>> {
    @Nullable
    @Override
    public List<PsiElement> collectInformation(@NotNull PsiFile file) {
        final List<PsiElement> unusedKeys = new ArrayList<>();

        if (!TranslationUtils.isTranslationFile(file) || !Settings.getInstance(file.getProject()).annotateUnusedTranslation) {
            return unusedKeys;
        }

        final Project project = file.getProject();
        final FileBasedIndex index = FileBasedIndex.getInstance();

        TranslationParser translationParser = new TranslationParser();
        translationParser.parse(file, new TranslationParser.Handler() {
            @Override
            public void handle(String key, String text, ArrayHashElement element) {
                if (!isUsed(project, index, key)) {
                    unusedKeys.add(element.getKey());
                }
            }
        });
        return unusedKeys;
    }

    private boolean isUsed(Project project, FileBasedIndex index, String key) {
        if (key == null) {
            return false;
        }
        Collection<VirtualFile> files = index.getContainingFiles(TranslationCallIndex.KEY, key, GlobalSearchScope.allScope(project));

        return !files.isEmpty() || isUsed(project, index, TranslationUtils.getParentKey(key));
    }

    @Nullable
    @Override
    public List<PsiElement> doAnnotate(List<PsiElement> collectedInfo) {
        return collectedInfo;
    }

    @Override
    public void apply(@NotNull PsiFile file, List<PsiElement> unusedKeys, @NotNull AnnotationHolder holder) {
        for (PsiElement element : unusedKeys) {
            Annotation annotation = holder.createWarningAnnotation(element, "Unused Translation");
            annotation.registerFix(new RemoveTranslationIntentionAction());
        }
    }

}
