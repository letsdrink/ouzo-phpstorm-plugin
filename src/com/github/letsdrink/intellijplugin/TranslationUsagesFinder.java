package com.github.letsdrink.intellijplugin;

import com.github.letsdrink.intellijplugin.index.TranslationCallIndex;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.intellij.analysis.AnalysisScope;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class TranslationUsagesFinder {
    private final String searchedKey;

    public TranslationUsagesFinder(String searchedKey) {
        this.searchedKey = searchedKey;
    }

    public UsageInfo[] findUsages(final Project project) {
        final ProgressManager progressManager = ProgressManager.getInstance();

        Collection<VirtualFile> files = getFilesToSearch(project);

        final AnalysisScope scope = new AnalysisScope(project, files);

        final int totalFiles = scope.getFileCount();
        final Set<PsiElement> processed = new HashSet<PsiElement>();
        Runnable searchRunner = new Runnable() {
            @Override
            public void run() {
                scope.accept(new PsiElementVisitor() {
                    private int myFileCount = 0;

                    @Override
                    public void visitFile(PsiFile file) {
                        myFileCount++;
                        final ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                        if (progressIndicator != null) {
                            final VirtualFile virtualFile = file.getVirtualFile();
                            if (virtualFile != null) {
                                progressIndicator.setText2(ProjectUtil.calcRelativeToProjectPath(virtualFile, project));
                            }
                            progressIndicator.setFraction(((double) myFileCount) / totalFiles);
                        }
                        map(file, processed);

                    }
                });
            }
        };

        if (ApplicationManager.getApplication().isDispatchThread()) {
            if (!progressManager.runProcessWithProgressSynchronously(searchRunner, "Searching usages of " + searchedKey + " ...", true, project)) {
                return null;
            }
        } else {
            searchRunner.run();
        }

        return FluentIterable.from(processed).transform(toUsageInfo()).toArray(UsageInfo.class);
    }

    private Collection<VirtualFile> getFilesToSearch(Project project) {
        FileBasedIndex index = FileBasedIndex.getInstance();

        Collection<VirtualFile> files = new HashSet<>();
        String key = searchedKey;

        while (key != null) {
            files.addAll(index.getContainingFiles(TranslationCallIndex.KEY, key, GlobalSearchScope.allScope(project)));
            key = TranslationUtils.getParentKey(key);
        }
        return files;
    }

    private Function<PsiElement, UsageInfo> toUsageInfo() {
        return new Function<PsiElement, UsageInfo>() {
            @Nullable
            @Override
            public UsageInfo apply(@Nullable PsiElement psiElement) {
                return new UsageInfo(psiElement);
            }
        };
    }

    public void map(@NotNull PsiFile file, final Set<PsiElement> translationKeys) {
        TranslationCallParser translationCallParser = new TranslationCallParser();
        translationCallParser.parse(file, new TranslationCallParser.TranslationCallHandler() {
            @Override
            public void handleKey(String key, PsiElement keyElement) {
                if (searchedKey.equals(key) || TranslationUtils.isParent(key, searchedKey)|| TranslationUtils.isParent(searchedKey, key)) {
                    translationKeys.add(keyElement);
                }
            }
        });
    }
}
