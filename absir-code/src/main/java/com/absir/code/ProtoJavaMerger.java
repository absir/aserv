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
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

public class ProtoJavaMerger extends CodeJavaMerger {

    protected static final String JP_EXT_NAME = "JProtoBufProtoClass";

    protected static final int JP_EXT_NAME_LEN = JP_EXT_NAME.length();

    protected static final String PB_PRE_NAME = "P";

    protected Type VOID_TYPE = new VoidType();

    @Override
    public String getToClassName(String className) {
        int length = className.length();
        if (length > JP_EXT_NAME_LEN && className.endsWith(JP_EXT_NAME)) {
            return PB_PRE_NAME + className.substring(0, length - JP_EXT_NAME_LEN);
        }

        return className;
    }

    protected boolean isProtoClassName(String className) {
        if (className.length() > PB_PRE_NAME.length() && className.startsWith(PB_PRE_NAME)) {
            char chr = className.charAt(PB_PRE_NAME.length());
            return chr >= 'A' && chr <= 'Z';
        }

        return false;
    }

    @Override
    public void mergeCompilationUnit(CompilationUnit fromCompilationUnit, CompilationUnit toCompilationUnit,
                                     TypeDeclaration fromType, TypeDeclaration toType, Map<String, FieldDeclaration> fromFieldMap) {
        boolean enumReadable = false;
        int dirtyM = 0;
        if (toType instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) toType;
            if (typeDeclaration.getImplements() == null || typeDeclaration.getImplements().isEmpty()) {
                List<ClassOrInterfaceType> implementsList = new ArrayList<ClassOrInterfaceType>();
                implementsList.add(new ClassOrInterfaceType("IProto"));
                toCompilationUnit.getImports()
                        .add(new ImportDeclaration(new NameExpr("com.absir.data.value.IProto"), false, false));
                typeDeclaration.setImplements(implementsList);
            }

            List<AnnotationExpr> annotationExprs = toType.getAnnotations();
            if (annotationExprs != null) {
                for (AnnotationExpr annotationExpr : annotationExprs) {
                    String name = annotationExpr.getName().toString();
                    if (name.equals("ADirty")) {
                        dirtyM = 1;

                    } else if (name.equals("ADirtyM")) {
                        dirtyM = 2;
                    }
                }
            }

            if (dirtyM > 0) {
                if (typeDeclaration.getExtends() == null || typeDeclaration.getExtends().isEmpty()) {
                    String extendName = dirtyM == 1 ? "DDiry" : "DDirtyM";
                    toCompilationUnit.getImports().add(
                            new ImportDeclaration(new NameExpr("com.absir.data.base." + extendName), false, false));
                    List<ClassOrInterfaceType> extendsList = new ArrayList<ClassOrInterfaceType>();
                    extendsList.add(new ClassOrInterfaceType(extendName));
                    typeDeclaration.setExtends(extendsList);
                }
            }

        } else if (fromType instanceof EnumDeclaration) {
            enumReadable = true;
        }

        Map<String, MethodDeclaration> toMethodMap = new HashMap<String, MethodDeclaration>();
        Map<String, FieldDeclaration> toFieldMap = new HashMap<String, FieldDeclaration>();
        if (!enumReadable) {
            for (BodyDeclaration body : toType.getMembers()) {
                if (body instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) body;
                    toMethodMap.put(methodDeclaration.getName(), methodDeclaration);

                } else if (body instanceof FieldDeclaration) {
                    FieldDeclaration toDeclaration = ((FieldDeclaration) body);
                    String name = toDeclaration.getVariables().get(0).toString();
                    FieldDeclaration fromDeclaration = fromFieldMap.get(name);
                    List<AnnotationExpr> toAnnotationExprs = new ArrayList<AnnotationExpr>();
                    if (toDeclaration.getAnnotations() != null) {
                        for (AnnotationExpr annotationExpr : toDeclaration.getAnnotations()) {
                            if (!annotationExpr.getName().getName().equals("Protobuf")) {
                                toAnnotationExprs.add(annotationExpr);
                            }
                        }
                    }

                    if (fromDeclaration != null) {
                        toFieldMap.put(name, toDeclaration);
                        toDeclaration.setModifiers(Modifier.PROTECTED);
                        if (fromDeclaration.getAnnotations() != null) {
                            toAnnotationExprs.addAll(fromDeclaration.getAnnotations());
                        }
                    }

                    toDeclaration.setAnnotations(toAnnotationExprs);
                }
            }
        }

        List<BodyDeclaration> addDeclarations = new ArrayList<BodyDeclaration>();
        List<BodyDeclaration> removeDeclarations = new ArrayList<BodyDeclaration>();
        if (enumReadable) {
            EnumDeclaration fromEnum = (EnumDeclaration) fromType;
            EnumDeclaration toEnum = (EnumDeclaration) toType;
            Map<String, EnumConstantDeclaration> nameMapEnum = new HashMap<String, EnumConstantDeclaration>();
            for (EnumConstantDeclaration declaration : fromEnum.getEntries()) {
                nameMapEnum.put(declaration.getName(), declaration);
            }

            List<EnumConstantDeclaration> toDeclarations = new ArrayList<EnumConstantDeclaration>();
            for (EnumConstantDeclaration declaration : toEnum.getEntries()) {
                EnumConstantDeclaration fromDeclaration = nameMapEnum.remove(declaration.getName());
                toDeclarations.add(fromDeclaration == null ? declaration : fromDeclaration);
            }

            for (EnumConstantDeclaration fromDeclaration : nameMapEnum.values()) {
                toDeclarations.add(fromDeclaration);
            }

            toEnum.setEntries(toDeclarations);

        } else {
            int index = 0;
            for (Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
                String name = entry.getKey();
                FieldDeclaration declaration = entry.getValue();
                if (!toFieldMap.containsKey(name)) {
                    declaration.setModifiers(Modifier.PROTECTED);
                    addDeclarations.add(declaration);
                }

                String cName = KernelString.capitalize(name);
                String getName = "get" + cName;
                if (!toMethodMap.containsKey(getName)) {
                    getName = "is" + cName;
                    if (!toMethodMap.containsKey(getName)) {
                        String type = declaration.getType().toString();
                        getName = ((type.equals("bool") || type.equals("Boolean")) ? "is" : "get") + cName;
                        addDeclarations.add(getGetter(name, getName, declaration));
                    }
                }

                String setName = "set" + cName;
                MethodDeclaration methodDeclaration = toMethodMap.get(setName);
                if (methodDeclaration == null) {
                    addDeclarations.add(getSetter(index, dirtyM, name, setName, declaration));

                } else {
                    updateSetter(index, dirtyM, getName, setName, methodDeclaration);
                }

                index++;
            }
        }

        if (!enumReadable) {
            if (!toMethodMap.containsKey("create")) {
                addDeclarations.add(getCreate(toType));
            }

            if (!toMethodMap.containsKey("clone")) {
                addDeclarations.add(getClone(toType));
            }

            MethodDeclaration methodDeclaration = toMethodMap.get("cloneDepth");
            if (methodDeclaration != null) {
                removeDeclarations.add(methodDeclaration);
            }

            addDeclarations.add(getCloneDepth(toType, fromFieldMap));

            if (!toMethodMap.containsKey("cloneMore")) {
                addDeclarations.add(getCloneMore(toType));
            }

            methodDeclaration = toMethodMap.get("mergeDirty");

            if (methodDeclaration != null) {
                removeDeclarations.add(methodDeclaration);
            }

            if (dirtyM > 0) {
                addDeclarations.add(getMergeDirty(toType, fromFieldMap));
            }
        }

        if (!addDeclarations.isEmpty() || !removeDeclarations.isEmpty()) {
            List<BodyDeclaration> bodyDeclarations = new ArrayList<BodyDeclaration>(toType.getMembers());
            bodyDeclarations.removeAll(removeDeclarations);
            bodyDeclarations.addAll(addDeclarations);

            Collections.sort(bodyDeclarations, new Comparator<BodyDeclaration>() {

                @Override
                public int compare(BodyDeclaration o1, BodyDeclaration o2) {
                    if (o1 instanceof FieldDeclaration) {
                        return o2 instanceof FieldDeclaration ? 0 : -1;
                    }

                    return o2 instanceof FieldDeclaration ? 1 : 0;
                }

            });

            toType.setMembers(bodyDeclarations);
        }
    }

    protected MethodDeclaration getGetter(String name, String getName, FieldDeclaration declaration) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName(getName);
        method.setType(declaration.getType());
        BlockStmt blockStmt = new BlockStmt();
        List<Statement> stmts = new ArrayList<Statement>();
        stmts.add(new ReturnStmt(new NameExpr(name)));
        blockStmt.setStmts(stmts);
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected MethodDeclaration getSetter(int index, int dirtyM, String name, String setName,
                                          FieldDeclaration declaration) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName(setName);
        method.setType(VOID_TYPE);
        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter parameter = new Parameter(declaration.getType(), new VariableDeclaratorId(name));
        parameters.add(parameter);
        method.setParameters(parameters);
        BlockStmt blockStmt = new BlockStmt();
        List<Statement> stmts = new ArrayList<Statement>();
        ExpressionStmt stmt = new ExpressionStmt(
                new AssignExpr(new NameExpr("this." + name), new NameExpr(name), Operator.assign));
        stmts.add(stmt);
        blockStmt.setStmts(stmts);

        if (dirtyM > 0) {
            List<Expression> args = new ArrayList<Expression>();
            args.add(new IntegerLiteralExpr(String.valueOf(index)));
            stmt = new ExpressionStmt(new MethodCallExpr(null, "setDirtyI", args));
            stmts.add(stmt);
        }

        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected void updateSetter(int index, int dirtyM, String name, String setName, MethodDeclaration declaration) {
        BlockStmt blockStmt = declaration.getBody();
        if (blockStmt != null) {
            List<Statement> statements = blockStmt.getStmts();
            if (statements != null) {
                Statement seStatement = null;
                for (Statement statement : statements) {
                    if (statement.toString().startsWith("setDirtyI")) {
                        seStatement = statement;
                        break;
                    }
                }

                if (seStatement == null) {
                    if (dirtyM > 0) {
                        List<Expression> args = new ArrayList<Expression>();
                        args.add(new IntegerLiteralExpr(String.valueOf(index)));
                        Statement stmt = new ExpressionStmt(new MethodCallExpr(null, "setDirtyI", args));
                        statements.add(stmt);
                    }

                } else {
                    if (dirtyM <= 0) {
                        statements.remove(seStatement);
                    }
                }
            }
        }
    }

    protected MethodDeclaration getCreate(TypeDeclaration toType) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("create");
        method.setType(new ReferenceType(new ClassOrInterfaceType(toType.getName())));
        BlockStmt blockStmt = new BlockStmt();
        List<Statement> stmts = new ArrayList<Statement>();
        stmts.add(new ReturnStmt(new NameExpr("new " + toType.getName() + "()")));
        blockStmt.setStmts(stmts);
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected MethodDeclaration getClone(TypeDeclaration toType) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("clone");
        method.setType(new ReferenceType(new ClassOrInterfaceType(toType.getName())));
        BlockStmt blockStmt = new BlockStmt();
        List<Statement> stmts = new ArrayList<Statement>();
        List<Expression> args = new ArrayList<Expression>();
        args.add(new IntegerLiteralExpr("0"));
        stmts.add(new ReturnStmt(new MethodCallExpr(null, "cloneDepth", args)));
        blockStmt.setStmts(stmts);
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected MethodDeclaration getCloneDepth(TypeDeclaration toType, Map<String, FieldDeclaration> fromFieldMap) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("cloneDepth");
        method.setType(new ReferenceType(new ClassOrInterfaceType(toType.getName())));
        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter parameter = new Parameter(new ClassOrInterfaceType("int"), new VariableDeclaratorId("_depth"));
        parameters.add(parameter);
        method.setParameters(parameters);
        BlockStmt blockStmt = new BlockStmt();

        // clone = create();
        List<Statement> stmts = new ArrayList<Statement>();
        ExpressionStmt stmt = new ExpressionStmt(new AssignExpr(new NameExpr(toType.getName() + " _clone"),
                new MethodCallExpr(null, "create"), Operator.assign));
        stmts.add(stmt);

        boolean nextDepth = false;

        // assign field
        List<Expression> args = new ArrayList<Expression>();
        args.add(new NameExpr("_nextDepth"));
        for (Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
            String name = entry.getKey();
            FieldDeclaration fieldDeclaration = entry.getValue();
            if (isProtoClassName(fieldDeclaration.getType().toString())) {
                if (!nextDepth) {
                    nextDepth = true;
                    // int nextDepth = depth;
                    stmt = new ExpressionStmt(new AssignExpr(new NameExpr("int _nextDepth"), new NameExpr("_depth - 1"),
                            Operator.assign));
                    stmts.add(stmt);
                }

                stmts.add(new ExpressionStmt(new AssignExpr(new NameExpr("_clone." + name),
                        new MethodCallExpr(new NameExpr("_nextDepth < 0 ? " + name + " : " + name), "cloneDepth", args),
                        Operator.assign)));
            } else {
                stmts.add(new ExpressionStmt(
                        new AssignExpr(new NameExpr("_clone." + name), new NameExpr(name), Operator.assign)));
            }
        }

        // cloneMore(clone);
        args = new ArrayList<Expression>();
        args.add(new NameExpr("_clone"));
        args.add(new NameExpr("_depth"));
        stmt = new ExpressionStmt(new MethodCallExpr(null, "cloneMore", args));
        stmts.add(stmt);

        stmts.add(new ReturnStmt(new NameExpr("_clone")));
        blockStmt.setStmts(stmts);
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected MethodDeclaration getCloneMore(TypeDeclaration toType) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("cloneMore");
        method.setType(VOID_TYPE);
        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter parameter = new Parameter(new ClassOrInterfaceType(toType.getName()),
                new VariableDeclaratorId("_clone"));
        parameters.add(parameter);
        parameters.add(new Parameter(new ClassOrInterfaceType("int"), new VariableDeclaratorId("_depth")));
        method.setParameters(parameters);
        BlockStmt blockStmt = new BlockStmt();
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

    protected MethodDeclaration getSetDirtyI(TypeDeclaration toType) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("setDirtyI");
        method.setType(VOID_TYPE);
        List<Parameter> parameters = new ArrayList<Parameter>();
        parameters.add(new Parameter(new ClassOrInterfaceType("int"), new VariableDeclaratorId("index")));
        method.setParameters(parameters);
        BlockStmt blockStmt = new BlockStmt();
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        return method;
    }

    protected MethodDeclaration getMergeDirty(TypeDeclaration toType, Map<String, FieldDeclaration> fromFieldMap) {
        MethodDeclaration method = new MethodDeclaration();
        method.setName("mergeDirty");
        method.setType(VOID_TYPE);
        List<Parameter> parameters = new ArrayList<Parameter>();
        Parameter parameter = new Parameter(new ClassOrInterfaceType(toType.getName()),
                new VariableDeclaratorId("_clone"));
        parameters.add(parameter);
        method.setParameters(parameters);
        int size = fromFieldMap.size();
        BlockStmt blockStmt = new BlockStmt();
        List<Statement> stmts = new ArrayList<Statement>();
        blockStmt.setStmts(stmts);
        List<Expression> initExpressions = new ArrayList<Expression>();
        initExpressions.add(new NameExpr("int i = 0"));
        List<Expression> updateExpressions = new ArrayList<Expression>();
        updateExpressions.add(new NameExpr("i++"));
        BlockStmt bodyStmt = new BlockStmt();

        int index = 0;
        List<Statement> bodyStms = new ArrayList<Statement>();
        blockStmt.setStmts(bodyStms);
        for (Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
            String name = entry.getKey();
            // FieldDeclaration declaration = entry.getValue();
            BlockStmt thenStmt = new BlockStmt();
            List<Statement> thenStmts = new ArrayList<Statement>();
            thenStmt.setStmts(thenStmts);
            thenStmts.add(new ExpressionStmt(
                    new AssignExpr(new NameExpr("_clone." + name), new NameExpr(name), Operator.assign)));
            // if(isDirty(0)) { _clone.a = a }
            Statement ifStms = new IfStmt(new NameExpr("isDirtyI(" + index + ")"), thenStmt, null);
            bodyStms.add(ifStms);
            index++;
        }

        // for(int i = 0; i < size; i++)
        stmts.add(new ForStmt(initExpressions, new NameExpr("i < " + size), updateExpressions, bodyStmt));
        method.setBody(blockStmt);
        method.setModifiers(Modifier.PUBLIC);
        return method;
    }

}
