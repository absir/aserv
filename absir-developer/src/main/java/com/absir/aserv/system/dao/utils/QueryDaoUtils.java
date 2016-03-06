/**
 * Copyright 2013 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2013-9-8 下午4:52:59
 */
package com.absir.aserv.system.dao.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.absir.aserv.jdbc.JdbcAlias;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.jdbc.JdbcUtils;
import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelLang;

/**
 * @author absir
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class QueryDaoUtils {

	/** COUNT_ALIAS */
	public static final String COUNT_ALIAS = "COUNT(" + JdbcAlias.ALIAS + ")";

	/** QUERY_CACHEABLE */
	public static final boolean QUERY_CACHEABLE = true;

	/**
	 * @param table
	 * @return
	 */
	public static String tableAlias(String table) {
		return table + ' ' + JdbcAlias.ALIAS;
	}

	/**
	 * @param query
	 * @param parameters
	 * @return
	 */
	public static void setParameter(Query query, List parameters) {
		if (parameters != null) {
			int position = 0;
			for (Object parameter : parameters) {
				if (parameter == KernelLang.NULL_OBJECT) {
					continue;
				}

				/*
				 * if (parameter instanceof List) {
				 * query.setParameter(position++, parameter);
				 * 
				 * } else { query.setParameter(position++, parameter); }
				 */
				query.setParameter(position++, parameter);
			}
		}
	}

	/**
	 * @param query
	 * @param parameters
	 * @return
	 */
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

	/**
	 * @param query
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 */
	public static void setQueryResult(Query query, int firstResult, int maxResults, boolean cacheable) {
		if (firstResult > 0) {
			query.setFirstResult(firstResult);
		}

		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}

		query.setCacheable(cacheable);
	}

	/**
	 * @param joinAlias
	 * @param conditions
	 * @param parameters
	 * @return
	 */
	public static String joinCondition(String joinAlias, Object[] conditions, Collection parameters) {
		String condition = JdbcUtils.Driver().conditionString(conditions, parameters);
		if (joinAlias != null) {
			condition = joinAlias + ' ' + condition;
		}

		return condition;
	}

	/**
	 * @param session
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	public static Query createQuery(Session session, String queryString, List parameters) {
		Query query = session.createQuery(queryString);
		setParameter(query, parameters);
		return query;
	}

	/**
	 * @param session
	 * @param queryString
	 * @param parameters
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 * @return
	 */
	public static Query createQuery(Session session, String queryString, List parameters, int firstResult, int maxResults,
			boolean cacheable) {
		Query query = createQuery(session, queryString, parameters);
		setQueryResult(query, firstResult, maxResults, cacheable);
		return query;
	}

	/**
	 * @param session
	 * @param queryString
	 * @param parameters
	 * @return
	 */
	public static Query createQueryArray(Session session, String queryString, Object... parameters) {
		Query query = session.createQuery(queryString);
		setParameterArray(query, parameters);
		return query;
	}

	/**
	 * @param query
	 * @return
	 */
	public static Object first(Query query) {
		query.setMaxResults(1);
		Iterator iterator = query.iterate();
		return iterator.hasNext() ? iterator.next() : null;
	}

	/**
	 * @param query
	 * @param toClass
	 * @return
	 */
	public static <T> T firstTo(Query query, Class<T> toClass) {
		return DynaBinder.to(first(query), toClass);
	}

	/**
	 * @param query
	 * @return
	 */
	public static List list(Query query) {
		Iterator iterator = query.iterate();
		List list = new ArrayList();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}

		return list;
	}

	/**
	 * @param session
	 * @param entityName
	 * @param id
	 * @return
	 */
	public static boolean isExists(Session session, String entityName, Serializable id) {
		return countQuery(session, entityName, new Object[] { "id", id }) > 0;
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @return
	 */
	public static long countQuery(Session session, String table, Object[] conditions) {
		return countQuery(session, table, null, conditions);
	}

	/**
	 * @param session
	 * @param table
	 * @param joinAlias
	 * @param conditions
	 * @return
	 */
	public static long countQuery(Session session, String table, String joinAlias, Object[] conditions) {
		return countQuery(session, table, joinAlias, conditions, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param table
	 * @param joinAlias
	 * @param conditions
	 * @param cacheable
	 * @return
	 */
	private static long countQuery(Session session, String table, String joinAlias, Object[] conditions, boolean cacheable) {
		List<Object> parameters = new ArrayList<Object>();
		return countQuery(session, tableAlias(table), joinCondition(joinAlias, conditions, parameters), parameters, cacheable);
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @param parameters
	 * @return
	 */
	public static long countQuery(Session session, String table, String conditions, List parameters) {
		return countQuery(session, tableAlias(table), conditions, parameters, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param tableAlias
	 * @param conditions
	 * @param parameters
	 * @param cacheable
	 * @return
	 */
	public static long countQuery(Session session, String tableAlias, String conditions, List parameters, boolean cacheable) {
		String queryString = JdbcUtils.Driver().selectString(tableAlias, COUNT_ALIAS, null, conditions, 0, 0, parameters);
		Iterator<Long> iterator = (Iterator<Long>) createQuery(session, queryString, parameters, 0, 0, cacheable).iterate();
		return iterator.hasNext() ? iterator.next() : 0;
	}

	/**
	 * @param session
	 * @param table
	 * @param values
	 * @return
	 */
	public static int insertQuery(Session session, String table, Object[] values) {
		List<Object> parameters = new ArrayList<Object>();
		String queryString = JdbcUtils.Driver().insertString(tableAlias(table), values, parameters);
		return createQuery(session, queryString, parameters).executeUpdate();
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @return
	 */
	public static int deleteQuery(Session session, String table, Object[] conditions) {
		List<Object> parameters = new ArrayList<Object>();
		String queryString = JdbcUtils.Driver().deleteString(tableAlias(table), conditions, parameters);
		return createQuery(session, queryString, parameters).executeUpdate();
	}

	/**
	 * @param session
	 * @param table
	 * @param values
	 * @param conditions
	 * @return
	 */
	public static int updateQuery(Session session, String table, Object[] values, Object[] conditions) {
		List<Object> parameters = new ArrayList<Object>();
		String queryString = JdbcUtils.Driver().updateString(tableAlias(table), values, conditions, parameters);
		return createQuery(session, queryString, parameters).executeUpdate();
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @return
	 */
	public static Object select(Session session, String table, Object[] conditions) {
		return select(session, table, null, conditions);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @return
	 */
	public static Object select(Session session, String table, String args, Object[] conditions) {
		List list = selectQuery(session, table, args, conditions, 0, 1);
		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, Object[] conditions, int firstResult, int maxResults) {
		return selectQuery(session, table, null, conditions, firstResult, maxResults);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, Object[] conditions, int firstResult, int maxResults) {
		return selectQuery(session, table, args, conditions, null, firstResult, maxResults);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, Object[] conditions, String queue, int firstResult,
			int maxResults) {
		return selectQuery(session, table, args, conditions, queue, firstResult, maxResults, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, Object[] conditions, String queue, int firstResult,
			int maxResults, boolean cacheable) {
		return selectQuery(session, table, args, null, conditions, queue, firstResult, maxResults, cacheable);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param joinAlias
	 * @param conditions
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 * @return
	 */
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

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @param parameters
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 * @return
	 */
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

	/**
	 * @param session
	 * @param table
	 * @param jdbcCondition
	 * @return
	 */
	public static Object selectQuery(Session session, String table, JdbcCondition jdbcCondition) {
		return selectQuery(session, table, null, jdbcCondition);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @return
	 */
	public static Object selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition) {
		List list = selectQuery(session, table, args, jdbcCondition, 0, 1);
		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, int firstResult,
			int maxResults) {
		return selectQuery(session, table, args, jdbcCondition, null, firstResult, maxResults);
	}

	/**
	 * @param session
	 * @param table
	 * @param jdbcCondition
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, String queue, int firstResult,
			int maxResults) {
		return selectQuery(session, table, null, jdbcCondition, queue, firstResult, maxResults);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
			int firstResult, int maxResults) {
		return selectQuery(session, table, args, jdbcCondition, queue, firstResult, maxResults, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @param queue
	 * @param firstResult
	 * @param maxResults
	 * @param cacheable
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
			int firstResult, int maxResults, boolean cacheable) {
		return selectQuery(session, table, args, jdbcCondition == null ? null : jdbcCondition.getJoinAlias(),
				jdbcCondition == null ? null : jdbcCondition.getConditionList().toArray(), queue, firstResult, maxResults,
				cacheable);
	}

	/**
	 * @param session
	 * @param table
	 * @param jdbcCondition
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, JdbcPage jdbcPage) {
		return selectQuery(session, table, jdbcCondition, null, jdbcPage);
	}

	/**
	 * @param session
	 * @param table
	 * @param jdbcCondition
	 * @param queue
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
		return selectQuery(session, table, null, jdbcCondition, queue, jdbcPage);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @param queue
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
			JdbcPage jdbcPage) {
		return selectQuery(session, table, args, jdbcCondition, queue, jdbcPage, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param jdbcCondition
	 * @param queue
	 * @param jdbcPage
	 * @param cacheable
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, JdbcCondition jdbcCondition, String queue,
			JdbcPage jdbcPage, boolean cacheable) {
		String joinAlias = jdbcCondition == null ? null : jdbcCondition.getJoinAlias();
		Object[] conditions = jdbcCondition == null ? null : jdbcCondition.getConditionList().toArray();
		return selectQuery(session, table, args, joinAlias, conditions, queue, jdbcPage, cacheable);
	}

	/**
	 * @param session
	 * @param table
	 * @param conditions
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, Object[] conditions, JdbcPage jdbcPage) {
		return selectQuery(session, table, null, conditions, jdbcPage);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param conditions
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, Object[] conditions, JdbcPage jdbcPage) {
		return selectQuery(session, table, args, null, conditions, jdbcPage);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param joinAlias
	 * @param conditions
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions,
			JdbcPage jdbcPage) {
		return selectQuery(session, table, args, joinAlias, conditions, null, jdbcPage);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param joinAlias
	 * @param conditions
	 * @param queue
	 * @param jdbcPage
	 * @return
	 */
	public static List selectQuery(Session session, String table, String args, String joinAlias, Object[] conditions, String queue,
			JdbcPage jdbcPage) {
		return selectQuery(session, table, args, joinAlias, conditions, queue, jdbcPage, QUERY_CACHEABLE);
	}

	/**
	 * @param session
	 * @param table
	 * @param args
	 * @param joinAlias
	 * @param conditions
	 * @param queue
	 * @param jdbcPage
	 * @param cacheable
	 * @return
	 */
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
