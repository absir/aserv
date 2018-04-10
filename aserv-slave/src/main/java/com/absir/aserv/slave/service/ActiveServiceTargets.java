/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年7月10日 下午3:22:43
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.slave.bean.base.JbServerTargets;
import com.absir.aserv.slave.domain.OTargetsActivity;
import com.absir.aserv.system.bean.value.JiActive;
import com.absir.aserv.system.service.ActiveService.ActiveServiceData;
import com.absir.bean.inject.value.Inject;
import com.absir.core.kernel.KernelLang.ObjectEntry;

public abstract class ActiveServiceTargets<T extends JiActive, K> extends ActiveServiceData<T, ObjectEntry<T, K>> {

    private OTargetsActivity<ObjectEntry<T, K>> targetsActivity;

    public JbServerTargets getBeanLTargets(T active) {
        return (JbServerTargets) active;
    }

    public ObjectEntry<T, K> getSingleEntry(long serverId) {
        return targetsActivity.getSingleActivity(serverId);
    }

    public K getSingleEntryValue(long serverId) {
        ObjectEntry<T, K> entry = getSingleEntry(serverId);
        return entry == null ? null : entry.getValue();
    }

    @Inject
    protected void initService() {
        targetsActivity = new OTargetsActivity<ObjectEntry<T, K>>();
    }

    @Override
    protected ObjectEntry<T, K> createActiveContext(T active) {
        ObjectEntry<T, K> entry = new ObjectEntry<T, K>(active, createActiveContextSingle(active));
        addObjectEntry(targetsActivity, active, entry);
        return entry;
    }

    public void addObjectEntry(OTargetsActivity<ObjectEntry<T, K>> activity, T active, ObjectEntry<T, K> entry) {
        activity.addActivity(getBeanLTargets(entry.getKey()), entry);
    }

    protected abstract K createActiveContextSingle(T active);

    protected void updateActiverMap() {
        OTargetsActivity<ObjectEntry<T, K>> activity = new OTargetsActivity<ObjectEntry<T, K>>();
        for (ObjectEntry<T, K> entry : activerMap.getOnlineActiveContexts().values()) {
            addObjectEntry(activity, entry.getKey(), entry);
        }

        targetsActivity = activity;
    }

    @Override
    protected ObjectEntry<T, K> updateActiveContext(T active, ObjectEntry<T, K> activeContext) {
        activeContext.setKey(active);
        return activeContext;
    }

    @Override
    protected void reloadAllActiveContext(boolean hasClosed) {
        super.reloadAllActiveContext(hasClosed);
        updateActiverMap();
    }

}
