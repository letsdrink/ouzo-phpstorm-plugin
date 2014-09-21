package com.github.letsdrink.intellijplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression;

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

        String text = getText(finalPsiElement);

        TranslationParser translationParser = new TranslationParser(finalPsiElement.getProject());
        String key = translationParser.getKey(text);

        TranslationDialog dialog = new TranslationDialog(key, text, new TranslationDialog.OkCallback() {
            @Override
            public void onClick(final String key, final String plText, final String enText) {
                replaceTextWithTranslation(key, finalPsiElement, editor);
                addTranslation(key, enText, "en.php", finalPsiElement.getProject());
                addTranslation(key, plText, "pl.php", finalPsiElement.getProject());
            }
        });
        dialog.pack();
        dialog.setTitle("Input translation key");
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private String getText(PsiElement psiElement) {
        if (psiElement.getParent() instanceof StringLiteralExpression) {
            return ((StringLiteralExpression) psiElement.getParent()).getContents();
        }
        return psiElement.getText();
    }

    private void addTranslation(String key, String enText, String langFile, final Project project) {
        PsiFile enFile = getPsiFile(langFile, project);

        final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        final Document document = manager.getDocument(enFile);

        final String finalInsertString = "\n'" + key + "' => '" + enText + "',";
        new WriteCommandAction(project) {
            @Override
            protected void run(Result result) throws Throwable {
                document.insertString(document.getTextLength(), finalInsertString);
                manager.doPostponedOperationsAndUnblockDocument(document);
                manager.commitDocument(document);
            }

            @Override
            public String getGroupID() {
                return "Translation Extraction";
            }
        }.execute();
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

    private PsiFile getPsiFile(String langFile, Project project) {
        PsiFile[] files = FilenameIndex.getFilesByName(project, langFile, GlobalSearchScope.allScope(project));
        for (PsiFile file : files) {
            if (!file.getVirtualFile().getCanonicalPath().contains("vendor")) {
                return file;
            }
        }
        return null;
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
