package com.github.letsdrink.intellijplugin;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Collection;

public class PhpIndexUtils {
    public static Optional<Method> getClassMethod(PhpClass phpClass, final String methodName) {
        return Iterables.tryFind(phpClass.getMethods(), new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                return method.getName().equals(methodName);
            }
        });
    }

    public static Method getClassMethod(Project project, String phpClassName, String methodName) {
        PhpIndex phpIndex = PhpIndex.getInstance(project);
        Collection<PhpClass> phpClasses = phpIndex.getClassesByFQN(phpClassName);

        for (PhpClass phpClass : phpClasses) {
            Optional<Method> method = getClassMethod(phpClass, methodName);
            if (method.isPresent()) {
                return method.get();
            }
        }
        return null;
    }
}
