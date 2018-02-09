package com.absir.aserv.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge;

public interface IConsistent {

    public static final IConsistent ME = BeanFactoryUtils.get(IConsistent.class);

    public void pubConfigure(JConfigureBase configureBase);

    public boolean isMergeEntity(String entityName, Class<?> entityClass);

    public void pubMergeEntity(String entityKey, String entityName, Object entity, IEntityMerge.MergeType mergeType);
}
