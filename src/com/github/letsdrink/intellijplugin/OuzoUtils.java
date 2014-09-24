package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

public class OuzoUtils {
    public static boolean isInViewDir(PsiFile file) {
        return VfsUtil.getRelativePath(file.getVirtualFile(), Settings.getInstance(file.getProject()).getOuzoProjectRoot(), '/').startsWith("application/view");
    }

    public static PsiFile getViewPsiFile(PsiFile context, String viewName) {
        VirtualFile virtualFile = Settings.getInstance(context.getProject()).getOuzoProjectRoot().findFileByRelativePath("/application/view/" + viewName + ".phtml");
        if (virtualFile == null) {
            return null;
        }
        return PsiManager.getInstance(context.getProject()).findFile(virtualFile);
    }
}
