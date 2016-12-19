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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.List;

public class ThriftCodecJavaMerger extends ThriftJavaMerger {

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
    protected String getFieldAnnotationName() {
        return "ThriftField";
    }

}
