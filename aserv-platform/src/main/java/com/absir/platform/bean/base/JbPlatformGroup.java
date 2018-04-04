package com.absir.platform.bean.base;

import com.absir.aserv.system.bean.value.JaLang;

import javax.persistence.MappedSuperclass;

/**
 * Created by absir on 2016/12/2.
 */
@MappedSuperclass
public abstract class JbPlatformGroup extends JbPlatform {

    @JaLang("组号")
    private String groupIds;

    @JaLang("不审核")
    private boolean notReview;

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    public boolean isNotReview() {
        return notReview;
    }

    public void setNotReview(boolean notReview) {
        this.notReview = notReview;
    }
}
