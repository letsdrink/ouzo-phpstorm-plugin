package com.github.letsdrink.intellijplugin;


import com.intellij.find.findUsages.CustomUsageSearcher;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public class TranslationUsageSearcher extends CustomUsageSearcher {
    @Override
    public void processElementUsages(@NotNull PsiElement psiElement, @NotNull Processor<Usage> usageProcessor, @NotNull FindUsagesOptions findUsagesOptions) {


        UsageInfo2UsageAdapter usageInfo2UsageAdapter = new UsageInfo2UsageAdapter(new UsageInfo(psiElement));
        usageProcessor.process(usageInfo2UsageAdapter);
        System.out.println("processElementUsages");
        System.out.println(usageInfo2UsageAdapter.toString());
        System.out.println(usageInfo2UsageAdapter.getPlainText());
    }
}
