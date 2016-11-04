package com.example.jcdc.emotionsample.model;

/**
 * Created by jcdc on 11/4/16.
 */

public class Image {
    byte[] data;

    public Image(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
