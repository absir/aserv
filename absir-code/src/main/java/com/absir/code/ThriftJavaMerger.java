/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月5日 下午8:02:19
 */
package com.absir.code;

import com.absir.core.kernel.KernelString;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.ArrayList;
import java.util.List;

public class ThriftJavaMerger extends BeanJavaMerger {

    @Override
    protected boolean isBeanType(String className, TypeDeclaration toType) {
        return className != null && !className.startsWith("R") && !className.endsWith("Service");
    }

    @Override
    protected void setBeanInterface(List<ClassOrInterfaceType> implementsList, CompilationUnit toCompilationUnit) {
        if (!hasClass(implementsList, "IThrift")) {
            implementsList.add(new ClassOrInterfaceType("IThrift"));
            toCompilationUnit.getImports()
                    .add(new ImportDeclaration(new NameExpr("com.absir.data.value.IThrift"), false, false));
            toCompilationUnit.getImports()
                    .add(new ImportDeclaration(new NameExpr("com.fasterxml.jackson.annotation.JsonIgnore"), false, false));
        }
    }

    @Override
    protected boolean isBeanField(FieldDeclaration fieldDeclaration, String name) {
        return !name.startsWith("__");
    }

    @Override
    protected String getDefinedAnnotationNames(BodyDeclaration bodyDeclaration) {
        return null;
    }

    @Override
    protected boolean isDefinedBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString) {
        return true;
    }

    @Override
    protected boolean isCloneableClassName(String className) {
        return className.startsWith("T");
    }

    @Override
    public String getToClassName(String className) {
        return className;
    }

    @Override
    protected boolean isCouldMergeType(TypeDeclaration type) {
        return false;
    }

    @Override
    protected int getModifierFieldDeclaration(String name, FieldDeclaration fieldDeclaration) {
//        if ("__isset_bitfield".equals(name)) {
//            return Modifier.PROTECTED | Modifier.TRANSIENT;
//        }

        return super.getModifierFieldDeclaration(name, fieldDeclaration);
    }

    @Override
    protected void processBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString) {
        if (bodyDeclaration instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
            declarationAsString = methodDeclaration.getName();
            if (declarationAsString.startsWith("isSet") && methodDeclaration.getParameters().isEmpty()) {
                List<AnnotationExpr> annotationExprs = methodDeclaration.getAnnotations();
                if (getAnnotation(annotationExprs, "JsonIgnore") == null) {
                    if (annotationExprs == null) {
                        annotationExprs = new ArrayList<AnnotationExpr>();
                    }

                    annotationExprs.add(new MarkerAnnotationExpr(new NameExpr("JsonIgnore")));
                }
            }
        }
    }

    @Override
    protected Expression getCloneAssignExpression(String name, Expression from) {
        List<Expression> args = new ArrayList<Expression>();
        args.add(from == null ? new NameExpr(name) : from);
        return new MethodCallExpr(new NameExpr("_clone"), "set" + KernelString.capitalize(name), args);
    }
}
