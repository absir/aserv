package com.absir.platform.bean;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.core.kernel.KernelString;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * Created by absir on 2016/12/5.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "平台")
public class JPlatformConfigure extends JConfigureBase {

    @JaLang("新开时间")
    private long newlyTime;

    @JaLang("审核字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, Integer> reviewMap;

    @JaLang("映射字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> mappingMap;

    public long getNewlyTime() {
        return newlyTime;
    }

    public void setNewlyTime(long newlyTime) {
        this.newlyTime = newlyTime;
    }

    public Map<String, Integer> getReviewMap() {
        return reviewMap;
    }

    public void setReviewMap(Map<String, Integer> reviewMap) {
        this.reviewMap = reviewMap;
    }

    public Map<String, String> getMappingMap() {
        return mappingMap;
    }

    public void setMappingMap(Map<String, String> mappingMap) {
        this.mappingMap = mappingMap;
    }

    public int getReviewType(String packageName, String versionName, String fromStr) {
        if (reviewMap != null) {
            Integer review = reviewMap.get(KernelString.isEmpty(fromStr) ? (packageName + '@' + versionName) : (packageName + '@' + versionName + '@' + fromStr));
            if (review != null) {
                return review;
            }
        }

        return 0;
    }

}
