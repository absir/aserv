/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-8-8 下午6:48:03
 */
package com.absir.aserv.support.developer;

import com.absir.aserv.system.bean.value.JeEditable;

import java.util.List;
import java.util.Map;

public interface IField {

    public JCrudField getCrudField();

    public String getName();

    public Class<?> getType();

    public IField getValueField();

    public String getEntityName();

    public String getValueEntityName();

    public String getCaption();

    public String[] getGroups();

    public boolean isGenerated();

    public boolean isCanOrder();

    public boolean isNullable();

    public boolean isCollection();

    public String getMappedBy();

    public List<String> getTypes();

    public JeEditable getEditable();

    public Map<String, Object> getMetas();

    public Object getDefaultEntity();
}
