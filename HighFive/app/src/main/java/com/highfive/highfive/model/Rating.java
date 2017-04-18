package com.highfive.highfive.model;

/**
 * Created by dan on 18.04.17.
 */

public class Rating {
    private float neutral;
    private float negative;
    private float positive;

    public float getNeutral() {
        return neutral;
    }

    public void setNeutral(float neutral) {
        this.neutral = neutral;
    }

    public float getNegative() {
        return negative;
    }

    public void setNegative(float negative) {
        this.negative = negative;
    }

    public float getPositive() {
        return positive;
    }

    public void setPositive(float positive) {
        this.positive = positive;
    }
}
