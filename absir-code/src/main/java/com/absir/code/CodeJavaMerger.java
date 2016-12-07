/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月5日 上午10:27:08
 */
package com.absir.code;

import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class CodeJavaMerger {

    protected static final String JAVA_EXT_NAME = ".java";

    public String getJavaExtName() {
        return JAVA_EXT_NAME;
    }

    public abstract String getToClassName(String className);

    public void mergeBaseDir(File fromDirFile, File toDirFile) throws Exception {
        Map<String, File> packageClassNameMapFile = new HashMap<String, File>();
        readFromDir("", fromDirFile, packageClassNameMapFile);
        if (toDirFile.exists()) {
            writeToDir("", toDirFile, packageClassNameMapFile);
        }

        for (Entry<String, File> entry : packageClassNameMapFile.entrySet()) {
            String packageClassName = entry.getKey();
            String className = HelperFileName.getBaseName(packageClassName);
            File fromFile = entry.getValue();
            File toFile = new File(toDirFile, packageClassName + getJavaExtName());
            String fromName = fromFile.getName();
            mergeCompilationUnitFile(fromName.substring(0, fromName.length() - getJavaExtName().length()), className,
                    JavaParser.parse(fromFile), null, toFile);
        }
    }

    protected void readFromDir(String packageName, File fromDirFile, Map<String, File> packageClassNameMapFile)
            throws Exception {
        for (File fromFile : fromDirFile.listFiles()) {
            String fileName = fromFile.getName();
            if (fromFile.isDirectory()) {
                readFromDir(packageName + "/" + fileName, fromFile, packageClassNameMapFile);

            } else {
                if (fileName.endsWith(getJavaExtName())) {
                    String className = fileName.substring(0, fileName.length() - getJavaExtName().length());
                    String toClassName = getToClassName(className);
                    if (toClassName != null) {
                        packageClassNameMapFile.put(packageName + "/" + toClassName, fromFile);
                    }
                }
            }
        }
    }

    protected void writeToDir(String packageName, File toDirFile, Map<String, File> packageClassNameMapFile)
            throws Exception {
        for (File toFile : toDirFile.listFiles()) {
            String fileName = toFile.getName();
            if (toFile.isDirectory()) {
                writeToDir(packageName + "/" + fileName, toFile, packageClassNameMapFile);

            } else {
                if (fileName.endsWith(getJavaExtName())) {
                    String className = fileName.substring(0, fileName.length() - getJavaExtName().length());
                    File fromFile = packageClassNameMapFile.remove(packageName + "/" + className);
                    if (fromFile != null) {
                        String fromName = fromFile.getName();
                        mergeCompilationUnitFile(fromName.substring(0, fromName.length() - getJavaExtName().length()),
                                className, JavaParser.parse(fromFile), JavaParser.parse(toFile), toFile);
                    }
                }
            }
        }
    }

    protected void refactorType(Type type) {
        while (type != null) {
            if (type instanceof ReferenceType) {
                type = ((ReferenceType) type).getType();
                continue;

            } else if (type instanceof ClassOrInterfaceType) {
                ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType) type;
                String className = classOrInterfaceType.getName();
                className = getToClassName(className);
                if (className != null) {
                    classOrInterfaceType.setName(className);
                }

                List<Type> typeArgs = classOrInterfaceType.getTypeArgs();
                if (typeArgs != null && !typeArgs.isEmpty()) {
                    for (Type typeArg : typeArgs) {
                        refactorType(typeArg);
                    }
                }
            }

            break;
        }
    }

    protected void mergeCompilationUnitFile(String fromClassName, String className, CompilationUnit fromCompilationUnit,
                                            CompilationUnit toCompilationUnit, File toFile) throws Exception {
        TypeDeclaration fromType = null;
        for (TypeDeclaration type : fromCompilationUnit.getTypes()) {
            if (type.getName().equals(fromClassName)) {
                fromType = type;
            }
        }

        if (fromType == null) {
            throw new Exception("not find fromType : " + fromClassName);
        }

        TypeDeclaration toType = null;
        if (toCompilationUnit == null || toCompilationUnit.getTypes() == null) {
            toCompilationUnit = fromCompilationUnit;
            toType = fromType;
            toType.setName(className);

        } else {
            for (TypeDeclaration type : toCompilationUnit.getTypes()) {
                if (type.getName().equals(className)) {
                    toType = type;
                }
            }

            if (toType == null) {
                toType = fromType;
                toCompilationUnit.getTypes().add(toType);
            }
        }

        Map<String, FieldDeclaration> fromFieldMap = new LinkedHashMap<String, FieldDeclaration>();
        for (BodyDeclaration bodyDeclaration : fromType.getMembers()) {
            if (bodyDeclaration instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
                refactorType(fieldDeclaration.getType());
                fromFieldMap.put(fieldDeclaration.getVariables().get(0).getId().toString(), fieldDeclaration);

            } else if (bodyDeclaration instanceof ConstructorDeclaration) {
                ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) bodyDeclaration;
                constructorDeclaration.setName(className);
            }
        }

        mergeCompilationUnit(fromCompilationUnit, toCompilationUnit, fromType, toType, fromFieldMap);
        HelperFile.write(toFile, toCompilationUnit.toString());
    }

    public abstract void mergeCompilationUnit(CompilationUnit fromCompilationUnit, CompilationUnit toCompilationUnit,
                                              TypeDeclaration fromType, TypeDeclaration toType, Map<String, FieldDeclaration> fromFieldMap);
}
