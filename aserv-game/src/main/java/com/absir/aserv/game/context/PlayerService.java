/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年11月13日 下午4:15:55
 */
package com.absir.aserv.game.context;

import com.absir.aserv.data.value.DataQuery;
import com.absir.aserv.game.bean.JbPlayer;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextFactory;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelLang.CallbackTemplate;
import com.absir.core.util.UtilAbsir;
import com.absir.orm.transaction.value.Transaction;
import com.absir.platform.bean.JPlatformUser;
import com.absir.platform.service.PlatformUserService;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

@SuppressWarnings({"rawtypes", "unchecked"})
@Base
@Bean
public abstract class PlayerService {

    public static final PlayerService ME = BeanFactoryUtils.get(PlayerService.class);

    /**
     * 用户名验证
     */
    public static final Pattern NAME_PATTERN = Pattern.compile("([\\w]|[\\u4e00-\\u9fa5]){2,8}");

    protected static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    /**
     * 载入玩家
     */
    @Transaction(readOnly = true)
    public void load(JbPlayerContext playerContext) {
        playerContext.load();
    }

    /**
     * 保存玩家
     */
    @Transaction
    public void save(JbPlayerContext playerContext) {
        playerContext.save();
    }

    /**
     * 检测用户名是否可用
     */
    public boolean isMatchName(String name) {
        return name == null || NAME_PATTERN.matcher(name).matches();
    }

    /**
     * 选择平台服务区
     */
    @Transaction
    protected void selectServerId(JPlatformUser platformUser, long serverId) {
        platformUser.setServerId(serverId);
        platformUser.setPlayerId(ME.getPlayerId(null, serverId, platformUser.getUserId()));
        BeanDao.getSession().merge(platformUser);
    }

    /**
     * 获取用户角色ID
     */
    public Long getPlayerId(Long serverId, JiUserBase userBase) {
        JPlatformUser platformUser = PlatformUserService.getPlatformUser(userBase, null);
        if (platformUser == null) {
            return null;
        }

        if (serverId != null && !(serverId.equals(platformUser.getServerId()))) {
            ME.selectServerId(platformUser, serverId);
        }

        return platformUser.getPlayerId();
    }

    /**
     * 查找用户角色ID
     */
    @Transaction(readOnly = true)
    @DataQuery(value = "SELECT o.id FROM JPlayer o WHERE o.serverId = ? AND o.userId = ?")
    public abstract Long playerId(Long serverId, Long userId);

    /**
     * 查找用户角色ID
     */
    @Transaction(readOnly = true)
    @DataQuery(value = "SELECT o.id FROM JPlayer o WHERE o.id = ? AND o.serverId = ? AND o.userId = ?")
    public abstract Long playerId(Long id, Long serverId, Long userId);

    /**
     * 查找用户角色ID
     */
    public Long getPlayerId(Long playerId, Long serverId, Long userId) {
        return playerId == null ? ME.playerId(serverId, userId) : ME.playerId(playerId, serverId, userId);
    }

    /**
     * 查找或创建角色ID
     */
    public Long openPlayerId(Long serverId, Long userId) {
        Long playerId = openPlayerId(serverId, userId);
        if (playerId == null) {
            JbPlayer player = AGameComponent.ME.createPlayer(serverId);
            player.setCreateTime(ContextUtils.getContextTime());
            player.setServerId(serverId);
            player.setUserId(userId);
            BeanService.ME.persist(player);
            playerId = player.getId();
        }

        return playerId;
    }

    /**
     * 获取用户IDS
     */
    @DataQuery("SELECT o.id FROM JPlayer o WHERE o.id IN :p0")
    protected abstract Long[] getPlayerIds(long[] playerIds);

    /**
     * 获取用户IDS
     */
    @DataQuery("SELECT o.id FROM JPlayer o WHERE o.serverId IN :p0")
    protected abstract Long[] getPlayerIdsFromServerIds(long[] serverIds);

    // 查找用户编号
    @Transaction(readOnly = true)
    @DataQuery("SELECT o.id FROM JPlayer o WHERE o.serverId = ?")
    public abstract Long findPlayerId(Long serverId);

    // 查找用户编号
    @Transaction(readOnly = true)
    @DataQuery("SELECT o.id FROM JPlayer o WHERE o.serverId = ? AND o.name = ?")
    public abstract Long findPlayerId(Long serverId, String name);

    // 查找用户
    @Transaction(readOnly = true)
    @DataQuery("SELECT o FROM JPlayer o WHERE o.serverId = ? AND o.name = ?")
    public abstract JbPlayer findPlayerByName(Long serverId, String name);

    // 查找用户姓名
    @Transaction(readOnly = true)
    @DataQuery("SELECT o.name FROM JPlayer o WHERE o.id = ? AND o.serverId = ?")
    protected abstract String findPlayerServerId(Long playerId, Long serverId);

    // 查找用户
    @Transaction(readOnly = true)
    @DataQuery("SELECT o FROM JPlayer o WHERE o.id = ? AND o.serverId = ?")
    protected abstract JbPlayer findPlayerById(Long playerId, Long serverId);

    /**
     * 查找用户姓名
     */
    public String getPlayerName(Long playerId, long serverId) {
        JbPlayerContext playerContext = AGameComponent.ME.findPlayerContext(playerId);
        if (playerContext == null) {
            return ME.findPlayerServerId(playerId, serverId);
        }

        JbPlayer player = playerContext.getPlayer();
        if (player.getServerId() != serverId) {
            return null;
        }

        return player.getName();
    }

    /**
     * 查找用户
     */
    public JbPlayer getPlayerById(Long playerId, long serverId) {
        JbPlayerContext playerContext = AGameComponent.ME.findPlayerContext(playerId);
        if (playerContext == null) {
            return ME.findPlayerById(playerId, serverId);
        }

        JbPlayer player = playerContext.getPlayer();
        if (player.getServerId() != serverId) {
            return null;
        }

        return player;
    }

    /**
     * 角色列表
     */
    @Transaction(readOnly = true)
    public List<JbPlayer> players(Long serverId, JiUserBase userBase) {
        JPlatformUser platformUser = PlatformUserService.getPlatformUser(userBase, null);
        boolean userDirty = false;
        Session session = BeanDao.getSession();
        if (serverId == null) {
            serverId = platformUser.getServerId();

        } else if (!serverId.equals(platformUser.getServerId())) {
            userDirty = true;
            platformUser.setServerId(serverId);
        }

        List<JbPlayer> players = QueryDaoUtils.createQueryArray(session,
                "SELECT o FROM JPlayer o WHERE o.serverId = ? AND o.userId = ?", serverId, platformUser.getId()).list();
        Long playerId = platformUser.getPlayerId();
        if (playerId != null && !players.isEmpty()) {
            Long matchId = null;
            for (JbPlayer player : players) {
                if (player.getId().equals(playerId)) {
                    matchId = playerId;
                    break;
                }
            }

            if (matchId == null) {
                matchId = players.get(0).getId();
                userDirty = true;
                platformUser.setPlayerId(matchId);
            }
        }

        if (userDirty) {
            session.merge(platformUser);
        }

        return players;
    }

    /**
     * 创建角色
     */
    @Transaction(rollback = Exception.class)
    public JbPlayer createPlayer(Long serverId, JiUserBase userBase, String name) {
        JPlatformUser platformUser = PlatformUserService.getPlatformUser(userBase, null);
        if (serverId == null) {
            serverId = platformUser.getServerId();

        } else {
            platformUser.setServerId(serverId);
        }

        JbPlayer player = AGameComponent.ME.createPlayer(serverId);
        player.setServerId(serverId);
        player.setUserId(userBase.getUserId());
        player.setName(name);
        player.setPlatform(platformUser.getPlatform());
        player.setUsername(platformUser.getUsername());
        Session session = BeanDao.getSession();
        session.persist(player);
        platformUser.setPlayerId(player.getId());
        session.merge(platformUser);
        return player;
    }

    /**
     * 获取玩家
     */
    public JbPlayer findPlayer(Long playerId) {
        JbPlayerContext playerContext = AGameComponent.ME.findPlayerContext(playerId);
        return playerContext == null ? getPlayer(playerId) : playerContext.getPlayer();
    }

    @Transaction(readOnly = true)
    protected JbPlayer getPlayer(Long playerId) {
        return (JbPlayer) BeanDao.get(BeanDao.getSession(), AGameComponent.ME.PLAYER_CLASS, playerId);
    }

    /**
     * 获取玩家列表
     */
    public List<? extends JbPlayer> findPlayers(List<Long> playerIds) {
        List<JbPlayer> players = new ArrayList<JbPlayer>(playerIds.size());
        Map<Long, Integer> unfinds = null;
        JbPlayerContext playerContext;
        JbPlayer player;
        int i = 0;
        for (Long playerId : playerIds) {
            if (playerId == null) {
                continue;
            }

            playerContext = AGameComponent.ME.findPlayerContext(playerId);
            if (playerContext == null) {
                player = null;
                if (unfinds == null) {
                    unfinds = new HashMap<Long, Integer>();
                }

                unfinds.put(playerId, i);

            } else {
                player = playerContext.getPlayer();
            }

            players.add(player);
            i++;
        }

        if (unfinds != null) {
            for (JbPlayer getPlayer : getPlayers(unfinds.keySet())) {
                Integer index = unfinds.get(getPlayer.getId());
                if (index != null) {
                    players.set(index, getPlayer);
                }
            }
        }

        Iterator<JbPlayer> iterator = players.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == null) {
                iterator.remove();
            }
        }

        return players;
    }

    /**
     * 获取玩家列表
     */
    @Transaction(readOnly = true)
    protected List<JbPlayer> getPlayers(Collection<Long> playerIds) {
        return QueryDaoUtils.createQueryArray(BeanDao.getSession(), "SELECT o FROM JPlayer o WHERE o.id in (:ids)")
                .setParameterList("ids", playerIds).list();
    }

    /**
     * 保存玩家修改
     */
    @Transaction(rollback = Throwable.class)
    protected void mergePlayer(Long playerId, CallbackTemplate<JbPlayer> playerModifier) {
        Session session = BeanDao.getSession();
        JbPlayer player = (JbPlayer) BeanDao.get(session, AGameComponent.ME.PLAYER_CLASS, playerId);
        playerModifier.doWith(player);
        session.save(player);
    }

    /**
     * 修改玩家属性
     */
    public void modifyPlayer(Long playerId, Runnable playerModifier) {
        String tokenId = UtilAbsir.getId(AGameComponent.ME.PLAYER_CONTEXT_CLASS, playerId);
        ContextFactory contextFactory = ContextUtils.getContextFactory();
        try {
            synchronized (contextFactory.getToken(tokenId)) {
                playerModifier.run();
            }

        } finally {
            contextFactory.clearToken(tokenId);
        }
    }

    /**
     * 修改玩家属性
     */
    public void modifyPlayer(Long playerId, CallbackTemplate<JbPlayer> playerModifier) {
        String tokenId = UtilAbsir.getId(AGameComponent.ME.PLAYER_CONTEXT_CLASS, playerId);
        ContextFactory contextFactory = ContextUtils.getContextFactory();
        try {
            synchronized (contextFactory.getToken(tokenId)) {
                JbPlayerContext playerContext = AGameComponent.ME.findPlayerContext(playerId);
                if (playerContext == null) {
                    mergePlayer(playerId, playerModifier);

                } else {
                    synchronized (playerContext) {
                        playerModifier.doWith(playerContext.getPlayer());
                    }

                    playerContext.writeModifyMessage();
                }
            }

        } finally {
            contextFactory.clearToken(tokenId);
        }
    }

    /**
     * 角色交互
     */
    public void doPlayerTarget(Long playerId, Long targetId, Runnable mutualRunnable) {
        if (playerId == targetId) {
            return;
        }

        if (playerId > targetId) {
            // 固定顺序，保证不会锁死
            Long tId = playerId;
            playerId = targetId;
            targetId = tId;
        }

        String tokenId = UtilAbsir.getId(AGameComponent.ME.PLAYER_CONTEXT_CLASS, playerId);
        String tokenTarget = UtilAbsir.getId(AGameComponent.ME.PLAYER_CONTEXT_CLASS, targetId);
        ContextFactory contextFactory = ContextUtils.getContextFactory();
        try {
            synchronized (contextFactory.getToken(tokenId)) {
                try {
                    synchronized (contextFactory.getToken(tokenTarget)) {
                        mutualRunnable.run();
                    }

                } finally {
                    contextFactory.clearToken(tokenTarget);
                }
            }

        } finally {
            contextFactory.clearToken(tokenId);
        }
    }

}
