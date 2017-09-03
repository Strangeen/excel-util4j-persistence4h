package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.Process;
import online.dinghuiye.api.entity.*;
import online.dinghuiye.api.persistence.RowRecordPersistencor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * hibernate作为持久化层的实现
 *
 * @author Strangeen on 2017/8/4
 *
 * @author Strangeen on 2017/9/3
 * @version 2.1.0
 */
public class RowRecordPersistencorHibernateImpl implements RowRecordPersistencor {

    private static final Logger logger = LoggerFactory.getLogger(RowRecordPersistencorHibernateImpl.class);

    private SessionFactory sessionFactory;

    /**
     * @param sessionFactory hibernate的sessionFactory
     */
    public RowRecordPersistencorHibernateImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void persist(List<RowRecord> rowRecordList, TransactionMode mode, Process process) {

        if (process != null)
            process.setNode(ProcessNode.PERSISTENCE);

        // 存储操作
        if (mode == TransactionMode.SINGLETON)
            persistSingleton(rowRecordList, process);
        else if (mode == TransactionMode.MULTIPLE)
            persistMultiple(rowRecordList, process);
        else throw new RuntimeException("impossible value of mode");
    }


    @Override
    public void persist(List<RowRecord> rowRecordList, TransactionMode mode) {

        persist(rowRecordList, mode,null);
    }

    /**
     * 整体事务形式的保存方法{@link TransactionMode#MULTIPLE}
     *
     * @param rowRecordList rowRecordList
     * @param process 进度对象{@link Process}
     */
    private void persistMultiple(List<RowRecord> rowRecordList, Process process) {

        Session session = null;
        Transaction tx = null;
        try {
            session = openSession();
            tx = session.beginTransaction();
            for (RowRecord rowRecord : rowRecordList) {
                try {
                    if (rowRecord.getResult() != null &&
                            rowRecord.getResult().getResult() != ResultStatus.SUCCESS) continue;

                    for (Object pojoObj : rowRecord.getPojoRecordMap().values()) {
                        session.save(pojoObj);
                    }

                    if (process != null) {
                        process.setExcuted(process.getExcuted() + 1);
                        process.change();
                    }

                } catch (Exception e) {
                    logger.warn("Multiple保存失败", e);
                    rowRecord.getResult().setResult(ResultStatus.FAIL).setMsg("保存失败");
                    throw e;
                }
            }
            commit(tx);

        } catch (Exception e) {
            rollback(tx);
        } finally {
            closeSession(session);
        }
    }

    /**
     * 单条事务形式的保存方法{@link TransactionMode#SINGLETON}
     *
     * @param rowRecordList rowRecordList
     * @param process 进度对象{@link Process}
     */
    private void persistSingleton(List<RowRecord> rowRecordList, Process process) {

        for (RowRecord rowRecord : rowRecordList) {

            persist(rowRecord);

            if (process != null) {
                process.setExcuted(process.getExcuted() + 1);
                process.change();
            }
        }
    }

    private void persist(RowRecord rowRecord) {
        if (rowRecord.getResult() != null &&
                rowRecord.getResult().getResult() != ResultStatus.SUCCESS) return;

        Session session = null;
        Transaction tx = null;
        try {
            session = openSession();
            tx = session.beginTransaction();
            for (Object pojoObj : rowRecord.getPojoRecordMap().values()) {
                session.save(pojoObj);
            }
            commit(tx);

        } catch (Exception e) {
            logger.warn("Singleton保存失败", e);
            rollback(tx);
            rowRecord.getResult().setResult(ResultStatus.FAIL).setMsg("保存失败");
        } finally {
            closeSession(session);
        }
    }


    private Session openSession() {
        return sessionFactory.openSession();
    }

    private void closeSession(Session session) {
        if (session != null) {
            session.close();
        }
    }

    private void commit(Transaction tx) {
        if (tx != null) {
            tx.commit();
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null) {
            tx.rollback();
        }
    }
}