/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-4-2 下午10:35:36
 */
package com.absir.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelReflect;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UtilAnnotation {

	/**
	 * @param obj
	 * @param copy
	 */
	public static void copy(Annotation obj, Object copy) {
		final Class cls = copy.getClass();
		for (Method method : obj.getClass().getMethods()) {
			if (method.getParameterTypes().length == 0) {
				Field field = KernelReflect.declaredField(cls, method.getName());
				if (field != null && field.getType().isAssignableFrom(method.getReturnType())) {
					KernelReflect.set(copy, field, KernelReflect.invoke(obj, method));
				}
			}
		}
	}

	/**
	 * @param annotationClass
	 * @param value
	 * @return
	 */
	public static Object defaultValue(Class<? extends Annotation> annotationClass, String value) {
		Method method = KernelReflect.declaredMethod(annotationClass, value);
		if (method != null) {
			return method.getDefaultValue();
		}

		return null;
	}

	/**
	 * @param annotation
	 * @return
	 */
	public static <T extends Annotation> T clone(T annotation) {
		return clone(annotation, null);
	}

	/**
	 * @param annotation
	 * @param memberValues
	 * @return
	 */
	public static <T extends Annotation> T clone(T annotation, Map<String, Object> memberValues) {
		if (memberValues == null) {
			memberValues = new HashMap<String, Object>();
		}

		for (Method method : annotation.getClass().getMethods()) {
			if (!memberValues.containsKey(method.getName())) {
				memberValues.put(method.getName(), KernelReflect.invoke(annotation, method));
			}
		}

		return (T) Proxy.newProxyInstance(AnnotationHandler.class.getClassLoader(), new Class[] { annotation.getClass() }, new AnnotationHandler(annotation.getClass(), memberValues));
	}

	/**
	 * @author absir
	 * 
	 */
	public static class AnnotationHandler extends UtilAbstractHandler {

		/** annotationClass */
		private Class<? extends Annotation> annotationClass;

		/** memberValues */
		private Map<String, Object> memberValues;

		/**
		 * @param annotationClass
		 * @param memberValues
		 */
		public AnnotationHandler(Class<? extends Annotation> annotationClass, Map<String, Object> memberValues) {
			this.annotationClass = annotationClass;
			this.memberValues = memberValues;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.aserv.handler.HandlerObject#invoke(java.lang.Object,
		 * java.lang.String, java.lang.Object[])
		 */
		@Override
		public Object invoke(Object object, String methodName, Object[] args) {
			if (memberValues.containsKey(methodName)) {
				Object result = memberValues.get(methodName);
				if (result.getClass().isArray()) {
					result = KernelArray.clone(result);
				}

				return result;

			} else {
				Method method = KernelReflect.declaredMethod(annotationClass, methodName);
				if (method == null) {
					return KernelLang.NULL_OBJECT;

				} else {
					return method.getDefaultValue();
				}
			}
		}
	}
}
