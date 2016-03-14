/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月23日 下午2:45:27
 */
package com.absir.scene;

import com.absir.aserv.game.value.OReportDetail;

/**
 * @author absir
 */
@SuppressWarnings("rawtypes")
public interface ISceneBroadcast<T extends ISceneObject, E> {

    /**
     * @param sceneObject
     * @param event
     * @param reportDetail
     */
    public boolean broadcast(T sceneObject, E event, OReportDetail reportDetail);

}
