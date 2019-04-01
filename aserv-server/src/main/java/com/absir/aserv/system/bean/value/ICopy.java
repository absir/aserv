package com.absir.aserv.system.bean.value;

public interface ICopy {

    Object copyFrom();

    void copyDone(boolean success, Object old);

}