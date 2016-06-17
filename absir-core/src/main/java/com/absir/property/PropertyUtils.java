/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-6 下午12:14:57
 */
package com.absir.property;

import com.absir.bean.basis.BeanConfig;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.property.value.BeanName;
import com.absir.property.value.Prop;
import com.absir.property.value.Properties;
import com.absir.property.value.Property;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PropertyUtils {

    public static final int TRANSIENT_MODIFIER = Modifier.TRANSIENT | Modifier.STATIC | Modifier.FINAL;

    private static Map<Class<?>, PropertyHolder> Class_Map_Property_Holder = new HashMap<Class<?>, PropertyHolder>();

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

    private static PropertyContext getPropertyContext(Map<String, Object> propertyMap, String name) {
        PropertyContext propertyContext = (PropertyContext) propertyMap.get(name);
        if (propertyContext == null) {
            propertyContext = new PropertyContext();
            propertyContext.name = name;
            propertyMap.put(name, propertyContext);
        }

        return propertyContext;
    }

    private static void addPropertyMap(final Map<String, Object> propertyMap, boolean propertyTree, Class<?> beanClass, final PropertySupply propertySupply) {
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
                BeanName beanName = PropertyUtils.getFieldAnnotation(beanClass, field, BeanName.class);
                if (beanName != null) {
                    propertyContext.beanName = beanName.value();
                }

                propertyContext.prop(PropertyUtils.getFieldAnnotation(beanClass, field, Prop.class));
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
                        BeanName beanName = BeanConfigImpl.getMethodAnnotation(method, BeanName.class);
                        if (beanName != null) {
                            propertyContext.beanName = beanName.value();
                        }

                        propertyContext.prop(BeanConfigImpl.getMethodAnnotation(method, Prop.class));
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

                        propertyContext.prop(BeanConfigImpl.getMethodAnnotation(method, Prop.class));
                        propertyContext.propertyObject = propertySupply.getPropertyObjectSetter(propertyContext.propertyObject, method);

                    } else {
                        propertyMap.put(name, propertySupply.getPropertyObjectSetter((PropertyObject) propertyMap.get(name), method));
                    }
                }
            }
        }

        Properties properties = BeanConfigImpl.getTypeAnnotation(beanClass, Properties.class);
        if (properties != null) {
            for (Property property : properties.value()) {
                if (!KernelString.isEmpty(property.name())) {
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

        final Set<String> names = new HashSet<String>(propertyMap.keySet());
        for (Field field : beanClass.getDeclaredFields()) {
            names.add(field.getName());
        }

        KernelClass.doWithAncestRevertClass(beanClass, new KernelLang.CallbackBreak<Class<?>>() {
            @Override
            public void doWith(Class<?> template) throws KernelLang.BreakException {
                Map<String, Object> properties = getPropertiesForBeanClass(template);
                if (properties != null && !properties.isEmpty()) {
                    for (String name : names) {
                        BeanConfigImpl.ParamsAnnotations annotations = BeanConfigImpl.getParamsAnnotations(properties, name);
                        if (annotations != null) {
                            propertyMap.put(name, propertySupply.getPropertyObjectParams(annotations.findAnnotation(NoProperty.class) ? null : (PropertyObject) propertyMap.get(name), annotations));
                        }
                    }
                }
            }

        }, true);
    }

    protected static final Map<String, Map<String, Object>> beanClassMapProperties = new HashMap<String, Map<String, Object>>();

    public static Map<String, Object> getPropertiesForBeanClass(Class<?> beanClass) {
        return getPropertiesForBeanClass(beanClass, "properties");
    }

    public static Map<String, Object> getPropertiesForBeanClass(Class<?> beanClass, String category) {
        String beanClassCategory = beanClass + "@" + category;
        Map<String, Object> properties = beanClassMapProperties.get(beanClassCategory);
        if (properties != null) {
            return properties;
        }

        BeanConfig config = BeanFactoryUtils.getBeanConfig();
        File propertiesFile = new File(config.getClassPath() + category + "/" + beanClass.getSimpleName() + ".properties");
        if (propertiesFile.exists()) {
            properties = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(config, properties, propertiesFile, null);
        }

        if (properties == null || properties.isEmpty()) {
            properties = (Map<String, Object>) (Object) KernelLang.NULL_MAP;
        }

        beanClassMapProperties.put(beanClassCategory, properties);
        return properties;
    }

    public static @interface NoProperty {

    }

    public static <T extends Annotation> T getFieldAnnotation(Class<?> beanClass, Field field, final Class<T> annotationClass) {
        final KernelLang.ObjectTemplate<Object> annotationTemplate = new KernelLang.ObjectTemplate<Object>();
        final String name = field.getName();
        if (beanClass == null) {
            beanClass = field.getDeclaringClass();
        }

        KernelClass.doWithAncestRevertClass(beanClass, new KernelLang.CallbackBreak<Class<?>>() {
            @Override
            public void doWith(Class<?> template) throws KernelLang.BreakException {
                Map<String, Object> properties = getPropertiesForBeanClass(template);
                if (properties != null && !properties.isEmpty()) {
                    BeanConfigImpl.ParamsAnnotations annotations = BeanConfigImpl.getParamsAnnotations(properties, name);
                    if (annotations != null) {
                        T annotation = annotations.getAnnotation(annotationClass);
                        if (annotation != null) {
                            annotationTemplate.object = annotation;
                            throw new KernelLang.BreakException();
                        }

                        if (annotations.findAnnotation(NoProperty.class)) {
                            annotationTemplate.object = KernelLang.NULL_OBJECT;
                            throw new KernelLang.BreakException();
                        }
                    }
                }
            }

        }, true);

        Object annotation = annotationTemplate.object;
        if (annotation == null) {
            return field.getAnnotation(annotationClass);

        } else {
            return annotation == KernelLang.NULL_OBJECT ? null : (T) annotation;
        }
    }
}
