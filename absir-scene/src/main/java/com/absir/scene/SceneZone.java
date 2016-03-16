/**
 * Copyright 2014 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2014年10月20日 上午11:03:35
 */
package com.absir.scene;

import com.absir.aserv.game.value.OReportDetail;
import com.absir.context.bean.IStep;
import com.absir.core.util.UtilLinked;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("rawtypes")
public class SceneZone<T extends ISceneObject, E> implements IStep, ISceneBroadcast<T, E> {

    public static final String OBJECT_EFFECT = "O";

    @JsonIgnore
    private UtilLinked<T> sceneObjects = new UtilLinked<T>();

    @JsonIgnore
    private UtilLinked<ISceneBroadcast<T, E>> sceneBroadCasts = new UtilLinked<ISceneBroadcast<T, E>>();

    public void addSceneObject(T sceneObject) {
        sceneObjects.add(sceneObject);
    }

    public void removeSceneObject(T sceneObject) {
        sceneObjects.remove(sceneObject);
    }

    public void addSceneBroadcast(ISceneBroadcast<T, E> sceneBroadcast) {
        sceneBroadCasts.add(sceneBroadcast);
    }

    public void removeSceneBroadcast(ISceneBroadcast<T, E> sceneBroadcast) {
        sceneBroadCasts.remove(sceneBroadcast);
    }

    public Iterator<T> iterator() {
        return sceneObjects.iterator();
    }

    public boolean stepDone(long contextTime) {
        sceneBroadCasts.syncRemoves();
        List<ISceneBroadcast<T, E>> sceneBroadCastAdds = sceneBroadCasts.syncAdds();
        if (sceneBroadCastAdds != null) {
            Iterator<T> iterator = sceneObjects.iterator();
            T sceneObject = null;
            while (iterator.hasNext()) {
                sceneObject = iterator.next();
                if (sceneObject.isSensory()) {
                    broadcast(sceneObject, null, new OReportDetail(sceneObject.getId(), null, OBJECT_EFFECT, sceneObject.getStatusObject()));
                }
            }
        }

        List<T> adds = sceneObjects.syncAdds();
        List<T> removes = sceneObjects.syncRemoves();
        if (adds != null) {
            for (T add : adds) {
                if (add.isSensory()) {
                    broadcast(add, null, new OReportDetail(add.getId(), null, OBJECT_EFFECT, add.getStatusObject()));
                }
            }
        }

        if (removes != null) {
            for (T remove : removes) {
                if (remove.isSensory()) {
                    broadcast(remove, null, new OReportDetail(remove.getId(), null, OBJECT_EFFECT, null));
                }
            }
        }

        Iterator<T> iterator = sceneObjects.iterator();
        T sceneObject = null;
        boolean sensory;
        while (iterator.hasNext()) {
            sceneObject = iterator.next();
            sensory = sceneObject.isSensory();
            if (sceneObject.stepDone(contextTime)) {
                iterator.remove();
                if (sensory) {
                    broadcast(sceneObject, null, new OReportDetail(sceneObject.getId(), null, OBJECT_EFFECT, null));
                }

            } else {
                if (sensory != sceneObject.isSensory()) {
                    if (sensory) {
                        broadcast(sceneObject, null, new OReportDetail(sceneObject.getId(), null, OBJECT_EFFECT, null));

                    } else {
                        broadcast(sceneObject, null, new OReportDetail(sceneObject.getId(), null, OBJECT_EFFECT, sceneObject.getStatusObject()));
                    }
                }
            }
        }

        return sceneObject == null;
    }

    @Override
    public boolean broadcast(T sceneObject, E event, OReportDetail reportDetail) {
        Iterator<ISceneBroadcast<T, E>> iterator = sceneBroadCasts.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().broadcast(sceneObject, event, reportDetail)) {
                iterator.remove();
            }
        }

        return true;
    }

}
