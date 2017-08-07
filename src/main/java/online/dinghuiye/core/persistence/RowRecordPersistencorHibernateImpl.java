package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.ResultStatus;
import online.dinghuiye.api.entity.RowRecord;
import online.dinghuiye.api.entity.RowRecordHandleResult;
import online.dinghuiye.api.persistence.RowRecordPersistencor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Observer;

/**
 * Created by Strangeen on 2017/8/4.
 *
 * hibernate作为持久化层的实现
 */
public class RowRecordPersistencorHibernateImpl implements RowRecordPersistencor {

    private SessionFactory sessionFactory;

    public RowRecordPersistencorHibernateImpl(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }


    @Override
    public void persist(List<RowRecord> rowRecordList, Observer processObserver) {

        Process process = null;
        if (processObserver != null) {
            process = new Process(rowRecordList.size());
            process.addObserver(processObserver);
        }

        for (RowRecord rowRecord : rowRecordList) {
            persist(rowRecord);
            if (processObserver != null) {
                process.setExcuted(process.getExcuted() + 1);
                process.change();
            }
        }
    }


    @Override
    public void persist(List<RowRecord> rowRecordList) {

        for (RowRecord rowRecord : rowRecordList) {
            persist(rowRecord);
        }
    }

    @Override
    public void persist(RowRecord rowRecord) {
        if (rowRecord.getResult() != null &&
                rowRecord.getResult().getResult() != ResultStatus.SUCCESS) return;
        Session session = null;
        Transaction tx = null;
        try {
            session = getSession();
            tx = session.beginTransaction();
            for (Object pojoObj : rowRecord.getPojoRecordMap().values()) {
                session.save(pojoObj);
            }
            commit(tx);
        } catch (Exception e) {
            e.printStackTrace();
            rollback(tx);
            rowRecord.setResult(new RowRecordHandleResult(ResultStatus.FAIL, "保存失败"));
        } finally {
            closeSession(session);
        }
    }

    private Session getSession() {
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