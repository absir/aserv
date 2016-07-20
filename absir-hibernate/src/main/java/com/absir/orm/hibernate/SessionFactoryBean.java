/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-1-22 上午9:49:44
 */
package com.absir.orm.hibernate;

import com.absir.bean.basis.Basis;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.config.IBeanFactoryStopping;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Value;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelReflect;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocEntity;
import com.absir.orm.hibernate.boost.EntityAssoc.AssocField;
import com.absir.orm.hibernate.boost.EntityAssoc.EntityAssocEntity;
import com.absir.orm.value.JePermission;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.Map.Entry;

@Basis
@Bean
public class SessionFactoryBean implements IBeanFactoryStopping {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryBean.class);
    @Value("driver.shared")
    private boolean driverShared;
    @Value("driver.stopping.command")
    private Set<String> driverStoppingCommand;
    private SessionFactoryImpl sessionFactory;
    private Map<String, SessionFactoryImpl> nameMapSessionFactory = new HashMap<String, SessionFactoryImpl>();
    private Map<SessionFactoryImpl, String> sessionFactoryMapName = new HashMap<SessionFactoryImpl, String>();
    @Value(value = "entity.assoc.depth")
    private int assocDepth = 8;
    private Map<String, JePermission[]> nameMapPermissions = new HashMap<String, JePermission[]>();
    private Map<String, List<AssocEntity>> nameMapAssocEntities = new HashMap<String, List<AssocEntity>>();
    private Map<String, List<AssocField>> nameMapAssocFields = new HashMap<String, List<AssocField>>();
    private Map<String, EntityAssocEntity> nameMapEntityAssocEntity = new HashMap<String, EntityAssocEntity>();
    private Map<String, String> entityNameMapJpaEntityName = new HashMap<String, String>();
    private Map<String, Entry<Class<?>, SessionFactory>> jpaEntityNameMapEntityClassFactory = new HashMap<String, Entry<Class<?>, SessionFactory>>();

    public static ConnectionProvider getConnectionProvider(SessionFactoryImpl sessionFactory) {
        try {
            Object connectionProvider = KernelObject.declaredGet(
                    sessionFactory.getJdbcServices().getBootstrapJdbcConnectionAccess(), "connectionProvider");
            return connectionProvider == null || !(connectionProvider instanceof ConnectionProvider) ? null
                    : (ConnectionProvider) connectionProvider;

        } catch (Exception e) {
            Environment.throwable(e);
        }

        return null;
    }

    public static void stopConnectionProvider(SessionFactoryImpl sessionFactory) {
        if (sessionFactory == null) {
            return;
        }

        try {
            sessionFactory.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectionProvider connectionProvider = getConnectionProvider(sessionFactory);
        if (connectionProvider == null) {
            return;
        }

        Method method = KernelReflect.declaredMethod(connectionProvider.getClass(), "close");
        if (method == null) {
            method = KernelReflect.declaredMethod(connectionProvider.getClass(), "stop");
            if (method == null) {
                method = KernelReflect.declaredMethod(connectionProvider.getClass(), "destory");
            }
        }

        if (method == null) {
            LOGGER.info("stop " + connectionProvider + " failed");

        } else {
            try {
                LOGGER.info("stop " + connectionProvider + " at " + method.getName());
                method.invoke(connectionProvider);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Object ds = KernelObject.declaredGet(connectionProvider, "ds");
        if (ds != null) {
            KernelObject.declaredSend(ds, "close", true);
        }
    }

    @Inject
    public void setSessionFactoryScanner(SessionFactoryScanner scanner) {
        LOGGER.info("setSessionFactoryScanner " + scanner);
    }

    protected void setSessionFactory(String name, SessionFactoryImpl sessionFactory) {
        if (KernelString.isEmpty(name)) {
            this.sessionFactory = sessionFactory;

        } else {
            nameMapSessionFactory.put(name, sessionFactory);
            sessionFactoryMapName.put(sessionFactory, name);
        }
    }

    public SessionFactoryImpl getSessionFactory() {
        return sessionFactory;
    }

    public SessionFactory getNameMapSessionFactory(String name) {
        return name == null ? sessionFactory : nameMapSessionFactory.get(name);
    }

    public String getSessionFactoryMapName(SessionFactory sessionFactory) {
        return sessionFactoryMapName.get(sessionFactory);
    }

    public Set<String> getNameMapSessionFactoryNames() {
        return nameMapSessionFactory.keySet();
    }

    public int getAssocDepth() {
        return assocDepth;
    }

    public Map<String, JePermission[]> getNameMapPermissions() {
        return nameMapPermissions;
    }

    public Map<String, List<AssocEntity>> getNameMapAssocEntities() {
        return nameMapAssocEntities;
    }

    public Map<String, List<AssocField>> getNameMapAssocFields() {
        return nameMapAssocFields;
    }

    public Map<String, EntityAssocEntity> getNameMapEntityAssocEntity() {
        return nameMapEntityAssocEntity;
    }

    public Map<String, String> getEntityNameMapJpaEntityName() {
        return entityNameMapJpaEntityName;
    }

    public Map<String, Entry<Class<?>, SessionFactory>> getJpaEntityNameMapEntityClassFactory() {
        return jpaEntityNameMapEntityClassFactory;
    }

    @Override
    public int getOrder() {
        return 2048;
    }

    @Override
    public void stopping(BeanFactory beanFactory) {
        LOGGER.info("stop begin");
        stopConnectionProvider(sessionFactory);
        for (SessionFactoryImpl sessionFactory : sessionFactoryMapName.keySet()) {
            stopConnectionProvider(sessionFactory);
        }

        Environment.setStarted(false);
        if (!driverShared) {
            Enumeration<Driver> enumeration = DriverManager.getDrivers();
            while (enumeration.hasMoreElements()) {
                Driver driver = enumeration.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);

                } catch (Exception e) {
                    LOGGER.error("deregisterDriver " + driver, e);
                }
            }
        }

        if (driverStoppingCommand != null) {
            for (String stoppingCommand : driverStoppingCommand) {
                KernelClass.invokeCommandString(stoppingCommand);
            }

            driverStoppingCommand = null;
        }

        LOGGER.info("stop complete");
    }
}
