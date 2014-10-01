package com.github.letsdrink.intellijplugin;

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
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class ExtractTranslationAction extends AnAction {
    private static String lastKeyPrefix;

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

        if (Iterables.all(asList(psiElement.getParent().getChildren()), isSupportedPredicate())) {
            psiElement = psiElement.getParent();
        }

        final PsiElement textPsiElement = psiElement;

        final String text = getTextToTranslate(textPsiElement);

        final Project project = textPsiElement.getProject();

        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationFileFacade> translationFileFacades = Lists.transform(translationFiles, TranslationFileFacade.createParser());

        List<String> keys = getKeys(translationFileFacades, text);

        TranslationDialog dialog = new TranslationDialog(new TranslationModel(translationFileFacades, keys, text), new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key, Map<String, String> translations) {
                lastKeyPrefix = TranslationUtils.getParentKey(key);
                replaceTextWithTranslation(key, textPsiElement, editor);
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    TranslationFileFacade parser = Iterables.find(translationFileFacades, TranslationFileFacade.languageEqualsFunction(entry.getKey()));
                    parser.addTranslation(key, entry.getValue());
                }
            }
        });
        dialog.showDialog();
    }

    private List<String> getKeys(List<TranslationFileFacade> translationFileFacades, String text) {
        List<String> keys = Lists.newArrayList(FluentIterable.from(translationFileFacades)
                .transformAndConcat(TranslationFileFacade.getKeysFunction(text)).toSet());

        if (lastKeyPrefix != null) {
            keys.add(lastKeyPrefix + ".");
        }
        return keys;
    }

    private Predicate<PsiElement> isSupportedPredicate() {
        return new Predicate<PsiElement>() {
            @Override
            public boolean apply(@Nullable PsiElement psiElement) {
                return isTypeSupported(psiElement);
            }
        };
    }

    private String getTextToTranslate(PsiElement psiElement) {
        if (psiElement instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) psiElement).getContents().trim();
        }
        if (isJsLiteral(psiElement.getNode().getElementType())) {
            return StringUtils.strip(psiElement.getText().trim(), "\"'");
        }
        return psiElement.getText().trim();
    }

    private void replaceTextWithTranslation(final String key, final PsiElement finalPsiElement, final Editor editor) {
        PsiDocumentManager.getInstance(finalPsiElement.getProject()).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        final TextRange range = finalPsiElement.getTextRange();
        new WriteCommandAction(finalPsiElement.getProject()) {
            @Override
            protected void run(Result result) throws Throwable {
                String insertString = "t('" + key + "')";

                PsiFile file = finalPsiElement.getContainingFile();
                if (!OuzoUtils.isInViewDir(file)) {
                    insertString = "I18n::" + insertString;
                }

                if (!isPhpString(finalPsiElement)) {
                    insertString = "<?= " + insertString + "?>";
                }
                if (isJsLiteral(finalPsiElement.getNode().getElementType())) {
                    insertString = "'" + insertString + "'";
                }
                editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), insertString);
                editor.getCaretModel().moveToOffset(range.getStartOffset());
            }

            @Override
            public String getGroupID() {
                return "Translation Extraction";
            }
        }.execute();
    }

    private boolean isTypeSupported(PsiElement psiElement) {
        IElementType elementType = psiElement.getNode().getElementType();
        return isXmlText(elementType) || elementType.toString().equals("XML_TEXT") || isJsLiteral(elementType) || isPhpString(psiElement.getParent()) || elementType.equals(PhpElementTypes.WHITE_SPACE);
    }

    private boolean isPhpString(PsiElement psiElement) {
        return PlatformPatterns.psiElement(PhpElementTypes.STRING).accepts(psiElement);
    }

    private boolean isXmlText(IElementType elementType) {
        return elementType == XmlTokenType.XML_DATA_CHARACTERS || elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;
    }

    private boolean isJsLiteral(IElementType elementType) {
        return elementType.toString().equals("JS:STRING_LITERAL") || "JS:LITERAL_EXPRESSION".equals(elementType.toString());
    }
}
