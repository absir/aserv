/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年9月4日 下午2:32:30
 */
package jetbrick.template;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelObject;
import jetbrick.bean.KlassInfo;
import jetbrick.bean.MethodInfo;
import jetbrick.template.resolver.GlobalResolver;
import jetbrick.template.resolver.clazz.ClassResolver;
import jetbrick.template.resolver.function.FunctionInvokerResolver;
import jetbrick.template.resolver.method.MethodInvokerResolver;
import jetbrick.template.resolver.tag.TagInvokerResolver;
import jetbrick.template.runtime.JetTagContext;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

public class VariableResolverBean {

    private final ClassResolver classResolver;

    private final MethodInvokerResolver methodInvokerResolver;

    private final FunctionInvokerResolver functionInvokerResolver;

    private final TagInvokerResolver tagInvokerResolver;

    protected JetEngine engine;

    private GlobalResolver variableResolver;

    public VariableResolverBean(JetEngine engine) {
        this.engine = engine;
        variableResolver = engine.getGlobalResolver();
        classResolver = (ClassResolver) KernelObject.declaredGet(variableResolver, "classResolver");
        methodInvokerResolver = (MethodInvokerResolver) KernelObject.declaredGet(variableResolver, "methodInvokerResolver");
        functionInvokerResolver = (FunctionInvokerResolver) KernelObject.declaredGet(variableResolver, "functionInvokerResolver");
        tagInvokerResolver = (TagInvokerResolver) KernelObject.declaredGet(variableResolver, "tagInvokerResolver");
    }

    public static void load(JetEngine engine, Properties configProperties) {
        DynaBinder.INSTANCE.bind(configProperties, null, engine.getConfig().getClass(), engine.getConfig());
    }

    public GlobalResolver getVariableResolver() {
        return variableResolver;
    }

    public void importClass(String name) {
        classResolver.importClass(name);
    }

    public void importPackage(String name) {
        classResolver.importClass(name + ".*");
    }

    public MethodInfo create(Method method) {
        KlassInfo klass = KlassInfo.create(method.getDeclaringClass());
        return klass.getDeclaredMethod(method.getName(), method.getParameterTypes());
    }

    public void registerMethod(String name, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return;
        }
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
            MethodInfo methodInfo = create(method);
            if (methodInfo != null) {
                methodInvokerResolver.register(methodInfo);
            }
        }
    }

    public void registerFunction(String name, Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
            MethodInfo methodInfo = create(method);
            if (methodInfo != null) {
                functionInvokerResolver.register(methodInfo);
            }
        }
    }

    public void registerTag(String name, Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
            if (!Void.TYPE.equals(method.getReturnType())) {
                return;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 0 && JetTagContext.class.equals(parameterTypes[0])) {
                MethodInfo methodInfo = create(method);
                if (methodInfo != null) {
                    tagInvokerResolver.register(methodInfo);
                }
            }
        }
    }
}
