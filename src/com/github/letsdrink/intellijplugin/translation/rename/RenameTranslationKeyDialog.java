package com.github.letsdrink.intellijplugin.translation.rename;


import com.github.letsdrink.intellijplugin.translation.index.TranslationCallIndex;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.util.indexing.FileBasedIndex;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public abstract class RenameTranslationKeyDialog extends RenameDialog {
    private final String key;

    public RenameTranslationKeyDialog(@NotNull Project project, @NotNull PsiElement psiElement, @Nullable PsiElement nameSuggestionContext, Editor editor) {
        super(project, psiElement, nameSuggestionContext, editor);
        key = getKey();

        getCbSearchInComments().setVisible(false);
    }

    /**
     * this method is called in super.constructor where key is not available yet
     */
    protected abstract String getKey();

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
    public void performRename(String s) {
        invokeRefactoring(new RenameTranslationProcessor(getProject(), key, getNewName()));
    }
}