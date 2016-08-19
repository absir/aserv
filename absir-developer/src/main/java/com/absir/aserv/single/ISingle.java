package com.absir.aserv.single;

import com.absir.aserv.system.bean.JVerifier;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Inject;

/**
 * Created by absir on 16/8/19.
 */
@Inject
public interface ISingle {

    public static final ISingle ME = BeanFactoryUtils.get(ISingle.class);

    public JVerifier enterSingle(String singleId);

    public void exitSingle(JVerifier verifier);
}
