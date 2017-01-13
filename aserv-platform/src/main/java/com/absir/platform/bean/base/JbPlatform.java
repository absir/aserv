package com.absir.platform.bean.base;

import com.absir.aserv.system.bean.base.JbBase;
import com.absir.aserv.system.bean.value.JaEdit;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.aserv.system.bean.value.JiOpen;
import com.absir.aserv.system.bean.value.JiOrdinal;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Map;

/**
 * Created by absir on 2016/12/2.
 */
@MappedSuperclass
public abstract class JbPlatform extends JbBase implements JiOpen, JiOrdinal {

    @JaLang("开启")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean open;

    @JaLang("备注")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private String mark;

    @JaLang("审核")
    @JaEdit(groups = JaEdit.GROUP_LIST)
    private boolean review;

    @JaLang("平台")
    @Column(length = 2048)
    private String platforms;

    @JaLang("渠道")
    @Column(length = 2048)
    private String channels;

    @JaLang("包名")
    @Column(length = 2048)
    private String packageNames;

    @JaLang("最小版本号")
    private double minVersionDouble;

    @JaLang("最大版本号")
    private double maxVersionDouble;

    @JaLang("匹配来源")
    private String matchFromStr;

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

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getPackageNames() {
        return packageNames;
    }

    public void setPackageNames(String packageNames) {
        this.packageNames = packageNames;
    }

    public double getMinVersionDouble() {
        return minVersionDouble;
    }

    public void setMinVersionDouble(double minVersionDouble) {
        this.minVersionDouble = minVersionDouble;
    }

    public double getMaxVersionDouble() {
        return maxVersionDouble;
    }

    public void setMaxVersionDouble(double maxVersionDouble) {
        this.maxVersionDouble = maxVersionDouble;
    }

    public String getMatchFromStr() {
        return matchFromStr;
    }

    public void setMatchFromStr(String matchFromStr) {
        this.matchFromStr = matchFromStr;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public Map.Entry<String, KernelLang.IMatcherType> forMatchFromEntry() {
        if (matchFromEntry == null && matchFromStr != null) {
            if (KernelString.isEmpty(matchFromStr)) {
                matchFromStr = null;

            } else {
                matchFromEntry = KernelLang.MatcherType.getMatchEntry(matchFromStr, true);
            }
        }

        return matchFromEntry;
    }
}
