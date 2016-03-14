/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-5-23 下午3:12:32
 */
package com.absir.aserv.system.helper;

import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.system.bean.proxy.JiSort;
import com.absir.core.kernel.KernelArray;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import java.util.List;

/**
 * @author absir
 */
public class HelperCondition {

    /**
     * SQL_PARTERN
     */
    private static final String[] SQL_PARTERN = new String[]{"AND ", "OR "};

    /**
     * SQL_EMPTY
     */
    private static final String[] SQL_EMPTY = KernelArray.repeat("", SQL_PARTERN.length);

    /**
     * SQL_EXPRESSION
     */
    private static final String[] SQL_EXPRESSION = new String[]{";", "SELECT", "INSERT", "UPDATE", "DELETE"};

    /**
     * @param expression
     * @return
     */
    public static String expression(String expression) {
        expression = HelperString.replaceEach(expression.toUpperCase(), SQL_PARTERN, SQL_EMPTY);
        if (expression.length() > 8) {
            if (HelperString.indexOfAny(expression, SQL_EXPRESSION) >= 0) {
                expression = "";
            }
        }

        return expression;
    }

    /**
     * @param queue
     * @return
     */
    public static String orderQueue(String queue) {
        if (queue != null) {
            StringBuilder queryBuilder = new StringBuilder("ORDER BY ");
            String[] queues = queue.split(" ");
            int length = queues.length;
            for (int i = 0; i < length; i++) {
                queue = queues[i];
                if (i > 0) {
                    queryBuilder.append(", ");
                }

                queryBuilder.append(queue);
                int next = i + 1;
                if (next < length) {
                    queue = queues[next].toUpperCase();
                    if ("ASC".equals(queue) || "DESC".equals("queue")) {
                        queryBuilder.append(' ');
                        queryBuilder.append(queue);
                    }
                }
            }

            queue = queryBuilder.toString();
        }

        return queue;
    }

    /**
     * @param entityClass
     * @param queue
     * @return
     */
    public static String orderQueue(Class<?> entityClass, String queue) {
        if (entityClass != null && JiSort.class.isAssignableFrom(entityClass)) {
            if (KernelString.isEmpty(queue)) {
                queue = "ORDER BY o.sort";

            } else {
                queue += ", o.sort";
            }
        }

        return queue;
    }

    /**
     * @param conditions
     * @return
     */
    public static void bracket(List<Object> conditions) {
        int last = conditions.size();
        if (last > 2) {
            last -= 2;
            conditions.set(0, "(" + conditions.get(0));
            conditions.set(last, conditions.get(last) + ")");
        }
    }

    /**
     * @param conditions
     */
    public static void concatOR(List<Object> conditions) {
        int last = conditions.size();
        if (last > 2) {
            last -= 2;
            conditions.set(0, "OR " + conditions.get(0));
            conditions.set(last, conditions.get(last) + ")");
        }
    }

    /**
     * @param conditions
     * @param expression
     * @return
     */
    public static void concatOR(List<Object> conditions, String expression) {
        if (conditions.size() > 0) {
            conditions.add("OR " + expression);

        } else {
            conditions.add(expression);
        }
    }

    /**
     * @param conditions
     */
    public static boolean leftOR(List<Object> conditions) {
        int last = conditions.size();
        if (last < 2) {
            return false;
        }

        last -= 2;
        conditions.set(last, "OR " + conditions.get(last));
        return true;
    }

    /**
     * @param conditions
     * @return
     */
    public static boolean leftBracket(List<Object> conditions) {
        int last = conditions.size();
        if (last < 2) {
            return false;
        }

        last -= 2;
        conditions.set(last, "(" + conditions.get(last));
        return true;
    }

    /**
     * @param conditions
     * @return
     */
    public static boolean rigthBracket(List<Object> conditions) {
        int last = conditions.size();
        if (last < 2) {
            return false;
        }

        last -= 2;
        conditions.set(last, conditions.get(last) + ")");
        return true;
    }

    /**
     * @param jdbcCondition
     * @param joinPropertyName
     * @param joinAlias
     */
    public static void leftJoin(JdbcCondition jdbcCondition, String joinPropertyName, String joinAlias) {
        jdbcCondition.setJoinAlias(" LEFT JOIN " + jdbcCondition.getPropertyAlias() + "." + joinPropertyName + " " + joinAlias + " " + jdbcCondition.getJoinAlias());
    }

    /**
     * @param jdbcCondition
     * @param joinPropertyName
     * @param joinAlias
     */
    public static void leftJoinFetch(JdbcCondition jdbcCondition, String joinPropertyName, String joinAlias) {
        jdbcCondition.setJoinAlias(" LEFT JOIN FETCH " + jdbcCondition.getPropertyAlias() + "." + joinPropertyName + " " + joinAlias + jdbcCondition.getJoinAlias());
    }

    /**
     * @param jdbcCondition
     * @param joinEntityName
     * @param joinAlias
     */
    public static void crossJoin(JdbcCondition jdbcCondition, String joinEntityName, String joinAlias) {
        jdbcCondition.setJoinAlias(jdbcCondition.getJoinAlias() + " , " + joinEntityName + " " + joinAlias);
    }

    /**
     * @param jdbcCondition
     * @param joinEntityName
     * @param joinAlias
     * @param joinId
     * @param alias
     * @param entityId
     * @param joinConditions
     */
    public static void crossJoin(JdbcCondition jdbcCondition, String joinEntityName, String joinAlias, String joinId, String alias, String entityId, List<Object> joinConditions) {
        crossJoin(jdbcCondition, joinEntityName, joinAlias);
        concatOR(joinConditions, joinAlias + "." + joinId + " = " + alias + "." + entityId);
        joinConditions.add(KernelLang.NULL_OBJECT);
    }
}
