package com.github.letsdrink.intellijplugin;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.php.lang.PhpLanguage;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.MethodReference;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OuzoUtils {
    public static boolean isInViewDir(PsiFile file) {
        VirtualFile ouzoProjectRoot = Settings.getInstance(file.getProject()).getOuzoProjectRoot();
        String relativePath = VfsUtil.getRelativePath(file.getVirtualFile(), ouzoProjectRoot, '/');
        return relativePath != null && relativePath.startsWith("application/view");
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
        Method containingMethod = PsiTreeUtil.getParentOfType(psiElement, Method.class);
        PhpClass controllerClass = containingMethod.getContainingClass();

        if (containingMethod == null || !controllerClass.getName().endsWith("Controller")) {
            return Collections.emptyList();
        }

        Collection<MethodReference> methodCalls = PsiTreeUtil.collectElementsOfType(containingMethod, MethodReference.class);
        Project project = psiElement.getProject();
        Method renderMethod = OuzoUtils.getOuzoViewRenderMethod(project);

        FluentIterable<String> viewNames = FluentIterable.from(methodCalls)
                .filter(PsiFunctions.isCallTo(renderMethod))
                .transform(PsiFunctions.extractFirstArgumentStringContent());

        String resource = controllerClass.getName().replaceAll("Controller", "");

        return FluentIterable.from(Iterables.concat(viewNames, Arrays.asList(resource + "/" + containingMethod.getName())))
                .transform(getViewPsiFileFunction(project))
                .filter(Predicates.notNull())
                .toList();
    }

    private static Function<String, PsiFile> getViewPsiFileFunction(final Project project) {
        return new Function<String, PsiFile>() {
            @Nullable
            @Override
            public PsiFile apply(@Nullable String viewName) {
                return getViewPsiFile(project, viewName);
            }
        };
    }

    public static Method getOuzoViewRenderMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\View", "render");
    }

    public static Method getRouteResourceMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\Routing\\Route", "resource");
    }

    public static Method getRouteGetMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\Routing\\Route", "get");
    }

    public static Method getRoutePostMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\Routing\\Route", "post");
    }

    public static Method getRouteDeleteMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\Routing\\Route", "delete");
    }

    public static Method getRoutePutMethod(Project project) {
        return PhpIndexUtils.getClassMethod(project, "Ouzo\\Routing\\Route", "put");
    }
}