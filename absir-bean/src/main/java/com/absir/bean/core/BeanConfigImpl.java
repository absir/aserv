/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-10 下午1:43:31
 */
package com.absir.bean.core;

import com.absir.bean.basis.BeanConfig;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanConfigProvider;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.helper.HelperIO;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.core.kernel.KernelLang.MatcherType;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAnnotation;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BeanConfigImpl implements BeanConfig {

    protected boolean outEnvironmentDenied = true;

    private BeanConfig beanConfig;

    private String classPath;

    private String resourcePath;

    private Environment environment = Environment.getEnvironment();

    private Map<String, Object> configMap = new HashMap<String, Object>();

    public BeanConfigImpl(IBeanConfigProvider beanConfigProvider) {
        this(beanConfigProvider, null);
    }

    public BeanConfigImpl(IBeanConfigProvider beanConfigProvider, String classPath) {
        if (classPath == null) {
            classPath = HelperFileName.getClassPath(null);
        }

        BeanFactory beanFactory = BeanFactoryUtils.get();
        this.beanConfig = beanFactory == null ? null : beanFactory.getBeanConfig();
        setClassPath(classPath);
        setResourcePath(classPath);
        Set<String> propertyFilenames = new HashSet<String>();
        Set<String> loadedPropertyFilenames = new HashSet<String>();
        Map<String, CallbackTemplate<String>> beanConfigTemplates = new HashMap<String, CallbackTemplate<String>>();
        loadBeanConfig(beanConfigProvider, propertyFilenames, loadedPropertyFilenames, beanConfigTemplates);
        resourcePath = HelperFileName.normalizeNoEndSeparator(resourcePath) + HelperFileName.SYSTEM_SEPARATOR;
        readProperties(resourcePath + "config.properties", propertyFilenames, loadedPropertyFilenames,
                beanConfigTemplates);
        readProperties(resourcePath + getEnvironment().name().toLowerCase() + ".properties", propertyFilenames,
                loadedPropertyFilenames, beanConfigTemplates);
        readProperties(resourcePath + "properties", propertyFilenames, loadedPropertyFilenames, beanConfigTemplates);
        while (true) {
            Iterator<String> iterator = propertyFilenames.iterator();
            if (iterator.hasNext()) {
                String filename = iterator.next();
                iterator.remove();
                readProperties(filename, propertyFilenames, loadedPropertyFilenames, beanConfigTemplates);

            } else {
                break;
            }
        }
    }

    public static void readProperties(final BeanConfig beanConfig, final Map<String, Object> configMap,
                                      File propertyFile, final Map<String, CallbackTemplate<String>> beanConfigTemplates) {
        if (propertyFile.exists()) {
            try {
                InputStream inputStream = new FileInputStream(propertyFile);
                readProperties(beanConfig, configMap, inputStream, beanConfigTemplates);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConfigEnvironments(BeanConfig beanConfig, String[] environments) {
        if (environments == null) {
            return true;
        }

        for (String environment : environments) {
            if (KernelString.isEmpty(environment)) {
                return true;
            }

            int pos = environment.indexOf('&');
            if (pos > 0) {
                String[] params = environment.split("&", 2);
                String value = KernelDyna.to(getValue(beanConfig, params[0]), String.class);
                if (value != null) {
                    String match = params[1];
                    if (!match.isEmpty()) {
                        Entry<String, KernelLang.IMatcherType> entry = MatcherType.getMatchEntry(match.toLowerCase());
                        if (MatcherType.isMatch(value.toLowerCase(), entry)) {
                            return true;
                        }
                    }
                }

            } else {
                if (beanConfig == null || beanConfig.getEnvironment().toString().equals(environment)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void readProperties(final BeanConfig beanConfig, final Map<String, Object> configMap,
                                      InputStream inputStream, final Map<String, CallbackTemplate<String>> beanConfigTemplates) {
        try {
            HelperIO.doWithReadLine(inputStream, new CallbackBreak<String>() {

                private StringBuilder blockBuilder;

                private int blockAppending;

                @Override
                public void doWith(String template) throws BreakException {
                    int length = template.length();
                    if (length < 1) {
                        return;
                    }

                    char chr = template.charAt(0);
                    if (blockBuilder == null) {
                        if (chr == '#') {
                            return;

                        } else if (chr == '{' && length == 2 && template.charAt(1) == '"') {
                            blockBuilder = new StringBuilder();
                            blockAppending = 1;
                            return;
                        }

                    } else if (blockAppending > 0) {
                        if (chr == '"' && length == 2 && template.charAt(1) == '}') {
                            blockAppending = 0;

                        } else {
                            if (blockAppending > 1) {
                                blockBuilder.append("\r\n");

                            } else {
                                blockAppending = 2;
                            }

                            blockBuilder.append(beanConfig == null ? template : beanConfig.getExpression(template));
                        }

                        return;
                    }

                    if (length < 3) {
                        return;
                    }

                    int index = template.indexOf('=');
                    if (index > 0 && index < length) {
                        String name;
                        chr = template.charAt(index - 1);
                        if (chr == '.' || chr == '#' || chr == '+') {
                            if (index < 1) {
                                return;
                            }

                            name = template.substring(0, index - 1);

                        } else {
                            chr = 0;
                            name = template.substring(0, index);
                        }

                        length = name.length();
                        if (length == 0) {
                            return;
                        }

                        template = template.substring(index + 1);
                        if (beanConfig == null) {
                            template = KernelString.unTransferred(template);
                            if (blockBuilder != null) {
                                if (template.length() > 0) {
                                    blockBuilder.append("\r\n");
                                    blockBuilder.append(template);
                                }

                                template = blockBuilder.toString();
                                blockBuilder = null;
                                blockAppending = 0;
                            }

                            configMap.put(name, template);

                        } else {
                            name = name.trim();
                            if (length == 0) {
                                return;
                            }

                            length = name.length();
                            template = template.trim();
                            String[] environments = null;
                            index = name.indexOf('|');
                            if (index > 0) {
                                if (length <= 1) {
                                    return;
                                }

                                String environmentParams = name.substring(index + 1);
                                name = name.substring(0, index).trim();
                                length = name.length();
                                if (length == 0) {
                                    return;
                                }

                                environments = environmentParams.trim().split("\\|");
                            }

                            if (isConfigEnvironments(beanConfig, environments)) {
                                template = beanConfig.getExpression(KernelString.unTransferred(template));
                                if (blockBuilder != null) {
                                    if (template.length() > 0) {
                                        if (template.length() > 0) {
                                            blockBuilder.append("\r\n");
                                            blockBuilder.append(template);
                                        }
                                    }

                                    template = blockBuilder.toString();
                                    blockBuilder = null;
                                    blockAppending = 0;
                                }

                                CallbackTemplate<String> callbackTemplate = chr == 0
                                        ? beanConfigTemplates == null ? null : beanConfigTemplates.get(name) : null;
                                if (callbackTemplate == null) {
                                    if (beanConfig == null || beanConfig.isOutEnvironmentDenied()) {
                                        Object value = template;
                                        if (chr != 0) {
                                            Object old;
                                            switch (chr) {
                                                case '.':
                                                    old = DynaBinder.to(configMap.get(name), String.class);
                                                    if (old != null) {
                                                        value = old + template;
                                                    }

                                                    break;
                                                case '#':
                                                    old = DynaBinder.to(configMap.get(name), String.class);
                                                    if (old != null) {
                                                        value = old + "\r\n" + template;
                                                    }

                                                    break;
                                                case '+':
                                                    old = DynaBinder.to(configMap.get(name), List.class);
                                                    if (old != null) {
                                                        ((List) old).add(template);
                                                        value = old;
                                                    }

                                                    break;
                                                default:
                                                    break;
                                            }
                                        }

                                        configMap.put(name, value);
                                    }

                                } else {
                                    callbackTemplate.doWith(template);
                                }

                            } else if (blockBuilder != null) {
                                blockBuilder = null;
                                blockAppending = 0;
                            }
                        }
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readDirProperties(final BeanConfig beanConfig, final Map<String, Object> configMap,
                                         File propertyDir, final Map<String, CallbackTemplate<String>> beanConfigTemplates) {
        if (propertyDir.exists()) {
            File[] files = propertyDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".properties");
                }
            });

            for (File file : files) {
                readProperties(beanConfig, configMap, file, beanConfigTemplates);
            }
        }
    }

    public static void writeProperties(Map<String, ?> configMap, File propertyFile) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, ?> entry : configMap.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append('=');
            stringBuilder.append(entry.getValue());
            stringBuilder.append("\r\n");
        }

        try {
            HelperFile.write(propertyFile, stringBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object getValue(BeanConfig beanConfig, String name) {
        Object object = beanConfig == null ? null : beanConfig.getConfigValue(name);
        if (object == null) {
            if (name.length() > 1) {
                char chr = name.charAt(0);
                switch (chr) {
                    case '%':
                        object = System.getenv(name.substring(1));
                        break;
                    case '@':
                        object = System.getProperties().getProperty(name.substring(1));
                        break;
                    case '$':
                        object = beanConfig == null ? object : beanConfig.getConfigValue(name.substring(1));
                        break;
                    default:
                        break;
                }
            }
        }

        return object;
    }

    public static <T> T getMapValue(Map map, Object name, String beanName, Class<T> toClass) {
        Object obj = map.get(name);
        if (obj != null) {
            T toObject = DynaBinder.to(obj, beanName, toClass);
            if (toObject != obj) {
                map.put(name, toObject);
            }

            return toObject;
        }

        return null;
    }

    public static Object getMapValue(Map map, Object name, String beanName, Type toType) {
        Object obj = map.get(name);
        if (obj != null) {
            Object toObject = DynaBinder.INSTANCE.bind(obj, beanName, toType);
            if (toObject != obj) {
                map.put(name, toObject);
            }

            return toObject;
        }

        return null;
    }

    public boolean isOutEnvironmentDenied() {
        return outEnvironmentDenied;
    }

    protected void loadBeanConfig(IBeanConfigProvider beanConfigProvider, final Set<String> propertyFilenames,
                                  final Set<String> loadedPropertyFilenames,
                                  final Map<String, CallbackTemplate<String>> beanConfigTemplates) {
        beanConfigTemplates.put("environment", new CallbackTemplate<String>() {

            @Override
            public void doWith(String template) {
                try {
                    Environment env = Environment.valueOf(template.toUpperCase());
                    if (env != null) {
                        setEnvironment(env);
                    }

                } catch (Exception e) {
                }
            }
        });

        beanConfigTemplates.put("outEnvironment", new CallbackTemplate<String>() {
            @Override
            public void doWith(String template) {
                outEnvironmentDenied = true;
            }
        });

        beanConfigTemplates.put("inEnvironment", new CallbackTemplate<String>() {
            @Override
            public void doWith(String template) {
                try {
                    Environment env = Environment.valueOf(template.toUpperCase());
                    if (env != null) {
                        outEnvironmentDenied = env == environment;
                    }

                } catch (Exception e) {
                    outEnvironmentDenied = false;
                }
            }
        });

        beanConfigTemplates.put("inConfigure", new CallbackTemplate<String>() {
            @Override
            public void doWith(String template) {
                outEnvironmentDenied = getExpressionValue(template, null, boolean.class);
            }
        });

        beanConfigTemplates.put("resourcePath", new CallbackTemplate<String>() {

            @Override
            public void doWith(String template) {
                template = getResourcePath(template);
                setResourcePath(HelperFileName.normalizeNoEndSeparator(template) + HelperFileName.SYSTEM_SEPARATOR);
            }

        });

        beanConfigTemplates.put("properties", new CallbackTemplate<String>() {

            @Override
            public void doWith(String template) {
                for (String filename : template.split(",")) {
                    filename = filename.trim();
                    if (filename.length() > 0) {
                        filename = getClassPath(filename);
                        filename = HelperFileName.normalizeNoEndSeparator(filename);
                        if (!loadedPropertyFilenames.contains(filename)) {
                            propertyFilenames.add(filename);
                        }
                    }
                }
            }

        });

        if (beanConfigProvider != null) {
            beanConfigProvider.loadBeanConfig(this, propertyFilenames, loadedPropertyFilenames, beanConfigTemplates);
        }
    }

    private void readProperties(String filename, Set<String> propertyFilenames, Set<String> loadedPropertyFilenames,
                                Map<String, CallbackTemplate<String>> beanConfigTemplates) {
        if (!loadedPropertyFilenames.add(filename)) {
            return;
        }

        File propertyFile = new File(filename);
        if (propertyFile.exists()) {
            if (propertyFile.isDirectory()) {
                File[] files = propertyFile.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        if (name.endsWith(".properties")) {
                            int index = name.indexOf('_');
                            return index <= 0 || name.substring(0, index).equals(getEnvironment().name().toLowerCase())
                                    ? true : false;
                        }

                        return false;
                    }
                });

                for (File file : files) {
                    filename = file.getPath();
                    if (!loadedPropertyFilenames.contains(filename)) {
                        propertyFilenames.add(file.getPath());
                    }
                }

            } else {
                readProperties(this, configMap, propertyFile, beanConfigTemplates);
            }
        }
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
        configMap.put("classPath", classPath);
        System.setProperty("classPath", classPath);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
        configMap.put("resourcePath", resourcePath);
        System.setProperty("resourcePath", resourcePath);
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
        Environment.setEnvironment(environment);
    }

    public Object getValue(String name) {
        return getValue(this, name);
    }

    public void setValue(String name, Object obj) {
        configMap.put(name, obj);
    }

    public Object getConfigValue(String name) {
        Object obj = configMap.get(name);
        if (obj == null && !configMap.containsKey(name)) {
            if (beanConfig != null) {
                obj = beanConfig.getConfigValue(name);
            }
        }

        return obj;
    }

    public String getExpression(String expression) {
        return getExpression(expression, false);
    }

    public String getExpression(String expression, boolean strict) {
        int fromIndex = expression.indexOf("${");
        int length = expression.length();
        if (fromIndex >= 0 && fromIndex < length - 2) {
            StringBuilder stringBuilder = new StringBuilder();
            int endIndex = 0;
            while (true) {
                if (fromIndex > endIndex) {
                    stringBuilder.append(expression.substring(endIndex, fromIndex));

                } else if (fromIndex < endIndex) {
                    if (fromIndex < 0) {
                        if (length > endIndex) {
                            stringBuilder.append(expression.substring(endIndex, length));
                        }
                    }

                    break;
                }

                if ((endIndex = expression.indexOf('}', fromIndex)) < 0) {
                    stringBuilder.append(expression.substring(fromIndex));
                    break;
                }

                fromIndex += 2;
                if (fromIndex < endIndex) {
                    Object value = getValue(expression.substring(fromIndex, endIndex));
                    if (value == null) {
                        if (strict) {
                            return null;
                        }

                    } else {
                        stringBuilder.append(value);
                    }
                }

                fromIndex = expression.indexOf("${", endIndex++);
            }

            expression = stringBuilder.toString();
            expression.replace("$$", "$");
        }

        return expression;
    }

    public <T> T getExpressionObject(String expression, String beanName, Class<T> toClass) {
        Object obj = getExpression(expression);
        if (obj == expression) {
            obj = getMapValue(configMap, expression, beanName, toClass);
            if (obj == null && !configMap.containsKey(expression)) {
                if (beanConfig != null) {
                    return beanConfig.getExpressionObject(expression, beanName, toClass);
                }

                return null;
            }
        }

        return DynaBinder.to(obj, beanName, toClass);
    }

    public Object getExpressionObject(String expression, String beanName, Type toType) {
        Object obj = getExpression(expression);
        if (obj == expression) {
            obj = getMapValue(configMap, expression, beanName, toType);
            if (obj == null && !configMap.containsKey(expression)) {
                if (beanConfig != null) {
                    obj = beanConfig.getExpressionObject(expression, beanName, toType);
                }
            }

            return obj;

        } else {
            return DynaBinder.INSTANCE.bind(obj, beanName, toType);
        }
    }

    @Override
    public <T> T getExpressionValue(String expression, String beanName, Class<T> toClass) {
        return DynaBinder.to(getExpressionObject(expression, beanName, toClass), null, toClass);
    }

    @Override
    public <T> T getExpressionDefaultValue(String expression, String beanName, Class<T> toClass) {
        T value = getExpressionObject(expression, beanName, toClass);
        if (value == null) {
            value = DynaBinder.to(expression, beanName, toClass);
        }

        return value;
    }

    @Override
    public Object getExpressionDefaultValue(String expression, String beanName, Type toType) {
        Object value = getExpressionObject(expression, beanName, toType);
        if (value == null) {
            value = DynaBinder.INSTANCE.bind(expression, beanName, toType);
        }

        return value;
    }

    @Override
    public String getClassPath(String filename) {
        return getResourcePath(filename, classPath);
    }

    @Override
    public String getResourcePath(String filename) {
        return getResourcePath(filename, resourcePath);
    }

    @Override
    public String getResourcePath(String filename, String nullPrefix) {
        filename = filename.replace("classpath:", classPath);
        filename = filename.replace("resourcePath:", resourcePath);
        if (KernelString.isEmpty(HelperFileName.getPrefix(filename))) {
            filename = nullPrefix + filename;
        }

        return filename;
    }

    public static class ParamsAnnotations {

        protected Map<String, String[]> nameMapParams;

        protected Map<Class<? extends Annotation>, Object> classMapAnnotation;

        protected boolean matchFind;

        public <T extends Annotation> T getAnnotation(Class<T> cls) {
            if (cls == null) {
                return null;
            }

            Object annotation = nameMapParams == null ? null : nameMapParams.get(cls);
            if (annotation == null) {
                Map<String, String[]> mapParams = nameMapParams;
                if (mapParams != null) {
                    String[] params = mapParams.remove(KernelClass.getClassSharedSimpleName(cls));
                    if (params != null) {
                        annotation = UtilAnnotation.newInstance(cls, params, 1);
                        if (mapParams.isEmpty()) {
                            nameMapParams = null;
                        }
                    }
                }
            }

            return (T) annotation;
        }

        public boolean findAnnotation(Class<? extends Annotation> cls) {
            return classMapAnnotation.containsKey(cls) || nameMapParams.containsKey(KernelClass.getClassSharedSimpleName(cls));
        }
    }

    public static ParamsAnnotations getParamsAnnotations(Object property) {
        ParamsAnnotations annotations = null;
        if (property != null) {
            if (property.getClass() == ParamsAnnotations.class) {
                annotations = (ParamsAnnotations) property;

            } else {
                List<Object> list;
                if (property.getClass() == String.class) {
                    list = new ArrayList<Object>();
                    list.add(property);

                } else {
                    list = DynaBinder.to(property, List.class);
                }

                Map<String, String[]> nameMapParams = new HashMap<String, String[]>();
                for (Object obj : list) {
                    String param = DynaBinder.to(obj, String.class);
                    if (!KernelString.isEmpty(param)) {
                        String[] params = param.split(",");
                        nameMapParams.put(params[0], params);
                    }
                }

                if (!nameMapParams.isEmpty()) {
                    annotations = new ParamsAnnotations();
                    annotations.nameMapParams = nameMapParams;
                }
            }
        }

        return annotations;
    }

    public static ParamsAnnotations getParamsAnnotations(Map<String, Object> properties, String name) {
        ParamsAnnotations annotations = null;
        Object property = properties.get(name);
        if (property != null) {
            annotations = getParamsAnnotations(property);
            if (annotations == null) {
                properties.remove(name);

            } else if ((Object) annotations != properties) {
                properties.put(name, annotations);
            }
        }

        return annotations;
    }

    protected static Map<String, ParamsAnnotations> nameMapParamsAnnotations;

    protected static List<MatchParamsAnnotations> matchParamsAnnotationsList;

    protected static class MatchParamsAnnotations {

        protected Entry<String, KernelLang.IMatcherType> macherEntry;

        protected ParamsAnnotations paramsAnnotations;

    }

    protected static void loadParamsAnnotations() {
        if (nameMapParamsAnnotations == null) {
            Map<String, ParamsAnnotations> annotationsMap = new HashMap<String, ParamsAnnotations>();
            List<MatchParamsAnnotations> annotationsList = new ArrayList<MatchParamsAnnotations>();
            BeanConfig config = BeanFactoryUtils.getBeanConfig();
            File annotationsFile = new File(config.getClassPath() + "annotations");
            if (annotationsFile.exists()) {
                Map<String, Object> properties = new LinkedHashMap<String, Object>();
                BeanConfigImpl.readDirProperties(config, properties, annotationsFile, null);
                for (String name : properties.keySet()) {
                    ParamsAnnotations annotations = BeanConfigImpl.getParamsAnnotations(properties, name);
                    if (annotations != null) {
                        Entry<String, KernelLang.IMatcherType> macherEntry = KernelLang.MatcherType.getMatchEntry(name);
                        if (macherEntry.getValue() == MatcherType.NORMAL) {
                            annotationsMap.put(macherEntry.getKey(), annotations);

                        } else {
                            MatchParamsAnnotations paramsAnnotations = new MatchParamsAnnotations();
                            paramsAnnotations.macherEntry = macherEntry;
                            paramsAnnotations.paramsAnnotations = annotations;
                            annotations.matchFind = true;
                            annotationsList.add(0, paramsAnnotations);
                        }
                    }
                }
            }

            nameMapParamsAnnotations = annotationsMap;
            matchParamsAnnotationsList = annotationsList;
        }
    }

    public static ParamsAnnotations getMemberParamsAnnotations(String classNameMember, boolean findMatch) {
        loadParamsAnnotations();
        ParamsAnnotations paramsAnnotations = nameMapParamsAnnotations.get(classNameMember);
        if (paramsAnnotations == null && findMatch) {
            for (MatchParamsAnnotations matchParamsAnnotations : matchParamsAnnotationsList) {
                if (MatcherType.isMatch(classNameMember, matchParamsAnnotations.macherEntry)) {
                    paramsAnnotations = matchParamsAnnotations.paramsAnnotations;
                    break;
                }
            }
        }

        return paramsAnnotations;
    }

    public static @interface NoConfigure {

    }

    public static <T extends Annotation> T getTypeAnnotation(Class<?> type, Class<T> annotationClass) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(type.getName(), false);
        if (annotations != null) {
            T annotation = annotations.getAnnotation(annotationClass);
            if (annotation == null) {
                if (annotations.findAnnotation(NoConfigure.class)) {
                    return null;
                }

            } else {
                return annotation;
            }
        }

        return type.getAnnotation(annotationClass);
    }

    public static boolean findTypeAnnotation(Class<?> type, Class<? extends Annotation> annotationClass) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(type.getName(), false);
        if (annotations != null) {
            if (annotations.findAnnotation(annotationClass)) {
                return true;

            } else {
                if (annotations.findAnnotation(NoConfigure.class)) {
                    return false;
                }
            }
        }

        return type.getAnnotation(annotationClass) != null;
    }

    public static <T extends Annotation> T getFieldAnnotation(Field field, Class<T> annotationClass) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(field.getDeclaringClass() + "." + field.getName(), false);
        if (annotations != null) {
            T annotation = annotations.getAnnotation(annotationClass);
            if (annotation == null) {
                if (annotations.findAnnotation(NoConfigure.class)) {
                    return null;
                }

            } else {
                return annotation;
            }
        }

        return field.getAnnotation(annotationClass);
    }

    public static boolean findFieldAnnotation(Field field, Class<? extends Annotation> annotationClass) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(field.getDeclaringClass() + "." + field.getName(), false);
        if (annotations != null) {
            if (annotations.findAnnotation(annotationClass)) {
                return true;

            } else {
                if (annotations.findAnnotation(NoConfigure.class)) {
                    return false;
                }
            }
        }

        return field.getAnnotation(annotationClass) != null;
    }

    public static <T extends Annotation> T getMethodAnnotation(Method method, Class<T> annotationClass) {
        return getMethodAnnotation(method, annotationClass, false);
    }

    public static <T extends Annotation> T getMethodAnnotation(Method method, Class<T> annotationClass, boolean findMatch) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(method.getDeclaringClass() + "." + method.getName(), findMatch);
        if (annotations != null) {
            T annotation = annotations.getAnnotation(annotationClass);
            if (annotation == null) {
                if (findMatch && !annotations.matchFind) {
                    return getMethodAnnotation(method, annotationClass, false);
                }

                if (annotations.findAnnotation(NoConfigure.class)) {
                    return null;
                }

            } else {
                return annotation;
            }
        }

        return method.getAnnotation(annotationClass);
    }

    public static boolean findMethodAnnotation(Method method, Class<? extends Annotation> annotationClass) {
        return findMethodAnnotation(method, annotationClass, false);
    }

    public static boolean findMethodAnnotation(Method method, Class<? extends Annotation> annotationClass, boolean findMatch) {
        ParamsAnnotations annotations = getMemberParamsAnnotations(method.getDeclaringClass() + "." + method.getName(), findMatch);
        if (annotations != null) {
            if (annotations.findAnnotation(annotationClass)) {
                return true;

            } else {
                if (findMatch && !annotations.matchFind) {
                    return findMethodAnnotation(method, annotationClass, false);
                }

                if (annotations.findAnnotation(NoConfigure.class)) {
                    return false;
                }
            }
        }

        return method.getAnnotation(annotationClass) != null;
    }

    public static <T extends Annotation> T getAccessorAnnotation(UtilAccessor.Accessor accessor, Class<T> annotationClass) {
        Method getter = accessor.getGetter();
        if (getter == null) {
            Field field = accessor.getField();
            if (field == null) {
                return getMethodAnnotation(accessor.getSetter(), annotationClass);
            }

            return getFieldAnnotation(field, annotationClass);
        }

        return getMethodAnnotation(getter, annotationClass);
    }

}
