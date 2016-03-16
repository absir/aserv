/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014-4-10 上午10:49:18
 */
package com.absir.aserv.system.domain;

import com.absir.core.base.IBase;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DActiverMap<T extends IBase<? extends Serializable>, K> {

    private Map<Serializable, K> onlineActiveContexts;

    public DActiverMap() {
        onlineActiveContexts = createActiveContexts();
        if (onlineActiveContexts == null) {
            onlineActiveContexts = createActiveContextMap();
        }
    }

    public Map<Serializable, K> getOnlineActiveContexts() {
        return onlineActiveContexts;
    }

    protected abstract Map<Serializable, K> createActiveContexts();

    protected Map<Serializable, K> createActiveContextMap() {
        return new LinkedHashMap<Serializable, K>();
    }

    protected abstract boolean isClosed(K activeContext);

    protected abstract K createActiveContext(T active);

    protected abstract K updateActiveContext(T active, K activeContext);

    protected abstract void closeActiveContext(Serializable id, K activeContext);

    protected abstract void reloadAllActiveContext(boolean hasClosed);

    public void setActives(Collection<T> actives) {
        boolean hasClosed = false;
        Map<Serializable, K> onlineActiveContextMap = createActiveContextMap();
        for (T active : actives) {
            Serializable id = active.getId();
            K activeContext = onlineActiveContexts.get(id);
            if (activeContext == null || isClosed(activeContext)) {
                activeContext = createActiveContext(active);

            } else {
                K context = activeContext;
                activeContext = updateActiveContext(active, activeContext);
                if (activeContext == null || activeContext != context) {
                    hasClosed = true;
                    closeActiveContext(id, context);
                }

                if (activeContext == null || isClosed(activeContext)) {
                    activeContext = createActiveContext(active);
                }
            }

            if (activeContext != null) {
                onlineActiveContextMap.put(id, activeContext);
            }
        }

        for (Entry<Serializable, K> entry : onlineActiveContexts.entrySet()) {
            Serializable id = entry.getKey();
            if (!onlineActiveContextMap.containsKey(id)) {
                hasClosed = true;
                closeActiveContext(id, entry.getValue());
            }
        }

        setActiveContextMap(onlineActiveContextMap);
        reloadAllActiveContext(hasClosed);
    }

    public void setActiveContextMap(Map<Serializable, K> onlineActiveContextMap) {
        onlineActiveContexts = onlineActiveContextMap;
    }
}
