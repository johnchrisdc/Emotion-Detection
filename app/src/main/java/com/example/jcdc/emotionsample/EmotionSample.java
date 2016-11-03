package com.example.jcdc.emotionsample;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * Created by jcdc on 11/4/16.
 */

public class EmotionSample extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }
}
