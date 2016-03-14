/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-13 下午5:14:51
 */
package com.absir.aserv.data;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperString;
import com.absir.core.base.Base;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author absir
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DataQueryDetached {

    /**
     * SQL_QUEUE_PATTERN
     */
    public static final String SQL_QUEUE_PATTERN = " @ ";
    /**
     * sql
     */
    private String sql;
    /**
     * nativeSql
     */
    private boolean nativeSql;
    /**
     * sessionFactory
     */
    private SessionFactory sessionFactory;
    /**
     * returnType
     */
    private Class<?> returnType;
    /**
     * cacheable
     */
    private boolean cacheable;
    /**
     * queryReturnInvoker
     */
    private QueryReturnInvoker queryReturnInvoker;
    /**
     * parameterMetas
     */
    private Object[] parameterMetas;
    /**
     * countQueryDetached
     */
    private DataQueryDetached countQueryDetached;
    /**
     * resultTransformer
     */
    private ResultTransformer resultTransformer;

    /**
     * @param sql
     * @param nativeSql
     * @param sessionName
     * @param returnType
     * @param cacheable
     * @param excuteType
     * @param aliasType
     * @param parameterTypes
     * @param parameterNames
     * @param firstResultsPos
     * @param maxResultsPos
     */
    public DataQueryDetached(String sql, boolean nativeSql, String sessionName, Class<?> returnType, boolean cacheable, Class<?> excuteType, Class<?> aliasType, Class<?>[] parameterTypes,
                             String[] parameterNames, int firstResultsPos, int maxResultsPos) {
        this.sql = sql;
        this.nativeSql = nativeSql;
        sessionFactory = KernelString.isEmpty(sessionName) ? null : SessionFactoryUtils.get().getNameMapSessionFactory(sessionName);
        this.returnType = returnType;
        this.cacheable = cacheable;
        if (excuteType == null || excuteType == void.class) {
            excuteType = returnType;
        }

        if (excuteType == null) {
            queryReturnInvoker = QueryReturnInvoker.EXECUTE;

        } else {
            if (Iterator.class.isAssignableFrom(excuteType)) {
                queryReturnInvoker = QueryReturnInvoker.ITERATE;

            } else if (excuteType.isArray()) {
                queryReturnInvoker = QueryReturnInvoker.LIST;

            } else if (Collection.class.isAssignableFrom(excuteType)) {
                queryReturnInvoker = nativeSql || aliasType == Collection.class ? QueryReturnInvoker.LIST : QueryReturnInvoker.LIST_ITERATE;

            } else if (Map.class.isAssignableFrom(excuteType)) {
                queryReturnInvoker = QueryReturnInvoker.MAP;

            } else if (!Query.class.isAssignableFrom(returnType)) {
                int pos = HelperString.indexOfIgnoreCase(sql, "SELECT");
                if (pos <= 0 || HelperString.trimToNull(sql.substring(0, pos)) != null) {
                    queryReturnInvoker = QueryReturnInvoker.SINGLE;

                } else {
                    queryReturnInvoker = QueryReturnInvoker.EXECUTE;
                }
            }

            // NULL IS Query
        }

        // setResultTransformer
        if (aliasType == null || aliasType == void.class || aliasType == DataQuery.class) {

        } else if (aliasType == Map.class) {
            resultTransformer = Transformers.ALIAS_TO_ENTITY_MAP;

        } else if (aliasType == List.class) {
            resultTransformer = Transformers.TO_LIST;

        } else if (!KernelClass.isCustomClass(aliasType)) {
            resultTransformer = Transformers.aliasToBean(aliasType);
        }

        int jdbcPagePos = -1;
        int length = parameterTypes.length;
        parameterMetas = new Object[length];
        for (int i = 0; i < length; i++) {
            if (i == firstResultsPos) {
                // firstResultsPos
                parameterMetas[i] = Integer.class;

            } else if (i == maxResultsPos) {
                // maxResultsPos
                parameterMetas[i] = Long.class;

            } else {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType == null || parameterType == void.class) {
                    // ingore parameter
                    parameterMetas[i] = void.class;

                } else if (Session.class.isAssignableFrom(parameterType)) {
                    // session parameter
                    parameterMetas[i] = Session.class;

                } else if (JdbcPage.class.isAssignableFrom(parameterType)) {
                    // jdbcPage parameter
                    jdbcPagePos = i;
                    parameterMetas[i] = JdbcPage.class;
                }
            }
        }

        if (parameterNames != null) {
            for (int i = 0; i < length; i++) {
                if (parameterMetas[i] == null) {
                    String parameterName = parameterNames[i];
                    if (KernelString.isEmpty(parameterName)) {
                        parameterName = "p" + i;
                    }

                    if (sql.indexOf(':' + parameterName) > 0) {
                        parameterMetas[i] = parameterName;
                    }
                }
            }
        }

        if (jdbcPagePos >= 0) {
            int selectPos = HelperString.indexOfIgnoreCase(sql, "SELECT ");
            if (selectPos >= 0) {
                int fromPos = HelperString.indexOfIgnoreCase(sql, " FROM", selectPos);
                if (fromPos > 0) {
                    int splitPos = HelperString.indexOf(sql, ',', selectPos);
                    // generate count sql
                    String countSql = "SELECT COUNT(" + sql.substring(selectPos + 7, splitPos < 0 || splitPos > fromPos ? fromPos : splitPos) + ") " + sql.substring(fromPos);
                    splitPos = HelperString.lastIndexOf(countSql, SQL_QUEUE_PATTERN);
                    if (splitPos > 0) {
                        countSql = countSql.substring(0, splitPos);
                        this.sql = this.sql.replace(SQL_QUEUE_PATTERN, " ");
                    }

                    // ingore parameter jdbcPage
                    parameterTypes[jdbcPagePos] = void.class;
                    // ingore parameter firstResultsPos
                    if (firstResultsPos >= 0 && firstResultsPos < length) {
                        parameterTypes[firstResultsPos] = void.class;
                    }

                    // ingore parameter maxResultsPos
                    if (maxResultsPos >= 0 && maxResultsPos < length) {
                        parameterTypes[maxResultsPos] = void.class;
                    }

                    countQueryDetached = new DataQueryDetached(countSql, nativeSql, sessionName, Long.class, cacheable, null, null, parameterTypes, parameterNames, -1, -1);
                }
            }
        }

        // parameterMetas is null
        for (length--; length >= 0; length--) {
            if (parameterMetas[length] != null) {
                break;
            }
        }

        if (length < 0) {
            parameterMetas = null;
        }
    }

    /**
     * @return the resultTransformer
     */
    public ResultTransformer getResultTransformer() {
        return resultTransformer;
    }

    /**
     * @param resultTransformer the resultTransformer to set
     */
    public void setResultTransformer(ResultTransformer resultTransformer) {
        this.resultTransformer = resultTransformer;
    }

    /**
     * @param parameters
     * @return
     */
    public Object run(Object... parameters) {
        return invoke(parameters);
    }

    /**
     * @param parameters
     * @return
     */
    public Object invoke(Object[] parameters) {
        Session session = null;
        JdbcPage jdbcPage = null;
        int length = parameters.length;
        if (parameterMetas != null) {
            for (int i = 0; i < length; i++) {
                Object parameterMeta = parameterMetas[i];
                if (parameterMeta == Session.class) {
                    session = (Session) parameters[i];

                } else if (parameterMeta == JdbcPage.class) {
                    jdbcPage = (JdbcPage) parameters[i];
                }
            }
        }

        if (session == null) {
            session = sessionFactory == null ? BeanDao.getSession() : sessionFactory.getCurrentSession();
        }

        Query query = nativeSql ? session.createSQLQuery(sql) : session.createQuery(sql);
        query.setCacheable(cacheable);
        if (resultTransformer == null) {
            if (nativeSql && queryReturnInvoker == QueryReturnInvoker.SINGLE) {
                query.setResultTransformer(Transformers.aliasToBean(returnType));
            }

        } else {
            query.setResultTransformer(resultTransformer);
        }

        if (!(jdbcPage == null || countQueryDetached == null)) {
            // jdbcPage countQueryDetached
            jdbcPage.setTotalCount(((Long) countQueryDetached.invoke(parameters)).intValue());
            query.setFirstResult(jdbcPage.getFirstResult());
            query.setMaxResults(jdbcPage.getPageSize());
        }

        if (parameterMetas == null) {
            QueryDaoUtils.setParameterArray(query, parameters);

        } else {
            int position = 0;
            for (int i = 0; i < length; i++) {
                Object parameterMeta = parameterMetas[i];
                if (parameterMeta == null) {
                    // set parameter position
                    query.setParameter(position++, parameters[i]);

                } else if (parameterMeta == Integer.class) {
                    // setFirstResult
                    query.setFirstResult((Integer) parameters[i]);

                } else if (parameterMeta == Long.class) {
                    // setMaxResults
                    query.setMaxResults((Integer) parameters[i]);

                } else if (parameterMeta.getClass() == String.class) {
                    // set parameter name
                    Object parameter = parameters[i];
                    if (parameter == null) {
                        query.setParameter((String) parameterMeta, parameter);

                    } else if (parameter.getClass().isArray()) {
                        // set Parameter Array
                        query.setParameterList((String) parameterMeta, DynaBinder.to(parameter, Object[].class));

                    } else if (Collection.class.isAssignableFrom(parameter.getClass())) {
                        // set Parameter Collection
                        query.setParameterList((String) parameterMeta, (Collection) parameter);

                    } else {
                        query.setParameter((String) parameterMeta, parameter);
                    }
                }
            }
        }

        return queryReturnInvoker == null ? query : queryReturnInvoker.invoke(query, returnType);
    }

    /**
     * @author absir
     */
    private static enum QueryReturnInvoker {

        EXECUTE {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                return DynaBinder.to(query.executeUpdate(), returnType);
            }
        },

        ITERATE {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                return query.iterate();
            }
        },

        SINGLE {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                Iterator iterator = query.iterate();
                return iterator.hasNext() ? DynaBinder.to(iterator.next(), returnType) : null;
            }
        },

        LIST {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                return DynaBinder.to(query.list(), returnType);
            }
        },

        LIST_ITERATE {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                Collection toObject = KernelClass.newInstance(DynaBinder.toCollectionClass((Class<Collection>) returnType));
                Iterator iterator = query.iterate();
                while (iterator.hasNext()) {
                    toObject.add(iterator.next());
                }

                return toObject;
            }
        },

        MAP {
            @Override
            public Object invoke(Query query, Class<?> returnType) {
                Map toObject = KernelClass.newInstance(DynaBinder.toMapClass((Class<Map>) returnType));
                Iterator iterator = query.iterate();
                boolean base = false;
                SessionImplementor session = null;
                ClassMetadata classMetadata = null;
                while (iterator.hasNext()) {
                    Object bean = iterator.next();
                    if (!base && classMetadata == null) {
                        if (bean instanceof Base) {
                            base = true;

                        } else {
                            session = (SessionImplementor) KernelObject.declaredGet(query, "session");
                            classMetadata = SessionFactoryUtils.getClassMetadata(null, bean.getClass(), session.getFactory());
                        }
                    }

                    toObject.put(base ? ((Base) bean).getId() : classMetadata.getIdentifier(bean, session), bean);
                }

                return toObject;
            }
        },;

        public abstract Object invoke(Query query, Class<?> returnType);
    }
}
