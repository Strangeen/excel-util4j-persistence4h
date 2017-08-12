package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.ResultStatus;
import online.dinghuiye.api.entity.RowRecord;
import online.dinghuiye.api.entity.RowRecordHandleResult;
import online.dinghuiye.api.entity.TransactionMode;
import online.dinghuiye.core.persistence.testcase.User;
import online.dinghuiye.core.persistence.testcase.UserExtraInfo;
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
 * at 2017/8/6
 */
public class TestRowRecordPersistencorHibernateImplForCascadePojo {

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

                User user = new User();
                user.setUsername("123456abc" + i);

                UserExtraInfo info = new UserExtraInfo();
                info.setName("test" + i);
                info.setUser(user);

                user.setInfo(info);

                pojoMap.put(User.class, user);

                rr.setResult(new RowRecordHandleResult(ResultStatus.SUCCESS, null));
            }
        }
    }


    // 没报错即可写入
    @Test
    public void testPersistSingleton() {

        long start = System.currentTimeMillis();
        handler.persist(dataList, TransactionMode.SINGLETON);
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");

        for (RowRecord rr : dataList) {
            Assert.assertEquals(ResultStatus.SUCCESS, rr.getResult().getResult());
            Assert.assertNotNull(rr.get(User.class).getId());
            Assert.assertNotNull(rr.get(User.class).getInfo().getId());
        }
    }

    // 报错均不能写入
    @Test
    public void testPersistMultiple() {

        long start = System.currentTimeMillis();
        handler.persist(dataList, TransactionMode.MULTIPLE);
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");

        for (RowRecord rr : dataList) {
            Assert.assertEquals(ResultStatus.SUCCESS, rr.getResult().getResult());
            Assert.assertNotNull(rr.get(User.class).getId());
            Assert.assertNotNull(rr.get(User.class).getInfo().getId());
        }
    }

    @Test
    public void testPersistSingletonWithObserver() {

        Double[] expectProcessArr = new Double[]{50.0, 100.0};
        List<Double> actualProcessList = new ArrayList<>();

        handler.persist(dataList, TransactionMode.SINGLETON,
                (Observable o, Object arg) -> {
                    System.out.println(arg);
                    actualProcessList.add((Double) arg);
                }
        );

        Assert.assertArrayEquals(expectProcessArr, actualProcessList.toArray());
    }

    @Test
    public void testPersistMultipleWithObserver() {

        Double[] expectProcessArr = new Double[]{50.0, 100.0};
        List<Double> actualProcessList = new ArrayList<>();

        handler.persist(dataList, TransactionMode.MULTIPLE,
                (Observable o, Object arg) -> {
                    System.out.println(arg);
                    actualProcessList.add((Double) arg);
                }
        );

        Assert.assertArrayEquals(expectProcessArr, actualProcessList.toArray());
    }

    @After
    public void destroy() {
        if (factory != null) {
            factory.close();
        }
    }

}
