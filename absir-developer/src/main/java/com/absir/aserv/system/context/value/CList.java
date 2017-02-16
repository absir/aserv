package com.absir.aserv.system.context.value;

import java.util.ArrayList;

/**
 * Created by absir on 2017/2/16.
 */
public class CList<T> extends ArrayList<T> {

    @Override
    public T get(int index) {
        int size = size();
        return size == 0 ? null : index < 0 ? super.get(0) : index < size ? super.get(index) : super.get(size - 1);
    }

}
