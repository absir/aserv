package com.absir.aserv.single;

import com.absir.aserv.system.bean.JVerifier;

/**
 * Created by absir on 16/8/19.
 */
public class SingleUtils {

    public static final JVerifier VERIFIER = new JVerifier();

    public static JVerifier enterSingle(String singleId) {
        if (ISingle.ME == null) {
            return VERIFIER;
        }

        return ISingle.ME == null ? VERIFIER : ISingle.ME.enterSingle(singleId);
    }

    public static void exitSingle(JVerifier verifier) {
        if (ISingle.ME != null) {
            ISingle.ME.exitSingle(verifier);
        }
    }
}
