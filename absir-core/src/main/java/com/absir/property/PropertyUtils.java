/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014-1-6 下午12:14:57
 */
package com.absir.property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.absir.core.kernel.KernelString;
import com.absir.property.value.BeanName;
import com.absir.property.value.Prop;
import com.absir.property.value.Properties;
import com.absir.property.value.Property;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PropertyUtils {

	/**
	 * @param parameterAnnotations
	 * @return
	 */
	public static String[] paramterBeanNames(Annotation[][] parameterAnnotations) {
		int length = parameterAnnotations.length;
		if (length == 0) {
			return null;
		}

		String[] paramterBeanNames = new String[length];
		for (int i = 0; i < length; i++) {
			for (Annotation annotation : parameterAnnotations[i]) {
				if (annotation instanceof BeanName) {
					String name = ((BeanName) annotation).value();
					if (!KernelString.isEmpty(name)) {
						paramterBeanNames[i] = name;
					}

					break;
				}
			}
		}

		return paramterBeanNames;
	}

	/** Class_Map_Property_Holder */
	private static Map<Class<?>, PropertyHolder> Class_Map_Property_Holder = new HashMap<Class<?>, PropertyHolder>();

	/** TRANSIENT_MODIFIER */
	public static final int TRANSIENT_MODIFIER = Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL;

	/**
	 * @param beanClass
	 * @return
	 */
	public static PropertyHolder getPropertyMap(Class<?> beanClass, PropertySupply propertySupply) {
		PropertyHolder propertyHolder = Class_Map_Property_Holder.get(beanClass);
		int propertyIndex = propertySupply.getSupplyIndex();
		if (propertyHolder == null || !propertyHolder.holded(propertyIndex)) {
			synchronized (beanClass) {
				propertyHolder = Class_Map_Property_Holder.get(beanClass);
				Map<String, Object> propertyMap = null;
				boolean propertyTree = false;
				if (propertyHolder == null) {
					propertyHolder = new PropertyHolder();
					Class_Map_Property_Holder.put(beanClass, propertyHolder);
					propertyHolder.holded(propertySupply.getSupplyIndex());
					propertyMap = new LinkedHashMap<String, Object>();
					propertyTree = true;

				} else if (!propertyHolder.holded(propertyIndex)) {
					propertyMap = new HashMap<String, Object>();
				}

				if (propertyMap != null) {
					addPropertyMap(propertyMap, propertyTree, beanClass, propertySupply);
					propertyHolder.doHolded(propertyIndex, beanClass, propertyMap, propertyTree);
				}
			}
		}

		return propertyHolder;
	}

	/**
	 * @param propertyMap
	 * @param name
	 * @return
	 */
	private static PropertyContext getPropertyContext(Map<String, Object> propertyMap, String name) {
		PropertyContext propertyContext = (PropertyContext) propertyMap.get(name);
		if (propertyContext == null) {
			propertyContext = new PropertyContext();
			propertyContext.name = name;
			propertyMap.put(name, propertyContext);
		}

		return propertyContext;
	}

	/**
	 * @param propertyMap
	 * @param propertyTree
	 * @param beanClass
	 * @param propertySupply
	 */
	private static void addPropertyMap(Map<String, Object> propertyMap, boolean propertyTree, Class<?> beanClass, PropertySupply propertySupply) {
		if (beanClass == null || beanClass == Object.class) {
			return;
		}

		addPropertyMap(propertyMap, propertyTree, beanClass.getSuperclass(), propertySupply);
		String name = null;
		for (Field field : beanClass.getDeclaredFields()) {
			if ((field.getModifiers() & TRANSIENT_MODIFIER) != 0) {
				continue;
			}

			name = field.getName();
			if (propertyTree) {
				PropertyContext propertyContext = getPropertyContext(propertyMap, name);
				BeanName beanName = field.getAnnotation(BeanName.class);
				if (beanName != null) {
					propertyContext.beanName = beanName.value();
				}

				propertyContext.prop(field.getAnnotation(Prop.class));
				propertyContext.propertyObject = propertySupply.getPropertyObject(propertyContext.propertyObject, field);

			} else {
				propertyMap.put(name, propertySupply.getPropertyObject((PropertyObject) propertyMap.get(name), field));
			}
		}

		for (Method method : beanClass.getDeclaredMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				continue;
			}

			name = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 0 && Modifier.isPublic(method.getModifiers()) && name.length() > 2) {
				if (name.startsWith("is")) {
					name = KernelString.unCapitalize(name.substring(2));

				} else if (name.length() > 3 && method.getName().startsWith("get")) {
					name = KernelString.unCapitalize(name.substring(3));
				}

				if (name != method.getName()) {
					if (propertyTree) {
						PropertyContext propertyContext = getPropertyContext(propertyMap, name);
						BeanName beanName = method.getAnnotation(BeanName.class);
						if (beanName != null) {
							propertyContext.beanName = beanName.value();
						}

						propertyContext.prop(method.getAnnotation(Prop.class));
						propertyContext.propertyObject = propertySupply.getPropertyObjectGetter(propertyContext.propertyObject, method);

					} else {
						propertyMap.put(name, propertySupply.getPropertyObjectGetter((PropertyObject) propertyMap.get(name), method));
					}
				}

			} else if (parameterTypes.length == 1 && name.length() > 3) {
				if (method.getName().startsWith("set")) {
					name = KernelString.unCapitalize(name.substring(3));
					if (propertyTree) {
						PropertyContext propertyContext = getPropertyContext(propertyMap, name);
						String beanName = paramterBeanNames(method.getParameterAnnotations())[0];
						if (!KernelString.isEmpty(beanName)) {
							propertyContext.beanName = beanName;
						}

						propertyContext.prop(method.getAnnotation(Prop.class));
						propertyContext.propertyObject = propertySupply.getPropertyObjectSetter(propertyContext.propertyObject, method);

					} else {
						propertyMap.put(name, propertySupply.getPropertyObjectSetter((PropertyObject) propertyMap.get(name), method));
					}
				}
			}
		}

		Properties properties = beanClass.getAnnotation(Properties.class);
		if (properties != null) {
			for (Property property : properties.value()) {
				if (!KernelString.isEmpty(name)) {
					name = property.name();
				}

				if (propertyTree) {
					PropertyContext propertyObject = getPropertyContext(propertyMap, name);
					for (Prop prop : property.props()) {
						propertyObject.prop(prop);
					}

					propertyObject.propertyObject = propertySupply.getPropertyObject(propertyObject.propertyObject, property.infos());

				} else {
					propertyMap.put(name, propertySupply.getPropertyObject((PropertyObject) propertyMap.get(name), property.infos()));
				}
			}
		}
	}

}
