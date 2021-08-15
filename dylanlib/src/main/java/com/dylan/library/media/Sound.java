package com.dylan.library.media;

/**
 * Author: Dylan
 * Date: 2021/8/14
 * Desc:
 */

public class Sound {
    private float rate=1.0f;
    private float pitch=1.0f;
    private float speed=1.0f;
    private float volume=1.0f;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
