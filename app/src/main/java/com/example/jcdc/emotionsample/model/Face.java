package com.example.jcdc.emotionsample.model;

import android.icu.text.MessagePattern;
import android.util.Log;

import com.example.jcdc.emotionsample.Variables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jcdc on 11/4/16.
 */

public class Face {

    private FaceRectangle faceRectangle;
    private Scores scores;

    public Scores getScores() {
        return scores;
    }

    public FaceRectangle getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(FaceRectangle faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public static Face getFace(String image_url) throws Exception{

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, new Gson().toJson( new Image(image_url)));
        Request request = new Request.Builder()
                .url(Variables.oxford_url)
                .addHeader("Ocp-Apim-Subscription-Key", "0ab1e875004a4d1d8a2b36ad828f6fb3")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String data = response.body().string();

        Type arraysKoBeh = new TypeToken<ArrayList<Face>>(){}.getType();
        return ((ArrayList<Face>) new Gson().fromJson(data, arraysKoBeh)).get(0); //Get the only one
    }

}
