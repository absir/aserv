/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-11-6 上午10:27:23
 */
package com.absir.aserv.system.helper;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.core.base.IBase;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
public class HelperBase {

    /**
     * @param base
     * @return
     */
    public static Serializable getLazyId(IBase base) {
        if (base == null) {
            return null;
        }

        if (base instanceof HibernateProxy) {
            return ((HibernateProxy) base).getHibernateLazyInitializer().getIdentifier();

        } else {
            return base.getId();
        }
    }

    /**
     * @param bases
     * @return
     */
    public static Serializable[] getBaseIds(IBase[] bases) {
        int length = bases.length;
        Serializable[] ids = new Serializable[length];
        for (int i = 0; i < length; i++) {
            ids[i] = bases[i].getId();
        }

        return ids;
    }

    /**
     * @param bases
     * @return
     */
    public static Serializable[] getBaseIds(Collection<? extends IBase> bases) {
        Serializable[] ids = new Serializable[bases.size()];
        int i = 0;
        for (IBase base : bases) {
            ids[i++] = base.getId();
        }

        return ids;
    }

    /**
     * @param bases
     * @param crudSupply
     * @return
     */
    public static Object[] getBaseIds(String entityName, Collection<?> bases, ICrudSupply crudSupply) {
        Object[] ids = new Serializable[bases.size()];
        int i = 0;
        for (Object base : bases) {
            ids[i++] = crudSupply.getIdentifier(entityName, base);
        }

        return ids;
    }
}
