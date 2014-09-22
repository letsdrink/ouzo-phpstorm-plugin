package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.PsiFile;

public class OuzoUtils {
    public static boolean isInViewDir(PsiFile file) {
        return VfsUtil.getRelativePath(file.getVirtualFile(), Settings.getInstance(file.getProject()).getOuzoProjectRoot(), '/').startsWith("application/view");
    }
}
