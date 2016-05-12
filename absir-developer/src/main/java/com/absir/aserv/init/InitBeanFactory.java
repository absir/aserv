/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年1月26日 下午1:34:07
 */
package com.absir.aserv.init;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.utils.SearchServiceUtils;
import com.absir.bean.basis.Base;
import com.absir.bean.basis.BeanDefine;
import com.absir.bean.basis.BeanFactory;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.InjectOrder;
import com.absir.bean.inject.value.Value;
import com.absir.context.config.BeanProviderContext;
import com.absir.core.base.Environment;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.helper.HelperFile;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.BreakException;
import com.absir.core.kernel.KernelLang.CallbackBreak;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelUtil;
import com.absir.core.util.UtilAccessor;
import com.absir.orm.hibernate.SessionFactoryBean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import com.absir.orm.value.JoEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

@Base
@Bean
public class InitBeanFactory {

    public static final InitBeanFactory ME = BeanFactoryUtils.get(InitBeanFactory.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(InitBeanFactory.class);

    @Value(value = "appName")
    protected String appName;

    @Value(value = "appRoute")
    protected String appRoute;

    protected String oldVersion;

    @Value("appCode")
    protected String appCode = "";

    @Value("version")
    protected String version = "0.0.1";

    @Value("versionName")
    protected String versionName = "developer";

    @Value("initCheck")
    protected boolean initCheck = true;

    private int versionSqlCount = 0;

    private String versionRevert;

    private Map<String, Object> nameMapInitBean = new HashMap<String, Object>();

    public boolean isRequireInit() {
        return initCheck && KernelObject.equals(oldVersion, "0");
    }

    public boolean isVersionChange() {
        return initCheck && (BeanFactoryUtils.getEnvironment() == Environment.DEVELOP || !KernelObject.equals(oldVersion, version));
    }

    /**
     * 升级删除文件
     *
     * @param old
     * @param version
     */
    public static void upgradeRM(String old, String version) {
        final String classPath = BeanFactoryUtils.getBeanConfig().getClassPath();
        File rmFile = new File(classPath + "rm/");
        if (rmFile.exists() && rmFile.isDirectory()) {
            for (File file : rmFile.listFiles()) {
                String fileVersion = HelperFileName.getBaseName(file.getName());
                if (!file.isDirectory() && (old == null || KernelUtil.compareVersion(old, fileVersion) <= 0)
                        && (version != null && KernelUtil.compareVersion(version, fileVersion) > 0)) {
                    CallbackBreak<String> rmCallbackBreak = new CallbackBreak<String>() {

                        @Override
                        public void doWith(String template) throws BreakException {
                            File file = new File(classPath + template);
                            if (file.exists()) {
                                HelperFile.deleteQuietly(file);
                            }
                        }
                    };

                    try {
                        HelperFile.doWithReadLine(file, rmCallbackBreak);

                    } catch (IOException e) {
                        LOGGER.error("upgradeRM " + file, e);
                    }
                }
            }
        }
    }

    public String getAppName() {
        return appName;
    }

    public String getAppRoute() {
        return appRoute;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getVersion() {
        return version;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public boolean isInitCheck() {
        return initCheck;
    }

    public void setInitCheck(boolean initCheck) {
        this.initCheck = initCheck;
    }

    public int getVersionSqlCount() {
        return versionSqlCount;
    }

    public String getVersionRevert() {
        return versionRevert;
    }

    public Object getNameInitBean(String name) {
        return nameMapInitBean.get(name);
    }

    public void putNameInitBean(String name, Object bean) {
        if (bean == null) {
            nameMapInitBean.remove(name);

        } else {
            nameMapInitBean.put(name, bean);
        }
    }

    /**
     * 开始初始化
     */
    @InjectOrder(-2048)
    @Inject
    public void start() {
        JEmbedSS embedSS = new JEmbedSS();
        embedSS.setEid(appCode);
        embedSS.setMid("version");
        JConfigure configure = BeanService.ME.get(JConfigure.class, embedSS);
        oldVersion = configure == null ? "0" : configure.getValue();
        if (isVersionChange()) {
            // 数据升级
            upgradeData(embedSS, configure);
            initData(BeanFactoryUtils.getBeanConfig().getClassPath() + "data/");
        }
    }

    /**
     * 数据升级
     *
     * @param embedSS
     * @param configure
     */
    protected void upgradeData(JEmbedSS embedSS, JConfigure configure) {
        String old = oldVersion;
        if (KernelUtil.compareVersion(old, version) < 0) {
            SessionFactoryBean sessionFactoryBean = SessionFactoryUtils.get();
            if (old.equals("0")) {
                old = null;
            }

            upgradeRM(old, version);
            upgradeData(null, old, sessionFactoryBean.getSessionFactory());
            for (String name : sessionFactoryBean.getNameMapSessionFactoryNames()) {
                upgradeData(name, old, sessionFactoryBean.getNameMapSessionFactory(name));
            }

            if (configure == null) {
                configure = new JConfigure();
                configure.setId(embedSS);
            }

            configure.setValue(this.version);
            BeanService.ME.merge(configure);
        }

        // 计算最小可以恢复版本
        embedSS = new JEmbedSS();
        embedSS.setEid(appCode);
        embedSS.setMid("version.revert");
        if (versionSqlCount <= 0) {
            configure = BeanService.ME.get(JConfigure.class, embedSS);
            versionRevert = configure == null ? "0.0.1" : configure.getValue();

        } else {
            configure = new JConfigure();
            configure.setId(embedSS);
            configure.setValue(version);
            BeanService.ME.merge(configure);
            versionRevert = version;
        }
    }

    /**
     * 数据库升级
     *
     * @param name
     * @param old
     * @param sessionFactory
     */
    protected void upgradeData(final String name, String old, SessionFactory sessionFactory) {
        JdbcConnectionAccess connectionProvider = ((SessionFactoryImpl) sessionFactory).getJdbcServices()
                .getBootstrapJdbcConnectionAccess();
        Connection connection = null;
        try {
            connection = connectionProvider.obtainConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            File upgradeFile = KernelUtil.mustMatchFile(BeanFactoryUtils.getBeanConfig().getClassPath() + "/upgrades",
                    name, metaData.getDatabaseProductName(), metaData.getDatabaseProductVersion());
            if (upgradeFile != null && upgradeFile.isDirectory()) {
                Map<String, VersionSqlName> versionMap = new HashMap<String, VersionSqlName>();
                // 直接初始化版本
                String iVersion = null;
                for (String fileName : upgradeFile.list()) {
                    int length = fileName.length();
                    if (length > 4 && fileName.endsWith(".sql")) {
                        VersionSqlName sqlName = new VersionSqlName(fileName.substring(0, length - 4));
                        if ((old == null || KernelUtil.compareVersion(old, sqlName.getVersion()) < 0)
                                && KernelUtil.compareVersion(version, sqlName.getVersion()) >= 0) {
                            if (old == null && sqlName.getType() == 1) {
                                // 直接初始化版本，最低版本
                                if (iVersion == null || KernelUtil.compareVersion(iVersion, sqlName.getVersion()) < 0) {
                                    iVersion = sqlName.getVersion();
                                }
                            }

                            if (iVersion == null || KernelUtil.compareVersion(iVersion, sqlName.getVersion()) <= 0) {
                                versionMap.put(sqlName.getVersionOrderName(), sqlName);
                            }
                        }
                    }
                }

                if (!versionMap.isEmpty()) {
                    List<String> versions = new ArrayList<String>(versionMap.keySet());
                    Collections.sort(versions, KernelUtil.VERSION_COMPARATOR);
                    for (final String version : versions) {
                        VersionSqlName sqlName = versionMap.get(version);
                        if (iVersion == null) {
                            if (sqlName.getType() == 1) {
                                continue;
                            }

                        } else {
                            int compare = KernelUtil.compareVersion(iVersion, sqlName.getVersion());
                            if (compare < 0 || (compare == 0 && sqlName.getType() == 0)) {
                                continue;
                            }
                        }

                        // 执行版本相关文件
                        versionSqlCount++;
                        File versionSql = new File(upgradeFile, sqlName.getSqlName() + ".sql");
                        try {
                            final Statement statement = connection.createStatement();
                            HelperFile.doWithReadLine(versionSql, new CallbackBreak<String>() {

                                private boolean annotation;

                                private StringBuilder stringBuilder = new StringBuilder();

                                @Override
                                public void doWith(String template) throws BreakException {
                                    template = template.trim();
                                    if (!annotation) {
                                        // 常用注释
                                        if (template.startsWith("--")) {
                                            return;
                                        }

                                        if (template.startsWith("/*")) {
                                            annotation = true;
                                        }
                                    }

                                    if (annotation) {
                                        if (template.endsWith("*/")) {
                                            // 注释完毕
                                            annotation = false;
                                            return;
                                        }

                                    } else {
                                        stringBuilder.append(template);
                                        if (template.endsWith(";")) {
                                            // 执行升级语句
                                            String sql = stringBuilder.toString();
                                            stringBuilder = new StringBuilder();
                                            try {
                                                statement.execute(sql);

                                            } catch (Exception e) {
                                                LOGGER.error("upgradeData " + name + " : " + version + " => " + sql, e);
                                            }
                                        }
                                    }
                                }
                            });

                            statement.close();

                        } catch (Exception e) {
                            LOGGER.error("upgradeData " + name + " : " + version, e);
                        }
                    }
                }
            }

        } catch (Exception e) {
            LOGGER.error("upgradeData " + name, e);

        } finally {
            if (connection != null) {
                try {
                    connectionProvider.releaseConnection(connection);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化数据
     *
     * @param filename
     */
    public void initData(String filename) {
        List<BeanDefine> beanDefines = new ArrayList<BeanDefine>();
        Map<String, Boolean> contextFilenames = new HashMap<String, Boolean>();
        BeanFactory beanFactory = BeanFactoryUtils.get();
        BeanProviderContext.registerBeanDefine(filename, beanFactory, null, beanDefines, contextFilenames, null);
        for (BeanDefine beanDefine : beanDefines) {
            Object initBean = beanDefine.getBeanObject(beanFactory);
            if (initBean != null && initBean instanceof InitBean) {
                mergeInitBean((InitBean) initBean);
            }
        }

        clear();
    }

    /**
     * 清理数据
     */
    public void clear() {
        nameMapInitBean.clear();
    }

    /**
     * 同步数据
     */
    public void mergeInitBean(InitBean initBean) {
        if (initBean.getBeans() != null) {
            JoEntity joEntity = new JoEntity(initBean.getEntityName(), initBean.getEntityClass());
            String[] merges = initBean.getMerges();
            if (merges != null && merges.length == 0) {
                merges = null;
            }

            for (Map<String, Object> bean : initBean.getBeans()) {
                try {
                    ME.mergeInitBean(joEntity, merges, bean);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 同步数据
     *
     * @param joEntity
     * @param merges
     * @param bean
     */
    @Transaction
    public void mergeInitBean(JoEntity joEntity, String[] merges, Map<String, Object> bean) {
        Object mergeBean = null;
        for (Entry<String, Object> entry : bean.entrySet()) {
            Object value = entry.getValue();
            if (value != null && value instanceof String) {
                String name = (String) value;
                if (name.length() > 1 && name.charAt(0) == '@') {
                    String[] names = name.substring(1).split("\\.", 2);
                    Object ref = getNameInitBean(names[0]);
                    if (ref != null) {
                        if (names.length > 1) {
                            ref = UtilAccessor.getAccessorObj(ref, names[1]);
                        }

                        entry.setValue(ref);
                    }
                }
            }
        }

        Session session = BeanDao.getSession();
        if (merges != null) {
            List<Object> conditions = new ArrayList<Object>();
            for (String merge : merges) {
                Object value = bean.get(merge);
                if (value == null && !bean.containsKey(merge)) {
                    conditions = null;
                    break;
                }

                if (value == null) {
                    conditions.add(merge);
                    conditions.add(KernelLang.NULL_OBJECT);

                } else {
                    conditions.add(merge + " =");
                    conditions.add(value);
                }
            }

            JdbcCondition jdbcCondition = conditions == null ? null
                    : SearchServiceUtils.getSearchCondition(joEntity.getEntityName(), null, conditions, null);
            mergeBean = jdbcCondition == null ? null
                    : QueryDaoUtils.selectQuery(session, joEntity.getEntityName(), jdbcCondition);
        }

        if (mergeBean == null) {
            mergeBean = DynaBinder.to(bean, joEntity.getEntityName(), joEntity.getEntityClass());
            mergeBean = session.merge(mergeBean);
        }

        Object name = bean.get("@ref");
        if (name != null) {
            nameMapInitBean.put(name.toString(), mergeBean);
        }
    }

    protected static class VersionSqlName {

        protected String sqlName;

        protected String version;

        protected String suffix;

        protected int type;

        public VersionSqlName(String sqlName) {
            this.sqlName = sqlName;
            String[] versions = sqlName.split("_", 2);
            version = versions[0];
            if (versions.length > 1) {
                suffix = versions[1];
                if (suffix.startsWith("update")) {
                    type = 0;

                } else if (suffix.startsWith("data")) {
                    type = 2;

                } else {
                    type = 1;
                }
            }
        }

        public String getSqlName() {
            return sqlName;
        }

        public String getVersion() {
            return version;
        }

        public String getSuffix() {
            return suffix;
        }

        public int getType() {
            return type;
        }

        public String getVersionOrderName() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(version);
            stringBuilder.append('.');
            stringBuilder.append(type);
            stringBuilder.append('.');
            if (suffix != null) {
                stringBuilder.append(suffix);
            }

            return stringBuilder.toString();
        }
    }
}
