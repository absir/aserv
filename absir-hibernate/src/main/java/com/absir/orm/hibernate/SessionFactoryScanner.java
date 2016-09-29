/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-21 下午7:09:05
 */
package com.absir.orm.hibernate;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanDefineScanner;
import com.absir.bean.config.IBeanDefineSupply;
import com.absir.bean.core.BeanConfigImpl;
import com.absir.bean.core.BeanFactoryImpl;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.context.lang.LangBundle;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.EntityAssoc;
import com.absir.orm.value.JaConfig;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionFactoryImpl;

import javax.persistence.Entity;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

@SuppressWarnings("unchecked")
@Basis
@Bean
public class SessionFactoryScanner implements IBeanDefineSupply, IBeanDefineScanner {

    private List<Class<?>> entityClasses = new ArrayList<Class<?>>();

    @Inject
    private void setSessionFactoryBoost(SessionFactoryBoost sessionFactoryBoost) {
        BeanFactory beanFactory = BeanFactoryUtils.get();
        ConfigurationBoost.sessionFactoryBoost = sessionFactoryBoost;
        File hibernate = new File(beanFactory.getBeanConfig().getClassPath() + "hibernate");
        if (hibernate.exists() && hibernate.isDirectory()) {
            final List<String> names = new ArrayList<String>();
            File[] configFiles = hibernate.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    int length = name.length();
                    if (length > 9 && name.endsWith(".cfg.xml")) {
                        name = name.substring(0, length - 8);
                        if ("hibernate".equals(name)) {
                            name = KernelLang.NULL_STRING;
                        }

                        names.add(name);
                        return true;
                    }

                    return false;
                }
            });

            int size = names.size();
            List<ConfigurationBoost> configurationBoosts = new ArrayList<ConfigurationBoost>();
            for (int i = 0; i < size; i++) {
                ConfigurationBoost configurationBoost = new ConfigurationBoost();
                configurationBoost.configure(configFiles[i]);
                configurationBoosts.add(configurationBoost);
            }

            for (Class<?> entityClass : entityClasses) {
                JaConfig jaConfig = BeanConfigImpl.getTypeAnnotation(entityClass, JaConfig.class);
                if (jaConfig != null && (jaConfig.value().length == 0 || KernelString.isEmpty(jaConfig.value()[0]))) {
                    jaConfig = null;
                }

                for (int i = 0; i < size; i++) {
                    String name = names.get(i);
                    Configuration config = configurationBoosts.get(i);
                    if (jaConfig == null) {
                        if (name == KernelLang.NULL_STRING) {
                            config.addAnnotatedClass(entityClass);
                            break;
                        }

                    } else if (KernelArray.contain(jaConfig.value(), name)) {
                        config.addAnnotatedClass(entityClass);
                    }
                }
            }

            for (int i = 0; i < size; i++) {
                String name = names.get(i);
                ConfigurationBoost configurationBoost = configurationBoosts.get(i);
                if (name == KernelLang.NULL_STRING && LangBundle.isI18n()) {
                    configurationBoost.boostLocale();
                }

                Properties properties = configurationBoost.getProperties();
                for (Entry<Object, Object> entry : properties.entrySet()) {
                    Object value = entry.getValue();
                    if (value != null && value instanceof String) {
                        entry.setValue(beanFactory.getBeanConfig().getExpression((String) value));
                    }
                }

                SessionFactoryImpl sessionFactory = (SessionFactoryImpl) configurationBoost
                        .buildSessionFactory(new StandardServiceRegistryBuilder().applySettings(properties).build());
                SessionFactoryUtils.get().setSessionFactory(name, sessionFactory);
            }
        }

        entityClasses.clear();
        EntityAssoc.boost(SessionFactoryUtils.get());
    }

    @Override
    public int getOrder() {
        return -256;
    }

    @Override
    public List<BeanDefine> getBeanDefines(BeanFactoryImpl beanFactory, Class<?> beanType) {
        if (BeanConfigImpl.getTypeAnnotation(beanType, Entity.class) != null) {
            entityClasses.add(beanType);
            //return KernelLang.NULL_LIST_SET;
        }

        return null;
    }
}
