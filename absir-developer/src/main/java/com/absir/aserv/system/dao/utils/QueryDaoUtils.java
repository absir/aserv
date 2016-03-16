/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-8 下午4:52:59
 */
package com.absir.aserv.system.dao.utils;

import com.absir.aserv.jdbc.JdbcAlias;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.jdbc.JdbcUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelLang;
import org.hibernate.Query;
import org.hibernate.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class QueryDaoUtils {

    public static final String COUNT_ALIAS = "COUNT(" + JdbcAlias.ALIAS + ")";

    public static final boolean QUERY_CACHEABLE = true;

    public static String tableAlias(String table) {
        return table + ' ' + JdbcAlias.ALIAS;
    }

    public static void setParameter(Query query, List parameters) {
        if (parameters != null) {
            int position = 0;
            for (Object parameter : parameters) {
                if (parameter == KernelLang.NULL_OBJECT) {
                    continue;
                }

                query.setParameter(position++, parameter);
            }
        }
    }

    public static void setParameterArray(Query query, Object... parameters) {
        if (parameters != null) {
            int position = 0;
            for (Object parameter : parameters) {
                if (parameter == KernelLang.NULL_OBJECT) {
                    continue;
                }

                if (parameter instanceof List) {
                    query.setParameter(position++, parameter);

                } else {
                    query.setParameter(position++, parameter);
                }
            }
        }
    }

    public static void setQueryResult(Query query, int firstResult, int maxResults, boolean cacheable) {
        if (firstResult > 0) {
            query.setFirstResult(firstResult);
        }

        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        query.setCacheable(cacheable);
    }

    public static String joinCondition(String joinAlias, Object[] conditions, Collection parameters) {
        String condition = JdbcUtils.Driver().conditionString(conditions, parameters);
        if (joinAlias != null) {
            condition = joinAlias + ' ' + condition;
        }

        return condition;
    }

    public static Query createQuery(Session session, String queryString, List parameters) {
        Query query = session.createQuery(queryString);
        setParameter(query, parameters);
        return query;
    }

    public static Query createQuery(Session session, String queryString, List parameters, int firstResult, int maxResults,
                                    boolean cacheable) {
        Query query = createQuery(session, queryString, parameters);
        setQueryResult(query, firstResult, maxResults, cacheable);
        return query;
    }

    public static Query createQueryArray(Session session, String queryString, Object... parameters) {
        Query query = session.createQuery(queryString);
        setParameterArray(query, parameters);
        return query;
    }

    public static Object first(Query query) {
        query.setMaxResults(1);
        Iterator iterator = query.iterate();
        return iterator.hasNext() ? iterator.next() : null;
    }

    public static <T> T firstTo(Query query, Class<T> toClass) {
        return DynaBinder.to(first(query), toClass);
    }

    public static List list(Query query) {
        Iterator iterator = query.iterate();
        List list = new ArrayList();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    public static boolean isExists(Session session, String entityName, Serializable id) {
        return countQuery(session, entityName, new Object[]{"id", id}) > 0;
    }

    public static long countQuery(Session session, String table, Object[] conditions) {
        return countQuery(session, table, null, conditions);
    }

    public static long countQuery(Session session, String table, String joinAlias, Object[] conditions) {
        return countQuery(session, table, joinAlias, conditions, QUERY_CACHEABLE);
    }

    private static long countQuery(Session session, String table, String joinAlias, Object[] conditions, boolean cacheable) {
        List<Object> parameters = new ArrayList<Object>();
        return countQuery(session, tableAlias(table), joinCondition(joinAlias, conditions, parameters), parameters, cacheable);
    }

    public static long countQuery(Session session, String table, String conditions, List parameters) {
        return countQuery(session, tableAlias(table), conditions, parameters, QUERY_CACHEABLE);
    }

    public static long countQuery(Session session, String tableAlias, String conditions, List parameters, boolean cacheable) {
        String queryString = JdbcUtils.Driver().selectString(tableAlias, COUNT_ALIAS, null, conditions, 0, 0, parameters);
        Iterator<Long> iterator = (Iterator<Long>) createQuery(session, queryString, parameters, 0, 0, cacheable).iterate();
        return iterator.hasNext() ? iterator.next() : 0;
    }

    public static int insertQuery(Session session, String table, Object[] values) {
        List<Object> parameters = new ArrayList<Object>();
        String queryString = JdbcUtils.Driver().insertString(tableAlias(table), values, parameters);
        return createQuery(session, queryString, parameters).executeUpdate();
    }

    public static int deleteQuery(Session session, String table, Object[] conditions) {
        List<Object> parameters = new ArrayList<Object>();
        String queryString = JdbcUtils.Driver().deleteString(tableAlias(table), conditions, parameters);
        return createQuery(session, queryString, parameters).executeUpdate();
    }

    public static int updateQuery(Session session, String table, Object[] values, Object[] conditions) {
        List<Object> parameters = new ArrayList<Object>();
        String queryString = JdbcUtils.Driver().updateString(tableAlias(table), values, conditions, parameters);
        return createQuery(session, queryString, parameters).executeUpdate();
    }

    public static Object select(Session session, String table, Object[] conditions) {
        return select(session, table, null, conditions);
    }

    public static Object select(Session session, String table, String args, Object[] conditions) {
        List list = selectQuery(session, table, args, conditions, 0, 1);
        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public static List selectQuery(Session session, String table, Object[] conditions, int firstResult, int maxResults) {
        return selectQuery(session, table, null, conditions, firstResult, maxResults);
    }

    public static List selectQuery(Session session, String table, String args, Object[] conditions, int firstResult, int maxResults) {
        return selectQuery(session, table, args, conditions, null, firstResult, maxResults);
    }

    public static List selectQuery(Session session, String table, String args, Object[] conditions, String queue, int firstResult,
                                   int maxResults) {
        return selectQuery(session, table, args, conditions, queue, firstResult, maxResults, QUERY_CACHEABLE);
    }

    public static List selectQuery(Session session, String table, String args, Object[] conditions, String queue, int firstResult,
                                   int maxResults, boolean cacheable) {
        return selectQuery(session, table, args, null, conditions, queue, firstResult, maxResults, cacheable);
    }

    public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions, String queue,
                                   int firstResult, int maxResults, boolean cacheable) {
        if (args == null) {
            args = JdbcAlias.ALIAS;
        }

        List<Object> parameters = new ArrayList<Object>();
        String queryString = JdbcUtils.Driver().selectString(tableAlias(table), args, joinAlias, conditions, queue, 0, 0,
                parameters);
        return createQuery(session, queryString, parameters, firstResult, maxResults, cacheable).list();
    }

    public static List selectQuery(Session session, String table, String args, String conditions, List parameters, String queue,
                                   int firstResult, int maxResults, boolean cacheable) {
        if (args == null) {
            args = JdbcAlias.ALIAS;
        }

        if (conditions != null) {
            queue = queue == null ? conditions : (conditions + ' ' + queue);
        }

        String queryString = JdbcUtils.Driver().selectString(table, args, null, queue, 0, 0, parameters);
        return createQuery(session, queryString, parameters, firstResult, maxResults, cacheable).list();
    }

    public static Object selectQuery(Session session, String table, JdbcCondition jdbcCondition) {
        return selectQuery(session, table, null, jdbcCondition);
    }

    public static Object selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition) {
        List list = selectQuery(session, table, args, jdbcCondition, 0, 1);
        if (list.size() > 0) {
            return list.get(0);
        }

        return null;
    }

    public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, int firstResult,
                                   int maxResults) {
        return selectQuery(session, table, args, jdbcCondition, null, firstResult, maxResults);
    }

    public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, String queue, int firstResult,
                                   int maxResults) {
        return selectQuery(session, table, null, jdbcCondition, queue, firstResult, maxResults);
    }

    public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
                                   int firstResult, int maxResults) {
        return selectQuery(session, table, args, jdbcCondition, queue, firstResult, maxResults, QUERY_CACHEABLE);
    }

    public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
                                   int firstResult, int maxResults, boolean cacheable) {
        return selectQuery(session, table, args, jdbcCondition == null ? null : jdbcCondition.getJoinAlias(),
                jdbcCondition == null ? null : jdbcCondition.getConditionList().toArray(), queue, firstResult, maxResults,
                cacheable);
    }

    public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, JdbcPage jdbcPage) {
        return selectQuery(session, table, jdbcCondition, null, jdbcPage);
    }

    public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        return selectQuery(session, table, null, jdbcCondition, queue, jdbcPage);
    }

    public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
                                   JdbcPage jdbcPage) {
        return selectQuery(session, table, args, jdbcCondition, queue, jdbcPage, QUERY_CACHEABLE);
    }

    public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
                                   JdbcPage jdbcPage, boolean cacheable) {
        String joinAlias = jdbcCondition == null ? null : jdbcCondition.getJoinAlias();
        Object[] conditions = jdbcCondition == null ? null : jdbcCondition.getConditionList().toArray();
        return selectQuery(session, table, args, joinAlias, conditions, queue, jdbcPage, cacheable);
    }

    public static List selectQuery(Session session, String table, Object[] conditions, JdbcPage jdbcPage) {
        return selectQuery(session, table, null, conditions, jdbcPage);
    }

    public static List selectQuery(Session session, String table, String args, Object[] conditions, JdbcPage jdbcPage) {
        return selectQuery(session, table, args, null, conditions, jdbcPage);
    }

    public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions,
                                   JdbcPage jdbcPage) {
        return selectQuery(session, table, args, joinAlias, conditions, null, jdbcPage);
    }

    public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions, String queue,
                                   JdbcPage jdbcPage) {
        return selectQuery(session, table, args, joinAlias, conditions, queue, jdbcPage, QUERY_CACHEABLE);
    }

    public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions, String queue,
                                   JdbcPage jdbcPage, boolean cacheable) {
        table = tableAlias(table);
        List<Object> parameters = new ArrayList<Object>();
        joinAlias = joinCondition(joinAlias, conditions, parameters);
        int firstResult = 0, maxResults = 0;
        if (jdbcPage != null) {
            jdbcPage.setTotalCount((int) countQuery(session, table, joinAlias, parameters, cacheable));
            firstResult = jdbcPage.getFirstResult();
            maxResults = jdbcPage.getPageSize();
        }

        return selectQuery(session, table, args, joinAlias, parameters, queue, firstResult, maxResults, cacheable);
    }
}
