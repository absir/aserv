/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-8 下午2:43:55
 */
package com.absir.orm.hibernate.boost;

/**
 * @author absir
 *
 */
public interface IEntityMerge<T> {

    /**
     * @param entityName
     * @param entity
     * @param mergeType
     * @param mergeEvent
     */
    public void merge(String entityName, T entity, MergeType mergeType, Object mergeEvent);

    /**
     * @author absir
     */
    public enum MergeType {

        /**
         * INSERT
         */
        INSERT,

        /**
         * UPDATE
         */
        UPDATE,

        /** DELETE */
        DELETE;
    }

}
