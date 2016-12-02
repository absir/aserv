package com.absir.platform.bean.base;

import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JaName;
import com.absir.aserv.system.bean.value.JiOpen;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import org.hibernate.annotations.Type;

import javax.persistence.MappedSuperclass;
import java.util.Map;
import java.util.Set;

/**
 * Created by absir on 2016/12/2.
 */
@MappedSuperclass
public abstract class JbPlatform implements JiOpen {

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    @JaName("JPlatform")
    @JaLang("平台")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> platformIds;

    private boolean allPlatformIds;

    @JaName("JPlatform")
    @JaLang("排除平台")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> excludePlatformIds;

    @JaName("JChannel")
    @JaLang("渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> channelIds;

    @JaLang("全渠道")
    private boolean allChannelIds;

    @JaName("JChannel")
    @JaLang("排除渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> excludeChannelIds;

    @JaLang("最小版本号")
    private int minVersionCode;

    @JaLang("最大版本号")
    private int maxVersionCode;

    @JaLang("匹配来源")
    private String matchFrom;

    private transient Map.Entry<String, KernelLang.IMatcherType> matchFromEntry;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Set<String> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(Set<String> platformIds) {
        this.platformIds = platformIds;
    }

    public boolean isAllPlatformIds() {
        return allPlatformIds;
    }

    public void setAllPlatformIds(boolean allPlatformIds) {
        this.allPlatformIds = allPlatformIds;
    }

    public Set<String> getExcludePlatformIds() {
        return excludePlatformIds;
    }

    public void setExcludePlatformIds(Set<String> excludePlatformIds) {
        this.excludePlatformIds = excludePlatformIds;
    }

    public Set<String> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(Set<String> channelIds) {
        this.channelIds = channelIds;
    }

    public boolean isAllChannelIds() {
        return allChannelIds;
    }

    public void setAllChannelIds(boolean allChannelIds) {
        this.allChannelIds = allChannelIds;
    }

    public Set<String> getExcludeChannelIds() {
        return excludeChannelIds;
    }

    public void setExcludeChannelIds(Set<String> excludeChannelIds) {
        this.excludeChannelIds = excludeChannelIds;
    }

    public int getMinVersionCode() {
        return minVersionCode;
    }

    public void setMinVersionCode(int minVersionCode) {
        this.minVersionCode = minVersionCode;
    }

    public int getMaxVersionCode() {
        return maxVersionCode;
    }

    public void setMaxVersionCode(int maxVersionCode) {
        this.maxVersionCode = maxVersionCode;
    }

    public String getMatchFrom() {
        return matchFrom;
    }

    public void setMatchFrom(String matchFrom) {
        this.matchFrom = matchFrom;
    }

    public Map.Entry<String, KernelLang.IMatcherType> forMatchFromEntry() {
        if (matchFromEntry == null && matchFrom != null) {
            if (KernelString.isEmpty(matchFrom)) {
                matchFrom = null;

            } else {
                matchFromEntry = KernelLang.MatcherType.getMatchEntry(matchFrom, true);
            }
        }

        return matchFromEntry;
    }
}
