/**
 * Copyright 2013 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2013-9-13 下午5:04:15
 */
package com.absir.aserv.configure;

import com.absir.aserv.consistent.ConsistentUtils;
import com.absir.aserv.system.bean.JConfigure;
import com.absir.aserv.system.bean.JEmbedSS;
import com.absir.aserv.system.service.BeanService;
import com.absir.client.helper.HelperJson;
import com.absir.core.base.IBase;
import com.absir.core.kernel.KernelClass;
import com.absir.core.kernel.KernelString;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class JConfigureBase implements IBase<Serializable> {

    @JsonIgnore
    protected transient boolean loaded;

    @JsonIgnore
    protected transient JConfigure jConfigure;

    @JsonIgnore
    private transient boolean deleteClear;

    @Override
    public Serializable getId() {
        return 0;
    }

    //@JaLang("删除清理")
    //@JaEdit(editable = JeEditable.ENABLE)
    public final boolean isDeleteClear() {
        return deleteClear;
    }

    public void setDeleteClear(boolean deleteClear) {
        this.deleteClear = deleteClear;
    }

    protected String getIdentifier() {
        return getClass().getName() + "@" + getId();
    }

    protected void loadInit() {
        loaded = true;
        jConfigure = (JConfigure) BeanService.ME.selectQuerySingle("SELECT o FROM JConfigure o WHERE o.id.eid = ? AND o.id.mid = ?", getIdentifier(), "");
        if (jConfigure != null && !KernelString.isEmpty(jConfigure.getValue())) {
            HelperJson.decodeForUpdating(jConfigure.getValue(), this);
        }
    }

    protected void copyFrom(JConfigureBase from) {
        HelperJson.decodeForUpdating(HelperJson.encodeNull(from), this);
    }

    public void merge() {
        if (jConfigure == null) {
            jConfigure = new JConfigure();
            jConfigure.setId(new JEmbedSS(getIdentifier(), ""));
        }

        jConfigure.setValue(HelperJson.encodeNull(this));
        BeanService.ME.merge(jConfigure);
        if (!loaded) {
            JConfigureUtils.reloadConfigure(getClass());
        }

        ConsistentUtils.pubConfigure(this);
    }

    protected void delete() {
        if (jConfigure != null) {
            jConfigure.setValue(null);
            BeanService.ME.delete(jConfigure);
        }

        if (loaded) {
            copyFrom(KernelClass.newInstance(getClass()));
        }

        ConsistentUtils.pubConfigure(this);
    }
}
