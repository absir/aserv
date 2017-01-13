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
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.List;

public class ProtoJavaMerger extends BeanJavaMerger {

    protected static final String JP_EXT_NAME = "JProtoBufProtoClass";

    protected static final int JP_EXT_NAME_LEN = JP_EXT_NAME.length();

    protected static final String PB_PRE_NAME = "P";

    @Override
    protected boolean isBeanType(String className, TypeDeclaration toType) {
        return className != null;
    }

    @Override
    protected void setBeanInterface(List<ClassOrInterfaceType> implementsList, CompilationUnit toCompilationUnit) {
        implementsList.add(new ClassOrInterfaceType("IProto"));
        toCompilationUnit.getImports()
                .add(new ImportDeclaration(new NameExpr("com.absir.data.value.IProto"), false, false));
    }

    @Override
    protected boolean isBeanField(FieldDeclaration fieldDeclaration, String name) {
        return true;
    }

    @Override
    protected String getDefinedAnnotationNames(BodyDeclaration bodyDeclaration) {
        return "Protobuf";
    }

    @Override
    protected boolean isDefinedBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString) {
        return false;
    }

    @Override
    protected boolean isCloneableClassName(String className) {
        if (className.length() > PB_PRE_NAME.length() && className.startsWith(PB_PRE_NAME)) {
            char chr = className.charAt(PB_PRE_NAME.length());
            if (chr >= 'A' && chr <= 'Z') {
                return !className.endsWith("Type");
            }
        }

        return false;
    }

    @Override
    public String getToClassName(String className) {
        int length = className.length();
        if (length > JP_EXT_NAME_LEN && className.endsWith(JP_EXT_NAME)) {
            return PB_PRE_NAME + className.substring(0, length - JP_EXT_NAME_LEN);
        }

        return className;
    }

    @Override
    protected boolean isCouldMergeType(TypeDeclaration type) {
        return false;
    }

}
