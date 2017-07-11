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
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
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
    protected String getDefinedAnnotationNames(BodyDeclaration bodyDeclaration) {
        return "ThriftField,ThriftConstructor";
    }

    @Override
    protected void processBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString) {
    }

    protected Expression getMergeDirtyAssignExpression(String name) {
        return new AssignExpr(new NameExpr("_clone." + name), new NameExpr(name), AssignExpr.Operator.assign);
    }

}
