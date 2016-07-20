/**
 * Copyright 2015 ABSir's Studio
 * <p/>
 * All right reserved
 * <p/>
 * Create on 2015年6月15日 上午10:06:22
 */
package com.absir.aserv.slave.service;

import com.absir.aserv.slave.bean.JMasterSynch;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.client.SocketAdapter;
import com.absir.client.callback.CallbackMsg;
import com.absir.context.core.ContextUtils;
import com.absir.context.schedule.value.Schedule;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDatabind;
import com.absir.orm.transaction.value.Transaction;
import com.absir.slave.InputSlaveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@SuppressWarnings("unchecked")
@Base
@Bean
public class SlaverMasterService {

    public static final SlaverMasterService ME = BeanFactoryUtils.get(SlaverMasterService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(SlaverMasterService.class);

    /**
     * 添加同步
     *
     * @param id
     * @param uri
     * @param postData
     */
    @Transaction
    public boolean addMasterSynch(String id, String uri, Object postData) {
        return addMasterSynch(0, id, uri, postData);
    }

    @Transaction
    public boolean addMasterSynch(int masterIndex, String id, String uri, Object postData) {
        JMasterSynch masterSynch = new JMasterSynch();
        masterSynch.setId(id);
        masterSynch.setMasterIndex(masterIndex);
        masterSynch.setUri(uri);
        masterSynch.setUpdateTime(System.currentTimeMillis());
        try {
            masterSynch.setPostData(postData == null ? null
                    : postData.getClass() == byte[].class ? (byte[]) postData : HelperDatabind.writeAsBytes(postData));
            BeanDao.getSession().merge(masterSynch);
            ME.checkSyncs();
            return true;

        } catch (Exception e) {
            LOGGER.error("addMasterSynch failed " + uri + " => " + postData, e);
        }

        return false;
    }

    /**
     * 服务区同步完成
     *
     * @param masterSynch
     * @param updateTime
     */
    @Transaction
    public void syncComplete(JMasterSynch masterSynch, long updateTime) {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "UPDATE JMasterSynch o SET o.synched = ? WHERE o.id = ? AND o.updateTime = ?", true,
                masterSynch.getId(), updateTime).executeUpdate();
    }

    /**
     * 检查数据同步
     */
    @Async(notifier = true)
    @Transaction(readOnly = true)
    @Schedule(fixedDelay = 300000, initialDelay = 20000)
    public void checkSyncs() {
        Iterator<JMasterSynch> iterator = QueryDaoUtils
                .createQueryArray(BeanDao.getSession(), "SELECT o FROM JMasterSynch o WHERE o.synched = ?", false)
                .iterate();
        final UtilAtom atom = new UtilAtom();
        while (iterator.hasNext()) {
            final JMasterSynch masterSynch = iterator.next();
            try {
                atom.increment();
                final long updateTime = masterSynch.getUpdateTime();
                InputSlaveContext.ME.getSlaveAdapter(masterSynch.getMasterIndex()).sendData(masterSynch.getUri(),
                        masterSynch.getPostData(), new CallbackMsg<String>() {

                            @Override
                            public void doWithBean(String bean, boolean ok, byte[] buffer, SocketAdapter adapter) {

                                try {
                                    if (ok) {
                                        ME.syncComplete(masterSynch, updateTime);
                                    }

                                } finally {
                                    atom.decrement();
                                }
                            }

                        });

            } catch (Exception e) {
                LOGGER.error("checkSyncs " + masterSynch.getId() + " " + masterSynch.getUri() + " => "
                        + masterSynch.getPostData(), e);
                atom.decrement();
            }
        }

        atom.await();
    }

    /**
     * 清除完成同步
     */
    @Async(notifier = true)
    @Transaction
    @Schedule(cron = "0 0 0 * * *")
    protected void clearSyncheds() {
        QueryDaoUtils.createQueryArray(BeanDao.getSession(),
                "DELETE FROM JMasterSynch o WHERE o.synched = ? AND o.updateTime < ?", true,
                ContextUtils.getContextTime() - UtilAbsir.WEEK_TIME).executeUpdate();
    }

}
