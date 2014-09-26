package com.github.letsdrink.intellijplugin.index;

import com.github.letsdrink.intellijplugin.PsiUtils;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.FunctionReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class TranslationCallIndex extends ScalarIndexExtension<String> {
    public static final ID<String, Void> KEY = ID.create("com.github.letsdrink.intellijplugin.index.TranslationCallIndex");

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
                Map<String, Void> map = new HashMap<String, Void>();

                PsiFile psiFile = fileContent.getPsiFile();
                Collection<FunctionReference> calls = PsiTreeUtil.collectElementsOfType(psiFile, FunctionReference.class);
                for (FunctionReference call : calls) {
                    if (call.getName().equals("t")) {
                        String key = PsiUtils.getContent(call.getParameters()[0]);
                        if (key != null) {
                            map.put(key, null);
                        }
                    }
                }
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
        return new DefaultFileTypeSpecificInputFilter(PhpFileType.INSTANCE);
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
