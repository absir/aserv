package com.absir.aserv.master.handler;

import com.absir.aserv.master.bean.JSlaveUpgradeStatus;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.service.BeanService;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.context.core.ContextUtils;
import com.absir.master.InputMaster;
import com.absir.orm.transaction.value.Transaction;
import com.absir.server.handler.HandlerType;
import com.absir.server.handler.IHandler;
import com.absir.server.in.Input;
import com.absir.server.on.OnPut;
import com.absir.server.value.Handler;
import com.absir.shared.bean.EUpgradeStatus;
import com.absir.shared.master.IMaster;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by absir on 2016/10/27.
 */
@Base
@Bean
@Handler
public class MasterHandler implements IHandler, IMaster {

    public static final MasterHandler ME = BeanFactoryUtils.get(MasterHandler.class);

    @Override
    public boolean _permission(OnPut onPut) {
        return InputMaster.onAuthentication(onPut.getInput());
    }

    @Override
    public void _finally(OnPut onPut, HandlerType.HandlerMethod method) {
    }

    @Override
    public long time() throws IOException {
        return System.currentTimeMillis();
    }

    @Override
    public InputStream download(String filePath) throws IOException {
        return UploadCrudFactory.ME.getUploadStream(filePath);
    }

    @Override
    public void upgradeStatues(EUpgradeStatus status, long progress, boolean failed) {
        Input input = OnPut.input();
        if (input != null && input instanceof InputMaster) {
            upgradeStatues((String) input.getId(), status, progress, failed);
        }
    }

    @Transaction
    public void upgradeStatues(String slaveId, EUpgradeStatus status, long progress, boolean failed) {
        JSlaveUpgradeStatus upgradeStatus = new JSlaveUpgradeStatus();
        upgradeStatus.setId(slaveId);
        upgradeStatus.setUpgradeStatus(status);
        upgradeStatus.setUpdateTime(ContextUtils.getContextTime());
        upgradeStatus.setProgress(progress);
        upgradeStatus.setFailed(failed);
        BeanService.ME.merge(upgradeStatus);
    }


}
