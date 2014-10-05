package com.github.letsdrink.intellijplugin.translation.index;


import com.github.letsdrink.intellijplugin.translation.TranslationParser;
import com.github.letsdrink.intellijplugin.translation.TranslationUtils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class TranslationKeyIndex  extends ScalarIndexExtension<String> {
    public static final ID<String, Void> KEY = ID.create("com.github.letsdrink.intellijplugin.index.TranslationKeyIndex");

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return new DataIndexer<String, Void, FileContent>() {
            @NotNull
            @Override
            public Map<String, Void> map(@NotNull FileContent fileContent) {
                final Map<String, Void> map = new HashMap<String, Void>();
                PsiFile psiFile = fileContent.getPsiFile();

                TranslationParser translationParser = new TranslationParser();
                translationParser.parse(psiFile, new TranslationParser.Handler() {
                    @Override
                    public void handle(String key, String text, ArrayHashElement element) {
                        map.put(key, null);
                    }
                });
                return map;
            }
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return EnumeratorStringDescriptor.INSTANCE;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
            @Override
            public boolean acceptInput(@NotNull VirtualFile virtualFile) {
                return virtualFile.getFileType() == PhpFileType.INSTANCE && TranslationUtils.isTranslationFile(virtualFile);
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
