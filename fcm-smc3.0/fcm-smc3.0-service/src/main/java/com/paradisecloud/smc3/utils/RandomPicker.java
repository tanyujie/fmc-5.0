package com.paradisecloud.smc3.utils;

import java.util.List;
import java.util.Random;


public class RandomPicker {

    private static final double PROBABILITY = 1 - Math.pow(0.98, 1.0/120);
    //private static final double PROBABILITY = 0.20;
    private static final int DURATION_MINUTES = 120;

    public static void pickRandomElement(List<String> elements, Callback callback) {

        Random random = new Random();
        Runnable task = new Runnable() {
            private int minutesPassed = 0;
            @Override
            public void run() {
                minutesPassed++;
                if (random.nextDouble() < PROBABILITY) {
                    String pickedElement = elements.get(random.nextInt(elements.size()));
                    callback.onElementPicked(pickedElement);
                } else if (minutesPassed >= DURATION_MINUTES) {
                    callback.onNoElementPicked();
                }
            }
        };

        new Thread(task).start();

    }

    public interface Callback {
        void onElementPicked(String element);

        void onNoElementPicked();
    }



}

