/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-3-11 下午2:46:17
 */
package com.absir.aserv.system.service.utils;

import com.absir.aserv.crud.CrudUtils;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.helper.HelperCondition;
import com.absir.client.helper.HelperJson;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelLang.PropertyFilter;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.value.JoEntity;
import com.absir.server.in.Input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author absir
 */
@SuppressWarnings("unchecked")
public class InputServiceUtils {

    /**
     * SEARCH_CONDITIONS_PARAMTER_STRING
     */
    private static final String SEARCH_CONDITIONS_PARAMTER_STRING = "searchConditions";
    /**
     * SEARCH_CONDITIONS_PARAMTER_ARRAY_LIST
     */
    private static final String SEARCH_CONDITIONS_PARAMTER_ARRAY_LIST = "searchConditionList";

    /**
     * @param entityName
     * @param input
     * @return
     */
    public static JdbcPage getJdbcPage(String entityName, Input input) {
        JdbcPage jdbcPage = new JdbcPage();
        return jdbcPage;
    }

    /**
     * @param entityName
     * @param jdbcPage
     * @param input
     * @return
     */
    public static JdbcPage getJdbcPage(String entityName, JdbcPage jdbcPage, Input input) {
        if (jdbcPage == null) {
            jdbcPage = getJdbcPage(entityName, input);
        }

        return jdbcPage;
    }

    /**
     * @param input
     * @return
     */
    public static String getOrderQueue(Input input) {
        String[] orderFields = input.getParams("orderField");
        String[] orderDirections = input.getParams("orderDirection");
        if (orderFields != null && orderDirections != null && orderFields.length == orderDirections.length) {
            Map<String, String> orderFieldMap = new HashMap<String, String>();
            input.setAttribute("orderField", orderFields[0]);
            input.setAttribute("orderDirection", orderDirections[0]);

            String orderQueue = "";
            int length = orderFields.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    orderQueue += " , ";

                } else {
                    if (KernelString.isEmpty(orderFields[i])) {
                        break;
                    }

                    orderQueue = "ORDER BY ";
                }

                orderFieldMap.put(orderFields[i], orderDirections[i]);
                orderQueue += "o." + orderFields[i] + " " + orderDirections[i];
            }

            input.setAttribute("orderFieldMap", orderFieldMap);
            return orderQueue;
        }

        return null;
    }

    /**
     * 获取请求查询条件
     *
     * @param entityName
     * @param filter
     * @param jdbcCondition
     * @param input
     * @return
     */
    public static JdbcCondition getSearchCondition(String entityName, PropertyFilter filter, JdbcCondition jdbcCondition, Input input) {
        return getSearchCondition(entityName, SessionFactoryUtils.getEntityClass(entityName), filter, jdbcCondition, input);
    }

    /**
     * 获取请求查询条件(包含非数据库)
     *
     * @param entityName
     * @param entityClass
     * @param filter
     * @param jdbcCondition
     * @param input
     * @return
     */
    public static JdbcCondition getSearchCondition(String entityName, Class<?> entityClass, PropertyFilter filter, JdbcCondition jdbcCondition, Input input) {
        if (entityClass == null) {
            return jdbcCondition;
        }

        if (jdbcCondition == null) {
            jdbcCondition = new JdbcCondition();
        }

        String searchConditions = input.getParam(SEARCH_CONDITIONS_PARAMTER_STRING);
        List<Object> conditions = HelperJson.decodeBase64Json(searchConditions, List.class);
        if (conditions == null) {
            conditions = new ArrayList<Object>();
            Map<String, Object[]> fieldMetas = SessionFactoryUtils.getEntityFieldMetas(entityName, entityClass);
            List<List<Object>> metasConditions = new ArrayList<List<Object>>();
            int size = 0;
            for (Entry<String, String[]> entry : ((Map<String, String[]>) (Object) input.getParamMap()).entrySet()) {
                String value = entry.getValue()[0];
                if (!KernelString.isEmpty(value)) {
                    String propertyPath = entry.getKey();
                    if (propertyPath.length() > 1 && propertyPath.charAt(0) == '.') {
                        for (String path : propertyPath.substring(1).split("\\|")) {
                            SearchServiceUtils.addSearchMetasCondition(filter, path, value, fieldMetas, metasConditions);
                        }

                    } else {
                        SearchServiceUtils.addSearchMetasCondition(filter, propertyPath, value, fieldMetas, metasConditions);
                    }

                    if (size != metasConditions.size()) {
                        size = metasConditions.size();
                        conditions.add(entry.getKey());
                        conditions.add(KernelLang.NULL_OBJECT);
                    }
                }
            }

            for (int m = 0; m < size; m++) {
                List<Object> metasCondition = metasConditions.get(m);
                int last = metasCondition.size() - 4;
                jdbcCondition.pushAlias();
                for (int i = 0; i < last; i += 2) {
                    Object[] metas = (Object[]) metasCondition.get(i + 1);
                    if (metas.length == 2) {
                        jdbcCondition.joinAliasProperyName(jdbcCondition.getAlias(), (String) metasCondition.get(i));
                    }
                }

                conditions.set(
                        m * 2 + 1,
                        SearchServiceUtils.addSearchJdbcCondition((Boolean) metasCondition.get(last), (String) metasCondition.get(last + 1), (Object[]) metasCondition.get(last + 2),
                                metasCondition.get(last + 3), jdbcCondition));
                jdbcCondition.popAlias();
            }

            String suggest = input.getParam("!suggest");
            if (!KernelString.isEmpty(suggest)) {
                conditions.add("!suggest");
                conditions.add(suggest);
                String[] fields = CrudUtils.getGroupFields(new JoEntity(entityName, entityClass), "sug");
                if (fields != null && fields.length > 0) {
                    int ms = metasConditions.size();
                    for (String field : fields) {
                        String propertyPath = field;
                        if (propertyPath.length() > 1 && propertyPath.charAt(0) == '.') {
                            for (String path : propertyPath.substring(1).split("\\|")) {
                                SearchServiceUtils.addSearchMetasCondition(filter, path, suggest, fieldMetas, metasConditions);
                            }

                        } else {
                            SearchServiceUtils.addSearchMetasCondition(filter, propertyPath, suggest, fieldMetas, metasConditions);
                        }
                    }

                    boolean first = true;
                    int me = metasConditions.size();
                    if (me > ms) {
                        for (int m = ms; m < me; m++) {
                            List<Object> metasCondition = metasConditions.get(m);
                            int last = metasCondition.size() - 4;
                            jdbcCondition.pushAlias();
                            for (int i = 0; i < last; i += 2) {
                                Object[] metas = (Object[]) metasCondition.get(i + 1);
                                if (metas.length == 2) {
                                    jdbcCondition.joinAliasProperyName(jdbcCondition.getAlias(), (String) metasCondition.get(i));
                                }
                            }

                            SearchServiceUtils.addSearchJdbcCondition((Boolean) metasCondition.get(last), (String) metasCondition.get(last + 1), (Object[]) metasCondition.get(last + 2),
                                    metasCondition.get(last + 3), jdbcCondition);

                            if (first) {
                                first = false;
                                HelperCondition.leftBracket(jdbcCondition.getConditions());

                            } else {
                                HelperCondition.leftOR(jdbcCondition.getConditions());
                            }

                            jdbcCondition.popAlias();
                        }

                        HelperCondition.rigthBracket(jdbcCondition.getConditions());
                    }
                }
            }

            input.setAttribute(SEARCH_CONDITIONS_PARAMTER_STRING, HelperJson.encodeBase64Json(conditions));

        } else {
            input.setAttribute(SEARCH_CONDITIONS_PARAMTER_STRING, searchConditions);
            jdbcCondition = SearchServiceUtils.getSearchCondition(entityName, filter, conditions, jdbcCondition);
        }

        input.setAttribute(SEARCH_CONDITIONS_PARAMTER_ARRAY_LIST, conditions);
        return jdbcCondition;
    }
}
