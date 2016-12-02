/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午2:43:55
 */
package com.absir.orm.hibernate.boost;

public interface IEntityMerge<T> {

    public void merge(String entityName, T entity, MergeType mergeType, Object mergeEvent);

    public enum MergeType {

        INSERT,

        UPDATE,

        DELETE,

        RELOAD,;
    }

}
