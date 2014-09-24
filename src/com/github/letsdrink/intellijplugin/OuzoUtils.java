package com.github.letsdrink.intellijplugin;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class OuzoUtils {
    public static boolean isInViewDir(PsiFile file) {
        return VfsUtil.getRelativePath(file.getVirtualFile(), Settings.getInstance(file.getProject()).getOuzoProjectRoot(), '/').startsWith("application/view");
    }

    public static PsiFile getViewPsiFile(Project project, String viewName) {
        VirtualFile virtualFile = Settings.getInstance(project).getOuzoProjectRoot().findFileByRelativePath("/application/view/" + viewName + ".phtml");
        if (virtualFile == null) {
            return null;
        }
        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    public static List<PsiFile> getViewPsiFiles(PsiElement psiElement) {
        if (psiElement.getLanguage() != PhpLanguage.INSTANCE) {
            return Collections.emptyList();
        }
        Method method = PsiTreeUtil.getParentOfType(psiElement, Method.class);
        if (method == null || !method.getContainingClass().getName().endsWith("Controller")) {
            return Collections.emptyList();
        }
        PhpClass controllerClass = method.getContainingClass();
        String resource = controllerClass.getName().replaceAll("Controller", "");
        return asList(OuzoUtils.getViewPsiFile(psiElement.getProject(), resource + "/" + method.getName()));
    }
}
