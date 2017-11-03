package com.absir.platform.service;

import com.absir.aserv.configure.JConfigureUtils;
import com.absir.aserv.master.bean.JSlave;
import com.absir.aserv.master.bean.JSlaveServer;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.IUser;
import com.absir.aserv.system.crud.PasswordCrudFactory;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.domain.DCacheOpen;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.impl.IdentityServiceLocal;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelCollection;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelList;
import com.absir.core.kernel.KernelString;
import com.absir.open.bean.JPayTrade;
import com.absir.open.bean.value.JePayStatus;
import com.absir.open.service.PayUtils;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.*;
import com.absir.platform.bean.base.JbPlatform;
import com.absir.thrift.IFaceServer;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.hibernate.Session;
import tplatform.*;

import java.util.*;

/**
 * Created by absir on 2016/12/2.
 */
@Base
@Bean
public abstract class PlatformServerService implements IEntityMerge<JSlaveServer>, IFaceServer<PlatformFromService.Iface>, PlatformFromService.Iface {

    public static final PlatformServerService ME = BeanFactoryUtils.get(PlatformServerService.class);

    public static final JPlatformConfigure CONFIGURE = JConfigureUtils.getConfigure(JPlatformConfigure.class);

    protected DCacheOpen<Long, JSetting> settingDCacheOpen;

    protected DCacheOpen<Long, JAnnouncement> announcementDCacheOpen;

    protected DCacheOpen<Long, JServer> serverDCacheOpen;

    protected List<JSetting> settingList;

    protected List<PlatformAnnouncement> announcementList;

    protected List<PlatformServer> serverList;

    public static boolean isExcludeIds(String ids, String id) {
        if (!KernelString.isEmpty(ids)) {
            if (KernelString.patternInclude(ids, id)) {
                return false;
            }

            return true;
        }

        return false;
    }

    protected static String[] getMoreDatas(List<String> moreDatas) {
        return moreDatas == null || moreDatas.size() == 0 ? null : (KernelCollection.toArray(moreDatas, String.class));
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
            Collections.sort(announcements, BeanService.COMPARATOR_ID_DESC);
            for (JAnnouncement announcement : announcements) {
                DAnnouncement dAnnouncement = announcement.getAnnouncement();
                if (dAnnouncement != null) {
                    PlatformAnnouncement platformAnnouncement = new PlatformAnnouncement();
                    platformAnnouncement.announcement = announcement;
                    platformAnnouncement.value = dAnnouncement;
                    list.add(platformAnnouncement);
                }
            }

            announcementList = list;

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    @Async(notifier = true)
    @Transaction(readOnly = true)
    protected void reloadServers() {
        List<PlatformServer> list = new ArrayList<PlatformServer>();
        try {
            List<JServer> servers = new ArrayList<JServer>(serverDCacheOpen.getCacheMap().values());
            Collections.sort(servers, BeanService.COMPARATOR_ID_DESC);
            for (JServer server : servers) {
                DServer[] dServers = server.getServers();
                if (dServers != null) {
                    int last = dServers.length - 1;
                    for (; last >= 0; last--) {
                        DServer dServer = dServers[last];
                        JSlaveServer slaveServer = BeanService.ME.get(JSlaveServer.class, dServer.getId());
                        if (slaveServer != null) {
                            PlatformServer platformServer = new PlatformServer();
                            platformServer.server = server;
                            platformServer.value = createDServer(dServer);
                            platformServer.dServer = dServer;
                            setDServer(platformServer, slaveServer);
                            list.add(platformServer);
                        }
                    }
                }
            }

            serverList = list;
            KernelList.sortOrderableDesc(serverList);

        } catch (ConcurrentModificationException e) {
            Environment.throwable(e);
        }
    }

    protected DServer createDServer(DServer server) {
        return server.clone();
    }

    // 合并自定义配置和Server服务配置
    protected void setDServer(PlatformServer platformServer, JSlaveServer slaveServer) {
        platformServer.beginTime = slaveServer.getBeginTime();
        platformServer.passTime = slaveServer.getPassTime();
        platformServer.closed = slaveServer.isClosed();
        DServer value = platformServer.value;
        DServer dServer = platformServer.dServer;
        value.setName(KernelString.isEmpty(dServer.getName()) ? slaveServer.getName() : dServer.getName());
        value.setSAddress(KernelString.isEmpty(dServer.getSAddress()) ? slaveServer.getServerAddress() : dServer.getSAddress());
        value.setsAddressV6(KernelString.isEmpty(dServer.getsAddressV6()) ? slaveServer.getServerAddressV6() : dServer.getSAddressV6());
        JSlave slave = slaveServer.getSlave();
        if (slave == null || slave.getSlaveServerPort() <= 0) {
            value.setPort(dServer.getPort() == 0 ? slaveServer.getPort() : dServer.getPort());

        } else {
            value.setPort(slave.getSlaveServerPort());
        }

        value.setDAddress(KernelString.isEmpty(dServer.getDAddress()) ? slaveServer.getResourceUrl() : dServer.getDAddress());
        value.setStatus(slaveServer.isClosed() ? EServerStatus.maintain : value.getStatus());
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
                        setDServer(platformServer, entity);
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

            } catch (RuntimeException e) {
                SessionFactoryUtils.throwNoConstraintViolationException(e);
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
    public JPlatformFrom getPlatformFromId(long id) {
        return BeanDao.get(BeanDao.getSession(), JPlatformFrom.class, id);
    }

    public boolean isMatchPlatform(JbPlatform platform, boolean review, DPlatformFrom platformFrom) {
        if (!platform.isOpen()) {
            return false;
        }

        if (review != platform.isReview()) {
            return false;
        }

        if (isExcludeIds(platform.getPlatforms(), platformFrom.getPlatform())) {
            return false;
        }

        if (isExcludeIds(platform.getChannels(), platformFrom.getChannel())) {
            return false;
        }

        if (isExcludeIds(platform.getPackageNames(), platformFrom.getPackageName())) {
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
    public DPlatformFromSetting setting(DPlatformFrom platformFrom, String versionName) throws TException {
        boolean review = CONFIGURE.isReview(platformFrom.getPackageName(), versionName);
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
        long contextTime = ContextUtils.getContextTime();
        List<DServer> servers = new ArrayList<DServer>(serverList.size());
        PlatformServer lastMatchClosedServer = null;
        for (PlatformServer server : serverList) {
            if (isMatchPlatform(server.getPlatform(), review, platformFrom)) {
                if (server.closed || server.beginTime < contextTime) {
                    if (server.beginTime - contextTime > 60000) {
                        lastMatchClosedServer = server;
                        continue;
                    }

                    server.value.setStatus(EServerStatus.wait);
                }

                servers.add(server.value);
            }
        }

        if (servers.isEmpty() && lastMatchClosedServer != null) {
            PlatformServer server = lastMatchClosedServer;
            server.value.setStatus(EServerStatus.wait);
            servers.add(server.value);
        }

        return servers;
    }

    protected String getPlatformFromChannel(JPlatformFrom platformFrom) {
        return platformFrom == null ? null : (platformFrom.getPlatform() + '@' + platformFrom.getChannel());
    }

    protected DIdentityResult loginUser(int fromId, boolean serverIds, JiUserBase user, String userData) {
        if (user == null) {
            return null;
        }

        DIdentityResult identityResult = new DIdentityResult();
        JPlatformUser platformUser = null;
        if (user instanceof JPlatformUser) {
            platformUser = (JPlatformUser) user;

        } else {
            if (user instanceof IPlatformUserId) {
                Long id = ((IPlatformUserId) user).getPlatformUserId();
                if (id != null && id != 0) {
                    platformUser = BeanService.ME.get(JPlatformUser.class, id);
                }
            }

            if (platformUser == null) {
                platformUser = PlatformUserService.ME.getPlatformUser("JUser", user.getUsername(), null);
            }
        }

        if (platformUser.isDisabled()) {
            identityResult.setUserId(0);

        } else {
            JPlatformFrom platformFrom = ME.getPlatformFromId(fromId);
            platformUser.setChannel(getPlatformFromChannel(platformFrom));
            PlatformUserService.ME.loginSessionUserType(platformUser, PlatformUserService.ME.getLifeTime(), 2);

            identityResult.setUserId(platformUser.getId());
            identityResult.setUserData(userData);
            identityResult.setSessionId(platformUser.getSessionId());
        }

        return identityResult;
    }

    @Override
    public DIdentityResult identity(int fromId, boolean serverIds, String identities) throws TException {
        String[] parameters = HelperString.split(identities, ',');
        JiUserBase userBase = IdentityServiceLocal.getUserBaseParams(parameters, null);
        return loginUser(fromId, serverIds, userBase, parameters[0] == null ? parameters[1] : null);
    }

    @Override
    public DLoginResult login(int fromId, boolean serverIds, String username, String password) throws TException {
        JiUserBase userBase = SecurityService.ME.getUserBase(username, 0);
        DLoginResult loginResult = new DLoginResult();
        if (userBase == null) {
            loginResult.setError(ELoginError.userNotExist);

        } else {
            IUser user = (IUser) (userBase);
            if (PasswordCrudFactory.getPasswordEncrypt(password, user.getSalt(), user.getSaltCount()).equals(user.getPassword())) {
                loginResult.setError(ELoginError.success);
                loginResult.setResult(loginUser(fromId, serverIds, userBase, null));
                loginResult.setUserId(userBase.getUserId());

            } else {
                loginResult.setError(ELoginError.passwordError);
            }
        }

        return loginResult;
    }

    @Override
    public DIdentityResult loginUUID(int fromId, boolean serverIds, String uuid) throws TException {
        return loginUser(fromId, serverIds, SecurityService.ME.openUserBase(uuid, null, "UUID", null), null);
    }

    @Override
    public DRegisterResult sign(int fromId, String username, String password) throws TException {
        return signUUID(fromId, username, password, null);
    }

    @Override
    public DOrderResult order(int fromId, DOrderInfo info) throws TException {
        DOrderResult orderResult = new DOrderResult();
        JPlatformFrom platformFrom = ME.getPlatformFromId(fromId);
        String[] moreDatas = getMoreDatas(info.getMoreDatas());
        JPayTrade payTrade = PayUtils.createTrade(info.getConfigureId(), info.getPlatform(), info.getPlatformData(), getPlatformFromChannel(platformFrom), info.getGoodsId(), info.getGoodsNumber(), (float) info.getAmount(), info.getUserId(), info.getServerId(), info.getPlayerId(), info.isShortTradeId(), moreDatas);
        // 透传TradeData
        payTrade.setTradeData(info.getTradeData());
        try {
            orderResult.setTradeData(PayUtils.orderTrade(payTrade, info.getPrepare(), moreDatas));

        } catch (Exception e) {
            throw new TException(e);
        }

        BeanService.ME.persist(payTrade);
        orderResult.setTradeId(payTrade.getId());
        return orderResult;
    }

    @Override
    public boolean validate(int fromId, DOrderValidator validator) throws TException {
        JPayTrade payTrade = BeanService.ME.get(JPayTrade.class, validator.getTradeId());
        if (payTrade == null) {
            return false;
        }

        Object result = PayUtils.payStatus(payTrade, validator.getConfigureId(), validator.getPlatform(), validator.getPlatformData(), validator.getTradeNo(), validator.getTradeReceipt(), 0, validator.isSanbox(), getMoreDatas(validator.getMoreDatas()), JePayStatus.PAYING, null);
        return result != null;
    }

    public interface IPlatformUserId {

        public Long getPlatformUserId();

    }

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

    public static class PlatformServer implements KernelList.Orderable {

        protected JServer server;

        protected DServer value;

        protected DServer dServer;

        protected long beginTime;

        protected long passTime;

        protected boolean closed;

        public JbPlatform getPlatform() {
            return server;
        }

        public Object getValue() {
            return value;
        }

        @Override
        public int getOrder() {
            return (int) (beginTime / 1000);
        }
    }

}
