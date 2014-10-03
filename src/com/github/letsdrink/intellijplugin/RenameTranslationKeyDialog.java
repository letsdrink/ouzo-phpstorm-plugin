package com.github.letsdrink.intellijplugin;


import com.github.letsdrink.intellijplugin.index.TranslationCallIndex;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.indexing.FileBasedIndex;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.github.letsdrink.intellijplugin.PsiUtils.getContent;

public class RenameTranslationKeyDialog extends RenameDialog {
    private Editor editor;
    private final String key;

    public RenameTranslationKeyDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor) {
        super(project, psiElement, nameSuggestionContext, editor);
        this.editor = editor;
        key = getKey();
    }

    private String getKey() {
        TranslationFileFacade translationFileFacade = new TranslationFileFacade(getPsiElement().getContainingFile());
        String key = translationFileFacade.getKey((com.jetbrains.php.lang.psi.elements.ArrayHashElement) getPsiElement());
        return key;
    }

    @NotNull
    @Override
    protected String getLabelText() {
        return "Rename translation key '" + getKey() + "' and it's usages to";
    }

    @Override
    public String[] getSuggestedNames() {
        return new String[]{getKey()};
    }

    @Override
    protected void canRun() throws ConfigurationException {
        String newName = getNewName();
        if (Comparing.strEqual(newName, key)) {
            throw new ConfigurationException(null);
        }
        if (StringUtils.isBlank(newName) || newName.endsWith(".")) {
            throw new ConfigurationException("\'" + getNewName() + "\' is not a valid key");
        }

        FileBasedIndex index = FileBasedIndex.getInstance();
        Collection<VirtualFile> files = index.getContainingFiles(TranslationCallIndex.KEY, newName, GlobalSearchScope.allScope(getProject()));
        if (!files.isEmpty()) {
            throw new ConfigurationException("\'" + getNewName() + "\' already exists");
        }
    }

    @Override
    public void performRename(final String newKey) {
        final Project project = getProject();

        TranslationUsagesFinder translationUsagesFinder = new TranslationUsagesFinder(key);
        final UsageInfo[] usageInfos = translationUsagesFinder.findUsages(project);


        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        for (TranslationFileFacade translationFileFacade : translationFileFacades) {
            // Document document = manager.getDocument(translationFileFacade.getPsiFile());
            //  manager.doPostponedOperationsAndUnblockDocument(document);


            ArrayHashElement translationElement = translationFileFacade.getTranslationElement(key);
            if (translationElement != null) {
                final PsiElement elementToRemove = TranslationRemoveUtils.findElementToRemove(translationElement);
                if (elementToRemove != null) {

                    final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);

                    final Document document = editor.getDocument();

                    new WriteCommandAction(project) {
                        @Override
                        protected void run(Result result) throws Throwable {
                            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
                            PsiUtils.deleteArrayElement(elementToRemove);
                            manager.commitDocument(document);
                        }

                        @Override
                        public String getGroupID() {
                            return "Remove Translation";
                        }
                    }.execute();
                    //translationFileFacade.addTranslation(newKey, getContent(translationElement.getValue()));
                }

                //
            }
            for (UsageInfo usageInfo : usageInfos) {
                PsiElement element = usageInfo.getElement();
                PsiElement psiElement = PhpPsiElementFactory.createFromText(project, PhpElementTypes.STRING, "'" + newKey + "'");
                element.replace(psiElement);
            }

            close(DialogWrapper.OK_EXIT_CODE);
        }

    }
}