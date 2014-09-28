package com.github.letsdrink.intellijplugin;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Factory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.*;
import com.intellij.util.Processor;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static java.util.Arrays.asList;

public class TranslationUsagesAction extends AnAction {
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();

        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (!TranslationUtils.isTranslationFile(psiFile)) {
            return;
        }

        PsiElement psiElement = PsiUtils.getCurrentElement(editor, psiFile);

        ArrayHashElement hashElement = PsiTreeUtil.getParentOfType(psiElement, ArrayHashElement.class);
        if (hashElement == null) {
            return;
        }

        TranslationParser translationParser = new TranslationParser(psiFile);
        String key = translationParser.getKey(hashElement);

        TranslationUsagesFinder translationUsagesFinder = new TranslationUsagesFinder(key);
        UsageInfo[] usageInfos = translationUsagesFinder.findUsages(project);
        Usage[] usages = FluentIterable.from(asList(usageInfos))
                .transform(toUsage())
                .toArray(Usage.class);

        final UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setTabText("Usages of " + key);
        presentation.setUsagesString(RefactoringBundle.message("usageView.usagesText"));
        presentation.setScopeText("Project");
        UsageViewManager.getInstance(project).showUsages(UsageTarget.EMPTY_ARRAY, usages, presentation, rerunFactory(project, translationUsagesFinder));
    }

    private Function<UsageInfo, Usage> toUsage() {
        return new Function<UsageInfo, Usage>() {
            @Nullable
            @Override
            public Usage apply(@Nullable UsageInfo usageInfo) {
                return new UsageInfo2UsageAdapter(usageInfo);
            }
        };
    }

    @NotNull
    private Factory<UsageSearcher> rerunFactory(@NotNull final Project project, final TranslationUsagesFinder translationUsagesFinder) {
        return new Factory<UsageSearcher>() {
            @Override
            public UsageSearcher create() {
                return new UsageInfoSearcherAdapter() {
                    @Override
                    protected UsageInfo[] findUsages() {
                        return translationUsagesFinder.findUsages(project);
                    }

                    @Override
                    public void generate(@NotNull Processor<Usage> processor) {
                        processUsages(processor, project);
                    }
                };
            }
        };
    }

}
