package com.absir.aserv.system.domain;

import com.absir.aserv.system.helper.HelperRandom;
import com.absir.core.kernel.KernelLang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class DRandomList<T> {

    public interface IRandElement<T> {

        Serializable forId();

        float getRare();

        T forElement();

    }

    LinkedHashMap<Serializable, HelperRandom.RandomPool<T>> idMapRandPool;

    public void clear() {
        idMapRandPool = null;
    }

    public void addCollection(Collection<? extends IRandElement<T>> randElements) {
        if (randElements == null || randElements.isEmpty()) {
            return;
        }

        if (idMapRandPool == null) {
            idMapRandPool = new LinkedHashMap<Serializable, HelperRandom.RandomPool<T>>();
        }

        for (IRandElement<T> randElement : randElements) {
            HelperRandom.RandomPool<T> randomPool = idMapRandPool.get(randElement.forId());
            if (randomPool == null) {
                randomPool = new HelperRandom.RandomPool<T>();
                idMapRandPool.put(randElement.forId(), randomPool);
            }

            randomPool.add(randElement.forElement(), randElement.getRare());
        }
    }

    public List<T> randElements() {
        if (idMapRandPool == null || idMapRandPool.isEmpty()) {
            return KernelLang.NULL_LIST_SET;
        }

        List<T> elements = new ArrayList<T>(idMapRandPool.size());
        for (HelperRandom.RandomPool<T> randomPool : idMapRandPool.values()) {
            elements.add(randomPool.randElement());
        }

        return elements;
    }

}
