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
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class CodeJavaMerger {

    protected static final String JAVA_EXT_NAME = ".java";
    protected static final int TRANSIENT_MODIFIER = Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL;

    public static AnnotationExpr getAnnotation(List<AnnotationExpr> annotations, String annotationName) {
        if (annotations != null) {
            for (AnnotationExpr annotation : annotations) {
                if (annotation.getName().toString().equals(annotationName)) {
                    return annotation;
                }
            }
        }

        return null;
    }

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

    protected abstract boolean isCouldMergeType(TypeDeclaration type);

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
        if (toCompilationUnit == null) {
            toCompilationUnit = fromCompilationUnit;
            toType = fromType;
            toType.setName(className);
        }

        if (toType == null) {
            for (TypeDeclaration type : toCompilationUnit.getTypes()) {
                if (type.getName().equals(className)) {
                    toType = type;
                    break;
                }
            }

            if (toType == null) {
                toType = fromType;
                toCompilationUnit.getTypes().add(toType);
            }
        }

        mergeFormTypeToType(className, fromCompilationUnit, toCompilationUnit, fromType, toType);
        for (TypeDeclaration from : fromCompilationUnit.getTypes()) {
            if (from.getName().equals(fromClassName)) {
                continue;
            }

            TypeDeclaration _to = null;
            for (TypeDeclaration to : toCompilationUnit.getTypes()) {
                if (to.getName().equals(from.getName())) {
                    _to = to;
                    break;
                }
            }

            if (!isCouldMergeType(from)) {
                if (_to != null) {
                    toCompilationUnit.getTypes().remove(_to);
                }

                toCompilationUnit.getTypes().add(fromType);
                continue;
            }

            if (_to == null) {
                _to = fromType;
                toCompilationUnit.getTypes().add(fromType);
            }

            mergeFormTypeToType(className, fromCompilationUnit, toCompilationUnit, from, _to);
        }

        HelperFile.write(toFile, toCompilationUnit.toString());
    }

    protected void mergeFormTypeToType(String className, CompilationUnit fromCompilationUnit, CompilationUnit toCompilationUnit,
                                       TypeDeclaration fromType, TypeDeclaration toType) {
        Map<String, FieldDeclaration> fromFieldMap = new LinkedHashMap<String, FieldDeclaration>();
        Map<String, BodyDeclaration> declarationMap = new LinkedHashMap<String, BodyDeclaration>();
        int initializerIndex = 0;
        for (BodyDeclaration bodyDeclaration : fromType.getMembers()) {
            if (bodyDeclaration instanceof FieldDeclaration) {
                FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
                String name = fieldDeclaration.getVariables().get(0).getId().toString();
                if ((fieldDeclaration.getModifiers() & TRANSIENT_MODIFIER) == 0) {
                    refactorType(fieldDeclaration.getType());
                    fromFieldMap.put(name, fieldDeclaration);

                } else {
                    declarationMap.put(name, fieldDeclaration);
                }

            } else if (bodyDeclaration instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                declarationMap.put(methodDeclaration.getDeclarationAsString(), methodDeclaration);

            } else if (className != null && bodyDeclaration instanceof ConstructorDeclaration) {
                ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) bodyDeclaration;
                constructorDeclaration.setName(className);
                declarationMap.put(constructorDeclaration.getDeclarationAsString(), constructorDeclaration);

            } else if (bodyDeclaration instanceof TypeDeclaration) {
                TypeDeclaration bodyDeclarationType = (TypeDeclaration) bodyDeclaration;
                TypeDeclaration toBodyDeclarationType = null;
                for (BodyDeclaration toBody : toType.getMembers()) {
                    if (toBody instanceof TypeDeclaration) {
                        TypeDeclaration typeDeclaration = (TypeDeclaration) toBody;
                        if (typeDeclaration.getName().equals(bodyDeclarationType.getName())) {
                            toBodyDeclarationType = typeDeclaration;
                            break;
                        }
                    }
                }

                if (!isCouldMergeType(bodyDeclarationType)) {
                    if (toBodyDeclarationType == null) {
                        toType.getMembers().add(bodyDeclarationType);

                    } else {
                        toType.getMembers().set(toType.getMembers().indexOf(toBodyDeclarationType), bodyDeclarationType);
                    }

                } else {
                    if (toBodyDeclarationType == null) {
                        toBodyDeclarationType = bodyDeclarationType;
                        toType.getMembers().add(toBodyDeclarationType);
                    }

                    mergeFormTypeToType(null, fromCompilationUnit, toCompilationUnit, bodyDeclarationType, toBodyDeclarationType);
                }

            } else if (bodyDeclaration instanceof InitializerDeclaration) {
                declarationMap.put("@Initializer" + (initializerIndex++), bodyDeclaration);
            }
        }

        mergeCompilationUnit(className, fromCompilationUnit, toCompilationUnit, fromType, toType, fromFieldMap, declarationMap);
    }

    public abstract void mergeCompilationUnit(String className, CompilationUnit fromCompilationUnit, CompilationUnit toCompilationUnit,
                                              TypeDeclaration fromType, TypeDeclaration toType, Map<String, FieldDeclaration> fromFieldMap, Map<String, BodyDeclaration> declarationMap);
}
