package com.absir.master;

import com.absir.client.SocketAdapter;

/**
 * Created by absir on 2016/11/10.
 */
public class MasterChannelAdapter extends SocketAdapter {

    public void setRegistered(boolean registered, long serverTime) {
        super.setRegistered(registered, serverTime);
    }


}
