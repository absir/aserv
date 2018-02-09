package com.absir.group.service;

import com.absir.aserv.single.ISingle;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperRandom;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Stopping;
import com.absir.bean.inject.value.Value;
import com.absir.context.core.ContextUtils;
import com.absir.core.base.Environment;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelObject;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAbsir;
import com.absir.core.util.UtilRuntime;
import com.absir.group.bean.JNode;
import com.absir.orm.hibernate.SessionFactoryUtils;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * Created by absir on 16/8/19.
 */
@Base
@Bean
public class ClusterService implements ISingle {

    public static final ClusterService ME = BeanFactoryUtils.get(ClusterService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    protected static final String NODE_ID_NAME = "nodeId";

    private Long nodeId;

    private String secretKey;

    private JNode node;

    @Value("cluster.group")
    private int group;

    @Value("cluster.idle.time")
    private long idleTime = 60000;

    private long idleSleep;

    public Long getNodeId() {
        return nodeId;
    }

    public String getSecretKey() {
        if (secretKey == null) {
            secretKey = HelperRandom.randSecondId(System.currentTimeMillis(), 3, hashCode(), HelperRandom.FormatType.DIG_LETTER);
        }

        return secretKey;
    }

    @Started
    public void started() {
        nodeId = KernelDyna.to(UtilRuntime.getRuntime(ClusterService.class, NODE_ID_NAME), Long.class);
        if (idleTime < 10000) {
            idleTime = 10000;
        }

        idleSleep = idleTime / 2;
        ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            ME.onlineNode();
                            idleSleep = idleTime / 2;

                        } catch (Throwable e) {
                            LOGGER.error("group online node error", e);
                            idleSleep /= 2;
                            if (idleSleep < 1000) {
                                idleSleep = 1000;
                            }
                        }

                        Thread.sleep(idleSleep);
                    }

                } catch (InterruptedException e) {

                }
            }
        });
    }

    @Transaction
    protected void onlineNode() {
        Session session = BeanDao.getSession();
        if (nodeId != null) {
            JNode node = session.get(JNode.class, nodeId);
        }

        long contextTime = ContextUtils.getContextTime();
        if (node == null) {
            node = new JNode();
            node.setId(nodeId);
            node.setOnlineTime(contextTime);
        }

        node.setGroup(group);
        node.setOfflineTime(contextTime + 60000);
        node.setSecretKey(getSecretKey());
        if (KernelString.isEmpty(node.getNodeAddress())) {
            try {
                node.setNodeAddress(InetAddress.getLocalHost().getHostAddress());

            } catch (Exception e) {
                Environment.throwable(e);
            }
        }

        if (node.getId() == null) {
            session.persist(node);
            nodeId = node.getId();
            UtilRuntime.setRuntime(ClusterService.class, NODE_ID_NAME, nodeId);

        } else {
            session.merge(node);
        }
    }

    @Stopping
    @Transaction
    protected void stopping() {
        if (nodeId != null) {
            Session session = BeanDao.getSession();
            JNode node = session.get(JNode.class, nodeId);
            if (node != null) {
                node.setOfflineTime(ContextUtils.getContextTime());
                session.merge(node);
            }
        }
    }

    /*
     * 集群执行竞争
     */
    public JVerifier getOperationVerifier(Session session, String id) {
        if (nodeId == null) {
            return null;
        }

        JVerifier verifier = BeanDao.loadReal(session, JVerifier.class, id, LockMode.NONE);
        long contextTime = ContextUtils.getContextTime();
        if (verifier != null) {
            if (verifier.getPassTime() > contextTime) {
                Long nid = KernelDyna.toLong(verifier.getTag());
                if (nid != null) {
                    JNode node = session.get(JNode.class, nid);
                    if (node != null && node.getOfflineTime() > contextTime && KernelObject.equals(node.getSecretKey(), verifier.getValue())) {
                        return null;
                    }
                }
            }

            QueryDaoUtils.createQueryArray(session, "DELETE FROM JVerifier o WHERE o.id = ? AND o.passTime = ?", verifier.getId(), verifier.getPassTime()).executeUpdate();
            session.clear();
        }

        verifier = new JVerifier();
        verifier.setId(id);
        verifier.setPassTime(contextTime + UtilAbsir.DAY_TIME);
        verifier.setTag(nodeId.toString());
        verifier.setValue(getSecretKey());
        try {
            session.persist(verifier);
            session.flush();

        } catch (RuntimeException e) {
            SessionFactoryUtils.throwNoConstraintViolationException(e);
            session.clear();
            return null;
        }

        return verifier;
    }

    public String getSingeVerifyId(String singleId) {
        return "SINGLE@" + singleId;
    }

    @Transaction
    public JVerifier enterSingle(String singleId) {
        singleId = getSingeVerifyId(singleId);
        Session session = BeanDao.getSession();
        return getOperationVerifier(session, singleId);
    }

    public void exitSingle(JVerifier verifier) {
        //VerifierService.ME.passOperation(ContextUtils.getContextTime(), verifier, true);
    }
}
