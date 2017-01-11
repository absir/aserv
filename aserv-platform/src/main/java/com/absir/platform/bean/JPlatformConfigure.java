package com.absir.platform.bean;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.menu.value.MaEntity;
import com.absir.aserv.menu.value.MaMenu;
import com.absir.aserv.system.bean.value.JaLang;
import org.hibernate.annotations.Type;

import java.util.Map;

/**
 * Created by absir on 2016/12/5.
 */
@MaEntity(parent = {@MaMenu("平台管理")}, name = "平台")
public class JPlatformConfigure extends JConfigureBase {

    @JaLang("审核字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, Boolean> reviewMap;

    @JaLang("映射字典")
    @Type(type = "com.absir.aserv.system.bean.type.JtJsonMap")
    private Map<String, String> mappingMap;

    public Map<String, Boolean> getReviewMap() {
        return reviewMap;
    }

    public void setReviewMap(Map<String, Boolean> reviewMap) {
        this.reviewMap = reviewMap;
    }

    public Map<String, String> getMappingMap() {
        return mappingMap;
    }

    public void setMappingMap(Map<String, String> mappingMap) {
        this.mappingMap = mappingMap;
    }

    public boolean isReview(String packageName, double versionCode) {
        return reviewMap == null || reviewMap.isEmpty() ? false : reviewMap.get(packageName + '@' + versionCode) == Boolean.TRUE;
    }

}
