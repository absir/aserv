package com.absir.aserv.crud;

import com.absir.aserv.facade.DMessage;

/**
 * Created by absir on 16/2/5.
 */
public interface ICrudSubmit<T extends Enum> {

    /**
     * @return
     */
    public Class<T> classForOption();

    /**
     * @param option
     * @return
     */
    public DMessage submitOption(T option);

}
