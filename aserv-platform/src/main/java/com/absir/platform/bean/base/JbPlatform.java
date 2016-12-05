package com.absir.platform.bean.base;

import com.absir.aserv.system.bean.value.*;
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
public abstract class JbPlatform implements JiOpen, JiOrdinal {

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    @JaLang("审核")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean review;

    @JaName("JPlatform")
    @JaLang("平台")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> platforms;

    private boolean allPlatforms;

    @JaName("JPlatform")
    @JaLang("排除平台")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> excludePlatforms;

    @JaName("JChannel")
    @JaLang("渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> channels;

    @JaLang("全渠道")
    private boolean allChannels;

    @JaName("JChannel")
    @JaLang("排除渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> excludeChannels;

    @JaName("JPackageName")
    @JaLang("渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> packageNames;

    @JaLang("全渠道")
    private boolean allPackageNames;

    @JaName("JPackageName")
    @JaLang("排除渠道")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonSet")
    private Set<String> excludePackageNames;

    @JaLang("最小版本号")
    private int minVersionCode;

    @JaLang("最大版本号")
    private int maxVersionCode;

    @JaLang("匹配来源")
    private String matchFrom;

    @JaLang("排序")
    private int ordinal;

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

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public Set<String> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<String> platforms) {
        this.platforms = platforms;
    }

    public boolean isAllPlatforms() {
        return allPlatforms;
    }

    public void setAllPlatforms(boolean allPlatforms) {
        this.allPlatforms = allPlatforms;
    }

    public Set<String> getExcludePlatforms() {
        return excludePlatforms;
    }

    public void setExcludePlatforms(Set<String> excludePlatforms) {
        this.excludePlatforms = excludePlatforms;
    }

    public Set<String> getChannels() {
        return channels;
    }

    public void setChannels(Set<String> channels) {
        this.channels = channels;
    }

    public boolean isAllChannels() {
        return allChannels;
    }

    public void setAllChannels(boolean allChannels) {
        this.allChannels = allChannels;
    }

    public Set<String> getExcludeChannels() {
        return excludeChannels;
    }

    public void setExcludeChannels(Set<String> excludeChannels) {
        this.excludeChannels = excludeChannels;
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(Set<String> packageNames) {
        this.packageNames = packageNames;
    }

    public boolean isAllPackageNames() {
        return allPackageNames;
    }

    public void setAllPackageNames(boolean allPackageNames) {
        this.allPackageNames = allPackageNames;
    }

    public Set<String> getExcludePackageNames() {
        return excludePackageNames;
    }

    public void setExcludePackageNames(Set<String> excludePackageNames) {
        this.excludePackageNames = excludePackageNames;
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

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
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
