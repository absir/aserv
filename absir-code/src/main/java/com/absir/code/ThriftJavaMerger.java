/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月5日 下午8:02:19
 */
package com.absir.code;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.List;

public class ThriftJavaMerger extends BeanJavaMerger {

    @Override
    protected boolean isBeanType(String className, TypeDeclaration toType) {
        return className != null && !className.endsWith("Service");
    }

    @Override
    protected void setBeanInterface(List<ClassOrInterfaceType> implementsList, CompilationUnit toCompilationUnit) {
        implementsList.add(new ClassOrInterfaceType("IThrift"));
        toCompilationUnit.getImports()
                .add(new ImportDeclaration(new NameExpr("com.absir.data.value.IThrift"), false, false));
    }

    @Override
    protected boolean isAnnotationConstructorDeclaration(ConstructorDeclaration constructorDeclaration) {
        return getAnnotation(constructorDeclaration.getAnnotations(), "ThriftConstructor") != null;
    }

    @Override
    protected boolean isAnnotationMethodDeclaration(MethodDeclaration methodDeclaration) {
        String name = methodDeclaration.getName();
        return name.equals("toString") || name.equals("equals") || name.equals("hashCode");
    }

    @Override
    protected String getFieldAnnotationName() {
        return "ThriftField";
    }

    @Override
    protected boolean isCloneableClassName(String className) {
        return false;
    }

    @Override
    public String getToClassName(String className) {
        return className;
    }

    @Override
    protected boolean isNeedMergeType(TypeDeclaration type) {
        return false;
    }
}
