package com.dylan.library.widget.visualizeview;

import android.media.audiofx.Visualizer;

/**
 * Author: Dylan
 * Date: 2023/12/3
 * Desc:
 */
public class VisualizerHelper {
    private Visualizer mVisualizer;
    private VisualizeCallback mCallback;
    private int mCount;

    public VisualizerHelper() {
    }

    public void setVisualCount(int count) {
        this.mCount = count;
    }

    public void setVisualizeCallback(VisualizeCallback callback) {
        this.mCallback = callback;
    }

    public void setAudioSessionId(int audioSessionId) {
        if (this.mVisualizer != null) {
            this.release();
        }

        this.mVisualizer = new Visualizer(audioSessionId);
        this.mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        this.mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                float[] model = new float[fft.length / 2 + 1];
                model[0] = (float)Math.abs(fft[1]);
                int j = 1;

                for(int i = 2; i < VisualizerHelper.this.mCount * 2; model[j] = Math.abs(model[j])) {
                    model[j] = (float)Math.hypot((double)fft[i], (double)fft[i + 1]);
                    i += 2;
                    ++j;
                }

                if (VisualizerHelper.this.mCallback != null) {
                    VisualizerHelper.this.mCallback.onFftDataCapture(model);
                }

            }
        }, Visualizer.getMaxCaptureRate() / 2, false, true);
        this.mVisualizer.setEnabled(true);
    }

    public void release() {
        if (this.mVisualizer != null) {
            this.mVisualizer.release();
        }

    }
}