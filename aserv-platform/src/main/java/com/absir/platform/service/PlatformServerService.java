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
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.data.helper.HelperDataFormat;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.*;
import com.absir.platform.bean.base.JbPlatform;
import com.absir.platform.dto.DReviewSetting;
import com.absir.platform.dto.DServer;
import com.absir.server.in.Input;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by absir on 2016/12/2.
 */
@Base
@Bean
public class PlatformServerService implements IEntityMerge<JSlaveServer> {

    public static final PlatformServerService ME = BeanFactoryUtils.get(PlatformServerService.class);

    public static final JPlatformConfigure CONFIGURE = JConfigureUtils.getConfigure(JPlatformConfigure.class);

    protected DCacheOpen<Long, JSetting> settingDCacheOpen;

    protected DCacheOpen<Long, JAnnouncement> announcementDCacheOpen;

    protected DCacheOpen<Long, JServer> serverDCacheOpen;

    protected List<JSetting> settingEntries;

    protected List<PlatformAnnouncement> announcementEntries;

    protected List<PlatformServer> serverEntries;

    public static class PlatformAnnouncement implements JiOrdinal, IPlatformGet {

        protected JAnnouncement.AnnouncementEntry entry;

        protected JAnnouncement announcement;

        @Override
        public int getOrdinal() {
            return entry.getOrdinal();
        }

        @Override
        public JbPlatform getPlatform() {
            return announcement;
        }

        @Override
        public Object getEntry() {
            return entry;
        }
    }

    public static class PlatformServer implements IPlatformGet {

        protected JServer server;

        protected JServer.ServerEntry entry;

        protected DServer dServer;

        @Override
        public JbPlatform getPlatform() {
            return server;
        }

        @Override
        public Object getEntry() {
            return dServer;
        }
    }

    public List<JSetting> getSettingEntries() {
        return settingEntries;
    }

    public List<PlatformAnnouncement> getAnnouncementEntries() {
        return announcementEntries;
    }

    public List<PlatformServer> getServerEntries() {
        return serverEntries;
    }

    public JPlatformFrom getPlatformFrom(Input input) {
        long fromId = KernelDyna.to(input.getParam("fromId"), long.class);
        JPlatformFrom platformFrom = null;
        if (fromId != 0) {
            platformFrom = BeanService.ME.get(JPlatformFrom.class, fromId);
        }

        if (platformFrom == null) {
            platformFrom = getPlatformFrom(input.getParam("platform"), input.getParam("channel"), input.getParam("packageName"), KernelDyna.to(input.getParam("versionCode"), int.class), input.getParam("from"), true);
        }

        return platformFrom;
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
                        JSlaveServer slaveServer = BeanService.ME.get(JSlaveServer.class, serverEntry.getId());
                        if (slaveServer != null) {
                            DServer dServer = createDServer(serverEntry);
                            setDServer(dServer, serverEntry, slaveServer);
                            PlatformServer platformServer = new PlatformServer();
                            platformServer.server = server;
                            platformServer.entry = serverEntry;
                            platformServer.dServer = dServer;
                            entries.add(platformServer);
                        }
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
        dServer.port = entry.getPort();
        dServer.weight = entry.getWeight();
        return dServer;
    }

    // 合并自定义配置和Server服务配置
    protected void setDServer(DServer dServer, JServer.ServerEntry entry, JSlaveServer slaveServer) {
        dServer.name = entry.getName();
        dServer.sAddress = entry.getsAddress();
        dServer.dAddress = entry.getdAddress();
    }

    @Override
    public void merge(String entityName, JSlaveServer entity, MergeType mergeType, Object mergeEvent) {
        if (mergeType == MergeType.INSERT) {
            return;
        }

        if (entity != null) {
            long serverId = entity.getId();
            for (PlatformServer platformServer : serverEntries) {
                if (platformServer.dServer.id == serverId) {
                    if (mergeType == MergeType.DELETE) {
                        break;

                    } else {
                        setDServer(platformServer.dServer, platformServer.entry, entity);
                    }
                }
            }
        }

        ME.reloadServers();
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

    public boolean isMatchPlatform(JbPlatform platform, boolean review, JPlatformFrom platformFrom) {
        if (!platform.isOpen()) {
            return false;
        }

        if (review != platform.isReview()) {
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

    public DReviewSetting reviewSetting(Input input) {
        JPlatformFrom platformFrom = getPlatformFrom(input);
        DReviewSetting reviewSetting = new DReviewSetting();
        reviewSetting.fromId = platformFrom.getId();
        boolean review = CONFIGURE.isReview(platformFrom.getPackageName(), platformFrom.getVersionCode());
        for (JSetting setting : settingEntries) {
            if (isMatchPlatform(setting, review, platformFrom)) {
                reviewSetting.setting = setting;
                break;
            }
        }

        return reviewSetting;
    }

    protected interface IPlatformGet {

        public JbPlatform getPlatform();

        public Object getEntry();
    }

    public <T extends IPlatformGet> void listResponse(Collection<T> list, final boolean review, Input input) throws IOException {
        OutputStream outputStream = input.getOutputStream();
        if (outputStream != null) {
            final JPlatformFrom platformFrom = getPlatformFrom(input);
            HelperDataFormat.JSON.writeGetTemplates(outputStream, list, new KernelLang.GetTemplate<T, Object>() {

                @Override
                public Object getWith(T template) {
                    if (isMatchPlatform(template.getPlatform(), review, platformFrom)) {
                        return template.getEntry();
                    }

                    return null;
                }
            });
        }
    }


}
