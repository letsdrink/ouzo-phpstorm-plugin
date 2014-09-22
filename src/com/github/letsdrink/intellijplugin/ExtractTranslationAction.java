package com.github.letsdrink.intellijplugin;

import com.google.common.base.Predicates;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractTranslationAction extends AnAction {
    public ExtractTranslationAction() {
        super("ExtractTranslation");
    }

    public void actionPerformed(AnActionEvent event) {

        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
        if (psiFile == null || editor == null)
            return;

        int offset = editor.getCaretModel().getOffset();
        PsiElement psiElement = psiFile.findElementAt(offset);
        if (psiElement == null || psiElement.getNode() == null)
            return;

        if (!isTypeSupported(psiElement)) {
            return;
        }
        if (isTypeSupported(psiElement.getParent())) {
            psiElement = psiElement.getParent();
        }
        final PsiElement finalPsiElement = psiElement;

        final String text = getTextToTranslate(finalPsiElement);

        final Project project = finalPsiElement.getProject();

        List<PsiFile> translationFiles = TranslationUtils.getTranslationFiles(project);
        final List<TranslationParser> translationParsers = Lists.transform(translationFiles, TranslationParser.createParser());

        String key = FluentIterable.from(translationParsers).transform(TranslationParser.getKeyFunction(text)).filter(Predicates.notNull()).first().or("");

        final Map<String, String> translations = createTranslationsMap(translationParsers, key, text);

        TranslationDialog dialog = new TranslationDialog(key, translations, new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key) {
                replaceTextWithTranslation(key, finalPsiElement, editor);
                for (Map.Entry<String, String> entry : translations.entrySet()) {
                    TranslationParser parser = Iterables.find(translationParsers, TranslationParser.languageEqualsFunction(entry.getKey()));
                    parser.addTranslation(key, entry.getValue());
                }
            }
        });
        dialog.pack();
        dialog.setTitle("Input translation key");
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private Map<String, String> createTranslationsMap(List<TranslationParser> translationParsers, String key, String text) {
        final Map<String, String> translations = new HashMap<String, String>();
        for (TranslationParser translationParser : translationParsers) {
            String translation = translationParser.getTranslation(key);
            translation = translation == null ? text : translation;
            translations.put(translationParser.getLanguage(), translation);
        }
        return translations;
    }

    private String getTextToTranslate(PsiElement psiElement) {
        if (psiElement.getParent() instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) psiElement.getParent()).getContents().trim();
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

                if (!isParentPhpString(finalPsiElement)) {
                    insertString = "<?=" + insertString + "?>";
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
        return isXmlText(elementType) || elementType.toString().equals("XML_TEXT") || isJsLiteral(elementType) || isParentPhpString(psiElement);

    }

    private boolean isParentPhpString(PsiElement psiElement) {
        return PlatformPatterns.psiElement(PhpElementTypes.STRING).accepts(psiElement.getParent());
    }

    private boolean isXmlText(IElementType elementType) {
        return elementType == XmlTokenType.XML_DATA_CHARACTERS || elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;
    }

    private boolean isJsLiteral(IElementType elementType) {
        return elementType.toString().equals("JS:STRING_LITERAL");
    }
}
