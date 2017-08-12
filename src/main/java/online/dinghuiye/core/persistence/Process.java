package online.dinghuiye.core.persistence;

import java.util.Observable;

/**
 * @author Strangeen
 * on 2017/8/6
 */
public class Process extends Observable {

    private Long excuted;
    private Long total;

    public Process(Long total) {
        this.excuted = 0L;
        this.total = total;
    }

    public Long getExcuted() {
        return excuted;
    }

    public void setExcuted(Long excuted) {
        this.excuted = excuted;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    /**
     * 获取进度
     * @return 进度百分比，没有格式化小数位
     */
    protected Double getProcess() {
        return Double.valueOf(excuted) / total * 100;
    }


    public void change() {
        setChanged();
        notifyObservers(getProcess());
    }
}
