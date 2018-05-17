package com.absir.code;

import com.absir.core.kernel.KernelString;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by absir on 2016/12/12.
 */
public abstract class BeanJavaMerger extends CodeJavaMerger {

    protected Type VOID_TYPE = new VoidType();

    protected abstract boolean isBeanType(String className, TypeDeclaration toType);

    protected abstract void setBeanInterface(List<ClassOrInterfaceType> implementsList, CompilationUnit toCompilationUnit);

    protected abstract boolean isBeanField(FieldDeclaration fieldDeclaration, String name);

    protected abstract String getDefinedAnnotationNames(BodyDeclaration bodyDeclaration);

    protected abstract boolean isDefinedBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString);

    protected abstract boolean isCloneableClassName(String className);

    protected List<AnnotationExpr> mergeAnnotationExpr(List<AnnotationExpr> fromAnnotations, List<AnnotationExpr> toAnnotations, BodyDeclaration bodyDeclaration) {
        List<AnnotationExpr> annotations = new ArrayList<AnnotationExpr>();
        Map<String, AnnotationExpr> fromAnnotationMap = null;
        if (fromAnnotations != null) {
            fromAnnotationMap = new LinkedHashMap<String, AnnotationExpr>();
            for (AnnotationExpr annotation : fromAnnotations) {
                fromAnnotationMap.put(annotation.getName().getName(), annotation);
            }
        }

        if (toAnnotations != null) {
            String definedAnnotationNames = getDefinedAnnotationNames(bodyDeclaration);
            for (AnnotationExpr annotation : toAnnotations) {
                String name = annotation.getName().getName();
                AnnotationExpr fromAnnotation = fromAnnotationMap == null ? null : fromAnnotationMap.remove(name);
                if (fromAnnotation == null) {
                    if (!KernelString.patternInclude(definedAnnotationNames, name)) {
                        annotations.add(annotation);
                    }

                } else {
                    annotations.add(fromAnnotation);
                }
            }
        }

        if (fromAnnotationMap != null) {
            for (AnnotationExpr annotation : fromAnnotationMap.values()) {
                annotations.add(annotation);
            }
        }

        return annotations;
    }

    protected void processBodyDeclaration(BodyDeclaration bodyDeclaration, String declarationAsString) {
    }

    protected BodyDeclaration mergeBodyDeclaration(TypeDeclaration toType, Map<String, BodyDeclaration> declarationMap, List<BodyDeclaration> removeDeclarations, int index, BodyDeclaration bodyDeclaration, String declarationAsString) {
        BodyDeclaration fromDeclaration = declarationMap.remove(declarationAsString);
        if (getAnnotation(bodyDeclaration.getAnnotations(), "AOverride") == null) {
            if (fromDeclaration != null) {
                bodyDeclaration = fromDeclaration;
                toType.getMembers().set(index, bodyDeclaration);

            } else {
                if (isDefinedBodyDeclaration(bodyDeclaration, declarationAsString)) {
                    removeDeclarations.add(bodyDeclaration);
                    bodyDeclaration = null;
                }
            }

            if (bodyDeclaration != null && bodyDeclaration instanceof MethodDeclaration) {
                processBodyDeclaration(bodyDeclaration, declarationAsString);
            }

        } else {
            if (fromDeclaration != null) {
                bodyDeclaration.setAnnotations(mergeAnnotationExpr(fromDeclaration.getAnnotations(), bodyDeclaration.getAnnotations(), bodyDeclaration));
            }
        }

        return bodyDeclaration;
    }

    protected int getModifierFieldDeclaration(String name, FieldDeclaration fieldDeclaration) {
        return Modifier.PROTECTED;
    }

    public static boolean hasClass(List<ClassOrInterfaceType> implementsList, String className) {
        if (implementsList != null) {
            for (ClassOrInterfaceType type : implementsList) {
                if (type.getName().equals(className)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasImport(List<ImportDeclaration> imports, String declaration) {
        if (imports != null) {
            for (ImportDeclaration importDeclaration : imports) {
                if (importDeclaration.getName().toString().equals(declaration)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void mergeCompilationUnit(String className, CompilationUnit fromCompilationUnit, CompilationUnit toCompilationUnit,
                                     TypeDeclaration fromType, TypeDeclaration toType, TypeDeclaration defineType, Map<String, FieldDeclaration> fromFieldMap, Map<String, BodyDeclaration> declarationMap) {
        boolean enumReadable = false;
        boolean isBean = false;
        int dirtyM = 0;
        if (toType instanceof ClassOrInterfaceDeclaration) {
            isBean = isBeanType(className, toType);
            if (isBean) {
                ClassOrInterfaceDeclaration typeDeclaration = (ClassOrInterfaceDeclaration) toType;
                if (fromType == toType || typeDeclaration.getImplements() == null || typeDeclaration.getImplements().isEmpty()) {
                    List<ClassOrInterfaceType> implementsList = typeDeclaration.getImplements();
                    implementsList = implementsList == null ? new ArrayList<ClassOrInterfaceType>() : new ArrayList<ClassOrInterfaceType>(implementsList);
                    setBeanInterface(implementsList, toCompilationUnit);
                    typeDeclaration.setImplements(implementsList);
                }

                List<AnnotationExpr> annotationExprs = defineType.getAnnotations();
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
                        String extendName = dirtyM == 1 ? "DDirty" : "DDirtyM";
                        String importName = "com.absir.data.base." + extendName;
                        if (!hasImport(toCompilationUnit.getImports(), importName)) {
                            toCompilationUnit.getImports().add(
                                    new ImportDeclaration(new NameExpr(importName), false, false));
                        }
                        
                        List<ClassOrInterfaceType> extendsList = new ArrayList<ClassOrInterfaceType>();
                        extendsList.add(new ClassOrInterfaceType(extendName));
                        typeDeclaration.setExtends(extendsList);
                    }
                }

                if (defineType != toType) {
                    toType.setAnnotations(mergeAnnotationExpr(fromType.getAnnotations(), defineType.getAnnotations(), typeDeclaration));
                }
            }

        } else if (fromType instanceof EnumDeclaration) {
            enumReadable = true;
        }

        Map<String, FieldDeclaration> defineFieldDeclarationMap = null;
        Map<String, EnumConstantDeclaration> defineEnumConstantDeclarationMap = null;
        if (defineType != toType) {
            if (enumReadable) {
                defineEnumConstantDeclarationMap = new HashMap<String, EnumConstantDeclaration>();
                for (EnumConstantDeclaration enumConstantDeclaration : ((EnumDeclaration) defineType).getEntries()) {
                    defineEnumConstantDeclarationMap.put(enumConstantDeclaration.getName(), enumConstantDeclaration);
                }

            } else {
                defineFieldDeclarationMap = new HashMap<String, FieldDeclaration>();
                for (BodyDeclaration bodyDeclaration : defineType.getMembers()) {
                    if (bodyDeclaration instanceof FieldDeclaration) {
                        FieldDeclaration fieldDeclaration = (FieldDeclaration) bodyDeclaration;
                        String name = fieldDeclaration.getVariables().get(0).getId().toString();
                        defineFieldDeclarationMap.put(name, fieldDeclaration);
                    }
                }
            }
        }

        Map<String, FieldDeclaration> toFieldMap = new HashMap<String, FieldDeclaration>();
        Map<String, MethodDeclaration> toMethodMap = new HashMap<String, MethodDeclaration>();
        List<BodyDeclaration> removeDeclarations = new ArrayList<BodyDeclaration>();
        {
            int index = -1;
            for (BodyDeclaration bodyDeclaration : toType.getMembers()) {
                index++;
                if (bodyDeclaration instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = ((FieldDeclaration) bodyDeclaration);
                    String name = fieldDeclaration.getVariables().get(0).getId().toString();
                    FieldDeclaration fromFieldDeclaration = fromFieldMap.remove(name);
                    if (fromFieldDeclaration == null) {
                        fromFieldDeclaration = (FieldDeclaration) declarationMap.remove(name);
                        if (fromFieldDeclaration != null) {
                            toType.getMembers().set(index, fromFieldDeclaration);

                        } else if (fieldDeclaration.getAnnotations() == null || fieldDeclaration.getAnnotations().isEmpty()) {
                            removeDeclarations.add(fieldDeclaration);
                        }

                    } else {
                        fieldDeclaration.setModifiers(getModifierFieldDeclaration(name, fieldDeclaration));
                        fieldDeclaration.setType(fromFieldDeclaration.getType());
                        FieldDeclaration defineFieldDeclaration = defineFieldDeclarationMap == null ? null : defineFieldDeclarationMap.remove(name);
                        fieldDeclaration.setAnnotations(mergeAnnotationExpr(fromFieldDeclaration.getAnnotations(), defineFieldDeclaration == null ? fieldDeclaration.getAnnotations() : defineFieldDeclaration.getAnnotations(), bodyDeclaration));
                        if (!enumReadable && isBeanField(fieldDeclaration, name)) {
                            //fieldDeclaration.setModifiers(Modifier.PROTECTED);
                            fromFieldMap.put(name, fromFieldDeclaration);
                            toFieldMap.put(name, fieldDeclaration);
                        }
                    }

                } else if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                    bodyDeclaration = mergeBodyDeclaration(toType, declarationMap, removeDeclarations, index, methodDeclaration, methodDeclaration.getDeclarationAsString());
                    if (bodyDeclaration != null) {
                        if (!enumReadable) {
                            methodDeclaration = (MethodDeclaration) bodyDeclaration;
                            toMethodMap.put(methodDeclaration.getName(), methodDeclaration);
                        }
                    }

                } else if (bodyDeclaration instanceof ConstructorDeclaration) {
                    mergeBodyDeclaration(toType, declarationMap, removeDeclarations, index, bodyDeclaration, ((ConstructorDeclaration) bodyDeclaration).getDeclarationAsString());

                } else if (bodyDeclaration instanceof InitializerDeclaration) {
                    mergeBodyDeclaration(toType, declarationMap, removeDeclarations, index, bodyDeclaration, "@Initializer0");
                }
            }
        }

        List<BodyDeclaration> addDeclarations = new ArrayList<BodyDeclaration>();
        if (enumReadable) {
            EnumDeclaration fromEnum = (EnumDeclaration) fromType;
            EnumDeclaration toEnum = (EnumDeclaration) toType;
            Map<String, EnumConstantDeclaration> nameMapEnum = new LinkedHashMap<String, EnumConstantDeclaration>();
            for (EnumConstantDeclaration declaration : fromEnum.getEntries()) {
                EnumConstantDeclaration defineDeclaration = defineEnumConstantDeclarationMap == null ? null : defineEnumConstantDeclarationMap.remove(declaration.getName());
                if (defineDeclaration != null) {
                    declaration.setAnnotations(mergeAnnotationExpr(declaration.getAnnotations(), defineDeclaration.getAnnotations(), declaration));
                }

                nameMapEnum.put(declaration.getName(), declaration);
            }

            List<EnumConstantDeclaration> toDeclarations = new ArrayList<EnumConstantDeclaration>();
            for (EnumConstantDeclaration declaration : toEnum.getEntries()) {
                EnumConstantDeclaration fromDeclaration = nameMapEnum.remove(declaration.getName());
                if (fromDeclaration == null) {
                    if (declaration.getAnnotations() == null || declaration.getAnnotations().isEmpty()) {
                        continue;
                    }

                    fromDeclaration = declaration;

                } else {
                    fromDeclaration.setAnnotations(mergeAnnotationExpr(fromDeclaration.getAnnotations(), declaration.getAnnotations(), fromDeclaration));
                }

                toDeclarations.add(fromDeclaration);
            }

            for (EnumConstantDeclaration fromDeclaration : nameMapEnum.values()) {
                toDeclarations.add(fromDeclaration);
            }

            toEnum.setEntries(toDeclarations);

        } else {
            if (isBean) {
                if (!declarationMap.isEmpty()) {
                    for (Map.Entry<String, BodyDeclaration> entry : declarationMap.entrySet()) {
                        String name = entry.getKey();
                        BodyDeclaration body = entry.getValue();
                        processBodyDeclaration(body, null);
                        addDeclarations.add(body);
                        if (body instanceof MethodDeclaration) {
                            MethodDeclaration methodDeclaration = (MethodDeclaration) body;
                            toMethodMap.put(methodDeclaration.getName(), methodDeclaration);
                        }
                    }
                }

                int index = 0;
                for (Map.Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
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
        }

        if (!enumReadable) {
            if (isBean) {
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
                new AssignExpr(new NameExpr("this." + name), new NameExpr(name), AssignExpr.Operator.assign));
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
                        statements.add(0, stmt);
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

    protected Expression getCloneAssignExpression(String name, Expression from) {
        return new AssignExpr(new NameExpr("_clone." + name), from == null ? new NameExpr(name) : from, AssignExpr.Operator.assign);
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
                new MethodCallExpr(null, "create"), AssignExpr.Operator.assign));
        stmts.add(stmt);

        boolean nextDepth = false;

        // assign field
        List<Expression> args = new ArrayList<Expression>();
        args.add(new NameExpr("_nextDepth"));
        for (Map.Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
            String name = entry.getKey();
            FieldDeclaration fieldDeclaration = entry.getValue();
            if (isCloneableClassName(fieldDeclaration.getType().toString())) {
                if (!nextDepth) {
                    nextDepth = true;
                    stmt = new ExpressionStmt(new AssignExpr(new NameExpr("int _nextDepth"), new NameExpr("_depth - 1"),
                            AssignExpr.Operator.assign));
                    stmts.add(stmt);
                }

                stmts.add(new ExpressionStmt(
                        getCloneAssignExpression(name, new MethodCallExpr(new NameExpr("_nextDepth < 0 ? " + name + " : " + name), "cloneDepth", args))
                ));

            } else {
                stmts.add(new ExpressionStmt(
                        getCloneAssignExpression(name, null)));
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
        for (Map.Entry<String, FieldDeclaration> entry : fromFieldMap.entrySet()) {
            String name = entry.getKey();
            // FieldDeclaration declaration = entry.getValue();
            BlockStmt thenStmt = new BlockStmt();
            List<Statement> thenStmts = new ArrayList<Statement>();
            thenStmt.setStmts(thenStmts);
            thenStmts.add(new ExpressionStmt(
                    getCloneAssignExpression(name, null)));
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
