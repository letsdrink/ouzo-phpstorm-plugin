package com.github.letsdrink.intellijplugin.translation;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ExtractTranslationAction extends AnAction {
    private static PreviousTranslationPrefixes previousPrefixes = new PreviousTranslationPrefixes();

    ElementTypeResolver typeResolver = new ElementTypeResolver();
    TranslationContentCreator translationContentCreator = new TranslationContentCreator(typeResolver);

    public ExtractTranslationAction() {
        super("ExtractTranslation");
    }

    public void actionPerformed(AnActionEvent event) {

        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        PsiElement psiElement = PsiUtils.getCurrentElement(editor, psiFile);

        if (!isTypeSupported(psiElement)) {
            return;
        }

        if (psiElement.getParent().getChildren().length > 0 && Iterables.all(asList(psiElement.getParent().getChildren()), isXmlText())) {
            psiElement = psiElement.getParent();
        }

        final PsiElement textPsiElement = psiElement;

        final String text = getTextToTranslate(textPsiElement);

        final Project project = textPsiElement.getProject();

        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        List<String> keys = Lists.newArrayList(FluentIterable.from(translationFileFacades)
                .transformAndConcat(TranslationFileFacade.getKeysFunction(text)).toSet());

        TranslationDialog dialog = new TranslationDialog(new TranslationModel(translationFileFacades, keys, previousPrefixes.getList(), text), new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
                previousPrefixes.addForKey(key);
                replaceTextWithTranslation(key, textPsiElement, editor);
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    TranslationFileFacade parser = Iterables.find(translationFileFacades, TranslationFileFacade.languageEqualsFunction(entry.getKey()));
                    parser.addTranslationInWriteCommand(key, entry.getValue());
                }
            }
        });
        dialog.showDialog();
    }

    private Predicate<PsiElement> isXmlText() {
        return new Predicate<PsiElement>() {
            @Override
            public boolean apply(@Nullable PsiElement psiElement) {
                return typeResolver.isXmlText(psiElement);
            }
        };
    }

    private String getTextToTranslate(PsiElement psiElement) {
        if (psiElement instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) psiElement).getContents().trim();
        }
        if (typeResolver.isJsLiteral(psiElement.getNode().getElementType())) {
            return StringUtils.strip(psiElement.getText().trim(), "\"'");
        }
        return psiElement.getText().trim();
    }

    private void replaceTextWithTranslation(final String key, final PsiElement psiElement, final Editor editor) {
        PsiDocumentManager.getInstance(psiElement.getProject()).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        final TextRange range = psiElement.getTextRange();
        new WriteCommandAction(psiElement.getProject()) {
            @Override
            protected void run(Result result) throws Throwable {
                String insertString = translationContentCreator.buildTranslation(key, psiElement);

                int startOffset = range.getStartOffset();
                int endOffset = range.getEndOffset();

                if (typeResolver.isTwigLiteral(psiElement)) {
                    startOffset--; //for quotes
                    endOffset++;
                }

                editor.getDocument().replaceString(startOffset, endOffset, insertString);
                editor.getCaretModel().moveToOffset(startOffset);
            }

            @Override
            public String getGroupID() {
                return "Translation Extraction";
            }
        }.execute();
    }

    private boolean isTypeSupported(PsiElement psiElement) {
        IElementType elementType = psiElement.getNode().getElementType();
        return typeResolver.isXmlText(elementType) ||
                typeResolver.isJsLiteral(elementType) ||
                typeResolver.isPhpString(psiElement.getParent()) ||
                typeResolver.isTwigLiteral(psiElement) ||
                elementType.equals(PhpElementTypes.WHITE_SPACE);
    }
}
