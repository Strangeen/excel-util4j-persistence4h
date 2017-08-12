package online.dinghuiye.core.persistence;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Strangeen on 2017/08/11
 */
public class TestProcess {

    @Test
    public void testGetAndSetTotal() {

        Process p = new Process(0L);
        p.setTotal(100L);
        Assert.assertEquals(true, p.getTotal() == 100);
    }
}
