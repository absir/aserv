/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-17 下午4:25:30
 */
package com.absir.bean.inject;

import com.absir.bean.basis.*;
import com.absir.bean.config.*;
import com.absir.bean.core.*;
import com.absir.bean.inject.value.*;
import com.absir.core.base.Environment;
import com.absir.core.kernel.*;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelLang.ObjectEntry;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"unchecked", "rawtypes"})
@Basis
@Bean
public class InjectBeanFactory implements IBeanFactorySupport, IBeanDefineSupply, IBeanDefineAware,
        IBeanObjectProcessor, IBeanFactoryAware, IAdapterSupport, IBeanFactoryStarted, IBeanFactoryStopping {

    protected static class MatchInjectParams {

        protected Entry<String, KernelLang.IMatcherType> macherEntry;

        protected String[][] paramsAry;

    }

    protected static List<MatchInjectParams> matchInjectParamses;

    public static String[][] getInjectParamsAry(String classNameMember) {
        if (matchInjectParamses == null) {
            List<MatchInjectParams> paramses = new ArrayList<MatchInjectParams>();
            BeanConfig config = BeanFactoryUtils.getBeanConfig();
            File injectsFile = new File(config.getClassPath() + "injects");
            if (injectsFile.exists()) {
                Map<String, Object> properties = new LinkedHashMap<String, Object>();
                BeanConfigImpl.readDirProperties(config, properties, injectsFile, null);
                for (String name : properties.keySet()) {
                    String[][] paramsAry = BeanConfigImpl.getParamsAry(properties, name, false);
                    if (paramsAry != null) {
                        MatchInjectParams params = new MatchInjectParams();
                        params.macherEntry = KernelLang.MatcherType.getMatchEntry(name);
                        params.paramsAry = paramsAry;
                        paramses.add(0, params);
                    }
                }
            }

            matchInjectParamses = paramses;
        }

        if (!matchInjectParamses.isEmpty()) {
            for (MatchInjectParams matchInjectParams : matchInjectParamses) {
                if (KernelLang.MatcherType.isMatch(classNameMember, matchInjectParams.macherEntry)) {
                    return matchInjectParams.paramsAry;
                }
            }
        }

        return null;
    }

    protected static final Comparator<Entry<Object, InjectInvoker>> OBJECT_INVOKER_COMPARATOR = new Comparator<Entry<Object, InjectInvoker>>() {

        @Override
        public int compare(Entry<Object, InjectInvoker> o1, Entry<Object, InjectInvoker> o2) {
            return o1.getValue().getOrder() - o2.getValue().getOrder();
        }
    };

    private static InjectBeanFactory Instance;

    private final Set<InjectObserver> injectObservers = Collections
            .newSetFromMap(new ConcurrentHashMap<InjectObserver, Boolean>());

    private final Set<InjectObserverObject> injectObserverObjects = Collections
            .newSetFromMap(new ConcurrentHashMap<InjectObserverObject, Boolean>());

    private final Map<InjectObserverSoftObject, List<SoftReference<Object>>> injectInvokerObserverSoftCaches = new ConcurrentHashMap<InjectObserverSoftObject, List<SoftReference<Object>>>();

    private final Map<Class<?>, Object[]> injectInvokerCaches = new HashMap<Class<?>, Object[]>();

    private boolean beanDefining;

    private IMethodDefine[] methodDefines;

    @Inject(type = InjectType.Selectable)
    private ITypeSupport[] typeSupports = new ITypeSupport[]{};

    private IFieldSupport[] fieldSupports = new IFieldSupport[]{new IFieldSupport() {

        @Override
        public InjectInvoker getInjectInvoker(BeanScope beanScope, BeanDefine beanDefine, Field field) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                return InjectFieldOrders.getInjectField(field, inject.value(), inject.type(),
                        field.getAnnotation(Orders.class));
            }

            Value value = field.getAnnotation(Value.class);
            if (value != null) {
                return new InjectValue(field, value);
            }

            if (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType())) {
                InjectConcat injectConcat = field.getAnnotation(InjectConcat.class);
                if (injectConcat != null) {
                    return new InjectArrayConcat(field, injectConcat);
                }
            }

            return null;
        }
    },

    };

    private Map<BeanMethod, Method> beanMethods = new HashMap<BeanMethod, Method>();

    private List<Entry<Object, InjectInvoker>> startedInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();

    private List<Entry<Object, InjectInvoker>> stoppingInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();

    private IMethodInject[] methodInjects = new IMethodInject[]{
            new IMethodInject<Started>() {

                @Override
                public boolean isRequired() {
                    return true;
                }

                @Override
                public Started getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
                    return method.getAnnotation(Started.class);
                }

                @Override
                public void setInjectMethod(Started inject, Method method, Object beanObject, InjectMethod injectMethod) {
                    startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectMethod));
                }

            },

            new IMethodInject<Stopping>() {

                @Override
                public boolean isRequired() {
                    return true;
                }

                @Override
                public Stopping getInjects(BeanScope beanScope, BeanDefine beanDefine, Method method) {
                    return method.getAnnotation(Stopping.class);
                }

                @Override
                public void setInjectMethod(Stopping inject, Method method, Object beanObject,
                                            InjectMethod injectMethod) {
                    stoppingInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectMethod));
                }
            },

    };

    private IMethodSupport[] methodSupports = new IMethodSupport[]{new IMethodSupport<Object[]>() {

        @Override
        public Object[] getInject(BeanScope beanScope, BeanDefine beanDefine, Method method) {
            int length = methodInjects.length;
            Object[] injects = new Object[length + 3];
            boolean injected = false;
            boolean required = true;
            for (int i = 0; i < length; i++) {
                IMethodInject methodInject = methodInjects[i];
                Object inject = methodInject.getInjects(beanScope, beanDefine, method);
                if (inject != null) {
                    injects[i] = inject;
                    if (!injected) {
                        injected = true;
                    }

                    if (required && !methodInject.isRequired()) {
                        required = false;
                    }
                }
            }

            Inject inject = method.getAnnotation(Inject.class);
            if (inject == null && !injected) {
                return null;
            }

            injects[length] = required;
            injects[length + 1] = inject;
            injects[length + 2] = method.getAnnotation(InjectOrder.class);
            return injects;
        }

        @Override
        public InjectInvoker getInjectInvoker(Object[] injects, Method method, Method beanMethod, Object beanObject,
                                              Map<Method, Set<Object>> methodMapInjects) {
            int length = methodInjects.length;
            boolean required = (Boolean) injects[length];
            Inject inject = (Inject) injects[length + 1];
            String value = inject == null ? null : inject.value();
            InjectType type = inject == null ? required ? InjectType.Required : InjectType.Selectable : inject.type();
            InjectOrder injectOrder = (InjectOrder) injects[length + 2];
            InjectMethod injectMethod = injectOrder == null || injectOrder.value() == 0
                    ? new InjectMethod(method, beanMethod, value, type)
                    : new InjectMethodOrder(method, beanMethod, value, type, injectOrder.value());
            for (int i = 0; i < length; i++) {
                Object inj = injects[i];
                if (inj != null) {
                    IMethodInject methodInject = methodInjects[i];
                    if (methodMapInjects != null) {
                        Set<Object> mInjects = methodMapInjects.get(beanMethod);
                        if (mInjects == null) {
                            mInjects = new HashSet<Object>();
                            mInjects.add(methodInject);
                            methodMapInjects.put(beanMethod, mInjects);

                        } else if (!mInjects.add(methodInject)) {
                            continue;
                        }
                    }

                    try {
                        methodInject.setInjectMethod(inj, method, beanObject, injectMethod);

                    } catch (Throwable e) {
                        throw new RuntimeException(
                                "Can not inject " + methodInjects[i] + "=>" + beanObject + '.' + method, e);
                    }
                }
            }

            return inject == null ? null : injectMethod;
        }
    },

    };

    public InjectBeanFactory(BeanFactory beanFactory) {
        Instance = this;
        beanDefining = true;
        methodDefines = KernelCollection.toArray(beanFactory.getBeanObjects(IMethodDefine.class), IMethodDefine.class);
    }

    public static InjectBeanFactory getInstance() {
        return Instance;
    }

    public Method getBeanMethod(Class<?> beanType, Method method) {
        if (Modifier.isPrivate(method.getModifiers()) || Modifier.isFinal(method.getModifiers())) {
            method.setAccessible(true);
            return method;
        }

        if (beanDefining) {
            BeanMethod beanMethod = new BeanMethod(beanType, method);
            method = beanMethods.get(beanMethod);
            if (method == null) {
                synchronized (beanMethods) {
                    method = beanMethods.get(beanMethod);
                    if (method == null) {
                        method = KernelReflect.declaredMethod(beanMethod.getBeanType(),
                                beanMethod.getMethod().getName(), beanMethod.getMethod().getParameterTypes());
                        if (method == null) {
                            return null;
                        }

                        beanMethods.put(beanMethod, method);
                    }
                }
            }

        } else {
            method = KernelReflect.declaredMethod(beanType, method.getName(), method.getParameterTypes());
        }

        return method;
    }

    @Inject(type = InjectType.Selectable)
    private void setFieldSupports(IFieldSupport[] fieldSupports) {
        this.fieldSupports = KernelArray.concat(this.fieldSupports, fieldSupports);
    }

    @Inject(type = InjectType.Selectable)
    private void setMethodSupports(IMethodSupport[] methodSupports) {
        this.methodSupports = KernelArray.concat(this.methodSupports, methodSupports);
    }

    @Inject(type = InjectType.Selectable)
    private void setMethodSupports(IMethodInject[] methodInjects) {
        this.methodInjects = KernelArray.concat(this.methodInjects, methodInjects);
    }

    @Override
    public boolean supports(BeanFactoryImpl beanFactory) {
        beanDefining = true;
        return true;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        Bean bean = beanType.getAnnotation(Bean.class);
        List<BeanDefine> beanDefines = null;
        if (bean != null || beanType.getAnnotation(Configure.class) != null) {
            BeanDefine beanDefine = bean == null || BeanDefineOriginal.isAbstractBeanType(beanType) ? null
                    : new InjectBeanDefine(new BeanDefineType(bean.value(), beanType), bean.scope());
            beanDefines = getBeanDefines(beanFactory, beanType, beanDefine);
        }

        if (beanDefines == null || beanDefines.isEmpty()) {
            if (beanType.getAnnotation(Inject.class) != null) {
                startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(null, new InjectBeanType(beanType)));
            }
        }

        return beanDefines;
    }

    public List<BeanDefine> getBeanDefines(final BeanFactoryImpl beanFactory, final Class<?> beanType,
                                           final BeanDefine beanDefine) {
        final List<BeanDefine> beanDefines = new ArrayList<BeanDefine>();
        if (beanDefine != null) {
            beanDefines.add(BeanDefineObject.getBeanDefine(beanType, beanDefine));
        }

        KernelReflect.doWithDeclaredFields(beanType, new CallbackBreak<Field>() {

            @Override
            public void doWith(Field template) throws BreakException {
                if (Modifier.isStatic(template.getModifiers()) && !Modifier.isFinal(template.getModifiers())
                        && template.getAnnotations().length > 0) {
                    InjectAdapter.inject(template);
                }
            }
        });

        final Set<Method> beanMethodSet = new HashSet<Method>();
        KernelReflect.doWithDeclaredMethods(beanType, new CallbackBreak<Method>() {

            @Override
            public void doWith(Method template) throws BreakException {
                Method beanMethod = null;
                Bean bean = null;
                if (Modifier.isStatic(template.getModifiers())) {
                    bean = template.getAnnotation(Bean.class);
                    if (bean == null) {
                        if (template.getAnnotations().length > 0) {
                            InjectAdapter.inject(template);
                        }

                        return;
                    }

                } else {
                    for (IMethodDefine methodDefine : methodDefines) {
                        Object define = methodDefine.getDefine(beanType, template, beanDefine);
                        if (define != null) {
                            if (beanMethod == null) {
                                beanMethod = getBeanMethod(beanType, template);
                                if (beanMethod == null) {
                                    return;

                                } else {
                                    if (!beanMethodSet.add(beanMethod)) {
                                        return;
                                    }
                                }
                            }

                            methodDefine.setDefine(define, beanType, beanMethod, template, beanDefine);
                        }
                    }
                }

                if (bean == null) {
                    bean = template.getAnnotation(Bean.class);
                    if (bean == null) {
                        return;
                    }
                }

                String beanName = BeanDefineMethod.getBeanName(bean.value(), template);
                BeanDefine beanDefineMethod = beanFactory.getBeanDefine(beanName);
                if (beanDefineMethod == null
                        || !template.getReturnType().isAssignableFrom(beanDefineMethod.getBeanType())) {
                    if (beanMethod == null) {
                        beanMethod = getBeanMethod(beanType, template);
                        if (beanMethod == null) {
                            return;

                        } else {
                            if (!beanMethodSet.add(beanMethod)) {
                                return;
                            }
                        }
                    }

                    beanDefineMethod = new InjectBeanDefine((new BeanDefineMethod(beanName, beanDefine, beanMethod)),
                            bean.scope());
                    beanDefines.add(beanDefineMethod);
                }
            }
        });

        return beanDefines;
    }

    public void getMethodEntries(final Class<?> beanType, final IMethodEntry methodEntry) {
        final Set<Method> beanMethodSet = new HashSet<Method>();
        KernelReflect.doWithDeclaredMethods(beanType, new CallbackBreak<Method>() {

            @Override
            public void doWith(Method template) throws BreakException {
                if (!Modifier.isStatic(template.getModifiers())) {
                    Object define = methodEntry.getMethod(beanType, template);
                    if (define != null) {
                        Method beanMethod = getBeanMethod(beanType, template);
                        if (beanMethod != null) {
                            if (!beanMethodSet.add(beanMethod)) {
                                return;
                            }

                            methodEntry.setMethodEntry(define, beanType, beanMethod, template);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void registerBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
        changedBeanDefine(beanFactory, beanDefine, true);
    }

    @Override
    public void unRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
        changedBeanDefine(beanFactory, beanDefine, false);
    }

    @Override
    public void replaceRegisterBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine) {
        changedBeanDefine(beanFactory, beanDefine, true);
    }

    private void changedBeanDefine(BeanFactoryImpl beanFactory, BeanDefine beanDefine, boolean register) {
        if (injectObservers.size() > 0) {
            for (InjectObserver injectObserver : injectObservers) {
                injectObserver.changed(beanFactory, beanDefine, null, register);
            }
        }

        if (injectObserverObjects.size() > 0) {
            for (InjectObserverObject injectObserverObject : injectObserverObjects) {
                injectObserverObject.changed(beanFactory, beanDefine, register);
            }
        }

        if (injectInvokerObserverSoftCaches.size() > 0) {
            for (Entry<InjectObserverSoftObject, List<SoftReference<Object>>> entry : injectInvokerObserverSoftCaches
                    .entrySet()) {
                entry.getKey().changed(beanFactory, beanDefine, entry.getValue(), register);
            }
        }
    }

    @Override
    public void processBeanObject(final BeanFactory beanFactory, final BeanScope beanScope, final BeanDefine beanDefine,
                                  final Object beanObject, final Object beanProxy) {
        final Class<?> beanType = beanObject.getClass();
        Object[] injectInvokers = beanScope == BeanScope.PROTOTYPE ? injectInvokerCaches.get(beanType) : null;
        if (injectInvokers == null) {
            synchronized (beanType) {
                if (beanScope == BeanScope.PROTOTYPE) {
                    injectInvokers = injectInvokerCaches.get(beanType);
                }

                if (injectInvokers == null) {
                    final Map<Method, Set<IMethodSupport>> methodMapSupports = new HashMap<Method, Set<IMethodSupport>>();
                    final Map<Method, Set<Object>> methodMapInjects = new HashMap<Method, Set<Object>>();
                    final List<InjectInvoker> iInvokerList = new ArrayList<InjectInvoker>();
                    final List<InjectInvoker> iInvokerScope = new ArrayList<InjectInvoker>();
                    final List<InjectInvoker> pInvokerList = new ArrayList<InjectInvoker>();
                    final List<InjectInvoker> pInvokerScope = new ArrayList<InjectInvoker>();
                    KernelReflect.doWithClasses(beanType, new CallbackBreak<Class<?>>() {

                        @Override
                        public void doWith(Class<?> template) throws BreakException {
                            if (typeSupports != null) {
                                for (ITypeSupport typeSupport : typeSupports) {
                                    InjectInvoker injectInvoker = typeSupport.getInjectInvoker(beanScope, beanDefine,
                                            template);
                                    if (injectInvoker != null) {
                                        iInvokerScope.add(injectInvoker);
                                    }
                                }
                            }

                            for (Field field : template.getDeclaredFields()) {
                                if (!(Modifier.isStatic(field.getModifiers())
                                        || Modifier.isFinal(field.getModifiers()))) {
                                    field.setAccessible(true);
                                    for (IFieldSupport fieldSupport : fieldSupports) {
                                        InjectInvoker injectInvoker = fieldSupport.getInjectInvoker(beanScope,
                                                beanDefine, field);
                                        if (injectInvoker != null) {
                                            iInvokerScope.add(injectInvoker);
                                        }
                                    }
                                }
                            }

                            for (Method method : template.getDeclaredMethods()) {
                                if (!Modifier.isStatic(method.getModifiers())) {
                                    Method beanMethod = null;
                                    for (IMethodSupport methodSupport : methodSupports) {
                                        boolean proxy = false;
                                        Object inject = methodSupport.getInject(beanScope, beanDefine, method);
                                        if (inject != null) {
                                            if (beanMethod == null) {
                                                method = getBeanMethod(beanType, method);
                                                if (method == null) {
                                                    break;
                                                }

                                                beanMethod = getBeanMethod(beanProxy.getClass(), method);
                                                if (beanMethod == null) {
                                                    beanMethod = method;

                                                } else {
                                                    proxy = true;
                                                }
                                            }

                                            Set<IMethodSupport> supports = methodMapSupports.get(beanMethod);
                                            if (supports == null) {
                                                supports = new HashSet<IMethodSupport>();
                                                supports.add(methodSupport);
                                                methodMapSupports.put(beanMethod, supports);

                                            } else {
                                                if (!supports.add(methodSupport)) {
                                                    continue;
                                                }
                                            }

                                            InjectInvoker injectInvoker = methodSupport.getInjectInvoker(inject, method,
                                                    beanMethod, proxy ? beanProxy : beanObject, methodMapInjects);
                                            if (injectInvoker != null) {
                                                if (proxy) {
                                                    pInvokerScope.add(injectInvoker);

                                                } else {
                                                    iInvokerScope.add(injectInvoker);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            iInvokerList.addAll(0, iInvokerScope);
                            iInvokerScope.clear();
                            pInvokerList.addAll(0, pInvokerScope);
                            pInvokerScope.clear();
                        }
                    });

                    // 注入排序
                    KernelList.sortOrderable(iInvokerList);
                    KernelList.sortOrderable(pInvokerList);
                    int iSize = iInvokerList.size();
                    int pSize = pInvokerList.size();
                    injectInvokers = iSize == 0 && pSize == 0 ? KernelLang.NULL_OBJECTS
                            : new Object[]{iSize == 0 ? KernelLang.NULL_LIST_SET : iInvokerList,
                            pSize == 0 ? KernelLang.NULL_LIST_SET : pInvokerList};
                    if (beanScope == BeanScope.PROTOTYPE) {
                        injectInvokerCaches.put(beanType, injectInvokers);
                        InjectObserverSoftObject injectObserverSoftObject = new InjectObserverSoftObject(beanType);
                        for (InjectInvoker injectInvoker : iInvokerList) {
                            if (injectInvoker instanceof InjectInvokerObserver) {
                                injectObserverSoftObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
                            }
                        }

                        if (injectObserverSoftObject.injectObservers != null) {
                            injectInvokerObserverSoftCaches.put(injectObserverSoftObject,
                                    new LinkedList<SoftReference<Object>>());
                        }

                        injectObserverSoftObject = new InjectObserverSoftObject(beanProxy.getClass());
                        for (InjectInvoker injectInvoker : pInvokerList) {
                            if (injectInvoker instanceof InjectInvokerObserver) {
                                injectObserverSoftObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
                            }
                        }

                        if (injectObserverSoftObject.injectObservers != null) {
                            injectInvokerObserverSoftCaches.put(injectObserverSoftObject,
                                    new LinkedList<SoftReference<Object>>());
                        }
                    }
                }
            }
        }

        // 清除缓存
        BeanDefineDiscover.clear();

        if (injectInvokers != KernelLang.NULL_OBJECTS) {
            for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[0]) {
                injectInvoker.invoke(beanFactory, beanObject);
            }

            for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[1]) {
                injectInvoker.invoke(beanFactory, beanProxy);
            }
        }

        if (beanScope == BeanScope.PROTOTYPE) {
            List<SoftReference<Object>> beanObjects = injectInvokerObserverSoftCaches.get(beanType);
            if (beanObjects != null) {
                synchronized (beanObjects) {
                    beanObjects.add(new SoftReference<Object>(beanObject));
                }
            }

            beanObjects = injectInvokerObserverSoftCaches.get(beanProxy.getClass());
            if (beanObjects != null) {
                synchronized (beanObjects) {
                    beanObjects.add(new SoftReference<Object>(beanProxy));
                }
            }

        } else {
            if (!injectObserverObjects.contains(beanObject)) {
                if (injectInvokers != KernelLang.NULL_OBJECTS) {
                    InjectObserverObject injectObserverObject = new InjectObserverObject(beanObject);
                    for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[0]) {
                        if (injectInvoker instanceof InjectInvokerObserver) {
                            injectObserverObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
                        }
                    }

                    if (injectObserverObject.injectObservers != null) {
                        injectObserverObjects.add(injectObserverObject);
                    }

                    injectObserverObject = new InjectObserverObject(beanProxy);
                    for (InjectInvoker injectInvoker : (List<InjectInvoker>) injectInvokers[1]) {
                        if (injectInvoker instanceof InjectInvokerObserver) {
                            injectObserverObject.addInjectInvoker((InjectInvokerObserver) injectInvoker);
                        }
                    }

                    if (injectObserverObject.injectObservers != null) {
                        injectObserverObjects.add(injectObserverObject);
                    }
                }
            }
        }
    }

    @Override
    public void adapter(BeanFactory beanFactory, Object beanObject, Collection<Field> fields,
                        Collection<Method> methods) {
        BeanDefine beanDefine = new BeanDefineOriginal(new BeanDefineType(InjectAdapter.class));
        InjectAdapter injectAdapter = (InjectAdapter) beanDefine.getBeanObject(beanFactory);
        for (Field field : fields) {
            for (IFieldSupport fieldSupport : fieldSupports) {
                InjectInvoker injectInvoker = fieldSupport.getInjectInvoker(BeanScope.SINGLETON, beanDefine, field);
                if (injectInvoker != null) {
                    injectInvoker.invoke(beanFactory, beanObject);
                    if (injectInvoker instanceof InjectInvokerObserver) {
                        InjectObserver injectObserver = ((InjectInvokerObserver) injectInvoker).getInjectObserver();
                        if (injectObserver != null) {
                            injectObservers.add(injectObserver);
                        }
                    }
                }
            }
        }

        for (Method method : methods) {
            for (IMethodSupport methodSupport : methodSupports) {
                Object inject = methodSupport.getInject(BeanScope.SINGLETON, beanDefine, method);
                if (inject != null) {
                    InjectInvoker injectInvoker = methodSupport.getInjectInvoker(inject, method, method, injectAdapter,
                            null);
                    if (injectInvoker != null) {
                        injectInvoker.invoke(beanFactory, beanObject);
                        if (injectInvoker instanceof InjectInvokerObserver) {
                            InjectObserver injectObserver = ((InjectInvokerObserver) injectInvoker).getInjectObserver();
                            if (injectObserver != null) {
                                injectObservers.add(injectObserver);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void beforeRegister(BeanFactoryImpl beanFactory) {
    }

    @Override
    public void afterRegister(BeanFactoryImpl beanFactory) {
        InjectAdapter injectAdapter = InjectAdapter.getInstance();
        if (injectAdapter != null) {
            for (IAdapterSupport adapterSupport : (List<IAdapterSupport>) (List) beanFactory
                    .getBeanObjects(IAdapterSupport.class)) {
                adapterSupport.adapter(beanFactory, injectAdapter, injectAdapter.fields, injectAdapter.methods);
            }
        }

        InjectAdapter.clear();

        for (BeanDefine beanDefine : beanFactory.getBeanDefines()) {
            if (beanDefine.getBeanScope() == BeanScope.SINGLETON) {
                beanDefine.getBeanObject(beanFactory);
            }
        }
    }

    public void addStated(Object beanObject, InjectInvoker injectInvoker) {
        if (beanDefining) {
            startedInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectInvoker));
        }
    }

    public void addStopping(Object beanObject, InjectInvoker injectInvoker) {
        stoppingInjectInvokers.add(new ObjectEntry<Object, InjectInvoker>(beanObject, injectInvoker));
    }

    @Override
    public void started(BeanFactory beanFactory) {
        if (startedInjectInvokers.size() > 0) {
            List<Entry<Object, InjectInvoker>> injectInvokers = startedInjectInvokers;
            startedInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();
            Collections.sort(injectInvokers, OBJECT_INVOKER_COMPARATOR);
            for (Entry<Object, InjectInvoker> entry : injectInvokers) {
                try {
                    entry.getValue().invoke(beanFactory, entry.getKey());

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        beanMethods.clear();
        beanDefining = false;
        BeanDefineDiscover.clear();
        beanFactory.unRegisterBeanType(InjectOnce.class);
        beanFactory.unRegisterWithoutBeanType(InjectRetain.class, IBeanDefineSupply.class, IBeanDefineAware.class,
                IBeanDefineProcessor.class, IBeanObjectProcessor.class);
    }

    @Override
    public void stopping(BeanFactory beanFactory) {
        Environment.setActive(false);
        if (stoppingInjectInvokers.size() > 0) {
            List<Entry<Object, InjectInvoker>> injectInvokers = stoppingInjectInvokers;
            stoppingInjectInvokers = new ArrayList<Entry<Object, InjectInvoker>>();
            Collections.sort(injectInvokers, OBJECT_INVOKER_COMPARATOR);
            for (Entry<Object, InjectInvoker> entry : injectInvokers) {
                try {
                    entry.getValue().invoke(beanFactory, entry.getKey());

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        Environment.setStarted(false);
    }
}
