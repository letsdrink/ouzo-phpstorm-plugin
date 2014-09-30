package com.github.letsdrink.intellijplugin;


import com.github.letsdrink.intellijplugin.index.TranslationCallIndex;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class UnusedTranslationAnnotator extends ExternalAnnotator<UnusedTranslationAnnotator.UnusedTranslations, UnusedTranslationAnnotator.UnusedTranslations> {
    static class UnusedTranslations {
        private final List<PsiElement> unusedKeys;
        public static final UnusedTranslations EMPTY = new UnusedTranslations(new ArrayList<PsiElement>());

        public UnusedTranslations(List<PsiElement> unusedKeys) {
            this.unusedKeys = unusedKeys;
        }

        public List<PsiElement> getUnusedKeys() {
            return unusedKeys;
        }
    }

    @Nullable
    @Override
    public UnusedTranslations collectInformation(@NotNull PsiFile file) {
        if (!TranslationUtils.isTranslationFile(file)) {
            return UnusedTranslations.EMPTY;
        }

        final Project project = file.getProject();
        final FileBasedIndex index = FileBasedIndex.getInstance();
        final List<PsiElement> unusedKeys = new ArrayList<>();

        TranslationParser translationParser = new TranslationParser();
        translationParser.parse(file, new TranslationParser.Handler() {
            @Override
            public void handle(String key, String text, ArrayHashElement element) {
                if (!isUsed(project, index, key)) {
                    unusedKeys.add(element.getKey());
                }
            }
        });
        return new UnusedTranslations(unusedKeys);
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
    public UnusedTranslations doAnnotate(UnusedTranslations collectedInfo) {
        return collectedInfo;
    }

    @Override
    public void apply(@NotNull PsiFile file, UnusedTranslations annotationResult, @NotNull AnnotationHolder holder) {
        for (PsiElement element : annotationResult.getUnusedKeys()) {
            holder.createWarningAnnotation(element, "Unused Translation");
        }
    }

}
