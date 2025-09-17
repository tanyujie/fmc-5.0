package com.paradisecloud.fcm.tencent.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class TencentDelayTask implements Delayed, Runnable{
    private String id = "";
    private long start = 0;

    public TencentDelayTask(String id, long delayInMilliseconds){
        this.id = id;
        this.start = System.currentTimeMillis() + delayInMilliseconds;
    }

    public String getId() {
        return id;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.start - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diff = this.start - ((TencentDelayTask) o).start;
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof TencentDelayTask)) {
            return false;
        }
        TencentDelayTask t = (TencentDelayTask)o;
        return this.id.equals(t.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}