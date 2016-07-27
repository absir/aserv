/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年7月29日 下午5:15:42
 */
package com.absir.aserv.lang;

import com.absir.aop.AopInterceptor;
import com.absir.aop.AopProxy;
import com.absir.aop.AopProxyHandler;
import com.absir.aop.AopProxyUtils;
import com.absir.aserv.crud.CrudHandler;
import com.absir.aserv.crud.CrudProperty;
import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.crud.value.ICrudBean;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.lang.value.Langs;
import com.absir.aserv.support.Developer;
import com.absir.aserv.system.bean.JEmbedSL;
import com.absir.aserv.system.bean.value.JaCrud.Crud;
import com.absir.aserv.system.crud.BeanCrudFactory;
import com.absir.aserv.system.helper.HelperBase;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.CrudService;
import com.absir.aserv.system.service.utils.CrudServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.IMethodEntry;
import com.absir.bean.inject.InjectBeanFactory;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.lang.ILangCode;
import com.absir.binder.BinderData;
import com.absir.binder.IBinder;
import com.absir.context.lang.LangBundle;
import com.absir.core.base.IBase;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.*;
import com.absir.core.kernel.KernelLang.ObjectEntry;
import com.absir.core.util.UtilAccessor;
import com.absir.core.util.UtilAccessor.Accessor;
import com.absir.orm.value.JoEntity;
import com.absir.property.PropertyData;
import com.absir.server.on.OnPut;
import net.sf.cglib.proxy.MethodProxy;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings({"unchecked", "rawtypes"})
@Base(order = -1)
@Bean
public class LangBundleImpl extends LangBundle implements ILangCode {

    public static final LangBundleImpl ME = BeanFactoryUtils.get(LangBundleImpl.class);

    public static final String RECORD = "LOCALE@";

    private static Map<String, Map<String, Map<String, Map<String, Object>>>> entityMapIdMapNameMapValue = new HashMap<String, Map<String, Map<String, Map<String, Object>>>>();

    private Map<String, Object> langConfigureMap;

    private Map<String, Object> unLangDeveloperMap;

    private Set<Class<?>> langInterfaces = new HashSet<Class<?>>();

    private Map<Method, LangEntryImpl> methodMapLangEntryImpl = new HashMap<Method, LangEntryImpl>();

    private Map<String, Map<Method, LangEntry>> entityMapLangInterceptors = new HashMap<String, Map<Method, LangEntry>>();

    public LangBundleImpl() {
        langInterfaces.add(ILangBase.class);
        langInterfaces.add(IBinder.class);
        langInterfaces.add(ICrudBean.class);
        InitLangBundleImpl(methodMapLangEntryImpl);
    }

    public static Map<String, Map<String, Object>> getLangNameMapValue(String entityName, String id) {
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue == null) {
            idMapNameMapValue = new HashMap<String, Map<String, Map<String, Object>>>();
            entityMapIdMapNameMapValue.put(entityName, idMapNameMapValue);
        }

        Map<String, Map<String, Object>> nameMapValue = idMapNameMapValue.get(id);
        if (nameMapValue == null) {
            nameMapValue = new HashMap<String, Map<String, Object>>();
            for (Map<String, Object> value : (List<Map<String, Object>>) BeanService.ME.selectQuery(
                    "SELECT o FROM JLocale o WHERE o.entity = ? AND o.id = ?", entityName, id)) {
                nameMapValue.put((String) value.get("name"), value);
            }

            idMapNameMapValue.put(id, nameMapValue);
        }

        return nameMapValue;
    }

    public static Map<String, Map<String, Object>> findLangNameMapValue(String entityName, String id) {
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            return idMapNameMapValue.get(id);
        }

        return null;
    }

    public static Map<String, Map<String, Object>> createLangNameMapValue(String entityName, String id) {
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue == null) {
            idMapNameMapValue = new HashMap<String, Map<String, Map<String, Object>>>();
            entityMapIdMapNameMapValue.put(entityName, idMapNameMapValue);
        }

        Map<String, Map<String, Object>> nameMapValue = idMapNameMapValue.get(id);
        if (nameMapValue == null) {
            nameMapValue = new HashMap<String, Map<String, Object>>();
            idMapNameMapValue.put(id, nameMapValue);
        }

        return nameMapValue;
    }

    public static Map<String, Map<String, Map<String, Object>>> getLangNameMapValues(String entityName, List<String> ids) {
        Map<String, Map<String, Map<String, Object>>> nameMapValues = new HashMap<String, Map<String, Map<String, Object>>>();
        for (Map<String, Object> value : (List<Map<String, Object>>) BeanService.ME.selectQuery(
                "SELECT o FROM JLocale o WHERE o.entity = ? AND o.id IN (?)", entityName,
                KernelCollection.toArray(ids, String.class))) {
            String id = (String) value.get("id");
            Map<String, Map<String, Object>> nameMapValue = nameMapValues.get(id);
            if (nameMapValue == null) {
                nameMapValue = new HashMap<String, Map<String, Object>>();
                nameMapValues.put(id, nameMapValue);
            }

            nameMapValue.put((String) value.get("name"), value);
        }

        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue == null) {
            idMapNameMapValue = new HashMap<String, Map<String, Map<String, Object>>>();
            entityMapIdMapNameMapValue.put(entityName, idMapNameMapValue);
        }

        idMapNameMapValue.putAll(nameMapValues);
        return nameMapValues;
    }

    protected static void deleteLangMapValue(String entityName, String id) {
        BeanService.ME.executeUpdate("DELETE FROM JLocale o WHERE o.entity = ?, o.id = ?", entityName, id);
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            idMapNameMapValue.remove(id);
        }
    }

    protected static void mergeLangMapValue(String entityName, String id, Map<String, Map<String, Object>> nameMapValue) {
        BeanService.ME.mergers("JLocale", nameMapValue.values());
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            idMapNameMapValue.put(id, nameMapValue);
        }
    }

    public static void clearLangNameMapValue(String entityName) {
        entityMapIdMapNameMapValue.remove(entityName);
    }

    private static void InitLangBundleImpl(Map<Method, LangEntryImpl> methodMapLangEntryImpl) {
        for (Method method : ILangBase.class.getMethods()) {
            LangEntryImpl langEntryImpl = null;
            if ("getLang".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        return interceptor.getLang((String) args[0], (Integer) args[1], (Class<?>) args[2]);
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangInterceptor inInterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                return ((ILangBase) proxyHandler.getBeanObject()).getLang((String) args[0], (Integer) args[1],
                                        (Class<?>) args[2]);
                            }
                        };
                    }
                };

            } else if ("setLang".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        interceptor.setLang((String) args[0], (Integer) args[1], args[2]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((ILangBase) proxyHandler.getBeanObject()).setLang((String) args[0], (Integer) args[1], args[2]);
                                return null;
                            }
                        };
                    }
                };

            } else if ("setLangEntity".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        interceptor.setLangValues((String[]) args[0]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((ILangBase) proxyHandler.getBeanObject()).setLangValues((String[]) args[0]);
                                return null;
                            }
                        };
                    }
                };
            }

            if (langEntryImpl != null) {
                methodMapLangEntryImpl.put(method, langEntryImpl);
            }
        }

        for (Method method : IBinder.class.getMethods()) {
            LangEntryImpl langEntryImpl = null;
            if ("bind".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        interceptor.processCrud(proxyHandler.getBeanObject(), (Crud) args[0], (CrudHandler) args[1]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((IBinder) proxyHandler.getBeanObject()).bind((String) args[0], (Object) args[1],
                                        (PropertyData) args[2], (BinderData) args[3]);
                                invoke(interceptor, proxy, iterator, proxyHandler, method, args, methodProxy);
                                return null;
                            }
                        };
                    }
                };
            }

            if (langEntryImpl != null) {
                methodMapLangEntryImpl.put(method, langEntryImpl);
            }
        }

        for (Method method : ICrudBean.class.getMethods()) {
            LangEntryImpl langEntryImpl = null;
            if ("processCrud".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        interceptor.processCrud(proxyHandler.getBeanObject(), (Crud) args[0], (CrudHandler) args[1]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((ICrudBean) proxyHandler.getBeanObject()).processCrud((Crud) args[0], (CrudHandler) args[1], null);
                                invoke(interceptor, proxy, iterator, proxyHandler, method, args, methodProxy);
                                return null;
                            }
                        };
                    }
                };
            }

            if (langEntryImpl != null) {
                methodMapLangEntryImpl.put(method, langEntryImpl);
            }
        }
    }

    protected Map<String, Object> getLangConfigureMap() {
        if (langConfigureMap != null) {
            langConfigureMap = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(), langConfigureMap, new File(BeanFactoryUtils
                    .getBeanConfig().getClassPath() + "langConfigure.properties"), null);
        }

        return langConfigureMap;
    }

    public String[] getLangValueTag(String name) {
        return BeanConfigImpl.getMapValue(getLangConfigureMap(), name, null, String[].class);
    }

    /**
     * 初始化
     */
    @Inject
    private void initLangBundleImpl() {
        File unLangFile = new File(BeanFactoryUtils.getBeanConfig().getClassPath() + "unLang.properties");
        if (unLangFile.exists()) {
            unLangDeveloperMap = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(null, unLangDeveloperMap, unLangFile, null);
        }
    }

    public String getunLang(String lang, String tag) {
        if (unLangDeveloperMap != null) {
            Object unLang = null;
            if (tag != null) {
                unLang = unLangDeveloperMap.get(tag + "@" + lang);
                if (unLang != null) {
                    return unLang.toString();
                }
            }

            unLang = unLangDeveloperMap.get(lang);
            if (unLang != null) {
                return unLang.toString();
            }
        }

        return lang;
    }

    protected Map<Method, LangEntry> getLangInterceptors(JoEntity joEntity) {
        return getLangInterceptors(
                joEntity.getEntityName() == null ? joEntity.getEntityClass().getName() : joEntity.getEntityName(),
                joEntity.getEntityClass());
    }

    protected Map<Method, LangEntry> getLangInterceptors(final String entityName, final Class<?> entityClass) {
        if (isI18n()) {
            Map<Method, LangEntry> langInterceptors = entityMapLangInterceptors.get(entityName);
            if (langInterceptors == null) {
                langInterceptors = new HashMap<Method, LangEntry>();
                final Map<Method, LangEntry> interceptors = langInterceptors;
                InjectBeanFactory.getInstance().getMethodEntries(entityClass, new IMethodEntry<LangEntry>() {

                    @Override
                    public LangEntry getMethod(Class<?> beanType, Method method) {
                        LangEntryImpl langEntryImpl = methodMapLangEntryImpl.get(method);
                        if (langEntryImpl == null) {
                            if (method.getParameterTypes().length == 0 && method.getName().startsWith("get")) {
                                if (BeanConfigImpl.getMethodAnnotation(method, Langs.class) != null) {
                                    return new LangEntryValue(KernelString.unCapitalize(method.getName().substring(3)), method
                                            .getReturnType());
                                }

                                Class<?> returnType = method.getReturnType();
                                if (!KernelClass.isBasicClass(returnType)) {
                                    String property = KernelString.unCapitalize(method.getName().substring(3));
                                    CrudProperty crudProperty = CrudUtils.getCrudProperty(new JoEntity(entityName, entityClass),
                                            property);
                                    if (crudProperty != null && crudProperty.getCrudProcessor() != null
                                            && crudProperty.getCrudProcessor() instanceof BeanCrudFactory) {
                                        final JoEntity joEntity = crudProperty.getValueEntity();
                                        if (joEntity != null && joEntity.getClass() != null) {
                                            if (KernelClass.isCustomClass(joEntity.getEntityClass())) {
                                                final Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity);
                                                if (langInterceptors != null) {
                                                    final boolean embed = joEntity.getEntityName() == null
                                                            || CrudService.ME.getCrudSupply(joEntity.getEntityName()) == null
                                                            || !IBase.class.isAssignableFrom(joEntity.getEntityClass());
                                                    if (returnType.isArray()) {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangInterceptor langInterceptor, Object value) {
                                                                // Auto-generated
                                                                // method stub
                                                                Object[] entities = (Object[]) value;
                                                                String[][] nameIds = null;
                                                                if (embed) {
                                                                    int size = entities.length;
                                                                    if (size > 0) {
                                                                        nameIds = new String[size][];
                                                                        for (int i = 0; i < size; i++) {
                                                                            nameIds[i] = new String[]{name, String.valueOf(i)};
                                                                        }
                                                                    }
                                                                }

                                                                return ME.getLangProxyArray(langInterceptor, joEntity, entities,
                                                                        nameIds);
                                                            }
                                                        };

                                                    } else if (Collection.class.isAssignableFrom(returnType)) {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangInterceptor langInterceptor, Object value) {
                                                                // Auto-generated
                                                                // method stub
                                                                Collection<?> entities = (Collection<?>) value;
                                                                String[][] nameIds = null;
                                                                if (embed) {
                                                                    int size = entities.size();
                                                                    if (size > 0) {
                                                                        nameIds = new String[size][];
                                                                        for (int i = 0; i < size; i++) {
                                                                            nameIds[i] = new String[]{name, String.valueOf(i)};
                                                                        }
                                                                    }
                                                                }

                                                                return ME.getLangProxyCollection(langInterceptor, joEntity,
                                                                        entities, nameIds);
                                                            }
                                                        };

                                                    } else if (Map.class.isAssignableFrom(returnType)) {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangInterceptor langInterceptor, Object value) {
                                                                // Auto-generated
                                                                // method stub
                                                                Map<?, ?> entities = (Map<?, ?>) value;
                                                                String[][] nameIds = null;
                                                                if (embed) {
                                                                    int size = entities.size();
                                                                    if (size > 0) {
                                                                        nameIds = new String[size][];
                                                                        for (int i = 0; i < size; i++) {
                                                                            nameIds[i] = new String[]{name, String.valueOf(i)};
                                                                        }
                                                                    }
                                                                }

                                                                return ME.getLangProxyMap(langInterceptor, joEntity, entities,
                                                                        nameIds);
                                                            }
                                                        };

                                                    } else {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangInterceptor langInterceptor, Object value) {
                                                                // Auto-generated
                                                                // method stub
                                                                return ME.getLangProxy(langInterceptor, joEntity, value,
                                                                        embed ? new String[]{name, null} : null,
                                                                        langInterceptors, null, null);
                                                            }
                                                        };
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            return langEntryImpl.getLangEntry();
                        }

                        return null;
                    }

                    @Override
                    public void setMethodEntry(LangEntry define, Class<?> beanType, final Method beanMethod, Method method) {
                        interceptors.put(beanMethod, define);
                        if (define instanceof LangEmbeded) {
                            LangEmbeded langEmbeded = (LangEmbeded) define;
                            if (langEmbeded.accessor != null && langEmbeded.accessor.getSetter() != null) {
                                interceptors.put(langEmbeded.accessor.getSetter(), new LangEntry() {

                                    @Override
                                    public Object invoke(LangInterceptor interceptor, Object proxy,
                                                         Iterator<AopInterceptor> iterator, AopProxyHandler proxyHandler, Method method,
                                                         Object[] args, MethodProxy methodProxy) throws Throwable {
                                        interceptor.removeEmbed(beanMethod);
                                        Object arg = args[0];
                                        if (arg != null && arg instanceof AopProxy) {
                                            args[0] = ((AopProxy) arg).getBeanObject();
                                        }

                                        return AopProxyHandler.VOID;
                                    }
                                });
                            }
                        }
                    }
                });

                if (langInterceptors.isEmpty()) {
                    langInterceptors = (Map<Method, LangEntry>) (Object) KernelLang.NULL_MAP;

                } else {
                    // 执行接口方法
                    langInterceptors.putAll(methodMapLangEntryImpl);
                }

                // 最后设置
                entityMapLangInterceptors.put(entityName, langInterceptors);
            }

            if ((Object) langInterceptors == KernelLang.NULL_MAP) {
                return null;
            }

            return langInterceptors;
        }

        return null;
    }

    public <T> T getLangProxy(String entityName, T entity) {
        if (isI18n() && entity instanceof IBase) {
            return getLangProxy(entityName, entity, ((IBase) entity).getId());
        }

        return entity;
    }

    public <T> T getLangProxy(String entityName, T entity, Object id) {
        if (entity == null) {
            return entity;
        }

        Map<Method, LangEntry> langInterceptors = getLangInterceptors(entityName, entity.getClass());
        if (langInterceptors != null) {
            AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
            proxy.getAopInterceptors().add(getLangInterceptor(entityName, DynaBinderUtils.getParamFromValue(id), langInterceptors));
            return (T) proxy;
        }

        return entity;
    }

    public <T> List<T> getLangProxy(final String entityName, final List<T> entities) {
        if (isI18n() && !entities.isEmpty()) {
            Object entity = entities.get(0);
            if (entity instanceof IBase) {
                return (List<T>) getLangProxy(entityName, new JoEntity(entityName, entity.getClass()), entities,
                        HelperBase.getBaseIds((Collection<? extends IBase>) entities));
            }
        }

        return entities;
    }

    public <T> List<T> getLangProxy(final String entityName, final List<T> entities, ICrudSupply crudSupply) {
        if (isI18n() && !entities.isEmpty()) {
            Object entity = entities.get(0);
            if (entity instanceof IBase) {
                return (List<T>) getLangProxy(entityName, new JoEntity(entityName, entity.getClass()), entities,
                        HelperBase.getBaseIds((Collection<? extends IBase>) entities));

            } else {
                return (List<T>) getLangProxy(entityName, new JoEntity(entityName, entity.getClass()), entities,
                        HelperBase.getBaseIds(entityName, entities, crudSupply));
            }
        }

        return entities;
    }

    public <T> Iterator<T> getLangProxy(final String entityName, final Iterator<T> entityIterator) {
        if (isI18n()) {
            return new Iterator<T>() {

                @Override
                public boolean hasNext() {
                    return entityIterator.hasNext();
                }

                @Override
                public T next() {
                    return getLangProxy(entityName, entityIterator.next());
                }

                @Override
                public void remove() {
                    entityIterator.remove();
                }
            };
        }

        return entityIterator;
    }

    public <T> T getLangProxy(String entityName, JoEntity joEntity, T entity, Object id) {
        if (isI18n()) {
            Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity);
            if (langInterceptors != null) {
                AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
                proxy.getAopInterceptors().add(
                        getLangInterceptor(joEntity.getEntityName() == null ? entityName : joEntity.getEntityName(),
                                DynaBinderUtils.getParamFromValue(id), langInterceptors));
                return (T) proxy;
            }
        }

        return entity;
    }

    protected <T> T getLangProxy(LangInterceptor parent, JoEntity joEntity, T entity, String[] nameId,
                                 Map<Method, LangEntry> langInterceptors, List<String> finds, List<LangInterceptor> interceptors) {
        AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
        LangInterceptor langInterceptor = getLangInterceptor(
                joEntity.getEntityName() == null ? parent.entityName : joEntity.getEntityName(), null, langInterceptors);
        langInterceptor.setNameId(parent, entity, nameId, null);
        String id = langInterceptor.id;
        if (id != null) {
            Map<String, Map<String, Object>> nameMapValue = findLangNameMapValue(langInterceptor.entityName, id);
            if (nameMapValue == null) {
                if (finds != null) {
                    finds.add(id);
                }

                if (interceptors != null) {
                    interceptors.add(langInterceptor);
                }

            } else {
                langInterceptor.nameMapValue = nameMapValue;
            }
        }

        proxy.getAopInterceptors().add(langInterceptor);
        return (T) proxy;
    }

    public <T> T[] getLangProxyArray(LangInterceptor parent, JoEntity joEntity, T[] entities, String[][] nameIds) {
        if (isI18n()) {
            int length = entities.length;
            if (length > 0) {
                Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity);
                if (langInterceptors != null) {
                    List<String> finds = new ArrayList<String>();
                    List<LangInterceptor> interceptors = new ArrayList<LangInterceptor>();
                    for (int i = 0; i < length; i++) {
                        entities[i] = getLangProxy(parent, joEntity, entities[i], nameIds == null ? null : nameIds[i],
                                langInterceptors, finds, interceptors);
                    }

                    getLangProxyFinds(finds, interceptors);
                }
            }
        }

        return entities;
    }

    protected void getLangProxyFinds(List<String> finds, List<LangInterceptor> interceptors) {
        int length = finds.size();
        if (length > 0) {
            String entityName = interceptors.get(0).entityName;
            Map<String, Map<String, Map<String, Object>>> nameMapValues = getLangNameMapValues(entityName, finds);
            for (int i = 0; i < length; i++) {
                String id = finds.get(i);
                Map<String, Map<String, Object>> nameMapValue = nameMapValues.get(id);
                if (nameMapValue == null) {
                    nameMapValue = createLangNameMapValue(entityName, id);
                }

                interceptors.get(i).nameMapValue = nameMapValue;
            }
        }
    }

    protected <T> List<T> getLangProxyList(LangInterceptor parent, JoEntity joEntity, Collection<T> entities, String[][] nameIds,
                                           Map<Method, LangEntry> langInterceptors) {
        List<String> finds = new ArrayList<String>();
        List<LangInterceptor> interceptors = new ArrayList<LangInterceptor>();
        List<T> list = new ArrayList<T>(entities);
        int length = list.size();
        for (int i = 0; i < length; i++) {
            list.set(
                    i,
                    getLangProxy(parent, joEntity, list.get(i), nameIds == null ? null : nameIds[i], langInterceptors, finds,
                            interceptors));
        }

        getLangProxyFinds(finds, interceptors);
        return list;
    }

    public <T> Collection<T> getLangProxyCollection(LangInterceptor parent, JoEntity joEntity, Collection<T> entities,
                                                    String[][] nameIds) {
        if (isI18n() && !entities.isEmpty()) {
            Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity.getEntityName(), joEntity.getEntityClass());
            if (langInterceptors != null) {
                List<T> list = getLangProxyList(parent, joEntity, entities, nameIds, langInterceptors);
                entities.clear();
                entities.addAll(list);
            }
        }

        return entities;
    }

    public <K, V> Map<K, V> getLangProxyMap(LangInterceptor parent, JoEntity joEntity, Map<K, V> entityMap, String[][] nameIds) {
        if (isI18n() && !entityMap.isEmpty()) {
            Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity.getEntityName(), joEntity.getEntityClass());
            if (langInterceptors != null) {
                if (langInterceptors != null) {
                    List<V> list = getLangProxyList(parent, joEntity, entityMap.values(), nameIds, langInterceptors);
                    int i = 0;
                    for (Entry<K, V> entry : entityMap.entrySet()) {
                        entry.setValue(list.get(i++));
                    }
                }
            }
        }

        return entityMap;
    }

    protected LangInterceptor getLangInterceptor(String entityName, String id, Map<Method, LangEntry> langInterceptors) {
        return new LangInterceptor(entityName, id, langInterceptors);
    }

    /**
     * 内置国际化资源写入
     */
    @Stopping
    public void stopping() {
        if (!resourceLangs.isEmpty()) {
            String var = locale.getLanguage();
            if (!KernelString.isEmpty(var)) {
                String resource = langResource + var;
                var = locale.getCountry();
                if (!KernelString.isEmpty(var)) {
                    resource += '_' + var;
                    var = locale.getVariant();
                    if (!KernelString.isEmpty(var)) {
                        resource += '_' + var;
                    }
                }

                File file = new File(resource + "/general.properties");
                BeanConfigImpl.writeProperties(resourceLangs, file);
                Developer.doEntry(file);
            }
        }
    }

    @Override
    public String getLandCode(String lang, Class<?> cls) {
        if (isI18n()) {
            String name = KernelMap.getKey(LangBundle.ME.getResourceBundle(), lang);
            if (!KernelString.isEmpty(name)) {
                String newName = cls.getName() + '.' + name;
                if (LangBundle.ME.getResourceBundle().containsKey(newName)) {
                    lang = newName;

                } else {
                    newName = cls.getSimpleName() + '.' + name;
                    if (LangBundle.ME.getResourceBundle().containsKey(newName)) {
                        lang = newName;
                    }
                }
            }
        }

        return lang;
    }

    protected static interface LangEntry {

        public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                             AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;
    }

    protected static class LangEntryValue extends ObjectEntry<String, Class<?>> implements LangEntry {

        public LangEntryValue(String name, Class<?> toType) {
            super(name, toType);
        }

        @Override
        public Object invoke(LangInterceptor interceptor, Object proxy, Iterator<AopInterceptor> iterator,
                             AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            OnPut onPut = OnPut.get();
            if (onPut != null) {
                Integer locale = onPut.getInput().getLocalCode();
                if (!ME.isLocaleCode(locale)) {
                    Object value = ((ILangBase) proxy).getLang(getKey(), locale, getValue());
                    if (value != null) {
                        return value;
                    }
                }
            }

            return AopProxyHandler.VOID;
        }
    }

    protected static abstract class LangEntryImpl implements LangEntry {

        private LangEntry langEntry;

        public LangEntry getLangEntry() {
            if (langEntry == null) {
                langEntry = generateLangEnry();
            }

            return langEntry;
        }

        protected abstract LangEntry generateLangEnry();
    }

    protected static abstract class LangEmbeded implements LangEntry {

        protected String name;

        protected Accessor accessor;

        public LangEmbeded(Class<?> beanType, String property) {
            name = property;
            accessor = UtilAccessor.getAccessorProperty(beanType, property);
        }

        @Override
        public Object invoke(LangInterceptor langInterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                             AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object value = proxyHandler.invoke(proxyHandler, iterator, method, args, methodProxy);
            if (value != null) {
                if (langInterceptor.embed(method)) {
                    value = langProxy(langInterceptor, value);
                    if (accessor != null && accessor.set(proxyHandler.getBeanObject(), value)) {
                        langInterceptor.setEmbed(method);
                    }
                }
            }

            return value;
        }

        public abstract Object langProxy(LangInterceptor langInterceptor, Object value);
    }

    protected static class LangInterceptor implements AopInterceptor<LangEntry> {

        private String entityName;

        private LangInterceptor parent;

        private String[] nameId;

        private String relateId;

        private String id;

        private Set<Method> embededs;

        private Map<Method, LangEntry> langInterceptors;

        private Map<String, Map<String, Object>> nameMapValue;

        private Map<JEmbedSL, Object> nameLocaleMapValue;

        public LangInterceptor(String entityName, String id, Map<Method, LangEntry> langInterceptors) {
            this.entityName = entityName;
            this.id = id;
            this.relateId = id;
            this.langInterceptors = langInterceptors;
        }

        @Override
        public Class<?> getInterface() {
            return null;
        }

        public void setNameId(LangInterceptor parent, Object entity, String[] nameId, CrudHandler crudHandler) {
            this.parent = parent;
            if (id == null) {
                this.nameId = nameId;
                initId(entity, crudHandler);
            }
        }

        public void initId(Object entity, CrudHandler crudHandler) {
            if (id == null) {
                if (nameId == null) {
                    Object oid = crudHandler == null ? ((IBase) entity).getId() : CrudServiceUtils.identifier(crudHandler
                            .getCrudEntity().getJoEntity().getEntityName(), entity, crudHandler.doCreate());
                    if (oid != null) {
                        id = DynaBinderUtils.to(oid, String.class);
                        relateId = id;
                    }

                } else {
                    if (parent != null && parent.id != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        if (parent.nameId != null && parent.nameId[0] != null) {
                            stringBuilder.append(parent.nameId[0]);
                            stringBuilder.append('.');
                        }

                        stringBuilder.append(nameId[0]);
                        nameId[0] = stringBuilder.toString();

                        stringBuilder = new StringBuilder();
                        if (parent.nameId != null && parent.nameId[1] != null) {
                            stringBuilder.append(parent.nameId[1]);

                        } else {
                            stringBuilder.append(parent.relateId);
                        }

                        if (nameId[1] != null) {
                            stringBuilder.append('.');
                            stringBuilder.append(nameId[1]);
                        }

                        nameId[1] = stringBuilder.toString();
                        id = nameId[0] + "@" + nameId[1];
                    }
                }
            }

            if (id != null && relateId == null) {
                relateId = parent.relateId;
                if (relateId == null) {
                    relateId = id;
                }
            }
        }

        public boolean embed(Method method) {
            return embededs == null || !embededs.contains(method);
        }

        public void setEmbed(Method method) {
            if (embededs == null) {
                embededs = new HashSet<Method>();
            }

            embededs.add(method);
        }

        public void removeEmbed(Method method) {
            if (embededs != null) {
                embededs.remove(method);
            }
        }

        @Override
        public LangEntry getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args)
                throws Throwable {
            return langInterceptors.get(method);
        }

        @Override
        public Object before(Object proxy, Iterator<AopInterceptor> iterator, LangEntry interceptor, AopProxyHandler proxyHandler,
                             Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return interceptor.invoke(this, proxy, iterator, proxyHandler, method, args, methodProxy);
        }

        @Override
        public Object after(Object proxy, Object returnValue, LangEntry interceptor, AopProxyHandler proxyHandler, Method method,
                            Object[] args, Throwable e) throws Throwable {
            return returnValue;
        }

        public Map<String, Map<String, Object>> getNameMapValue() {
            if (nameMapValue == null) {
                nameMapValue = getLangNameMapValue(entityName, id);
            }

            return nameMapValue;
        }

        public void setNameMapValue(Map<String, Map<String, Object>> nameMapValue) {
            this.nameMapValue = nameMapValue;
        }

        public Object getLang(String fieldName, Integer locale, Class<?> type) {
            Object value = null;
            if (nameLocaleMapValue != null) {
                value = DynaBinderUtils.getMapValue(nameLocaleMapValue, new JEmbedSL(fieldName, (long) locale), type);
                if (value != null) {
                    return value;
                }
            }

            if (id != null) {
                Map<String, Object> valueLocale = getNameMapValue().get(fieldName);
                if (valueLocale != null) {
                    value = DynaBinderUtils.getMapValue(valueLocale, "_" + locale, type);
                }
            }

            return value;
        }

        public void setLang(String fieldName, Integer locale, Object value) {
            if (nameLocaleMapValue == null) {
                nameLocaleMapValue = new HashMap<JEmbedSL, Object>();
            }

            nameLocaleMapValue.put(new JEmbedSL(fieldName, (long) locale), value);
        }

        public void setLangValues(String[] values) {
            for (String value : values) {
                String[] langs = value.split(",", 3);
                if (langs.length == 3) {
                    setLang(langs[0], DynaBinder.to(langs[1], Integer.class), langs[2]);
                }
            }
        }

        protected Map<String, Object> getMapValue(Map<String, Map<String, Object>> mapValue, String name, CrudHandler handler) {
            Map<String, Object> value = new HashMap<String, Object>();
            mapValue.put(name, value);
            if (handler.getCrudRecord() != null) {
                handler.getCrudRecord().put(RECORD + entityName + "@" + id, Boolean.TRUE);
            }

            value.put("entity", entityName);
            value.put("id", id);
            value.put("name", name);
            value.put("relateId", relateId);
            return value;
        }

        public void processCrud(Object entity, Crud crud, CrudHandler handler) {
            if (id == null) {
                if (entity == handler.getRoot() && nameId == null) {
                    JoEntity joEntity = handler.getCrudEntity().getJoEntity();
                    String entityName = joEntity == null ? null : joEntity.getEntityName();
                    if (entityName != null) {
                        ICrudSupply crudSupply = CrudService.ME.getCrudSupply(entityName);
                        crudSupply.mergeEntity(entityName, entity, true);
                    }
                }

                initId(entity, handler);
                if (id == null) {
                    return;
                }
            }

            if (crud == Crud.DELETE) {
                if (handler.getCrudRecord() == null || !handler.getCrudRecord().containsKey(RECORD + entityName + "@" + id)) {
                    deleteLangMapValue(entityName, id);
                }
            }

            if (nameMapValue == null && crud == Crud.CREATE) {
                getNameMapValue();
            }

            Map<String, Map<String, Object>> mapValue = new HashMap<String, Map<String, Object>>();
            if (nameMapValue != null) {
                for (Entry<Method, LangEntry> methodEntry : langInterceptors.entrySet()) {
                    LangEntry langEntry = methodEntry.getValue();
                    if (langEntry instanceof Entry) {
                        Entry<String, Class<?>> entry = (Entry<String, Class<?>>) langEntry;
                        if (entry.getKey() != null && !nameMapValue.containsKey(entry.getKey())) {
                            Map<String, Object> value = getMapValue(mapValue, entry.getKey(), handler);
                            value.put("_" + ME.getLocaleCode(),
                                    DynaBinderUtils.to(KernelReflect.invoke(entity, methodEntry.getKey()), String.class));
                        }
                    }
                }

                if (nameLocaleMapValue != null) {
                    for (Entry<JEmbedSL, Object> entry : nameLocaleMapValue.entrySet()) {
                        Map<String, Object> value = mapValue.get(entry.getKey().getEid());
                        if (value == null) {
                            value = getMapValue(mapValue, entry.getKey().getEid(), handler);
                        }

                        value.put("_" + entry.getKey().getMid(), DynaBinderUtils.to(entry.getValue(), String.class));
                    }
                }
            }

            if (!mapValue.isEmpty()) {
                mergeLangMapValue(entityName, id, mapValue);
            }
        }

        public void bind(Object entity, String name, Object value, PropertyData propertyData, BinderData binderData) {
            if (entity instanceof ILangBase) {
                String[] args = value.toString().split(".", 2);
                if (args.length == 2) {
                    ((ILangBase) entity).setLang(name, KernelDyna.toInteger(args[0], 0), args[1]);
                }
            }
        }
    }
}