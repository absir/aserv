package com.absir.platform.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.system.bean.value.JiOrdinal;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DCacheOpen;
import com.absir.aserv.system.service.BeanService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.*;
import com.absir.platform.bean.base.JbPlatform;
import com.absir.platform.dto.DServer;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.util.*;

/**
 * Created by absir on 2016/12/2.
 */
@Base
@Bean
public class PlatformService {

    public static final PlatformService ME = BeanFactoryUtils.get(PlatformService.class);

    public static final JPlatformConfigure CONFIGURE = JConfigureUtils.getConfigure(JPlatformConfigure.class);

    protected DCacheOpen<Long, JSetting> settingDCacheOpen;

    protected DCacheOpen<Long, JAnnouncement> announcementDCacheOpen;

    protected DCacheOpen<Long, JServer> serverDCacheOpen;

    protected List<JSetting> settingEntries;

    protected List<PlatformAnnouncement> announcementEntries;

    protected List<PlatformServer> serverEntries;

    public static class PlatformAnnouncement implements JiOrdinal {

        protected JAnnouncement.AnnouncementEntry entry;

        protected JAnnouncement announcement;

        @Override
        public int getOrdinal() {
            return entry.getOrdinal();
        }
    }

    public static class PlatformServer {

        protected JServer server;

        protected JServer.ServerEntry entry;

        protected DServer dServer;
    }

    @Transaction(readOnly = true)
    public JPlatformFrom getPlatformFrom(String platform, String channel, String packageName, int versionCode, String from, boolean persist) {
        String refId = platform + "@" + channel + "@" + packageName + "@" + versionCode + "@" + from;
        Session session = BeanDao.getSession();
        JPlatformFromRef ref = BeanDao.get(session, JPlatformFromRef.class, refId);
        if (ref == null) {
            JPlatformFrom platformFrom = new JPlatformFrom();
            platformFrom.setPlatform(platform);
            platformFrom.setChannel(channel);
            platformFrom.setPackageName(packageName);
            platformFrom.setVersionCode(versionCode);
            platformFrom.setFormInfo(from);
            if (!persist) {
                return platformFrom;
            }

            try {
                session.persist(platform);
                session.flush();
                ref = new JPlatformFromRef();
                ref.setId(refId);
                ref.setPlatformFrom(platformFrom);
                session.merge(platform);

            } catch (ConstraintViolationException e) {
                session.clear();
                ref = BeanDao.get(session, JPlatformFromRef.class, refId);
                if (ref == null) {
                    platformFrom = (JPlatformFrom) QueryDaoUtils.createQueryArray(session, "SELECT o FROM JPlatformFROM o WHERE o.platform = ? AND o.channel = ? AND o.packageName = ? AND o.versionCode = ? AND o.fromInfo = ?", platform, channel, packageName, versionCode, from).iterate().next();
                    ref = new JPlatformFromRef();
                    ref.setId(refId);
                    ref.setPlatformFrom(platformFrom);
                    session.merge(platform);
                }
            }
        }

        return ref.getPlatformFrom();
    }

    @Inject
    protected void initService() {
        settingDCacheOpen = new DCacheOpen<Long, JSetting>(JSetting.class, null);
        settingDCacheOpen.addEntityMerges();
        announcementDCacheOpen = new DCacheOpen<Long, JAnnouncement>(JAnnouncement.class, null);
        announcementDCacheOpen.addEntityMerges();
        serverDCacheOpen = new DCacheOpen<Long, JServer>(JServer.class, null);
        serverDCacheOpen.addEntityMerges();

        reloadCaches();

        reloadSettings();
        reloadAnnouncements();
        reloadServers();

        settingDCacheOpen.reloadListener = new Runnable() {
            @Override
            public void run() {
                ME.reloadSettings();
            }
        };
        announcementDCacheOpen.reloadListener = new Runnable() {
            @Override
            public void run() {
                ME.reloadAnnouncements();
            }
        };
        serverDCacheOpen.reloadListener = new Runnable() {
            @Override
            public void run() {
                ME.reloadServers();
            }
        };
    }

    /**
     * 重载实体
     */
    @Schedule(cron = "0 0 30 * * * *")
    @Transaction(readOnly = true)
    protected void reloadCaches() {
        Session session = BeanDao.getSession();
        settingDCacheOpen.reloadCache(session);
        announcementDCacheOpen.reloadCache(session);
        serverDCacheOpen.reloadCache(session);
    }

    @Async(notifier = true)
    protected void reloadSettings() {
        try {
            List<JSetting> settings = new ArrayList<JSetting>(settingDCacheOpen.getCacheMap().values());
            Collections.sort(settings, BeanService.COMPARATOR);
            settingEntries = settings;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    @Async(notifier = true)
    protected void reloadAnnouncements() {
        List<PlatformAnnouncement> entries = new ArrayList<PlatformAnnouncement>();
        try {
            List<JAnnouncement> announcements = new ArrayList<JAnnouncement>(announcementDCacheOpen.getCacheMap().values());
            Collections.sort(announcements, BeanService.COMPARATOR);
            for (JAnnouncement announcement : announcements) {
                JAnnouncement.AnnouncementEntry[] announcementEntries = announcement.getAnnouncementList();
                if (announcementEntries != null) {
                    for (JAnnouncement.AnnouncementEntry announcementEntry : announcementEntries) {
                        PlatformAnnouncement platformAnnouncement = new PlatformAnnouncement();
                        platformAnnouncement.announcement = announcement;
                        platformAnnouncement.entry = announcementEntry;
                        entries.add(platformAnnouncement);
                    }
                }
            }

            announcementEntries = entries;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    @Async(notifier = true)
    protected void reloadServers() {
        List<PlatformServer> entries = new ArrayList<PlatformServer>();
        try {
            List<JServer> servers = new ArrayList<JServer>(serverDCacheOpen.getCacheMap().values());
            Collections.sort(servers, BeanService.COMPARATOR);
            for (JServer server : servers) {
                JServer.ServerEntry[] serverEntries = server.getServerList();
                if (serverEntries != null) {
                    for (JServer.ServerEntry serverEntry : serverEntries) {
                        PlatformServer platformServer = new PlatformServer();
                        platformServer.server = server;
                        platformServer.entry = serverEntry;
                        DServer dServer = createDServer(serverEntry);
                        JSlaveServer slaveServer = BeanService.ME.get(JSlaveServer.class, serverEntry.getId());
                        setDServer(dServer, serverEntry, slaveServer);
                        entries.add(platformServer);
                    }
                }
            }

            serverEntries = entries;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    protected DServer createDServer(JServer.ServerEntry entry) {
        DServer dServer = new DServer();
        dServer.id = entry.getId();
        return dServer;
    }

    protected void setDServer(DServer dServer, JServer.ServerEntry entry, JSlaveServer slaveServer) {

    }

    public static boolean isExcludeIds(Set<String> excludeIds, boolean allIds, Set<String> ids, String id) {
        if (ids != null) {
            if (excludeIds != null && !excludeIds.isEmpty() && excludeIds.contains(id)) {
                return true;
            }

            if (!allIds) {
                if (ids == null || ids.isEmpty() || !ids.contains(id)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isMatchPlatform(JbPlatform platform, JPlatformFrom platformFrom) {
        if (!platform.isOpen()) {
            return false;
        }

        if (isExcludeIds(platform.getExcludePlatforms(), platform.isAllPlatforms(), platform.getPlatforms(), platformFrom.getPlatform())) {
            return false;
        }

        if (isExcludeIds(platform.getExcludeChannels(), platform.isAllChannels(), platform.getChannels(), platformFrom.getChannel())) {
            return false;
        }

        if (isExcludeIds(platform.getExcludePackageNames(), platform.isAllPackageNames(), platform.getPackageNames(), platformFrom.getPackageName())) {
            return false;
        }

        int versionCode = platformFrom.getVersionCode();
        if (versionCode > 0) {
            int code = platform.getMinVersionCode();
            if (code != 0 && versionCode < code) {
                return false;
            }

            code = platform.getMaxVersionCode();
            if (code != 0 && versionCode > code) {
                return false;
            }
        }

        String from = platformFrom.getFormInfo();
        if (from != null) {
            Map.Entry<String, KernelLang.IMatcherType> entry = platform.forMatchFromEntry();
            if (entry != null && !KernelLang.MatcherType.isMatch(from, entry)) {
                return false;
            }
        }

        return true;
    }

}
