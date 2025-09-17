package com.paradisecloud.smc3.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Smc3DelayTask implements Delayed, Runnable{
    private String id = "";
    private long start = 0;

    public Smc3DelayTask(String id, long delayInMilliseconds){
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
        long diff = this.start - ((Smc3DelayTask) o).start;
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
        if (!(o instanceof Smc3DelayTask)) {
            return false;
        }
        Smc3DelayTask t = (Smc3DelayTask)o;
        return this.id.equals(t.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}