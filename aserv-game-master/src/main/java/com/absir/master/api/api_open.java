/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月18日 下午2:15:37
 */
package com.absir.master.api;

import com.absir.aserv.system.api.ApiServer;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.basis.Base;
import com.absir.core.kernel.KernelString;
import com.absir.master.bean.dto.DAnnouncement;
import com.absir.master.bean.dto.DServer;
import com.absir.master.service.MasterChannelService;
import com.absir.master.service.MasterTradeService;
import com.absir.open.bean.JPayTrade;
import com.absir.open.service.utils.PayUtils;
import com.absir.platform.bean.JPlatformSession;
import com.absir.platform.service.PlatformService;
import com.absir.server.in.Input;
import com.absir.server.value.Body;
import com.absir.server.value.Nullable;
import com.absir.server.value.Param;
import com.absir.server.value.Server;

import java.util.List;
import java.util.UUID;

@Base
@Server
public class api_open extends ApiServer {

    @JaLang("公告列表")
    public List<DAnnouncement> announcements(String channel, String channelCode, String version) {
        return MasterChannelService.ME.getAnnouncements(channel, channelCode, version);
    }

    @JaLang("服务列表")
    public List<DServer> servers(String channel, String version) {
        return MasterChannelService.ME.getServers(channel, version);
    }

    @JaLang("购买")
    public String buy(String platform, String channel, long serverId, long playerId, int index, float amount) {
        return MasterTradeService.ME.buyDollar(platform, channel, serverId, playerId, index, amount).getId();
    }

    @JaLang("购买")
    public Object order(String platform, String channel, long serverId, long playerId, int index, float amount,
                        Input input) throws Exception {
        JPayTrade payTrade = MasterTradeService.ME.buyDollar(platform, channel, serverId, playerId, index, amount);
        return PayUtils.order(platform, channel, payTrade, input.getParamMap());
    }

    @JaLang("验证订单")
    public boolean validate(String platform, JPayTrade payTrade, String tradeNo) throws Exception {
        payTrade.setTradeNo(tradeNo);
        return PayUtils.process(platform, payTrade) == null;
    }

    @JaLang("购买验证")
    public boolean buyValidate(String platform, String channel, long serverId, long playerId, int index, float amount,
                               @Body String tradeNo) throws Exception {
        return MasterTradeService.ME.buyValidate(platform, channel, serverId, playerId, index, amount, tradeNo) != null;
    }

    @JaLang("一键登录")
    public String[] okLogin(@Param @Nullable String uuid, String channel, Input input) {
        if (KernelString.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
        }

        JPlatformSession session = PlatformService.ME.loginReSession("OKL", uuid, channel, input.getAddress(), input.getFacade().getUserAgent());
        return new String[]{uuid, session.getId()};
    }

    @JaLang("选择分区")
    public long selectServerId(long serverId, String sessionId, String channel) {
        return MasterChannelService.ME.selectServerId(serverId, sessionId, channel).getId();
    }

}
