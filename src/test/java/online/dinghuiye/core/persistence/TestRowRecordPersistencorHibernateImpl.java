package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.ResultStatus;
import online.dinghuiye.api.entity.RowRecord;
import online.dinghuiye.api.entity.RowRecordHandleResult;
import online.dinghuiye.api.entity.TransactionMode;
import online.dinghuiye.core.persistence.testcase.SchoolMan;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @author Strangeen
 * on 2017/8/6
 */
public class TestRowRecordPersistencorHibernateImpl {

    private SessionFactory factory;
    private RowRecordPersistencorHibernateImpl handler;

    private List<RowRecord> dataList;

    @Before
    public void init() {

        // 准备环境
        {
            Configuration configuration = new Configuration().configure("hbm.cfg.xml");
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(serviceRegistry);

            handler = new RowRecordPersistencorHibernateImpl(factory);
        }

        // 准备数据
        {
            for (int i = 0; i < 2; i ++) {
                RowRecord rr = new RowRecord();
                if (dataList == null) {
                    dataList = new ArrayList<>();
                }
                dataList.add(rr);
                Map<Class<?>, Object> pojoMap = new HashMap<>();
                rr.setPojoRecordMap(pojoMap);

                SchoolMan man = new SchoolMan();
                man.setName("测试" + i);
                pojoMap.put(SchoolMan.class, man);

                rr.setResult(new RowRecordHandleResult(ResultStatus.SUCCESS, null));
            }
        }
    }



    @Test
    public void testPersistSingleton() {

        dataList.forEach(rr ->
                rr.getPojoRecordMap().values().forEach(obj ->
                        Assert.assertNull(((SchoolMan) obj).getId())));

        long start = System.currentTimeMillis();
        handler.persist(dataList, TransactionMode.SINGLETON);
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");

        dataList.forEach(rr ->
                rr.getPojoRecordMap().values().forEach(obj ->
                        Assert.assertNotNull(((SchoolMan) obj).getId())));
    }

    @Test
    public void testPersistMultiple() {

        dataList.forEach(rr ->
                rr.getPojoRecordMap().values().forEach(obj ->
                        Assert.assertNull(((SchoolMan) obj).getId())));

        long start = System.currentTimeMillis();
        handler.persist(dataList, TransactionMode.MULTIPLE);
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");

        dataList.forEach(rr ->
                rr.getPojoRecordMap().values().forEach(obj ->
                        Assert.assertNotNull(((SchoolMan) obj).getId())));
    }

    @Test
    public void testPersistSingletonWithObserver() {

        Double[] expectProcessArr = new Double[]{50.0, 100.0};
        List<Double> actualProcessList = new ArrayList<>();

        handler.persist(dataList, TransactionMode.SINGLETON, (Observable o, Object arg) -> {
            System.out.println(arg);
            actualProcessList.add((Double) arg);
        });

        Assert.assertArrayEquals(expectProcessArr, actualProcessList.toArray());
    }

    @Test
    public void testPersistMultipleWithObserver() {

        Double[] expectProcessArr = new Double[]{50.0, 100.0};
        List<Double> actualProcessList = new ArrayList<>();

        handler.persist(dataList, TransactionMode.MULTIPLE, (Observable o, Object arg) -> {
            System.out.println(arg);
            actualProcessList.add((Double) arg);
        });

        Assert.assertArrayEquals(expectProcessArr, actualProcessList.toArray());
    }

    @After
    public void destroy() {
        if (factory != null) {
            factory.close();
        }
    }

}
