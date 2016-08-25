package com.absir.aserv.task;

import com.absir.aserv.single.SingleUtils;
import com.absir.aserv.system.bean.JPlan;
import com.absir.aserv.system.bean.JTask;
import com.absir.aserv.system.bean.JTaskBase;
import com.absir.aserv.system.bean.JVerifier;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.domain.DActiver;
import com.absir.async.value.Async;
import com.absir.bean.basis.Base;
import com.absir.bean.core.BeanFactoryUtils;
import com.absir.bean.inject.value.Bean;
import com.absir.bean.inject.value.Inject;
import com.absir.bean.inject.value.Started;
import com.absir.bean.inject.value.Value;
import com.absir.bean.lang.LangCodeUtils;
import com.absir.context.core.ContextAtom;
import com.absir.context.core.ContextService;
import com.absir.context.core.ContextUtils;
import com.absir.core.kernel.KernelString;
import com.absir.core.util.UtilAtom;
import com.absir.data.helper.HelperDatabind;
import com.absir.orm.hibernate.boost.IEntityMerge;
import com.absir.orm.hibernate.boost.L2EntityMergeService;
import com.absir.orm.transaction.value.Transaction;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by absir on 16/8/15.
 */
@Base
@Bean
public class TaskService extends ContextService {

    public static final TaskService ME = BeanFactoryUtils.get(TaskService.class);

    protected static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    public static String TASK_NOT_FOUND = LangCodeUtils.get("任务不存在", TaskService.class);

    public static String TASK_PARAM_ERROR = LangCodeUtils.get("任务参数不正确", TaskService.class);

    @Inject
    protected TaskFactory factory;

    public TaskFactory getFactory() {
        return factory;
    }

    public Class<?>[] getParamTypes(String name) {
        TaskFactory.TaskMethod taskMethod = factory.getTaskMethodMap() == null ? null : factory.getTaskMethodMap().get(name);
        return taskMethod == null ? null : taskMethod.paramTypes;
    }

    protected DActiver<JTask> taskDActiver;

    protected UtilAtom taskAtom;

    @Value("task.thread.count")
    protected int taskThreadCount = 10;

    protected DActiver<JPlan> planDActiver;

    @Started
    protected void started() {
        taskDActiver = new DActiver<JTask>("JTask");
        L2EntityMergeService.ME.addEntityMerges(JTask.class, new IEntityMerge<JTask>() {
            @Override
            public void merge(String entityName, JTask entity, MergeType mergeType, Object mergeEvent) {
                taskDActiver.merge(entity, mergeType, mergeEvent);
            }
        });

        taskAtom = taskThreadCount > 0 ? new ContextAtom(taskThreadCount) : new UtilAtom();

        planDActiver = new DActiver<JPlan>("JPlan");
        L2EntityMergeService.ME.addEntityMerges(JPlan.class, new IEntityMerge<JPlan>() {
            @Override
            public void merge(String entityName, JPlan entity, MergeType mergeType, Object mergeEvent) {
                planDActiver.merge(entity, mergeType, mergeEvent);
            }
        });

        long contextTime = ContextUtils.getContextTime();
        ME.reloadTask(contextTime);
        ME.reloadPlan(contextTime);
    }

    @Override
    public void step(long contextTime) {
        if (taskDActiver != null && taskDActiver.stepNext(contextTime)) {
            ME.reloadTask(contextTime);
        }

        if (planDActiver != null && planDActiver.stepNext(contextTime)) {
            ME.reloadPlan(contextTime);
        }
    }

    @Async(notifier = true)
    @Transaction
    public void reloadTask(long contextTime) {
        final boolean[] doContinues = new boolean[]{true};
        while (doContinues[0]) {
            doContinues[0] = false;
            List<JTask> tasks = taskDActiver.reloadActives(contextTime);
            if (tasks.isEmpty()) {
                break;

            } else {
                for (final JTask task : tasks) {
                    taskAtom.increment();
                    try {
                        ContextUtils.getThreadPoolExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (ME.doTaskBaseContinue(null, "JTask", task, taskAtom)) {
                                    doContinues[0] = true;
                                }
                            }
                        });

                    } catch (Throwable e) {
                        taskAtom.decrement();
                    }
                }

                taskAtom.await();
                contextTime = ContextUtils.getContextTime();
            }
        }
    }

    @Async(notifier = true)
    @Transaction(readOnly = true)
    public void reloadPlan(long contextTime) {
        final Session session = BeanDao.getSession();
        List<JPlan> plans = planDActiver.reloadActives(contextTime);
        boolean doContinue = true;
        while (doContinue) {
            doContinue = false;
            for (final JPlan plan : plans) {
                if (doTaskBaseContinue(session, "JPlan", plan, null)) {
                    doContinue = true;
                }
            }
        }
    }

    @Transaction
    protected boolean doTaskBaseContinue(Session session, String name, JTaskBase task, UtilAtom atom) {
        try {
            JVerifier verifier = SingleUtils.enterSingle(name + "@" + task.getId());
            if (verifier == null) {
                return false;
            }

            try {
                if (session == null) {
                    session = BeanDao.getSession();
                }

                task.setStartTag(verifier.getTag());
                task.setStartTime(ContextUtils.getContextTime());
                session.merge(task);
                session.flush();
                Map<String, TaskFactory.TaskMethod> taskMethodMap = factory.getTaskMethodMap();
                TaskFactory.TaskMethod taskMethod = taskMethodMap == null ? null : taskMethodMap.get(task.getName());
                task.setStartTag("0");
                if (taskMethod != null) {
                    try {
                        taskMethod.method.invoke(taskMethod.beanObject, HelperDatabind.PACK.readArray(task.getTaskData(), taskMethod.paramTypes));
                        task.setPassTime(ContextUtils.getContextTime());
                        session.merge(task);

                    } catch (Throwable e) {
                        LOGGER.error("do taskId[" + task.getId() + "] " + task.getName() + " error", e);
                        int retryCount = task.getRetryCount();
                        boolean doContinue = true;
                        if (retryCount >= 0) {
                            if (retryCount == 0) {
                                doContinue = false;
                                task.setPassTime(ContextUtils.getContextTime() - 1);

                            } else {
                                task.setRetryCount(--retryCount);
                            }

                            session.merge(task);
                        }

                        return doContinue;
                    }
                }

            } finally {
                SingleUtils.exitSingle(verifier);

            }

        } finally {
            if (atom != null) {
                atom.decrement();
            }
        }

        return false;
    }

    /*
     * 添加任务
     */
    @Transaction
    public void addTask(String name, long beginTime, long passTime, int retryCount, Object... params) throws IOException {
        JTask task = new JTask();
        task.setName(name);
        task.setTaskData(HelperDatabind.PACK.writeAsBytesArray(params));
        task.setBeginTime(beginTime);
        task.setPassTime(passTime);
        task.setRetryCount(retryCount);
        BeanDao.getSession().persist(task);
    }

    /*
     * 添加计划
     */
    @Transaction
    public void addPanel(String id, String name, long beginTime, long passTime, int retryCount, Object... params) throws IOException {
        JPlan plan = new JPlan();
        plan.setId(KernelString.isEmpty(id) ? name : id);
        plan.setName(name);
        plan.setTaskData(HelperDatabind.PACK.writeAsBytesArray(params));
        plan.setBeginTime(beginTime);
        plan.setPassTime(passTime);
        plan.setRetryCount(retryCount);
        BeanDao.getSession().merge(plan);
    }
}
