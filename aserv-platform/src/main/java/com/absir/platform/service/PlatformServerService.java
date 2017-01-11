package com.absir.platform.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DCacheOpen;
import com.absir.aserv.system.service.BeanService;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.*;
import com.absir.platform.bean.base.JbPlatform;
import com.absir.thrift.IFaceServer;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import tplatform.*;

import java.util.*;

/**
 * Created by absir on 2016/12/2.
 */
@Base
@Bean
public class PlatformServerService implements IEntityMerge<JSlaveServer>, IFaceServer<PlatformFromService.Iface>, PlatformFromService.Iface {

    public static final PlatformServerService ME = BeanFactoryUtils.get(PlatformServerService.class);

    public static final JPlatformConfigure CONFIGURE = JConfigureUtils.getConfigure(JPlatformConfigure.class);

    protected DCacheOpen<Long, JSetting> settingDCacheOpen;

    protected DCacheOpen<Long, JAnnouncement> announcementDCacheOpen;

    protected DCacheOpen<Long, JServer> serverDCacheOpen;

    protected List<JSetting> settingList;

    protected List<PlatformAnnouncement> announcementList;

    protected List<PlatformServer> serverList;

    public static class PlatformAnnouncement {

        protected JAnnouncement announcement;

        protected DAnnouncement value;

        public JbPlatform getPlatform() {
            return announcement;
        }

        public Object getValue() {
            return value;
        }

    }

    public static class PlatformServer {

        protected JServer server;

        protected DServer value;

        protected DServer dServer;

        public JbPlatform getPlatform() {
            return server;
        }

        public Object getValue() {
            return value;
        }
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

    @Inject
    protected void initService() {
        settingDCacheOpen = new DCacheOpen<Long, JSetting>(JSetting.class, null);
        settingDCacheOpen.addEntityMerges();
        announcementDCacheOpen = new DCacheOpen<Long, JAnnouncement>(JAnnouncement.class, null);
        announcementDCacheOpen.addEntityMerges();
        serverDCacheOpen = new DCacheOpen<Long, JServer>(JServer.class, null);
        serverDCacheOpen.addEntityMerges();
    }

    @Transaction
    @Started
    protected void startService() {
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
    @Async(notifier = true)
    @Schedule(cron = "0 30 0 * * *")
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
            List<JSetting> list = new ArrayList<JSetting>(settingDCacheOpen.getCacheMap().values());
            Collections.sort(list, BeanService.COMPARATOR);
            settingList = list;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    @Async(notifier = true)
    protected void reloadAnnouncements() {
        List<PlatformAnnouncement> list = new ArrayList<PlatformAnnouncement>();
        try {
            List<JAnnouncement> announcements = new ArrayList<JAnnouncement>(announcementDCacheOpen.getCacheMap().values());
            Collections.sort(announcements, BeanService.COMPARATOR);
            for (JAnnouncement announcement : announcements) {
                DAnnouncement[] dAnnouncements = announcement.getAnnouncements();
                if (dAnnouncements != null) {
                    for (DAnnouncement dAnnouncement : dAnnouncements) {
                        PlatformAnnouncement platformAnnouncement = new PlatformAnnouncement();
                        platformAnnouncement.announcement = announcement;
                        platformAnnouncement.value = dAnnouncement;
                        list.add(platformAnnouncement);
                    }
                }
            }

            announcementList = list;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    @Async(notifier = true)
    protected void reloadServers() {
        List<PlatformServer> list = new ArrayList<PlatformServer>();
        try {
            List<JServer> servers = new ArrayList<JServer>(serverDCacheOpen.getCacheMap().values());
            Collections.sort(servers, BeanService.COMPARATOR);
            for (JServer server : servers) {
                DServer[] dServers = server.getServers();
                if (dServers != null) {
                    for (DServer dServer : dServers) {
                        JSlaveServer slaveServer = BeanService.ME.get(JSlaveServer.class, dServer.getId());
                        if (slaveServer != null) {
                            DServer value = createDServer(dServer);
                            setDServer(value, dServer, slaveServer);
                            PlatformServer platformServer = new PlatformServer();
                            platformServer.server = server;
                            platformServer.value = value;
                            platformServer.dServer = dServer;
                            list.add(platformServer);
                        }
                    }
                }
            }

            serverList = list;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    protected DServer createDServer(DServer server) {
        return server.clone();
    }

    // 合并自定义配置和Server服务配置
    protected void setDServer(DServer value, DServer dServer, JSlaveServer slaveServer) {
        value.setName(KernelString.isEmpty(dServer.getName()) ? slaveServer.getName() : dServer.getName());
        value.setsAddress(KernelString.isEmpty(dServer.getsAddress()) ? slaveServer.getServerAddress() : dServer.getsAddress());
        value.setPort(dServer.getPort() == 0 ? slaveServer.getPort() : dServer.getPort());
        value.setdAddress(KernelString.isEmpty(dServer.getdAddress()) ? slaveServer.getResourceUrl() : dServer.getdAddress());
        value.setStatus(value.getStatus() == null || value.getStatus() == EServerStatus.normal ? (slaveServer.getSlave().isConnecting() ? EServerStatus.open : EServerStatus.maintain) : value.getStatus());
    }

    @Override
    public void merge(String entityName, JSlaveServer entity, MergeType mergeType, Object mergeEvent) {
        if (mergeType == MergeType.INSERT) {
            return;
        }

        if (entity != null) {
            boolean deleted = false;
            long serverId = entity.getId();
            for (PlatformServer platformServer : serverList) {
                if (platformServer.dServer.getId() == serverId) {
                    if (mergeType == MergeType.DELETE) {
                        deleted = true;
                        break;

                    } else {
                        setDServer(platformServer.value, platformServer.dServer, entity);
                    }
                }
            }

            if (!deleted) {
                return;
            }
        }

        ME.reloadServers();
    }

    @Transaction
    public JPlatformFrom getPlatformFrom(DPlatformFrom platformFrom) {
        String refId = platformFrom.getPlatform() + "@" + platformFrom.getChannel() + "@" + platformFrom.getPackageName() + "@" + platformFrom.getVersionDouble() + "@" + platformFrom.getFromStr();
        Session session = BeanDao.getSession();
        JPlatformFromRef ref = BeanDao.get(session, JPlatformFromRef.class, refId);
        if (ref == null) {
            JPlatformFrom jPlatformFrom = new JPlatformFrom();
            jPlatformFrom.setPlatform(platformFrom.getPlatform());
            jPlatformFrom.setChannel(platformFrom.getChannel());
            jPlatformFrom.setPackageName(platformFrom.getPackageName());
            jPlatformFrom.setVersionDouble(platformFrom.getVersionDouble());
            jPlatformFrom.setFromStr(platformFrom.getFromStr());

            try {
                session.persist(jPlatformFrom);
                session.flush();

            } catch (ConstraintViolationException e) {
                session.clear();
                ref = BeanDao.get(session, JPlatformFromRef.class, refId);
                if (ref == null) {
                    jPlatformFrom = (JPlatformFrom) QueryDaoUtils.createQueryArray(session, "SELECT o FROM JPlatformFrom o WHERE o.platform = ? AND o.channel = ? AND o.packageName = ? AND o.versionDouble = ? AND o.fromStr = ?", platformFrom.getPlatform(), platformFrom.getChannel(), platformFrom.getPackageName(), platformFrom.getVersionDouble(), platformFrom.getFromStr()).iterate().next();
                }
            }

            ref = new JPlatformFromRef();
            ref.setId(refId);
            ref.setPlatformFrom(jPlatformFrom);
            session.merge(ref);
        }

        return ref.getPlatformFrom();
    }

    @Transaction(readOnly = true)
    public JPlatformFrom getPlatformFromId(int id) {
        return BeanDao.get(BeanDao.getSession(), JPlatformFrom.class, (long) id);
    }

    public boolean isMatchPlatform(JbPlatform platform, boolean review, DPlatformFrom platformFrom) {
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

        double versionDouble = platformFrom.getVersionDouble();
        if (versionDouble > 0) {
            double minVersionDouble = platform.getMinVersionDouble();
            if (minVersionDouble != 0 && versionDouble < minVersionDouble) {
                return false;
            }

            double maxVersionDouble = platform.getMaxVersionDouble();
            if (maxVersionDouble != 0 && versionDouble > maxVersionDouble) {
                return false;
            }
        }

        String fromStr = platformFrom.getFromStr();
        if (!KernelString.isEmpty(fromStr)) {
            Map.Entry<String, KernelLang.IMatcherType> entry = platform.forMatchFromEntry();
            if (entry != null && !KernelLang.MatcherType.isMatch(fromStr, entry)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public TBaseProcessor<PlatformFromService.Iface> getBaseProcessor() {
        return new PlatformFromService.Processor<PlatformFromService.Iface>(ME);
    }

    @Override
    public DPlatformFromSetting setting(DPlatformFrom platformFrom) throws TException {
        boolean review = CONFIGURE.isReview(platformFrom.getPackageName(), platformFrom.getVersionDouble());
        DFromSetting fromSetting = null;
        for (JSetting setting : settingList) {
            if (isMatchPlatform(setting, review, platformFrom)) {
                fromSetting = setting.getFromSetting();
                break;
            }
        }

        JPlatformFrom jPlatformFrom = ME.getPlatformFrom(platformFrom);
        DPlatformFromSetting setting = new DPlatformFromSetting();
        setting.setFromId((int) (long) jPlatformFrom.getId());
        setting.setReview(review);
        setting.setSetting(fromSetting);
        return setting;
    }

    @Override
    public List<DAnnouncement> announcements(int fromId, boolean review) throws TException {
        DPlatformFrom platformFrom = ME.getPlatformFromId(fromId);
        List<DAnnouncement> announcements = new ArrayList<DAnnouncement>(announcementList.size());
        for (PlatformAnnouncement announcement : announcementList) {
            if (isMatchPlatform(announcement.getPlatform(), review, platformFrom)) {
                announcements.add(announcement.value);
            }
        }

        return announcements;
    }

    @Override
    public List<DServer> servers(int fromId, boolean review) throws TException {
        DPlatformFrom platformFrom = ME.getPlatformFromId(fromId);
        List<DServer> servers = new ArrayList<DServer>(serverList.size());
        for (PlatformServer server : serverList) {
            if (isMatchPlatform(server.getPlatform(), review, platformFrom)) {
                servers.add(server.value);
            }
        }

        return servers;
    }

    @Override
    public DIdentityResult identity(int fromId, long serverId, String identities) throws TException {
        return null;
    }

    @Override
    public DLoginResult login(int fromId, long serverId, String username, String password) throws TException {
        return null;
    }

    @Override
    public DRegisterResult sign(int fromId, long serverId, String username, String password) throws TException {
        return null;
    }

    @Override
    public EPasswordResult password(String sessionId, String oldPassword, String newPassword) throws TException {
        return null;
    }

    @Override
    public DOrderResult order(DOrderInfo info) throws TException {
        return null;
    }

    @Override
    public boolean validate(DOrderValidator validator) throws TException {
        return false;
    }

}
