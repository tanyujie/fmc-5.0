package com.paradisecloud.smc.service.delay.item;

import lombok.Getter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author nj
 * @param <T>
 */
@Getter
public class DelayedItem<T> implements Delayed {
    private T item;
    private long removeTime;

    public DelayedItem(T item, Long removeTime) {
        this.item = item;
        this.removeTime = removeTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return removeTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed delayed) {
        DelayedItem that = (DelayedItem) delayed;
        if (this.removeTime > that.removeTime) {
            return 1;
        } else if (this.removeTime == that.removeTime) {
            return 0;
        } else {
            return -1;
        }
    }
}
