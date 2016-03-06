/**
 * Copyright 2014 ABSir's Studio
 * 
 * All right reserved
 *
 * Create on 2014年10月20日 上午11:03:35
 */
package com.absir.scene;

import java.util.Iterator;
import java.util.List;

import com.absir.aserv.game.value.OReportDetail;
import com.absir.context.bean.IStep;
import com.absir.core.util.UtilLinked;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author absir
 *
 */
@SuppressWarnings("rawtypes")
public class SceneZone<T extends ISceneObject, E> implements IStep, ISceneBroadcast<T, E> {

	/** sceneObjects */
	@JsonIgnore
	private UtilLinked<T> sceneObjects = new UtilLinked<T>();

	/** sceneBroadCasts */
	@JsonIgnore
	private UtilLinked<ISceneBroadcast<T, E>> sceneBroadCasts = new UtilLinked<ISceneBroadcast<T, E>>();

	/**
	 * @param sceneObject
	 */
	public void addSceneObject(T sceneObject) {
		sceneObjects.add(sceneObject);
	}

	/**
	 * @param sceneObject
	 */
	public void removeSceneObject(T sceneObject) {
		sceneObjects.remove(sceneObject);
	}

	/**
	 * @param sceneObject
	 */
	public void addSceneBroadcast(ISceneBroadcast<T, E> sceneBroadcast) {
		sceneBroadCasts.add(sceneBroadcast);
	}

	/**
	 * @param sceneBroadcast
	 */
	public void removeSceneBroadcast(ISceneBroadcast<T, E> sceneBroadcast) {
		sceneBroadCasts.remove(sceneBroadcast);
	}

	/**
	 * @return
	 */
	public Iterator<T> iterator() {
		return sceneObjects.iterator();
	}

	/** OBJECT_EFFECT */
	public static final String OBJECT_EFFECT = "O";

	/**
	 * @param contextTime
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.absir.scene.ISceneBroadcast#broadcast(com.absir.scene.ISceneObject,
	 * java.lang.Object, java.lang.Object)
	 */
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
