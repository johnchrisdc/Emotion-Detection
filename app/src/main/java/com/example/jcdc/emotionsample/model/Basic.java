package com.example.jcdc.emotionsample.model;

import android.util.Log;

import com.example.jcdc.emotionsample.Variables;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jcdc on 11/4/16.
 */

public class Basic {

    private boolean success;
    private int status;

    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static Basic getResponse(String base64Image) throws Exception{
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("image", base64Image)
                .build();

        Request request = new Request.Builder()
                .url(Variables.imgur_url)
                .addHeader("Authorization", "Client-Id 3d972f742e464ff")
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        return new Gson().fromJson(data, Basic.class);
    }

}
