/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-13 下午5:08:29
 */
package com.absir.aserv.configure;

import com.absir.aop.AopBeanDefine;
import com.absir.aserv.dyna.DynaBinderUtils;
import com.absir.aserv.init.InitBeanFactory;
import com.absir.aserv.lang.LangBundleImpl;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.helper.HelperAccessor;
import com.absir.aserv.system.service.BeanService;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked"})
public abstract class JConfigureUtils {

    private static Map<Serializable, JConfigureBase> Configure_Class_Map_Instance = new HashMap<Serializable, JConfigureBase>();

    private static Map<Class<? extends JConfigureBase>, Class<? extends JConfigureBase>> Configure_Class_Map_Class = new HashMap<Class<? extends JConfigureBase>, Class<? extends JConfigureBase>>();

    public static <T> T getOption(String key, Class<T> toClass) {
        JConfigure configure = getConfigureKey(key);
        return DynaBinderUtils.to(configure == null ? null : configure.getValue(), toClass);
    }

    public static void setOption(String key, Object option) {
        setConfigureKey(key, DynaBinderUtils.to(option, String.class));
    }

    public static JConfigure getConfigureKey(String key) {
        JEmbedSS embedSS = new JEmbedSS(InitBeanFactory.ME.getAppCode(), key);
        return BeanService.ME.get(JConfigure.class, embedSS);
    }

    public static void setConfigureKey(String key, String value) {
        JEmbedSS embedSS = new JEmbedSS(InitBeanFactory.ME.getAppCode(), key);
        JConfigure configure = new JConfigure();
        configure.setId(embedSS);
        configure.setValue(value);
        BeanService.ME.merge(configure);
    }

    public static void put(Class<? extends JConfigureBase> cls, Class<? extends JConfigureBase> configureClass) {
        Configure_Class_Map_Class.put(cls, configureClass);
    }

    public static <T extends JConfigureBase> T getConfigure(Class<T> cls) {
        JConfigureBase configure = Configure_Class_Map_Instance.get(cls);
        if (configure == null) {
            synchronized (cls) {
                configure = Configure_Class_Map_Instance.get(cls);
                if (configure == null) {
                    Class<? extends JConfigureBase> configureClass = Configure_Class_Map_Class.get(cls);
                    if (configureClass == null) {
                        configure = KernelClass.newInstance(cls);
                        configure = LangBundleImpl.ME == null ? configure : LangBundleImpl.ME.getLangProxy(cls.getSimpleName(),
                                configure);
                        try {
                            initConfigure(configure);

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    } else {
                        configure = getConfigure(configureClass);
                    }

                    Configure_Class_Map_Instance.put(cls, configure);
                }
            }
        }

        return (T) configure;
    }

    private static void initConfigure(final JConfigureBase configureBase) {
        String identitier = configureBase.getIdentitier();
        Map<String, JConfigure> configureMap = new HashMap<String, JConfigure>();
        for (JConfigure configure : (List<JConfigure>) BeanService.ME.list("JConfigure", null, 0, 0, "o.id.eid", identitier)) {
            configureMap.put(configure.getId().getMid(), configure);
        }

        for (Field field : HelperAccessor.getFields(configureBase.getClass())) {
            JConfigure configure = configureMap.get(field.getName());
            if (configure == null) {
                configure = new JConfigure();
                configure.setId(new JEmbedSS(identitier, field.getName()));

            } else {
                KernelObject.declaredSetter(configureBase, field, configureBase.set(configure.getValue(), field));
            }

            configureBase.fieldMapConfigure.put(field, configure);
        }
    }

    public static <T extends JConfigureBase> String getConfigureId(Class<T> cls, Object... args) {
        return cls.getName() + KernelString.implode(args, ',');
    }

    public static <T extends JConfigureBase> T getConfigure(Class<T> cls, String configureKey, Object... initargs) {
        JConfigureBase configure = Configure_Class_Map_Instance.get(configureKey);
        if (configure == null) {
            synchronized (JConfigureUtils.class) {
                configure = Configure_Class_Map_Instance.get(configureKey);
                if (configure == null) {
                    configure = AopBeanDefine.instanceBeanObject(cls, initargs);
                    try {
                        initConfigure(configure);

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    Configure_Class_Map_Instance.put(configureKey, configure);
                }
            }
        }

        return (T) configure;
    }

    public static <T extends JConfigureBase> void clearConfigure(Class<T> cls) {
        synchronized (cls) {
            JConfigureBase configure = Configure_Class_Map_Instance.get(cls);
            if (configure != null) {
                configure.merge();
                Configure_Class_Map_Instance.remove(cls);
            }
        }
    }

    public static <T extends JConfigureBase> void clearConfigure(Class<T> cls, Object... initargs) {
        clearConfigure(getConfigureId(cls, initargs));
    }

    public static <T extends JConfigureBase> void clearConfigure(String configureKey) {
        synchronized (JConfigureUtils.class) {
            JConfigureBase configure = Configure_Class_Map_Instance.get(configureKey);
            if (configure != null) {
                configure.merge();
                Configure_Class_Map_Instance.remove(configureKey);
            }
        }
    }

    public <T extends JConfigureBase> T getConfigure(Class<T> cls, Object... initargs) {
        return initargs.length == 0 ? getConfigure(cls) : getConfigure(cls, getConfigureId(cls, initargs), initargs);
    }
}
