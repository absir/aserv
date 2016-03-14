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

/**
 * @author absir
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Base(order = -1)
@Bean
public class LangBundleImpl extends LangBundle {

    /**
     * ME
     */
    public static final LangBundleImpl ME = BeanFactoryUtils.get(LangBundleImpl.class);
    /**
     * RECORD
     */
    public static final String RECORD = "LOCALE@";
    /**
     * entityMapIdMapNameMapValue
     */
    private static Map<String, Map<String, Map<String, Map<String, Object>>>> entityMapIdMapNameMapValue = new HashMap<String, Map<String, Map<String, Map<String, Object>>>>();
    /**
     * langConfigureMap
     */
    private Map<String, Object> langConfigureMap;
    /**
     * unLangDeveloperMap
     */
    private Map<String, Object> unLangDeveloperMap;

    /**
     * langInterfaces
     */
    private Set<Class<?>> langInterfaces = new HashSet<Class<?>>();

    /**
     * methodMapLangEntryImpl
     */
    private Map<Method, LangEntryImpl> methodMapLangEntryImpl = new HashMap<Method, LangEntryImpl>();

    /**
     * entityMapLangInterceptors
     */
    private Map<String, Map<Method, LangEntry>> entityMapLangInterceptors = new HashMap<String, Map<Method, LangEntry>>();

    /**
     *
     */
    public LangBundleImpl() {
        langInterfaces.add(ILangBase.class);
        langInterfaces.add(IBinder.class);
        langInterfaces.add(ICrudBean.class);
        InitLangBundleImpl(methodMapLangEntryImpl);
    }

    /**
     * @param entityName
     * @param id
     * @return
     */
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

    /**
     * @param entityName
     * @param id
     * @return
     */
    public static Map<String, Map<String, Object>> findLangNameMapValue(String entityName, String id) {
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            return idMapNameMapValue.get(id);
        }

        return null;
    }

    /**
     * @param entityName
     * @param id
     * @return
     */
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

    /**
     * @param entityName
     * @param ids
     * @return
     */
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

    /**
     * @param entityName
     * @param id
     */
    protected static void deleteLangMapValue(String entityName, String id) {
        BeanService.ME.executeUpdate("DELETE FROM JLocale o WHERE o.entity = ?, o.id = ?", entityName, id);
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            idMapNameMapValue.remove(id);
        }
    }

    /**
     * @param entityName
     * @param mapValues
     */
    protected static void mergeLangMapValue(String entityName, String id, Map<String, Map<String, Object>> nameMapValue) {
        BeanService.ME.mergers("JLocale", nameMapValue.values());
        Map<String, Map<String, Map<String, Object>>> idMapNameMapValue = entityMapIdMapNameMapValue.get(entityName);
        if (idMapNameMapValue != null) {
            idMapNameMapValue.put(id, nameMapValue);
        }
    }

    /**
     * @param entityName
     */
    public static void clearLangNameMapValue(String entityName) {
        entityMapIdMapNameMapValue.remove(entityName);
    }

    /**
     * @param methodMapLangEntryImpl
     */
    private static void InitLangBundleImpl(Map<Method, LangEntryImpl> methodMapLangEntryImpl) {
        for (Method method : ILangBase.class.getMethods()) {
            LangEntryImpl langEntryImpl = null;
            if ("getLang".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        return iterceptor.getLang((String) args[0], (Integer) args[1], (Class<?>) args[2]);
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangIterceptor inIterceptor, Object proxy, Iterator<AopInterceptor> iterator,
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
                    public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        iterceptor.setLang((String) args[0], (Integer) args[1], args[2]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
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
                    public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        iterceptor.setLangValues((String[]) args[0]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
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
                    public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        iterceptor.proccessCrud(proxyHandler.getBeanObject(), (Crud) args[0], (CrudHandler) args[1]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((IBinder) proxyHandler.getBeanObject()).bind((String) args[0], (Object) args[1],
                                        (PropertyData) args[2], (BinderData) args[3]);
                                invoke(iterceptor, proxy, iterator, proxyHandler, method, args, methodProxy);
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
            if ("proccessCrud".equals(method.getName())) {
                langEntryImpl = new LangEntryImpl() {

                    @Override
                    public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                         AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                        iterceptor.proccessCrud(proxyHandler.getBeanObject(), (Crud) args[0], (CrudHandler) args[1]);
                        return null;
                    }

                    @Override
                    public LangEntry generateLangEnry() {
                        return new LangEntry() {

                            @Override
                            public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                                                 AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) {
                                ((ICrudBean) proxyHandler.getBeanObject()).proccessCrud((Crud) args[0], (CrudHandler) args[1]);
                                invoke(iterceptor, proxy, iterator, proxyHandler, method, args, methodProxy);
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

    /**
     * @return the langConfigureMap
     */
    protected Map<String, Object> getLangConfigureMap() {
        if (langConfigureMap != null) {
            langConfigureMap = new HashMap<String, Object>();
            BeanConfigImpl.readProperties(BeanFactoryUtils.getBeanConfig(), langConfigureMap, new File(BeanFactoryUtils
                    .getBeanConfig().getClassPath() + "langConfigure.properties"), null);
        }

        return langConfigureMap;
    }

    /**
     * @param name
     * @return
     */
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

    /**
     * @param lang
     * @param tag
     * @return
     */
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

    /**
     * @param joEntity
     * @return
     */
    protected Map<Method, LangEntry> getLangInterceptors(JoEntity joEntity) {
        return getLangInterceptors(
                joEntity.getEntityName() == null ? joEntity.getEntityClass().getName() : joEntity.getEntityName(),
                joEntity.getEntityClass());
    }

    /**
     * @param entityName
     * @param entityClass
     * @return
     */
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
                                if (method.getAnnotation(Langs.class) != null) {
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
                                                            public Object langProxy(LangIterceptor langIterceptor, Object value) {
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

                                                                return ME.getLangProxyArray(langIterceptor, joEntity, entities,
                                                                        nameIds);
                                                            }
                                                        };

                                                    } else if (Collection.class.isAssignableFrom(returnType)) {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangIterceptor langIterceptor, Object value) {
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

                                                                return ME.getLangProxyCollection(langIterceptor, joEntity,
                                                                        entities, nameIds);
                                                            }
                                                        };

                                                    } else if (Map.class.isAssignableFrom(returnType)) {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangIterceptor langIterceptor, Object value) {
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

                                                                return ME.getLangProxyMap(langIterceptor, joEntity, entities,
                                                                        nameIds);
                                                            }
                                                        };

                                                    } else {
                                                        return new LangEmbeded(beanType, property) {

                                                            @Override
                                                            public Object langProxy(LangIterceptor langIterceptor, Object value) {
                                                                // Auto-generated
                                                                // method stub
                                                                return ME.getLangProxy(langIterceptor, joEntity, value,
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
                                    public Object invoke(LangIterceptor iterceptor, Object proxy,
                                                         Iterator<AopInterceptor> iterator, AopProxyHandler proxyHandler, Method method,
                                                         Object[] args, MethodProxy methodProxy) throws Throwable {
                                        iterceptor.removeEmbed(beanMethod);
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

    /**
     * @param entityName
     * @param entity
     * @return
     */
    public <T> T getLangProxy(String entityName, T entity) {
        if (isI18n() && entity instanceof IBase) {
            return getLangProxy(entityName, entity, ((IBase) entity).getId());
        }

        return entity;
    }

    /**
     * @param entityName
     * @param entity
     * @param id
     * @return
     */
    public <T> T getLangProxy(String entityName, T entity, Object id) {
        if (entity == null) {
            return entity;
        }

        Map<Method, LangEntry> langInterceptors = getLangInterceptors(entityName, entity.getClass());
        if (langInterceptors != null) {
            AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
            proxy.getAopInterceptors().add(getLangIterceptor(entityName, DynaBinderUtils.getParamFromValue(id), langInterceptors));
            return (T) proxy;
        }

        return entity;
    }

    /**
     * @param entityName
     * @param entities
     * @return
     */
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

    /**
     * @param entityName
     * @param entities
     * @param crudSupply
     * @return
     */
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

    /**
     * @param entityName
     * @param entity
     * @return
     */
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

    /**
     * @param entityName
     * @param joEntity
     * @param entity
     * @param id
     * @return
     */
    public <T> T getLangProxy(String entityName, JoEntity joEntity, T entity, Object id) {
        if (isI18n()) {
            Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity);
            if (langInterceptors != null) {
                AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
                proxy.getAopInterceptors().add(
                        getLangIterceptor(joEntity.getEntityName() == null ? entityName : joEntity.getEntityName(),
                                DynaBinderUtils.getParamFromValue(id), langInterceptors));
                return (T) proxy;
            }
        }

        return entity;
    }

    /**
     * @param parent
     * @param joEntity
     * @param entity
     * @param nameId
     * @param langInterceptors
     * @param finds
     * @param iterceptors
     * @return
     */
    protected <T> T getLangProxy(LangIterceptor parent, JoEntity joEntity, T entity, String[] nameId,
                                 Map<Method, LangEntry> langInterceptors, List<String> finds, List<LangIterceptor> iterceptors) {
        AopProxy proxy = AopProxyUtils.getProxy(entity, langInterfaces, false, true);
        LangIterceptor langIterceptor = getLangIterceptor(
                joEntity.getEntityName() == null ? parent.entityName : joEntity.getEntityName(), null, langInterceptors);
        langIterceptor.setNameId(parent, entity, nameId, null);
        String id = langIterceptor.id;
        if (id != null) {
            Map<String, Map<String, Object>> nameMapValue = findLangNameMapValue(langIterceptor.entityName, id);
            if (nameMapValue == null) {
                if (finds != null) {
                    finds.add(id);
                }

                if (iterceptors != null) {
                    iterceptors.add(langIterceptor);
                }

            } else {
                langIterceptor.nameMapValue = nameMapValue;
            }
        }

        proxy.getAopInterceptors().add(langIterceptor);
        return (T) proxy;
    }

    /**
     * @param parent
     * @param joEntity
     * @param entities
     * @param nameIds
     * @return
     */
    public <T> T[] getLangProxyArray(LangIterceptor parent, JoEntity joEntity, T[] entities, String[][] nameIds) {
        if (isI18n()) {
            int length = entities.length;
            if (length > 0) {
                Map<Method, LangEntry> langInterceptors = getLangInterceptors(joEntity);
                if (langInterceptors != null) {
                    List<String> finds = new ArrayList<String>();
                    List<LangIterceptor> iterceptors = new ArrayList<LangIterceptor>();
                    for (int i = 0; i < length; i++) {
                        entities[i] = getLangProxy(parent, joEntity, entities[i], nameIds == null ? null : nameIds[i],
                                langInterceptors, finds, iterceptors);
                    }

                    getLangProxyFinds(finds, iterceptors);
                }
            }
        }

        return entities;
    }

    /**
     * @param finds
     * @param iterceptors
     */
    protected void getLangProxyFinds(List<String> finds, List<LangIterceptor> iterceptors) {
        int length = finds.size();
        if (length > 0) {
            String entityName = iterceptors.get(0).entityName;
            Map<String, Map<String, Map<String, Object>>> nameMapValues = getLangNameMapValues(entityName, finds);
            for (int i = 0; i < length; i++) {
                String id = finds.get(i);
                Map<String, Map<String, Object>> nameMapValue = nameMapValues.get(id);
                if (nameMapValue == null) {
                    nameMapValue = createLangNameMapValue(entityName, id);
                }

                iterceptors.get(i).nameMapValue = nameMapValue;
            }
        }
    }

    /**
     * @param parent
     * @param joEntity
     * @param entities
     * @param nameIds
     * @param langInterceptors
     * @return
     */
    protected <T> List<T> getLangProxyList(LangIterceptor parent, JoEntity joEntity, Collection<T> entities, String[][] nameIds,
                                           Map<Method, LangEntry> langInterceptors) {
        List<String> finds = new ArrayList<String>();
        List<LangIterceptor> iterceptors = new ArrayList<LangIterceptor>();
        List<T> list = new ArrayList<T>(entities);
        int length = list.size();
        for (int i = 0; i < length; i++) {
            list.set(
                    i,
                    getLangProxy(parent, joEntity, list.get(i), nameIds == null ? null : nameIds[i], langInterceptors, finds,
                            iterceptors));
        }

        getLangProxyFinds(finds, iterceptors);
        return list;
    }

    /**
     * @param parent
     * @param joEntity
     * @param entities
     * @param nameIds
     * @return
     */
    public <T> Collection<T> getLangProxyCollection(LangIterceptor parent, JoEntity joEntity, Collection<T> entities,
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

    /**
     * @param parent
     * @param joEntity
     * @param entityMap
     * @param nameIds
     * @return
     */
    public <K, V> Map<K, V> getLangProxyMap(LangIterceptor parent, JoEntity joEntity, Map<K, V> entityMap, String[][] nameIds) {
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

    /**
     * @param entityName
     * @param id
     * @param langInterceptors
     * @return
     */
    protected LangIterceptor getLangIterceptor(String entityName, String id, Map<Method, LangEntry> langInterceptors) {
        return new LangIterceptor(entityName, id, langInterceptors);
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

    /**
     * @author absir
     */
    protected static interface LangEntry {

        /**
         * @param iterceptor
         * @param proxy
         * @param iterator
         * @param proxyHandler
         * @param method
         * @param args
         * @param methodProxy
         * @return
         * @throws Throwable
         */
        public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                             AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable;
    }

    /**
     * @author absir
     */
    protected static class LangEntryValue extends ObjectEntry<String, Class<?>> implements LangEntry {

        /**
         * @param name
         * @param toType
         */
        public LangEntryValue(String name, Class<?> toType) {
            super(name, toType);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.absir.aserv.lang.LangBundleImpl.LangEntry#invoke(com.absir.
         * aserv.lang.LangBundleImpl.LangIterceptor, java.lang.Object,
         * java.util.Iterator, com.absir.aop.AopProxyHandler,
         * java.lang.reflect.Method, java.lang.Object[],
         * net.sf.cglib.proxy.MethodProxy)
         */
        @Override
        public Object invoke(LangIterceptor iterceptor, Object proxy, Iterator<AopInterceptor> iterator,
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

    /**
     * @author absir
     */
    protected static abstract class LangEntryImpl implements LangEntry {

        /**
         * langEntry
         */
        private LangEntry langEntry;

        /**
         * @return
         */
        public LangEntry getLangEntry() {
            if (langEntry == null) {
                langEntry = generateLangEnry();
            }

            return langEntry;
        }

        /**
         * @return
         */
        protected abstract LangEntry generateLangEnry();
    }

    /**
     * @author absir
     */
    protected static abstract class LangEmbeded implements LangEntry {

        /**
         * name
         */
        protected String name;

        /**
         * accessor
         */
        protected Accessor accessor;

        /**
         * @param beanType
         * @param property
         */
        public LangEmbeded(Class<?> beanType, String property) {
            name = property;
            accessor = UtilAccessor.getAccessorProperty(beanType, property);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.absir.aserv.lang.LangBundleImpl.LangEntry#invoke(com.absir.
         * aserv.lang.LangBundleImpl.LangIterceptor, java.lang.Object,
         * java.util.Iterator, com.absir.aop.AopProxyHandler,
         * java.lang.reflect.Method, java.lang.Object[],
         * net.sf.cglib.proxy.MethodProxy)
         */
        @Override
        public Object invoke(LangIterceptor langIterceptor, Object proxy, Iterator<AopInterceptor> iterator,
                             AopProxyHandler proxyHandler, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object value = proxyHandler.invoke(proxyHandler, iterator, method, args, methodProxy);
            if (value != null) {
                if (langIterceptor.embed(method)) {
                    value = langProxy(langIterceptor, value);
                    if (accessor != null && accessor.set(proxyHandler.getBeanObject(), value)) {
                        langIterceptor.setEmbed(method);
                    }
                }
            }

            return value;
        }

        /**
         * @param langIterceptor
         * @param value
         * @return
         */
        public abstract Object langProxy(LangIterceptor langIterceptor, Object value);
    }

    /**
     * @author absir
     */
    protected static class LangIterceptor implements AopInterceptor<LangEntry> {

        /**
         * entityName
         */
        private String entityName;

        /**
         * parent
         */
        private LangIterceptor parent;

        /**
         * nameId
         */
        private String[] nameId;

        /**
         * relateId
         */
        private String relateId;

        /**
         * id
         */
        private String id;

        /**
         * embededs
         */
        private Set<Method> embededs;

        /**
         * langInterceptors
         */
        private Map<Method, LangEntry> langInterceptors;

        /**
         * nameMapValue
         */
        private Map<String, Map<String, Object>> nameMapValue;

        /**
         * nameLocaleMapValue
         */
        private Map<JEmbedSL, Object> nameLocaleMapValue;

        /**
         * @param entityName
         * @param langInterceptors
         */
        public LangIterceptor(String entityName, String id, Map<Method, LangEntry> langInterceptors) {
            this.entityName = entityName;
            this.id = id;
            this.relateId = id;
            this.langInterceptors = langInterceptors;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.aop.AopInterceptor#getInterface()
         */
        @Override
        public Class<?> getInterface() {
            return null;
        }

        /**
         * @param parent
         * @param entity
         * @param nameId
         * @param crudHandler
         */
        public void setNameId(LangIterceptor parent, Object entity, String[] nameId, CrudHandler crudHandler) {
            this.parent = parent;
            if (id == null) {
                this.nameId = nameId;
                initId(entity, crudHandler);
            }
        }

        /**
         * @param entity
         * @param crudHandler
         */
        public void initId(Object entity, CrudHandler crudHandler) {
            if (id == null) {
                if (nameId == null) {
                    Object oid = crudHandler == null ? ((IBase) entity).getId() : CrudServiceUtils.identifier(crudHandler
                            .getCrudEntity().getJoEntity().getEntityName(), entity, crudHandler.isCreate());
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

        /**
         * @param embed
         * @return
         */
        public boolean embed(Method method) {
            return embededs == null || !embededs.contains(method);
        }

        /**
         * @param method
         */
        public void setEmbed(Method method) {
            if (embededs == null) {
                embededs = new HashSet<Method>();
            }

            embededs.add(method);
        }

        /**
         * @param method
         */
        public void removeEmbed(Method method) {
            if (embededs != null) {
                embededs.remove(method);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * com.absir.aop.AopInterceptor#getInterceptor(com.absir.aop.AopProxyHandler
         * , java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
         */
        @Override
        public LangEntry getInterceptor(AopProxyHandler proxyHandler, Object beanObject, Method method, Object[] args)
                throws Throwable {
            return langInterceptors.get(method);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.aop.AopInterceptor#before(java.lang.Object,
         * java.util.Iterator, java.lang.Object, com.absir.aop.AopProxyHandler,
         * java.lang.reflect.Method, java.lang.Object[],
         * net.sf.cglib.proxy.MethodProxy)
         */
        @Override
        public Object before(Object proxy, Iterator<AopInterceptor> iterator, LangEntry interceptor, AopProxyHandler proxyHandler,
                             Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            return interceptor.invoke(this, proxy, iterator, proxyHandler, method, args, methodProxy);
        }

        /*
         * (non-Javadoc)
         *
         * @see com.absir.aop.AopInterceptor#after(java.lang.Object,
         * java.lang.Object, java.lang.Object, com.absir.aop.AopProxyHandler,
         * java.lang.reflect.Method, java.lang.Object[], java.lang.Throwable)
         */
        @Override
        public Object after(Object proxy, Object returnValue, LangEntry interceptor, AopProxyHandler proxyHandler, Method method,
                            Object[] args, Throwable e) throws Throwable {
            return returnValue;
        }

        /**
         * @return the nameMapValue
         */
        public Map<String, Map<String, Object>> getNameMapValue() {
            if (nameMapValue == null) {
                nameMapValue = getLangNameMapValue(entityName, id);
            }

            return nameMapValue;
        }

        /**
         * @param nameMapValue the nameMapValue to set
         */
        public void setNameMapValue(Map<String, Map<String, Object>> nameMapValue) {
            this.nameMapValue = nameMapValue;
        }

        /**
         * @param fieldName
         * @param locale
         * @param type
         * @return
         */
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

        /**
         * @param fieldName
         * @param locale
         * @param value
         */
        public void setLang(String fieldName, Integer locale, Object value) {
            if (nameLocaleMapValue == null) {
                nameLocaleMapValue = new HashMap<JEmbedSL, Object>();
            }

            nameLocaleMapValue.put(new JEmbedSL(fieldName, (long) locale), value);
        }

        /**
         * @param values
         */
        public void setLangValues(String[] values) {
            for (String value : values) {
                String[] langs = value.split(",", 3);
                if (langs.length == 3) {
                    setLang(langs[0], DynaBinder.to(langs[1], Integer.class), langs[2]);
                }
            }
        }

        /**
         * @param mapValue
         * @param name
         * @param handler
         * @return
         */
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

        /**
         * @param entity
         * @param crud
         * @param handler
         */
        public void proccessCrud(Object entity, Crud crud, CrudHandler handler) {
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

        /**
         * @param entity
         * @param name
         * @param value
         * @param propertyData
         * @param binderData
         */
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