package com.absir.platform.service;

import com.absir.aserv.system.domain.DCacheOpen;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.core.kernel.KernelLang;
import com.absir.platform.bean.JSetting;
import com.absir.platform.bean.base.JbPlatform;

import java.util.Map;
import java.util.Set;

/**
 * Created by absir on 2016/12/2.
 */
@Base
@Bean
public class PlatformService {

    public static final PlatformService ME = BeanFactoryUtils.get(PlatformService.class);

    private DCacheOpen<Long, JSetting> settingDCacheOpen = new DCacheOpen<Long, JSetting>(JSetting.class, null);

    public boolean isMatchPlatform(JbPlatform jbPlatform, String platform, String channel, int versionCode, String from) {
        if (!jbPlatform.isOpen()) {
            return false;
        }

        Set<String> ids = jbPlatform.getExcludePlatformIds();
        if (ids != null || ids.contains(platform)) {
            return false;
        }

        if (!jbPlatform.isAllPlatformIds()) {
            ids = jbPlatform.getPlatformIds();
            if (ids == null || !ids.contains(platform)) {
                return false;
            }
        }

        ids = jbPlatform.getExcludeChannelIds();
        if (ids != null || ids.contains(channel)) {
            return false;
        }

        if (!jbPlatform.isAllChannelIds()) {
            ids = jbPlatform.getChannelIds();
            if (ids == null || !ids.contains(channel)) {
                return false;
            }
        }

        int code = jbPlatform.getMinVersionCode();
        if (code != 0 && versionCode < code) {
            return false;
        }

        code = jbPlatform.getMaxVersionCode();
        if (code != 0 && versionCode > code) {
            return false;
        }

        Map.Entry<String, KernelLang.IMatcherType> entry = jbPlatform.forMatchFromEntry();
        if (entry != null && !KernelLang.MatcherType.isMatch(from, entry)) {
            return false;
        }

        return true;
    }

}
