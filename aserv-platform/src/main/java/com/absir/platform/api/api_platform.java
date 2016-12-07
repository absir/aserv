package com.absir.platform.api;

import com.absir.aserv.system.api.ApiBase;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.platform.dto.DReviewSetting;
import com.absir.platform.service.PlatformServerService;
import com.absir.server.in.Input;

import java.io.IOException;

/**
 * Created by absir on 2016/12/1.
 */
@Base
@Bean
public class api_platform extends ApiBase {

    @JaLang("授权设置")
    public DReviewSetting review(String deviceId, Input input) {
        return PlatformServerService.ME.reviewSetting(input);
    }

    @JaLang("公告列表")
    public void announcements(boolean review, Input input) throws IOException {
        PlatformServerService.ME.listResponse(PlatformServerService.ME.getAnnouncementEntries(), review, input);
    }

    @JaLang("公告列表")
    public void servers(boolean review, Input input) throws IOException {
        PlatformServerService.ME.listResponse(PlatformServerService.ME.getServerEntries(), review, input);
    }


}
