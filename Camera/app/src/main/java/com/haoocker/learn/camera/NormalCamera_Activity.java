package com.haoocker.learn.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class NormalCamera_Activity extends AppCompatActivity {


    private FileInputStream mFis;
    private ImageView imageView;

    File tempFile;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normalcamera);
        Button button = findViewById(R.id.btn_camera);
        imageView = findViewById(R.id.imageView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NormalCamera_ActivityPermissionsDispatcher.takePhotoWithPermissionCheck(NormalCamera_Activity.this);
            }
        });
    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //dir???/storage/emulated/0/Android/data/com.haoocker.learn.camera/files/Pictures
        //tempFile???????????????????????????????????????, ???????????????????????????????????????????????????
        tempFile = new File(dir, "allens.png");
        //?????????????????????????????????Android7.0?????????????????????????????????
        // Build.VERSION.SDK_INT ??????app???????????????????????????????????????????????????????????? ??????8.1?????????SDK_INT???27
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            //?????????URI
            imageUri = Uri.fromFile(tempFile);
        } else {//????????????????????????????????????????????????27??????????????????FileProvider?????????
            //??????FileProvider ????????????????????????????????????tempFile??????
            imageUri = FileProvider.getUriForFile(NormalCamera_Activity.this, NormalCamera_Activity.this.getPackageName() + ".FileProvider",tempFile);
            System.out.println("authority???"+ imageUri.getAuthority());
            System.out.println("path???"+ imageUri.getPath());
            //????????????
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  //??????????????????????????????????????????????????????uri???????????????????????????
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(tempFile);
            String mFilePath = uri.getPath();

            System.out.println("mFilePath???"+ mFilePath);
            try {
                mFis = new FileInputStream(mFilePath);
                Bitmap bitmap = BitmapFactory.decodeStream(mFis);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mFis != null)
                        mFis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @OnShowRationale(Manifest.permission.CAMERA)
    void showRationaleForRecord(final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("???????????????????????????????????????????????????????????????")
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showRecordDenied() {
        Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void onRecordNeverAskAgain() {
        new AlertDialog.Builder(this)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ????????????????????????
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("??????????????????????????????,?????????????????????")
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NormalCamera_ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}