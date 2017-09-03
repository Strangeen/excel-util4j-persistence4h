package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.Process;
import online.dinghuiye.api.entity.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Strangeen at 2017/8/6
 *
 * @author Strangeen on 2017/9/3
 * @version 2.1.0
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
    public void testPersistSingletonWithProcess() {

        Process process = new Process((long) dataList.size());
        handler.persist(dataList, TransactionMode.SINGLETON, process);
        Assert.assertEquals(new Double(100.0), process.getProcess());
    }

    @Test
    public void testPersistMultipleWithProcess() {

        Process process = new Process((long) dataList.size());
        handler.persist(dataList, TransactionMode.MULTIPLE, process);
        Assert.assertEquals(new Double(100.0), process.getProcess());
    }

    @After
    public void destroy() {
        if (factory != null) {
            factory.close();
        }
    }

}
