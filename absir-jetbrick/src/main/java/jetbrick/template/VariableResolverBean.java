/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年9月4日 下午2:32:30
 */
package jetbrick.template;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import jetbrick.bean.KlassInfo;
import jetbrick.bean.MethodInfo;
import jetbrick.template.resolver.GlobalResolver;
import jetbrick.template.resolver.clazz.ClassResolver;
import jetbrick.template.resolver.function.FunctionInvokerResolver;
import jetbrick.template.resolver.method.MethodInvokerResolver;
import jetbrick.template.resolver.tag.TagInvokerResolver;
import jetbrick.template.runtime.JetTagContext;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelObject;

/**
 * @author absir
 *
 */
public class VariableResolverBean {

	/**
	 * @param engine
	 * @param configProperties
	 */
	public static void load(JetEngine engine, Properties configProperties) {
		DynaBinder.INSTANCE.bind(configProperties, null, engine.getConfig().getClass(), engine.getConfig());
	}

	/** engine */
	protected JetEngine engine;

	/** variableResolver */
	private GlobalResolver variableResolver;

	/** classResolver */
	private final ClassResolver classResolver;

	/** methodInvokerResolver */
	private final MethodInvokerResolver methodInvokerResolver;

	/** functionInvokerResolver */
	private final FunctionInvokerResolver functionInvokerResolver;

	/** tagInvokerResolver */
	private final TagInvokerResolver tagInvokerResolver;

	/**
	 * @param engine
	 */
	public VariableResolverBean(JetEngine engine) {
		this.engine = engine;
		variableResolver = engine.getGlobalResolver();
		classResolver = (ClassResolver) KernelObject.declaredGet(variableResolver, "classResolver");
		methodInvokerResolver = (MethodInvokerResolver) KernelObject.declaredGet(variableResolver, "methodInvokerResolver");
		functionInvokerResolver = (FunctionInvokerResolver) KernelObject.declaredGet(variableResolver, "functionInvokerResolver");
		tagInvokerResolver = (TagInvokerResolver) KernelObject.declaredGet(variableResolver, "tagInvokerResolver");
	}

	/**
	 * @return the variableResolver
	 */
	public GlobalResolver getVariableResolver() {
		return variableResolver;
	}

	/**
	 * @param name
	 */
	public void importClass(String name) {
		classResolver.importClass(name);
	}

	/**
	 * @param name
	 */
	public void importPackage(String name) {
		classResolver.importClass(name + ".*");
	}

	/**
	 * @param method
	 * @return
	 */
	public MethodInfo create(Method method) {
		KlassInfo klass = KlassInfo.create(method.getDeclaringClass());
		return klass.getDeclaredMethod(method.getName(), method.getParameterTypes());
	}

	/**
	 * @param name
	 * @param method
	 */
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

	/**
	 * @param name
	 * @param method
	 */
	public void registerFunction(String name, Method method) {
		int modifiers = method.getModifiers();
		if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
			MethodInfo methodInfo = create(method);
			if (methodInfo != null) {
				functionInvokerResolver.register(methodInfo);
			}
		}
	}

	/**
	 * @param name
	 * @param method
	 */
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
