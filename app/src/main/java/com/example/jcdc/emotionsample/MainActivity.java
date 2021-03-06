package com.example.jcdc.emotionsample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jcdc.emotionsample.helper.ImageHelper;
import com.example.jcdc.emotionsample.helper.StringHelper;
import com.example.jcdc.emotionsample.model.Basic;
import com.example.jcdc.emotionsample.model.Face;
import com.example.jcdc.emotionsample.model.Scores;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final int CODE_CAMERA_ACTIVITY = 69;

    private Context context;

    private Button camera_button;
    private ImageView image;

    private MultiplePermissionsListener multiplePermissionsListener;

    private Uri mUriPhotoTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);

        multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    openCamera();
                } else {
                    showPermissionErrorDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };

        camera_button = (Button) findViewById(R.id.button_camera);
        image = (ImageView) findViewById(R.id.image);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Dexter.isRequestOngoing())
                    Dexter.checkPermissions(multiplePermissionsListener, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_CAMERA_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                if (resultCode == RESULT_OK) {
                    Uri imageUri;
                    if (data == null || data.getData() == null) {
                        imageUri = mUriPhotoTaken;
                    } else {
                        imageUri = data.getData();
                    }

                    Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            imageUri, getContentResolver());

                    if (bitmap != null){
                        image.setImageBitmap(bitmap);

                        ByteArrayOutputStream output = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

                        Log.d("MainActivity", inputStream.toString());
                        new GetFace().execute(inputStream);
                    }else {
                        Toast.makeText(context, "Image is null", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File file = File.createTempFile("IMG_", ".jpg", storageDir);
            mUriPhotoTaken = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mUriPhotoTaken);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, CODE_CAMERA_ACTIVITY);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void showPermissionErrorDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("OMFG!");
        alertDialogBuilder
                .setMessage("This crappy app need permissions to capture images.")
                .setCancelable(false)
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!Dexter.isRequestOngoing())
                            Dexter.checkPermissions(multiplePermissionsListener, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showEmotionDialog(String emotion) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("Emotion");
        alertDialogBuilder
                .setMessage(emotion)
                .setCancelable(false)
                .setNegativeButton("Yay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class UploadImage extends AsyncTask<String, Void, Basic> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Uploading Image", "This may take forever", true);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Basic doInBackground(String... strings) {
            try {
                return Basic.getResponse(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Basic basic) {
            super.onPostExecute(basic);
            progressDialog.hide();

            if (basic != null) {
                Log.d("MainActivity", basic.getData().getLink());
                //    new GetFace().execute(basic.getData().getLink());
            } else {
                Toast.makeText(context, "Imgur error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetFace extends AsyncTask<InputStream, Void, Face> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Processing Image", "Is this really a face?", true);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected Face doInBackground(InputStream... strings) {
            try {
                return Face.getFace(strings[0]);
            } catch (Exception e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Face face) {
            super.onPostExecute(face);
            progressDialog.hide();
            if (face != null && face.getScores() != null) {
                Scores scores = face.getScores();

                HashMap<String, Double> hm = new HashMap<>();
                hm.put("anger", scores.getAnger());
                hm.put("contempt", scores.getContempt());
                hm.put("disgust", scores.getDisgust());
                hm.put("fear", scores.getFear());
                hm.put("happiness", scores.getHappiness());
                hm.put("neutral", scores.getNeutral());
                hm.put("sadness", scores.getSadness());
                hm.put("surprise", scores.getSurprise());

                Map.Entry<String, Double> maxEntry = null;

                for (Map.Entry<String, Double> entry : hm.entrySet()) {
                    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                        maxEntry = entry;
                    }
                }

                Log.d("MainActivity", maxEntry.getKey());
                showEmotionDialog(StringHelper.cap1stChar(maxEntry.getKey()));

            } else {
                Toast.makeText(context, "Emotion error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
