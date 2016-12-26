package com.absir.aserv.system.crud;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.JDict;
import com.absir.aserv.system.bean.base.JbBeanS;
import com.absir.aserv.system.bean.value.JaCrud;
import com.absir.aserv.system.service.EntityService;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelLang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by absir on 2016/12/22.
 */
@Base
@Bean
public class CrudDictSupply implements ICrudSupply {

    @Override
    public Set<Map.Entry<String, Class<?>>> getEntityNameMapClass() {
        return null;
    }

    @Override
    public String getTransactionName() {
        return null;
    }

    @Override
    public boolean support(JaCrud.Crud crud) {
        return false;
    }

    protected static final String NAME_PRE = "JDict@";

    protected static final int NAME_PRE_LENGTH = NAME_PRE.length();

    @Override
    public Class<?> getEntityClass(String entityName) {
        if (entityName.startsWith(NAME_PRE)) {
            return Map.Entry.class;
        }

        return null;
    }

    public static class DictBean extends JbBeanS {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name == null ? "" : name;
        }
    }

    @Override
    public String getIdentifierName(String entityName) {
        return "id";
    }

    @Override
    public Class<? extends Serializable> getIdentifierType(String entityName) {
        return String.class;
    }

    @Override
    public Object getIdentifier(String entityName, Object entity) {
        return ((IBase) entity).getId();
    }

    @Override
    public Object get(String entityName, Serializable id, JdbcCondition jdbcCondition) {
        return null;
    }

    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, int firstResult, int maxResults) {
        JDict dict = EntityService.ME.getDictCache().getCacheValue(entityName.substring(NAME_PRE_LENGTH));
        if (dict != null) {
            Map<String, String> metaMap = dict.getMetaMap();
            if (metaMap != null) {
                List<DictBean> beans = new ArrayList<DictBean>(metaMap.size());
                for (Map.Entry<String, String> entry : metaMap.entrySet()) {
                    DictBean bean = new DictBean();
                    bean.setId(entry.getKey());
                    bean.setName(entry.getValue());
                    beans.add(bean);
                }

                return beans;
            }
        }

        return KernelLang.NULL_LIST_SET;
    }

    @Override
    public List list(String entityName, JdbcCondition jdbcCondition, String queue, JdbcPage jdbcPage) {
        return list(entityName, jdbcCondition, queue, 0, 0);
    }

    @Override
    public Object create(String entityName) {
        return null;
    }

    @Override
    public void mergeEntity(String entityName, Object entity, boolean create) {

    }

    @Override
    public void deleteEntity(String entityName, Object entity) {

    }

    @Override
    public void evict(Object entity) {

    }

    @Override
    public void flush() {

    }
}
