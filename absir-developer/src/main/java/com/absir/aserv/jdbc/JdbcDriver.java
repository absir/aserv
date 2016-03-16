/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-3-7 下午10:37:03
 */
package com.absir.aserv.jdbc;

import com.absir.core.dyna.DynaBinder;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.core.kernel.KernelString.ImplodeBuilder;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JdbcDriver {

    public static final String PRAMER_NAME = "a";

    public static final Pattern CONDITION_PATTERN = Pattern.compile("^[\\s]*(AND|OR)[\\s]+", Pattern.CASE_INSENSITIVE);

    protected ImplodeBuilder insertImplodeBuilder = new ImplodeBuilder() {

        @Override
        public Object glue(StringBuilder builder, Object glue, int index, Object value, Object target) {
            insertGlue(builder, glue, index, value, (Object[]) target);
            return builder;
        }

    };

    protected ImplodeBuilder conditionImplodeBuilder = new ImplodeBuilder() {

        @Override
        public Object glue(StringBuilder builder, Object glue, int index, Object value, Object target) {
            Object[] targets = (Object[]) target;
            if (index == 0) {
                targets[0] = conditionGlue(builder, glue, index, value);

            } else {
                if (value == null) {
                    ((Collection) targets[1]).add(null);

                } else if (value != KernelLang.NULL_OBJECT) {
                    if (value instanceof Collection) {
                        ((Collection) targets[1]).addAll((Collection) value);

                    } else {
                        if (value.getClass().isArray()) {
                            int length = Array.getLength(value);
                            if (length == 1) {
                                ((Collection<Object>) targets[1]).add(DynaBinder.to(value, Object[].class)[0]);

                            } else {
                                targets[0] = KernelString.replaceLast((String) targets[0], "?",
                                        KernelString.repeateString(null, "?, ", length - 1, "?"));
                                KernelCollection.addAll((Collection<Object>) targets[1], DynaBinder.to(value, Object[].class));
                            }

                        } else {
                            ((Collection) targets[1]).add(value);
                        }
                    }
                }

                builder.append(targets[0]);
            }

            return builder;
        }
    };

    protected ImplodeBuilder updateImplodeBuilder = new ImplodeBuilder() {

        @Override
        public Object glue(StringBuilder builder, Object glue, int index, Object value, Object target) {
            updateGlue(builder, glue, index, value, (Collection) target);
            return builder;
        }
    };

    public String _argString() {
        return "*";
    }

    public String _whereString() {
        return "WHERE";
    }

    public String _countString() {
        return "COUNT(*)";
    }

    public String _countString(String value) {
        return "COUNT(" + value + ")";
    }

    public String _randString() {
        return "RAND()";
    }

    public String insertString(String table, Object[] values, Collection parameters) {
        StringBuffer buffer = new StringBuffer();
        String queryString = insertString(values, buffer, parameters);
        queryString = " ( " + queryString + " ) " + "VALUES" + " ( " + buffer.toString() + " ) ";
        return "INSERT INTO " + table + queryString;
    }

    protected String insertString(Object[] values, StringBuffer buffer, Collection parameters) {
        Object[] target = new Object[]{buffer, parameters};
        return KernelString.implode(values, insertImplodeBuilder, target, " , ", " , ");
    }

    protected void insertGlue(StringBuilder builder, Object glue, int index, Object value, Object[] target) {
        if (index == 1) {
            StringBuilder buffer = (StringBuilder) target[0];
            Collection collection = (Collection) target[1];
            if (buffer.length() > 0) {
                buffer.append(glue);
            }

            buffer.append("?");
            collection.add(value);

        } else {
            if (glue != null) {
                builder.append(glue);
            }

            builder.append(value);
        }
    }

    public String conditionString(Object[] conditions, Collection parameters) {
        return conditionString(_whereString(), conditions, parameters);
    }

    public String conditionString(String where, Object[] conditions, Collection parameters) {
        String whereString = "";
        if (conditions != null) {
            whereString = KernelString
                    .implode(conditions, conditionImplodeBuilder, new Object[]{"", parameters}, " and ", " = ");
            if (!KernelString.isEmpty(whereString)) {
                whereString = ' ' + where + ' ' + whereString;
            }
        }

        return whereString;
    }

    protected String conditionGlue(StringBuilder builder, Object glue, int index, Object value) {
        String str = String.valueOf(value);
        if (str.indexOf(' ') < 0) {
            str += " = ?";
            if (glue != null) {
                str = glue + str;
            }

        } else {
            Matcher matcher = CONDITION_PATTERN.matcher(str);
            if (matcher.find()) {
                int end = matcher.end();
                // AND,OR
                if ((end = str.indexOf(' ', end + 1)) > 0) {
                    // AND name
                    if (str.indexOf(' ', end + 1) < 0) {
                        // AND name =
                        str += " ?";
                    }
                    // AND name = ?

                } else {
                    // AND name
                    str += " = ?";
                }

                str = ' ' + str;

            } else {
                // name = ?, name <> ?
                int end = str.indexOf(' ');
                if (end < 0 || str.indexOf(' ', end + 1) < 0) {
                    str = str + " ?";
                }

                if (glue != null) {
                    str = glue + str;
                }
            }
        }

        return str;
    }

    public String deleteString(String table, Object[] conditions, Collection parameters) {
        String queryString = conditionString(conditions, parameters);
        return "DELETE FROM " + table + queryString;
    }

    public String updateString(String table, Object[] values, Object[] conditions, Collection parameters) {
        String queryString = updateString(values, parameters);
        return "UPDATE " + table + " SET " + queryString + conditionString(conditions, parameters);
    }

    protected String updateString(Object[] values, Collection parameters) {
        return KernelString.implode(values, updateImplodeBuilder, parameters, " , ", " = ");
    }

    protected void updateGlue(StringBuilder builder, Object glue, int index, Object value, Collection target) {
        if (index == 1) {
            target.add(value);
            value = "?";
        }

        if (glue != null) {
            builder.append(glue);
        }

        builder.append(value);
    }

    public String selectString(String table, String args, Object[] conditions, String queue, int firstResult, int maxResults,
                               Collection parameters) {
        return selectString(table, args, null, conditions, queue, firstResult, maxResults, parameters);
    }

    public String selectString(String table, String args, String joinAlias, Object[] conditions, String queue, int firstResult,
                               int maxResults, Collection parameters) {
        String queryString = "SELECT "
                + args
                + " FROM "
                + table
                + conditionString(KernelString.isEmpty(joinAlias) ? _whereString() : joinAlias + ' ' + _whereString(), conditions,
                parameters);
        if (queue != null) {
            queryString += ' ' + queue;
        }

        if (maxResults > 0) {
            queryString += " LIMIT " + firstResult + ", " + maxResults;

        } else if (firstResult > 0) {
            queryString += " LIMIT " + firstResult;
        }

        return queryString;
    }
}
