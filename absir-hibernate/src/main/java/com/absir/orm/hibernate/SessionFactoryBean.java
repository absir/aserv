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

/**
 * @author absir
 */
@Basis
@Bean
public class SessionFactoryBean implements IBeanFactoryStopping {

    /**
     * LOGGER
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(SessionFactoryBean.class);

    /**
     * driverShared
     */
    @Value("driver.shared")
    private boolean driverShared;

    /**
     * stoppingCommands
     */
    @Value("driver.stopping.command")
    private Set<String> driverStoppingCommand;

    /**
     * sessionFactory
     */
    private SessionFactoryImpl sessionFactory;

    /**
     * nameMapSessionFactory
     */
    private Map<String, SessionFactoryImpl> nameMapSessionFactory = new HashMap<String, SessionFactoryImpl>();

    /**
     * sessionFactoryMapName
     */
    private Map<SessionFactoryImpl, String> sessionFactoryMapName = new HashMap<SessionFactoryImpl, String>();

    @Value(value = "entity.assoc.depth")
    private int assocDepth = 8;

    /**
     * nameMapPermissions
     */
    private Map<String, JePermission[]> nameMapPermissions = new HashMap<String, JePermission[]>();

    /**
     * nameMapAssocEntities
     */
    private Map<String, List<AssocEntity>> nameMapAssocEntities = new HashMap<String, List<AssocEntity>>();

    /**
     * nameMapAssocFields
     */
    private Map<String, List<AssocField>> nameMapAssocFields = new HashMap<String, List<AssocField>>();

    /**
     * nameMapEntityAssocEntity
     */
    private Map<String, EntityAssocEntity> nameMapEntityAssocEntity = new HashMap<String, EntityAssocEntity>();

    /**
     * entityNameMapJpaEntityName
     */
    private Map<String, String> entityNameMapJpaEntityName = new HashMap<String, String>();

    /**
     * jpaEntityNameMapEntityClassFactory
     */
    private Map<String, Entry<Class<?>, SessionFactory>> jpaEntityNameMapEntityClassFactory = new HashMap<String, Entry<Class<?>, SessionFactory>>();

    /**
     * @param sessionFactory
     * @return
     */
    public static ConnectionProvider getConnectionProvider(SessionFactoryImpl sessionFactory) {
        try {
            Object connectionProvider = KernelObject.declaredGet(
                    sessionFactory.getJdbcServices().getBootstrapJdbcConnectionAccess(), "connectionProvider");
            return connectionProvider == null || !(connectionProvider instanceof ConnectionProvider) ? null
                    : (ConnectionProvider) connectionProvider;

        } catch (Exception e) {
            if (Environment.getEnvironment() == Environment.DEVELOP) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param connectionProvider
     */
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

    /**
     * @param name
     * @param sessionFactory
     */
    protected void setSessionFactory(String name, SessionFactoryImpl sessionFactory) {
        if (KernelString.isEmpty(name)) {
            this.sessionFactory = sessionFactory;

        } else {
            nameMapSessionFactory.put(name, sessionFactory);
            sessionFactoryMapName.put(sessionFactory, name);
        }
    }

    /**
     * @return the sessionFactory
     */
    public SessionFactoryImpl getSessionFactory() {
        return sessionFactory;
    }

    /**
     * @param name
     * @return
     */
    public SessionFactory getNameMapSessionFactory(String name) {
        return name == null ? sessionFactory : nameMapSessionFactory.get(name);
    }

    /**
     * @param sessionFactory
     * @return
     */
    public String getSessionFactoryMapName(SessionFactory sessionFactory) {
        return sessionFactoryMapName.get(sessionFactory);
    }

    /**
     * @return
     */
    public Set<String> getNameMapSessionFactoryNames() {
        return nameMapSessionFactory.keySet();
    }

    /**
     * @return the assocDepth
     */
    public int getAssocDepth() {
        return assocDepth;
    }

    /**
     * @return the nameMapPermissions
     */
    public Map<String, JePermission[]> getNameMapPermissions() {
        return nameMapPermissions;
    }

    /**
     * @return the nameMapAssocEntities
     */
    public Map<String, List<AssocEntity>> getNameMapAssocEntities() {
        return nameMapAssocEntities;
    }

    /**
     * @return the nameMapAssocFields
     */
    public Map<String, List<AssocField>> getNameMapAssocFields() {
        return nameMapAssocFields;
    }

    /**
     * @return the nameMapEntityAssocEntity
     */
    public Map<String, EntityAssocEntity> getNameMapEntityAssocEntity() {
        return nameMapEntityAssocEntity;
    }

    /**
     * @return the entityNameMapJpaEntityName
     */
    public Map<String, String> getEntityNameMapJpaEntityName() {
        return entityNameMapJpaEntityName;
    }

    /**
     * @return the jpaEntityNameMapEntityClassFactory
     */
    public Map<String, Entry<Class<?>, SessionFactory>> getJpaEntityNameMapEntityClassFactory() {
        return jpaEntityNameMapEntityClassFactory;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.absir.core.kernel.KernelList.Orderable#getOrder()
     */
    @Override
    public int getOrder() {
        return 2048;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.absir.bean.config.IBeanFactoryStopping#stopping(com.absir.bean.basis
     * .BeanFactory)
     */
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
