/**
 * Copyright 2015 ABSir's Studio
 * <p>
 * All right reserved
 * <p>
 * Create on 2015年11月18日 下午2:18:06
 */
package com.absir.master.service;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DCache;
import com.absir.aserv.system.domain.DCacheEntity;
import com.absir.aserv.system.domain.DCacheOpen;
import com.absir.aserv.system.service.BeanService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.kernel.KernelString;
import com.absir.core.kernel.KernelUtil;
import com.absir.master.bean.JChannel;
import com.absir.master.bean.JChannelAnnouncement;
import com.absir.master.bean.JChannelSlaveServer;
import com.absir.master.bean.JPlayer;
import com.absir.master.bean.dto.DAnnouncement;
import com.absir.master.bean.dto.DServer;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.service.PlatformService;
import org.hibernate.Session;

import java.util.*;

@SuppressWarnings("unchecked")
@Base
@Bean
public abstract class MasterChannelService {

    public static final MasterChannelService ME = BeanFactoryUtils.get(MasterChannelService.class);

    private DCacheEntity<JChannel> channelCache;

    private DCache<JChannelAnnouncement, List<DAnnouncement>> channelAnnouncementCache;

    private Map<String, List<DServer>> channelMapServers;

    @Inject
    protected void inject() {
        channelCache = new DCacheEntity<JChannel>(JChannel.class, null);
        channelAnnouncementCache = new DCacheOpen<List<DAnnouncement>, JChannelAnnouncement>(JChannelAnnouncement.class,
                null);
        channelMapServers = new HashMap<String, List<DServer>>();
        ME.reloadCache();
        L2EntityMergeService.ME.addEntityMerges(JSlaveServer.class, new IEntityMerge<JSlaveServer>() {

            @Override
            public void merge(String entityName, JSlaveServer entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                channelMapServers = new HashMap<String, List<DServer>>();
            }

        });
        L2EntityMergeService.ME.addEntityMerges(JChannelSlaveServer.class, new IEntityMerge<JChannelSlaveServer>() {

            @Override
            public void merge(String entityName, JChannelSlaveServer entity,
                              com.absir.orm.hibernate.boost.IEntityMerge.MergeType mergeType, Object mergeEvent) {
                channelMapServers = new HashMap<String, List<DServer>>();
            }

        });
    }

    /**
     * 重载缓存
     */
    @Schedule(cron = "0 0,30 * * * *")
    @Async(notifier = true)
    @Transaction(readOnly = true)
    protected void reloadCache() {
        Session session = BeanDao.getSession();
        channelCache.reloadCache(session);
        channelAnnouncementCache.reloadCache(session);
        channelMapServers = new HashMap<String, List<DServer>>();
    }

    protected String getChannelAlias(String channel, String version, boolean annc) {
        JChannel jChannel = channelCache.getCacheValue(channel);
        if (jChannel == null) {
            return null;
        }

        String alias = annc ? jChannel.getAnnouncementAlias() : jChannel.getServerAlias();
        channel = KernelString.isEmpty(alias) ? channel : alias;
        if (version != null) {
            String toVersion = jChannel.getVersion();
            if (toVersion != null && KernelUtil.compareVersion(version, toVersion) > 0) {
                return channel + "_review";
            }
        }

        return channel;
    }

    public List<DAnnouncement> getAnnouncements(String channel, String channelCode, String version) {
        List<DAnnouncement> announcements = KernelString.isEmpty(channelCode) ? null
                : channelAnnouncementCache.getCacheValue(channelCode);
        if (announcements != null) {
            return announcements;
        }

        String alias = ME.getChannelAlias(channel, version, true);
        if (alias == null) {
            return null;
        }

        return channelAnnouncementCache.getCacheValue(alias);
    }

    public List<DServer> getServers(String channel, String version) {
        String alias = ME.getChannelAlias(channel, version, false);
        if (alias == null) {
            return null;
        }

        return findServers(channel);
    }

    @Transaction
    @DataQuery("SELECT o FROM JSlaveServerChannel o WHERE o.channel = ? AND o.server.synched = TRUE ORDER BY o.id DESC")
    public abstract List<JChannelSlaveServer> getChannelServers(String channel);

    public List<DServer> findServers(String channel) {
        List<DServer> servers = channelMapServers.get(channel);
        if (servers == null) {
            servers = ME.selectServers(channel);
            channelMapServers.put(channel, servers);
        }

        return servers;
    }

    @Transaction(readOnly = true)
    public List<DServer> selectServers(String channel) {
        long currentTime = System.currentTimeMillis();
        Iterator<JChannelSlaveServer> iterator = QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "SELECT o FROM JSlaveServerChannel o WHERE o.channel = ? AND o.server.passTime > ? AND o.server.synched = TRUE ORDER BY o.id DESC",
                channel, currentTime).iterate();
        List<DServer> servers = new ArrayList<DServer>();
        boolean opened = false;
        while (iterator.hasNext()) {
            JChannelSlaveServer channelSlaveServer = iterator.next();
            JSlaveServer slaveServer = channelSlaveServer.getServer();
            JSlave slave = slaveServer.getHost();
            long beginTime = slaveServer.getBeginTime();
            int status = beginTime > currentTime ? 0 : slave.isConnecting() || slave.isForceOpen() ? 1 : 2;
            if (status == 0 && opened) {
                break;
            }

            DServer server = new DServer();
            servers.add(server);
            server.id = slaveServer.getId();
            String ip = slaveServer.getServerIP();
            if (KernelString.isEmpty(ip)) {
                ip = slave.getServerIP();
                if (KernelString.isEmpty(ip)) {
                    ip = slave.getIp();
                }
            }

            server.ip = ip;
            server.port = slaveServer.getPort();
            server.name = channelSlaveServer.getName();
            server.openTime = (int) ((currentTime - beginTime) / 1000);
            server.status = status;
            server.path = slave.getPath();
            if (status == 0) {
                break;
            }

            opened = true;
        }

        return servers;
    }

    public JPlayer getPlayer(long serverId, JPlatformUser platformUser, String channel) {
        JPlayer player = (JPlayer) BeanService.ME.selectQuerySingle("SELECT o FROM JPlayer WHERE o.serverId = ? AND o.userId = ?", serverId, platformUser.getId());
        if (player == null) {
            player = new JPlayer();
            player.setServerId(serverId);
            player.setUserId(platformUser.getUserId());
            player.setPlatform(platformUser.getPlatform());
            player.setChannel(platformUser.getChannel());
            player.setCreateTime(ContextUtils.getContextTime());
            BeanService.ME.persist(player);
        }

        return player;
    }

    /**
     * @return 1 success | 2 login failed | 3 can't login
     */
    public JPlayer selectServerId(long serverId, String sessionId, String channel) {
        JPlatformUser platformUser = PlatformService.ME.loginForSessionId(sessionId);
        if (platformUser == null) {
            return null;
        }

        JPlayer player = getPlayer(serverId, platformUser, channel);
        if (player == null) {
            return null;
        }

        return player;
    }

}
