package com.absir.aserv.game.service;

import com.absir.aserv.game.bean.JFriend;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.system.bean.JEmbedLL;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Query;

import java.util.List;

/**
 * Created by absir on 21/9/17.
 */
@Base
@Bean
public class FriendService {

    public static final FriendService ME = BeanFactoryUtils.get(FriendService.class);

    public boolean follow(Long playerId, Long targetPlayerId) {
        if (playerId == targetPlayerId) {
            return false;
        }

        JEmbedLL id = new JEmbedLL();
        id.setEid(playerId);
        id.setMid(targetPlayerId);
        JFriend friend = new JFriend();
        friend.setId(id);
        try {
            BeanService.ME.persist(friend);
            return true;

        } catch (RuntimeException e) {
            SessionFactoryUtils.throwNoConstraintViolationException(e);
        }

        return false;
    }

    public boolean unFollow(Long playerId, Long targetPlayerId) {
        return BeanService.ME.executeUpdate("DELETE FROM JFriend o WHERE o.id.eid = ? AND o.id.mid = ?", playerId, targetPlayerId) > 0;
    }

    @Transaction(readOnly = true)
    public List<Long> getFollowPlayerIds(Long playerId, int maxResults) {
        Query query = QueryDaoUtils.createQueryArray(BeanDao.getSession(), "SELECT o.id.mid FROM JFriend o WHERE o.id.eid = ?", playerId);
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }

        return query.list();
    }

    @Transaction(readOnly = true)
    public List<Long> getBeFollowedPlayerIds(Long playerId, JdbcPage jdbcPage) {
        return QueryDaoUtils.selectQuery(BeanDao.getSession(), "JFriend", "o.id.eid", new Object[]{"o.id.mid", playerId}, jdbcPage);
    }

}
