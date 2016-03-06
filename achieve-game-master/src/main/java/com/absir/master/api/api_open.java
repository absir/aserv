/**
 * Copyright 2015 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2015年11月18日 下午2:15:37
 */
package com.absir.master.api;

import java.util.List;

import com.absir.aserv.system.api.ApiServer;
import com.absir.aserv.system.bean.value.JaLang;
import com.absir.bean.basis.Base;
import com.absir.master.bean.dto.DAnnouncement;
import com.absir.master.bean.dto.DServer;
import com.absir.master.service.MasterChannelService;
import com.absir.master.service.MasterOpenService;
import com.absir.master.service.MasterTradeService;
import com.absir.open.bean.JPayTrade;
import com.absir.open.service.utils.PayUtils;
import com.absir.server.in.Input;
import com.absir.server.value.Body;
import com.absir.server.value.Server;

/**
 * @author absir
 *
 */
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

	@JaLang("注册")
	public int register(String username, String password, String email, long platformUserId) {
		return MasterOpenService.ME.register(username, password, email, platformUserId);
	}

	@JaLang("登录")
	public String login(String username, String password, int time, String channel) {
		return MasterOpenService.ME.login(username, password, time, channel);
	}

	@JaLang("修改密码")
	public int rePassword(String username, String password, int time, String newPassword, String email) {
		return MasterOpenService.ME.rePassword(username, password, time, newPassword, email);
	}

	@JaLang("绑定平台账号")
	public int bindPlatformUser(String platform, String platformUsername, String sessionId, String username,
			String password, String email) {
		return MasterOpenService.ME.bindPlatformUser(platform, platformUsername, sessionId, username, password, email);
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
		return PayUtils.proccess(platform, payTrade) == null;
	}

	@JaLang("购买验证")
	public boolean buyValidate(String platform, String channel, long serverId, long playerId, int index, float amount,
			@Body String tradeNo) throws Exception {
		return MasterTradeService.ME.buyValidate(platform, channel, serverId, playerId, index, amount, tradeNo) != null;
	}

}
