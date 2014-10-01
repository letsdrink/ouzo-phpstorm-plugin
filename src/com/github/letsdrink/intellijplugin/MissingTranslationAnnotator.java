package com.github.letsdrink.intellijplugin;


import com.github.letsdrink.intellijplugin.index.TranslationKeyIndex;
import com.google.common.base.Joiner;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MissingTranslationAnnotator extends ExternalAnnotator<List<PsiElement>, List<PsiElement>> {
    @Nullable
    @Override
    public List<PsiElement> collectInformation(@NotNull PsiFile file) {
        final Project project = file.getProject();
        final FileBasedIndex index = FileBasedIndex.getInstance();

        final List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<PsiElement> missingKeys = new ArrayList<>();

        TranslationCallParser translationCallParser = new TranslationCallParser();
        translationCallParser.parse(file, new TranslationCallParser.TranslationCallHandler() {
            @Override
            public void handleKey(String key, PsiElement keyElement) {
                if (!(keyElement.getContext() instanceof ParameterList)) {
                    return;
                }
                Collection<VirtualFile> files = index.getContainingFiles(TranslationKeyIndex.KEY, key, GlobalSearchScope.allScope(project));
                if (translationFiles.size() > files.size()) {
                    missingKeys.add(keyElement);
                }
                if (translationFiles.size() < files.size()) {
                    throw new IllegalStateException("Too many translation files in index: " + Joiner.on(", ").join(files));
                }
            }
        });
        return missingKeys;
    }

    @Nullable
    @Override
    public List<PsiElement> doAnnotate(List<PsiElement> collectedInfo) {
        return collectedInfo;
    }

    @Override
    public void apply(@NotNull PsiFile file, List<PsiElement> annotationResult, @NotNull AnnotationHolder holder) {
        for (PsiElement element : annotationResult) {
            Annotation warningAnnotation = holder.createWarningAnnotation(element, "Missing Translation");
            warningAnnotation.registerFix(new AddMissingTranslationIntentionAction());
        }
    }
}
