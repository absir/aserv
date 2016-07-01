package com.absir.aserv.crud;

import com.absir.server.in.InModel;

public interface ICrudSubmit<T extends Enum> {

    public Class<T> classForOption();

    public String submitOption(T option, InModel model);

}
