package online.dinghuiye.core.persistence;

import online.dinghuiye.api.entity.Process;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Strangeen on 2017/08/11
 *
 * @author Strangeen on 2017/9/3
 * @version 2.1.0
 */
public class TestProcess {

    @Test
    public void testGetAndSetTotal() {

        Process p = new Process(0L);
        p.setTotal(100L);
        Assert.assertEquals(true, p.getTotal() == 100);
    }
}
