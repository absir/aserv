package com.absir.aserv.crud;

import com.absir.aserv.facade.DMessage;

public interface ICrudSubmit<T extends Enum> {

    public Class<T> classForOption();

    public DMessage submitOption(T option);

}
