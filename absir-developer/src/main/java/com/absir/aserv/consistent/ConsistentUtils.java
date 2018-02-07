package com.absir.aserv.consistent;

import com.absir.aserv.configure.JConfigureBase;
import com.absir.aserv.single.ISingle;

public class ConsistentUtils {

    public static void pubConfigure(JConfigureBase configureBase) {
        if (IConsistent.ME != null && ISingle.ME != null) {
            IConsistent.ME.pubConfigure(configureBase);
        }
    }

}
