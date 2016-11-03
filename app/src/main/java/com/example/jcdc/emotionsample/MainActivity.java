package com.example.jcdc.emotionsample;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private final int CODE_CAMERA_ACTIVITY = 69;

    private Context context;

    private Button camera_button;
    private ImageView image;

    private PermissionListener cameraPermissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);

        cameraPermissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                openCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                showPermissionErrorDialog();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        };

        camera_button = (Button) findViewById(R.id.button_camera);
        image = (ImageView) findViewById(R.id.image);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Dexter.isRequestOngoing())
                    Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_CAMERA_ACTIVITY) {
            if (resultCode == RESULT_OK){
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                if (photo != null){
                    image.setImageBitmap(photo);
                }else {
                    Log.e("MainActivity", "404 image !found");
                }
            }
        }

    }

    private void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CODE_CAMERA_ACTIVITY);
    }

    private void showPermissionErrorDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        alertDialogBuilder.setTitle("OMFG!");
        alertDialogBuilder
                .setMessage("This crappy app needs Camera permission to capture images.")
                .setCancelable(false)
                .setPositiveButton("Try again",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        if (!Dexter.isRequestOngoing())
                            Dexter.checkPermission(cameraPermissionListener, Manifest.permission.CAMERA);
                    }
                })
                .setNegativeButton("Exit",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
