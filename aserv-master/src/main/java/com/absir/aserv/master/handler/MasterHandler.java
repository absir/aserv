package com.absir.aserv.master.handler;

import com.absir.bean.basis.Base;
import com.absir.bean.inject.value.Bean;
import com.absir.core.helper.HelperIO;
import com.absir.master.IMaster;
import com.absir.master.InputMaster;
import com.absir.server.handler.HandlerType;
import com.absir.server.handler.IHandler;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.value.Handler;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/10/27.
 */
@Base
@Bean
@Handler
public class MasterHandler implements IHandler, IMaster {

    static int s = 0;

    @Override
    public boolean _permission(OnPut onPut) {
        return InputMaster.onAuthentication(onPut.getInput());
    }

    @Override
    public void _finally(OnPut onPut, HandlerType.HandlerMethod method) {
    }

    @Override
    public long time() throws IOException {
        int i = s++ % 3;
        if (i == 1) {
            throw new RuntimeException();
        }

        if (i == 2) {
            throw new IOException();
        }

        System.out.println("MasterHandler.time");
        return System.currentTimeMillis();
    }

    @Override
    public InputStream test(InputStream inputStream) {

        System.out.println("test inputStream ");

        try {
            System.out.println("test = " + HelperIO.toString(inputStream));

            Input input = OnPut.input();
            input.readyOutputStream();

            HelperIO.write("test", input.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public InputStream test1(String name, InputStream inputStream) {
        try {
            System.out.println("test =  name = " + name + " :: " + HelperIO.toString(inputStream));

            Input input = OnPut.input();
            input.readyOutputStream();

            HelperIO.write("test", input.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void test2(String name, InputStream inputStream) {
        try {
            System.out.println("test2 =  name = " + name + " :: " + HelperIO.toString(inputStream));

            Input input = OnPut.input();
            input.readyOutputStream();

            HelperIO.write("test", input.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
