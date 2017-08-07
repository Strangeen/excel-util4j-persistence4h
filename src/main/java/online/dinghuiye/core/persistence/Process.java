package online.dinghuiye.core.persistence;

import java.util.Observable;

/**
 * Created by Strangeen on 2017/8/6.
 */
public class Process extends Observable {

    //private Double process;

    private Integer excuted;
    private Integer total;

    public Process(Integer total) {
        this.excuted = 0;
        this.total = total;
    }

    public Integer getExcuted() {
        return excuted;
    }

    public void setExcuted(Integer excuted) {
        this.excuted = excuted;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
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
