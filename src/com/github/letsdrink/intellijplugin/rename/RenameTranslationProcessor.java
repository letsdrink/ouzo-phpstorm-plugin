package com.github.letsdrink.intellijplugin.rename;

import com.github.letsdrink.intellijplugin.TranslationFileFacade;
import com.github.letsdrink.intellijplugin.TranslationUsagesFinder;
import com.github.letsdrink.intellijplugin.TranslationUtils;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.BaseRefactoringProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.usageView.UsageViewDescriptor;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


class RenameTranslationProcessor extends BaseRefactoringProcessor {
    private final String oldKey;
    private final String newKey;
    private UsageInfo[] usageInfos;

    public RenameTranslationProcessor(Project project, String oldKey, String newKey) {
        super(project);
        this.oldKey = oldKey;
        this.newKey = newKey;
    }

    @NotNull
    @Override
    protected UsageViewDescriptor createUsageViewDescriptor(UsageInfo[] usageInfos) {
        return new TranslationKeyUsageViewDescriptor(oldKey, newKey);
    }

    @NotNull
    @Override
    protected UsageInfo[] findUsages() {
        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(myProject);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        List<UsageInfo> result = new ArrayList<>();
        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            ArrayHashElement translationElement = translationFileFacade.getTranslationElement(oldKey);
            if (translationElement != null) {
                result.add(new RenameTranslationInLocaleUsageInfo(translationFileFacade, translationElement));
            }
        }

        TranslationUsagesFinder translationUsagesFinder = new TranslationUsagesFinder(oldKey);
        UsageInfo[] usageInfos = translationUsagesFinder.findUsages(myProject);

        for (UsageInfo usage : usageInfos) {
            result.add(new RenameTranslationKeyUsageInfo(usage.getElement()));
        }
        return Iterables.toArray(result, UsageInfo.class);
    }

    @Override
    protected void performRefactoring(UsageInfo[] usageInfos) {
        this.usageInfos = usageInfos;
        for (UsageInfo usageInfo : usageInfos) {
            RenameTranslationUsageInfo renameTranslationUsageInfo = (RenameTranslationUsageInfo) usageInfo;
            renameTranslationUsageInfo.performRefactoring(newKey);
        }
    }

    @Override
    protected void performPsiSpoilingRefactoring() {
        for (UsageInfo usageInfo : usageInfos) {
            RenameTranslationUsageInfo renameTranslationUsageInfo = (RenameTranslationUsageInfo) usageInfo;
            renameTranslationUsageInfo.performPsiSpoilingRefactoring(newKey);
        }
    }

    @Override
    protected String getCommandName() {
        return "Rename translation";
    }

}
