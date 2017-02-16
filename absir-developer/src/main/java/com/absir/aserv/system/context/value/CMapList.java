package com.absir.aserv.system.context.value;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by absir on 2017/2/16.
 */
public class CMapList<T extends IMapKey<? extends K>, K> {

    private Map<K, CList<T>> map;

    public void addCollection(Collection<? extends T> values) {
        Map<K, CList<T>> map = new HashMap<K, CList<T>>();
        for (T value : values) {
            K k = value.mapKey();
            if (k != null) {
                CList<T> list = map.get(k);
                if (list == null) {
                    list = new CList<T>();
                    map.put(k, list);
                }

                list.add(value);
            }
        }

        this.map = map;
    }

    public CList<T> getList(K key) {
        return map == null ? null : map.get(key);
    }

}
