package com.absir.dubbo.service.master;

/**
 * Created by absir on 16/3/21.
 */
public interface MasterService {

    /**
     * 逻辑登录
     *
     * @return lifeTime
     */
    public long login(long playerId, String sessionId);

}
