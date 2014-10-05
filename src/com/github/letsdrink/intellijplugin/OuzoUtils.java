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
    public static final String OUZO_VIEW_RENDER_FQN = "\\Ouzo\\View.render";
    public static final String OUZO_ROUTE_RESOURCE_FQN = "\\Ouzo\\Routing\\Route.resource";
    public static final String OUZO_ROUTE_GET_FQN = "\\Ouzo\\Routing\\Route.get";
    public static final String OUZO_ROUTE_POST_FQN = "\\Ouzo\\Routing\\Route.post";
    public static final String OUZO_ROUTE_DELETE_FQN = "\\Ouzo\\Routing\\Route.delete";
    public static final String OUZO_ROUTE_PUT_FQN = "\\Ouzo\\Routing\\Route.put";

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
        if (containingMethod == null) {
            return Collections.emptyList();
        }

        PhpClass controllerClass = containingMethod.getContainingClass();

        if (containingMethod == null || !controllerClass.getName().endsWith("Controller")) {
            return Collections.emptyList();
        }

        Collection<MethodReference> methodCalls = PsiTreeUtil.collectElementsOfType(containingMethod, MethodReference.class);
        Project project = psiElement.getProject();

        FluentIterable<String> viewNames = FluentIterable.from(methodCalls)
                .filter(PsiFunctions.isCallTo(OuzoUtils.OUZO_VIEW_RENDER_FQN))
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

    public static boolean isExpectedFile(PsiElement psiElement, String filename) {
        return PsiUtils.getContainingFilename(psiElement).equals(filename);
    }
}