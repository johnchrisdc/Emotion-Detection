package com.example.jcdc.emotionsample.model;

import com.example.jcdc.emotionsample.Variables;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
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

    public static final MediaType OCTET
            = MediaType.parse("application/octet-stream; charset=utf-8");

    public static Face getFace(InputStream image_url) throws Exception{
        byte[] data = IOUtils.toByteArray(image_url);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(OCTET, data);
        Request request = new Request.Builder()
                .url(Variables.oxford_url)
                .addHeader("Ocp-Apim-Subscription-Key", "0ab1e875004a4d1d8a2b36ad828f6fb3")
                .addHeader("Content-Type", "application/octet-stream")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String data_ = response.body().string();

        Type arraysKoBeh = new TypeToken<ArrayList<Face>>(){}.getType();
        return ((ArrayList<Face>) new Gson().fromJson(data_, arraysKoBeh)).get(0); //Get the only one
    }

}
