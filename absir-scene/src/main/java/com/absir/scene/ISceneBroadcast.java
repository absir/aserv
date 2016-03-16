/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月23日 下午2:45:27
 */
package com.absir.scene;

import com.absir.aserv.game.value.OReportDetail;

@SuppressWarnings("rawtypes")
public interface ISceneBroadcast<T extends ISceneObject, E> {

    public boolean broadcast(T sceneObject, E event, OReportDetail reportDetail);

}
