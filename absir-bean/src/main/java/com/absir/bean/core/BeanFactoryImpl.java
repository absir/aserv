/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-6-13 下午2:27:29
 */
package com.absir.bean.core;

import com.absir.bean.basis.*;
import com.absir.bean.config.IBeanDefineAware;
import com.absir.bean.config.IBeanDefineProcessor;
import com.absir.bean.config.IBeanObjectProcessor;
import com.absir.bean.config.IBeanSoftReferenceAware;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.FilterTemplate;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelList.Orderable;
import com.absir.core.kernel.KernelString;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class BeanFactoryImpl implements BeanFactory {

    private static BeanFactoryImpl Instance;

    private final BeanConfig beanConfig;

    private final Map<String, BeanDefine> beanNameDefineMap;

    private final List<IBeanDefineAware> beanDefineAwares;

    private final List<IBeanDefineProcessor> beanDefineProcessors;

    private final List<IBeanObjectProcessor> beanObjectProcessors;

    private final List<IBeanSoftReferenceAware> beanSoftReferenceAwares;

    private final Set<BeanSoftReference> beanSoftReferenceSet = Collections
            .newSetFromMap(new ConcurrentHashMap<BeanSoftReference, Boolean>());

    private final Map<String, Stack<BeanDefine>> beanNameDefineMapStack = new ConcurrentHashMap<String, Stack<BeanDefine>>();

    private BeanFactoryImpl beanFactoryImpl;

    private List<BeanSupply> beanSupplies;

    protected BeanFactoryImpl(BeanConfig beanConfig, ConcurrentHashMap<String, BeanDefine> beanNameDefineMap,
                              List<IBeanDefineAware> beanDefineAwares, List<IBeanDefineProcessor> beanDefineProcessors,
                              List<IBeanObjectProcessor> beanObjectProcessors, List<IBeanSoftReferenceAware> beanSoftReferenceAwares) {
        beanFactoryImpl = Instance;
        Instance = this;
        this.beanConfig = beanConfig;
        this.beanNameDefineMap = beanNameDefineMap;
        this.beanDefineAwares = beanDefineAwares;
        this.beanDefineProcessors = beanDefineProcessors;
        this.beanObjectProcessors = beanObjectProcessors;
        this.beanSoftReferenceAwares = beanSoftReferenceAwares;
    }

    protected static BeanFactoryImpl getInstance() {
        return Instance;
    }

    public static Class<?> getBeanType(Class<?> beanClass) {
        if (beanClass.isArray()) {
            return beanClass.getComponentType();
        }

        return beanClass;
    }

    public static Class<?> getBeanType(Type beanType) {
        Class<?> beanClass = KernelClass.rawClass(beanType);
        if (Collection.class.isAssignableFrom(beanClass)) {
            Type[] types = KernelClass.typeArguments(beanType);
            if (types.length == 1) {
                return KernelClass.rawClass(types[0]);
            }

        } else if (Map.class.isAssignableFrom(beanClass)) {
            Type[] types = KernelClass.typeArguments(beanType);
            if (types.length == 2) {
                return KernelClass.rawClass(types[1]);
            }
        }

        return getBeanType(beanClass);
    }

    public static BeanFactoryImpl getBeanFactoryImpl(BeanFactory beanFactory) {
        while (beanFactory != null) {
            if (beanFactory instanceof BeanFactoryImpl) {
                return (BeanFactoryImpl) beanFactory;
            }

            if (beanFactory instanceof BeanFactoryWrapper) {
                beanFactory = ((BeanFactoryWrapper) beanFactory).getBeanFactory();

            } else {
                break;
            }
        }

        return null;
    }

    public static <T extends BeanDefine> T getBeanDefine(BeanDefine beanDefine, Class<T> beanDefineClass) {
        while (beanDefine != null) {
            if (beanDefineClass.isAssignableFrom(beanDefine.getClass())) {
                return (T) beanDefine;

            } else if (beanDefine instanceof BeanDefineWrapper) {
                beanDefine = ((BeanDefineWrapper) beanDefine).beanDefine;

            } else {
                break;
            }
        }

        return null;
    }

    public static boolean containBeanDefine(BeanDefine beanDefine, BeanDefine define) {
        while (beanDefine != null) {
            if (beanDefine == define) {
                return true;
            }

            if (beanDefine instanceof BeanDefineWrapper) {
                beanDefine = ((BeanDefineWrapper) beanDefine).getBeanDefine();

            } else {
                break;
            }
        }

        return false;
    }

    public void setBeanFactoryImpl(BeanFactoryImpl beanFactoryImpl) {
        BeanFactoryImpl self = this;
        while (true) {
            BeanFactoryImpl parent = self.beanFactoryImpl;
            if (parent == null) {
                self.beanFactoryImpl = beanFactoryImpl;
                break;
            }

            self = parent;
        }
    }

    public void addBeanSupply(BeanSupply beanSupply) {
        if (beanSupplies == null) {
            beanSupplies = new ArrayList<BeanSupply>();
        }

        beanSupplies.add(beanSupply);
    }

    @Override
    public BeanConfig getBeanConfig() {
        return beanConfig;
    }

    @Override
    public Object getBeanObject(String beanName) {
        BeanDefine beanDefine = getBeanDefine(beanName);
        if (beanDefine != null) {
            Object beanObject = beanDefine.getBeanObject(this);
            if (beanObject == null) {
                unRegisterBeanDefine(beanDefine);

            } else {
                return beanObject;
            }
        }

        if (beanSupplies != null) {
            for (BeanSupply beanSupply : beanSupplies) {
                Object beanObject = beanSupply.getBeanObject(beanName);
                if (beanObject != null) {
                    return beanObject;
                }
            }
        }

        return beanFactoryImpl == null ? null : beanFactoryImpl.getBeanObject(beanName);
    }

    @Override
    public <T> T getBeanObject(Class<T> beanType) {
        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            BeanDefine beanDefine = iterator.next().getValue();
            if (beanType.isAssignableFrom(beanDefine.getBeanType())) {
                Object beanObject = beanDefine.getBeanObject(this);
                if (beanObject == null) {
                    iterator.remove();

                } else {
                    return (T) beanObject;
                }
            }
        }

        if (beanSupplies != null) {
            for (BeanSupply beanSupply : beanSupplies) {
                T beanObject = beanSupply.getBeanObject(beanType);
                if (beanObject != null) {
                    return beanObject;
                }
            }
        }

        return beanFactoryImpl == null ? null : beanFactoryImpl.getBeanObject(beanType);
    }

    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType) {
        return getBeanObject(beanName, beanType, false);
    }

    @Override
    public <T> T getBeanObject(String beanName, Class<T> beanType, boolean forcible) {
        if (beanType.isArray()) {
            List<Object> beanObjects = getBeanObjects((Class<Object>) beanType.getComponentType());
            if (beanObjects.isEmpty()) {
                if (forcible) {
                    throw new RuntimeException(
                            "BeanName = " + beanName + " is " + beanObjects + " not match " + beanType);
                }
            }

            return DynaBinder.to(beanObjects, beanType);
        }

        if (KernelString.isEmpty(beanName)) {
            beanName = null;
        }

        Object beanObject = beanName == null ? getBeanObject(beanType) : getBeanObject(beanName);
        if (beanObject == null || !beanType.isAssignableFrom(beanObject.getClass())) {
            beanObject = beanName == null ? null : getBeanObject(null, beanName, beanType, -1.0f);
        }

        if (beanObject == null && forcible) {
            throw new RuntimeException("BeanName = " + beanName + " is " + beanObject + " not match " + beanType);
        }

        return (T) beanObject;
    }

    private Object getBeanObject(Object beanObject, String beanName, Class<?> beanType, float max) {
        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            BeanDefine beanDefine = iterator.next().getValue();
            if (beanType.isAssignableFrom(beanDefine.getBeanType())) {
                Object object = beanDefine.getBeanObject(this);
                if (object == null) {
                    iterator.remove();
                    unRegisterBeanDefine(beanDefine);

                } else {
                    float similar = KernelString.similar(beanName, beanDefine.getBeanName());
                    if (similar > max) {
                        if (similar >= 1.0f) {
                            return object;
                        }

                        max = similar;
                        beanObject = object;
                    }
                }
            }
        }

        if (beanObject == null) {
            if (beanSupplies != null) {
                for (BeanSupply beanSupply : beanSupplies) {
                    beanObject = beanSupply.getBeanObject(beanName, beanType);
                    if (beanObject != null) {
                        return beanObject;
                    }
                }
            }
        }

        if (beanFactoryImpl != null) {
            beanObject = beanFactoryImpl.getBeanObject(beanObject, beanName, beanType, max);
        }

        return beanObject;
    }

    @Override
    public Object getBeanObject(String beanName, Type beanType, boolean forcible) {
        Class<?> beanClass = KernelClass.rawClass(beanType);
        if (Collection.class.isAssignableFrom(beanClass)) {
            Type[] types = KernelClass.typeArguments(beanType);
            if (types.length == 0) {
                List<Object> beanObjects = getBeanObjects(KernelClass.rawClass(types[0]));
                if (forcible && beanObjects.isEmpty()) {
                    throw new RuntimeException(
                            "BeanName = " + beanName + " is " + beanObjects + " not match " + beanType);
                }

                return DynaBinder.to(beanObjects, beanClass);
            }

        } else if (Map.class.isAssignableFrom(beanClass)) {
            Type[] types = KernelClass.typeArguments(beanType);
            if (types.length == 2) {
                Map<String, Object> beanDefineMap = getBeanObjectMap(getBeanDefineMap(KernelClass.rawClass(types[1])));
                if (forcible && beanDefineMap.isEmpty()) {
                    throw new RuntimeException(
                            "BeanName = " + beanName + " is " + beanDefineMap + " not match " + beanType);
                }

                if (beanName != null) {
                    String beanNamePrefix = beanName;
                    int beanNamePrefixLength = beanNamePrefix.length();
                    if (beanNamePrefixLength > 0 && !beanDefineMap.isEmpty()) {
                        if (beanNamePrefixLength == 1 && "*".equals(beanNamePrefix)) {
                            beanNamePrefixLength = 0;

                        } else {
                            beanNamePrefix = KernelString.unCapitalize(beanNamePrefix);
                        }

                        Map<String, Object> beanDefines = new HashMap<String, Object>();
                        if (beanNamePrefixLength > 0) {
                            for (Entry<String, Object> entry : beanDefineMap.entrySet()) {
                                beanName = entry.getKey();
                                int length = beanName.length();
                                if (length > 7 && beanName.endsWith("Service")) {
                                    beanName = KernelString.capitalize(beanName.substring(0, length - 7));

                                } else if (beanNamePrefixLength > 0 && beanNamePrefixLength < beanName.length()
                                        && beanName.startsWith(beanNamePrefix)) {
                                    beanName = beanName.substring(beanNamePrefixLength);
                                }

                                beanDefines.put(beanName, entry.getValue());
                            }
                        }

                        beanDefineMap = beanDefines;
                    }
                }

                return DynaBinder.to(beanDefineMap, beanClass);
            }
        }

        return getBeanObject(beanName, beanClass, forcible);
    }

    @Override
    public <T> List<T> getBeanObjects(Class<T> beanType) {
        return (List<T>) getBeanObjects(getBeanDefines(beanType), beanType);
    }

    private <T> List<T> getBeanObjects(List<BeanDefine> beanDefines, Class<T> beanType) {
        List<T> beanObjects = new ArrayList<T>();
        for (BeanDefine beanDefine : beanDefines) {
            Object beanObject = beanDefine.getBeanObject(this);
            if (beanObject == null) {
                unRegisterBeanDefine(beanDefine);

            } else {
                beanObjects.add((T) beanObject);
            }
        }

        if (Orderable.class.isAssignableFrom(beanType)) {
            KernelList.sortOrderable((List<Orderable>) beanObjects);
        }

        if (beanSupplies != null) {
            for (BeanSupply beanSupply : beanSupplies) {
                Collection<T> beans = beanSupply.getBeanObjects(beanType);
                if (beans != null) {
                    beanObjects.addAll(beans);
                }
            }
        }

        return beanObjects;
    }

    private Map<String, Object> getBeanObjectMap(Map<String, BeanDefine> beanDefineMap) {
        Iterator<Entry<String, BeanDefine>> iterator = beanDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, BeanDefine> entry = iterator.next();
            BeanDefine beanDefine = entry.getValue();
            Object beanObject = beanDefine.getBeanObject(this);
            if (beanObject == null) {
                iterator.remove();
                unRegisterBeanDefine(beanDefine);

            } else {
                ((Entry<String, Object>) (Object) entry).setValue(beanObject);
            }
        }

        return (Map<String, Object>) (Object) beanDefineMap;
    }

    /**
     * 获取对象
     *
     * @param filter
     * @return
     */
    public List<Object> getBeanObjects(FilterTemplate<BeanDefine> filter) {
        List<Object> beanObjects = new ArrayList<Object>();
        addBeanObjects(filter, beanObjects);
        return beanObjects;
    }

    private <T> void addBeanObjects(FilterTemplate<BeanDefine> filter, List<Object> beanObjects) {
        try {
            Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
            while (iterator.hasNext()) {
                BeanDefine beanDefine = iterator.next().getValue();
                if (filter.doWith(beanDefine)) {
                    Object beanObject = beanDefine.getBeanObject(this);
                    if (beanObject == null) {
                        iterator.remove();
                        unRegisterBeanDefine(beanDefine);

                    } else {
                        beanObjects.add(beanObject);
                    }
                }
            }

        } catch (BreakException e) {
        }

        if (beanFactoryImpl != null) {
            addBeanObjects(filter, beanObjects);
        }
    }

    @Override
    public BeanDefine getBeanDefine(String beanName, Class<?> beanType) {
        if (beanName != null) {
            if ("".equals(beanName)) {
                beanName = null;

            } else {
                BeanDefine beanDefine = getBeanDefine(beanName);
                if (beanDefine != null && beanType.isAssignableFrom(beanDefine.getBeanType())) {
                    return beanDefine;
                }
            }
        }

        return getBeanDefine(null, beanName, beanType, -1.0f);
    }

    private BeanDefine getBeanDefine(BeanDefine beanObject, String beanName, Class<?> beanType, float max) {
        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            BeanDefine beanDefine = iterator.next().getValue();
            if (beanType.isAssignableFrom(beanDefine.getBeanType())) {
                if (beanName == null) {
                    return beanDefine;
                }

                float similar = KernelString.similar(beanName, beanDefine.getBeanName());
                if (similar > max) {
                    if (similar >= 1.0f) {
                        return beanDefine;
                    }

                    max = similar;
                    beanObject = beanDefine;
                }
            }
        }

        return beanFactoryImpl == null ? beanObject
                : beanFactoryImpl.getBeanDefine(beanObject, beanName, beanType, max);
    }

    @Override
    public List<BeanDefine> getBeanDefines(Class<?> beanType) {
        List<BeanDefine> beanDefines = new ArrayList<BeanDefine>();
        addBeanDefines(beanType, beanDefines);
        return beanDefines;
    }

    private void addBeanDefines(Class<?> beanType, List<BeanDefine> beanDefines) {
        for (Entry<String, BeanDefine> entry : beanNameDefineMap.entrySet()) {
            BeanDefine beanDefine = entry.getValue();
            if (beanType.isAssignableFrom(beanDefine.getBeanType())) {
                beanDefines.add(beanDefine);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.addBeanDefines(beanType, beanDefines);
        }
    }

    public <T extends BeanDefine> List<BeanDefine> getBeanDefineList(Class<T> beanDefineClass) {
        List<BeanDefine> beanDefineList = new ArrayList<BeanDefine>();
        addBeanDefineList(beanDefineClass, beanDefineList);
        return beanDefineList;
    }

    private <T extends BeanDefine> void addBeanDefineList(Class<T> beanDefineClass, List<BeanDefine> beanDefineList) {
        for (Entry<String, BeanDefine> entry : beanNameDefineMap.entrySet()) {
            BeanDefine beanDefine = entry.getValue();
            if (getBeanDefine(beanDefine, beanDefineClass) != null) {
                beanDefineList.add(beanDefine);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.addBeanDefineList(beanDefineClass, beanDefineList);
        }
    }

    @Override
    public Map<String, BeanDefine> getBeanDefineMap(Class<?> beanType) {
        Map<String, BeanDefine> beanDefineMap = new HashMap<String, BeanDefine>();
        addBeanDefineMap(beanType, beanDefineMap);
        return beanDefineMap;
    }

    public void addBeanDefineMap(Class<?> beanType, Map<String, BeanDefine> beanDefineMap) {
        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            BeanDefine beanDefine = iterator.next().getValue();
            if (beanType.isAssignableFrom(beanDefine.getBeanType())) {
                beanDefineMap.put(beanDefine.getBeanName(), beanDefine);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.addBeanDefineMap(beanType, beanDefineMap);
        }
    }

    public Map<String, BeanDefine> getBeanDefineMap(FilterTemplate<BeanDefine> filter) {
        Map<String, BeanDefine> beanDefineMap = new HashMap<String, BeanDefine>();
        addBeanDefines(filter, beanDefineMap);
        return beanDefineMap;
    }

    public void addBeanDefines(FilterTemplate<BeanDefine> filter, Map<String, BeanDefine> beanDefineMap) {
        try {
            Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
            while (iterator.hasNext()) {
                BeanDefine beanDefine = iterator.next().getValue();
                if (filter.doWith(beanDefine)) {
                    beanDefineMap.put(beanDefine.getBeanName(), beanDefine);
                }
            }

        } catch (BreakException e) {
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.addBeanDefines(filter, beanDefineMap);
        }
    }

    @Override
    public <T> List<T> getSoftReferenceBeans(Class<T> beanType) {
        List<Object> beans = new ArrayList<Object>();
        addSoftReferenceBeans(beanType, beans);
        return (List<T>) beans;
    }

    private <T> void addSoftReferenceBeans(Class<T> beanType, List<Object> beans) {
        Iterator<BeanSoftReference> iterator = beanSoftReferenceSet.iterator();
        while (iterator.hasNext()) {
            BeanSoftReference beanSoftReference = iterator.next();
            Object beanObject = beanSoftReference.get();
            if (beanObject == null) {
                iterator.remove();
                unRegisterBeanSoftReference(beanSoftReference);
            }

            if (beanType.isAssignableFrom(beanObject.getClass())) {
                beans.add(beanObject);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.addSoftReferenceBeans(beanType, beans);
        }
    }

    @Override
    public List<Object> getSoftReferenceBeans(FilterTemplate<Object> filter) {
        List<Object> beans = new ArrayList<Object>();
        addSoftReferenceBeans(filter, beans);
        return beans;
    }

    private <T> void addSoftReferenceBeans(FilterTemplate<Object> filter, List<Object> beans) {
        try {
            Iterator<BeanSoftReference> iterator = beanSoftReferenceSet.iterator();
            while (iterator.hasNext()) {
                BeanSoftReference beanSoftReference = iterator.next();
                Object beanObject = beanSoftReference.get();
                if (beanObject == null) {
                    iterator.remove();
                    unRegisterBeanSoftReference(beanSoftReference);
                }

                if (filter.doWith(beanObject)) {
                    beans.add(beanObject);
                }
            }

        } catch (BreakException e) {
        }

        if (beanFactoryImpl != null) {
            addSoftReferenceBeans(filter, beans);
        }
    }

    @Override
    public BeanDefine processBeanDefine(BeanDefine beanDefine) {
        BeanDefine define;
        for (IBeanDefineProcessor beanDefineProcessor : beanDefineProcessors) {
            define = beanDefineProcessor.getBeanDefine(this, beanDefine);
            if (define != null) {
                beanDefine = define;
            }
        }

        return beanDefine;
    }

    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject) {
        processBeanObject(beanScope, beanDefine, beanObject, null);
    }

    @Override
    public void processBeanObject(BeanScope beanScope, BeanDefine beanDefine, Object beanObject, Object beanProxy) {
        if (beanObject == null) {
            return;
        }

        if (beanScope == null && beanDefine != null) {
            beanScope = beanDefine.getBeanScope();
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.processBeanObject(beanScope, beanDefine, beanObject);
        }

        if (beanProxy == null) {
            beanProxy = beanObject;
        }

        for (IBeanObjectProcessor beanObjectProcessor : beanObjectProcessors) {
            beanObjectProcessor.processBeanObject(this, beanScope, beanDefine, beanObject, beanProxy);
        }
    }

    @Override
    public BeanDefine registerBeanObject(Object beanObject) {
        return registerBeanObject(null, beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(String beanName, Object beanObject) {
        return registerBeanObject(beanName, BeanScope.SINGLETON, beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(String beanName, BeanScope beanScope, Object beanObject) {
        return registerBeanObject(beanObject.getClass(), beanName, beanScope, beanObject);
    }

    @Override
    public BeanDefine registerBeanObject(Class<?> beanType, String beanName, BeanScope beanScope, Object beanObject) {
        if (beanType == null || !beanType.isAssignableFrom(beanObject.getClass())) {
            beanType = beanObject.getClass();
        }

        BeanDefine beanDefine = BeanDefineAbstract.getBeanDefine(beanType, beanName, beanObject, beanScope, null);
        if (beanNameDefineMap.containsKey(beanDefine.getBeanName())) {
            throw new RuntimeException("BeanName =" + beanDefine.getBeanName() + " has registered!");
        }

        registerBeanDefine(null, new BeanDefineRegister(beanDefine));
        return beanDefine;
    }

    /**
     * 注册对象定义
     *
     * @param beanDefine
     */
    public void registerBeanDefine(BeanDefine beanDefine) {
        registerBeanDefine(beanNameDefineMap.get(beanDefine.getBeanName()), beanDefine);
    }

    /**
     * 注册对象定义
     *
     * @param registeredBeanDefine
     * @param beanDefine
     */
    protected void registerBeanDefine(BeanDefine registeredBeanDefine, BeanDefine beanDefine) {
        unRegisterBeanDefineImpl(registeredBeanDefine, null, null);
        beanNameDefineMap.put(beanDefine.getBeanName(), beanDefine);
        for (IBeanDefineAware beanDefineAware : beanDefineAwares) {
            beanDefineAware.registerBeanDefine(this, beanDefine);
        }
    }

    /**
     * 替换对象定义
     *
     * @param beanDefine
     */
    public void replaceRegisteredBeanDefine(BeanDefine beanDefine) {
        BeanDefine registeredBeanDefine = beanNameDefineMap.get(beanDefine.getBeanName());
        if (registeredBeanDefine == null || registeredBeanDefine.getBeanType() != beanDefine.getBeanType()) {
            registerBeanDefine(registeredBeanDefine, beanDefine);

        } else {
            replaceRegisteredBeanDefine(registeredBeanDefine, beanDefine);
        }
    }

    /**
     * 替换对象定义
     *
     * @param registeredBeanDefine
     * @param beanDefine
     */
    protected void replaceRegisteredBeanDefine(BeanDefine registeredBeanDefine, BeanDefine beanDefine) {
        if (beanNameDefineMap.containsKey(beanDefine.getBeanName())) {
            beanNameDefineMap.put(beanDefine.getBeanName(), beanDefine);
            if (registeredBeanDefine.getBeanScope() == BeanScope.PROTOTYPE
                    || beanDefine.getBeanScope() == BeanScope.PROTOTYPE) {
                for (IBeanDefineAware beanDefineAware : beanDefineAwares) {
                    beanDefineAware.replaceRegisterBeanDefine(this, beanDefine);
                }
            }

        } else {
            if (beanFactoryImpl != null) {
                beanFactoryImpl.replaceRegisteredBeanDefine(registeredBeanDefine, beanDefine);
            }
        }
    }

    /**
     * 注册栈对象
     *
     * @param beanObject
     */
    public void registerStackBeanObject(Object beanObject) {
        registerStackBeanDefine(new BeanDefineSingleton(beanObject));
    }

    /**
     * 注册栈对象定义
     *
     * @param beanDefine
     */
    public void registerStackBeanDefine(BeanDefine beanDefine) {
        BeanDefine registeredBeanDefine = beanNameDefineMap.get(beanDefine.getBeanName());
        if (registeredBeanDefine == null) {
            registerBeanDefine(registeredBeanDefine, beanDefine);

        } else {
            Stack<BeanDefine> beanDefineStatck = beanNameDefineMapStack.get(beanDefine.getBeanName());
            if (beanDefineStatck == null) {
                beanDefineStatck = new Stack<BeanDefine>();
                beanNameDefineMapStack.put(beanDefine.getBeanName(), beanDefineStatck);
            }

            beanDefineStatck.push(registeredBeanDefine);
            replaceRegisteredBeanDefine(registeredBeanDefine, beanDefine);
        }
    }

    /**
     * 注销栈对象
     *
     * @param beanObject
     */
    public void unRegisterStackBeanObject(Object beanObject) {
        unRegisterStackBeanDefine(BeanDefineType.getBeanName(null, beanObject.getClass()), null);
    }

    /**
     * 注销栈对象定义
     *
     * @param beanDefine
     */
    public void unRegisterStackBeanDefine(BeanDefine beanDefine) {
        unRegisterStackBeanDefine(beanDefine.getBeanName(), beanDefine);
    }

    /**
     * 注销栈对象定义
     *
     * @param name
     * @param beanDefine
     */
    private void unRegisterStackBeanDefine(String name, BeanDefine beanDefine) {
        Stack<BeanDefine> beanDefineStatck = beanNameDefineMapStack.get(name);
        if (beanDefineStatck == null || beanDefineStatck.size() <= 0) {
            unRegisterBeanDefine(name, null, beanDefine);

        } else {
            BeanDefine registeredBeanDefine = beanDefineStatck.pop();
            replaceRegisteredBeanDefine(registeredBeanDefine, registeredBeanDefine);
        }
    }

    @Override
    public void unRegisterBeanObject(Object beanObject) {
        unRegisterBeanObjectAll(null, beanObject);
    }

    @Override
    public void unRegisterBeanObject(String beanName) {
        unRegisterBeanDefine(beanName, null, null);
    }

    @Override
    public void unRegisterBeanObject(String beanName, Object beanObject) {
        unRegisterBeanDefine(beanName, beanObject, null);
    }

    @Override
    public void unRegisterBeanType(Class<?>... beanTypes) {
        if (beanTypes.length == 0) {
            return;
        }

        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        BeanDefine beanDefine;
        while (iterator.hasNext()) {
            beanDefine = iterator.next().getValue();
            if (beanDefine.getClass() != BeanDefineRegister.class
                    && KernelClass.isAssignableFrom(beanTypes, beanDefine.getBeanType())) {
                iterator.remove();
            }
        }
    }

    @Override
    public void unRegisterWithoutBeanType(Class<?>... beanTypes) {
        if (beanTypes.length == 0) {
            return;
        }

        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        BeanDefine beanDefine;
        while (iterator.hasNext()) {
            beanDefine = iterator.next().getValue();
            if (beanDefine.getClass() != BeanDefineRegister.class
                    && !KernelClass.isAssignableFrom(beanTypes, beanDefine.getBeanType())) {
                iterator.remove();
            }
        }
    }

    /**
     * 注销对象定义
     *
     * @param beanDefine
     */
    public void unRegisterBeanDefine(BeanDefine beanDefine) {
        unRegisterBeanDefine(null, null, beanDefine);
    }

    /**
     * 注销对象定义
     *
     * @param registeredBeanDefine
     * @param object
     * @param beanDefine
     */
    protected void unRegisterBeanDefine(String beanName, Object beanObject, BeanDefine beanDefine) {
        if (beanName == null) {
            if (beanDefine != null) {
                beanName = beanDefine.getBeanName();

            } else if (beanObject != null) {
                beanName = BeanDefineType.getBeanName(beanName, beanObject.getClass());
            }
        }

        BeanDefine registeredBeanDefine = beanNameDefineMap.get(beanName);
        if (registeredBeanDefine != null) {
            unRegisterBeanDefineImpl(registeredBeanDefine, beanObject, registeredBeanDefine);

        } else {
            if (beanFactoryImpl != null) {
                beanFactoryImpl.unRegisterBeanDefine(beanName, beanObject, registeredBeanDefine);
            }
        }
    }

    /**
     * 注销对象定义
     *
     * @param registeredBeanDefine
     * @param beanObject
     * @param beanDefine
     */
    protected void unRegisterBeanDefineImpl(BeanDefine registeredBeanDefine, Object beanObject, BeanDefine beanDefine) {
        if (registeredBeanDefine != null) {
            if (beanDefine != null && !registeredBeanDefine.equals(beanDefine)) {
                throw new RuntimeException("BeanName + " + registeredBeanDefine.getBeanName() + " unRegister "
                        + beanDefine + " not match " + registeredBeanDefine);

            } else if (beanObject != null) {
                if (!(registeredBeanDefine.getBeanScope() == BeanScope.PROTOTYPE
                        ? registeredBeanDefine.getBeanType() == beanObject.getClass()
                        : beanObject.equals(registeredBeanDefine.getBeanObject(this)))) {
                    throw new RuntimeException("BeanName + " + registeredBeanDefine.getBeanName() + " unRegister "
                            + beanDefine + " not match " + beanObject);
                }
            }

            beanNameDefineMap.remove(registeredBeanDefine.getBeanName());
            for (IBeanDefineAware beanDefineAware : beanDefineAwares) {
                beanDefineAware.unRegisterBeanDefine(this, registeredBeanDefine);
            }
        }
    }

    /**
     * 获取全部对象定义
     *
     * @return
     */
    public Collection<BeanDefine> getBeanDefines() {
        return beanNameDefineMap.values();
    }

    /**
     * 获取对象定义
     *
     * @param beanName
     * @return
     */
    public BeanDefine getBeanDefine(String beanName) {
        return beanNameDefineMap.get(beanName);
    }

    /**
     * 获取唯一对象定义
     *
     * @param beanComponent
     * @return
     */
    public BeanDefine getBeanDefineComponent(Object beanComponent) {
        if (beanComponent != null) {
            for (BeanDefine beanDefine : beanNameDefineMap.values()) {
                if (beanComponent.equals(beanDefine.getBeanComponent())) {
                    return beanDefine;
                }
            }
        }

        return null;
    }

    @Override
    public void registerBeanSoftObject(Object beanObject) {
        if (!beanSoftReferenceSet.contains(beanObject)) {
            registerBeanSoftReference(new BeanSoftReference(beanObject));
        }
    }

    /**
     * 注册引用对象
     *
     * @param beanSoftReference
     */
    public synchronized void registerBeanSoftReference(BeanSoftReference beanSoftReference) {
        for (IBeanSoftReferenceAware beanSoftReferenceAware : beanSoftReferenceAwares) {
            beanSoftReferenceAware.registerSoftReference(this, beanSoftReference);
        }
    }

    @Override
    public void unRegisterBeanSoftObject(Object beanObject) {
        unRegisterBeanSoftReference(beanObject);
    }

    /**
     * 注销引用对象
     *
     * @param beanObject
     */
    public void unRegisterBeanSoftReference(Object beanObject) {
        for (BeanSoftReference beanSoftReference : beanSoftReferenceSet) {
            if (beanSoftReference.equals(beanObject)) {
                for (IBeanSoftReferenceAware beanSoftReferenceAware : beanSoftReferenceAwares) {
                    beanSoftReferenceAware.unRegisterSoftReference(this, beanSoftReference);
                }

                return;
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.unRegisterBeanSoftObject(beanObject);
        }
    }

    /**
     * 注销对象清理
     *
     * @param beanName
     * @param beanObject
     */
    public void unRegisterBeanObjectAll(String beanName, Object beanObject) {
        unRegisterBeanObject(beanName, beanObject);
        unRegisterBeanSoftObject(beanObject);
    }

    /**
     * 清除对象软定义
     */
    public void clearBeanDefine() {
        Iterator<Entry<String, BeanDefine>> iterator = beanNameDefineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            BeanDefine beanDefine = iterator.next().getValue();
            if (beanDefine.getBeanObject(this) == null) {
                iterator.remove();
                unRegisterBeanDefineImpl(beanDefine, null, null);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.clearBeanDefine();
        }
    }

    /**
     * 清除对象软引用
     */
    public void clearBeanSoftReference() {
        Iterator<BeanSoftReference> iterator = beanSoftReferenceSet.iterator();
        while (iterator.hasNext()) {
            BeanSoftReference beanSoftReference = iterator.next();
            if (beanSoftReference.get() == null) {
                iterator.remove();
                unRegisterBeanSoftReference(beanSoftReference);
            }
        }

        if (beanFactoryImpl != null) {
            beanFactoryImpl.clearBeanSoftReference();
        }
    }

    /**
     * 清除对象工厂软引用
     */
    public void clearBeanFactory() {
        clearBeanDefine();
        clearBeanSoftReference();
    }
}
