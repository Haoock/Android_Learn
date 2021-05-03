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
        //dir：/storage/emulated/0/Android/data/com.haoocker.learn.camera/files/Pictures
        //tempFile是用来存储每一张拍照的图片, 当前拍照的图片会最终存在这个目录中
        tempFile = new File(dir, "allens.png");
        //如果安装该软件的手机为Android7.0以下依旧使用原来的方法
        // Build.VERSION.SDK_INT 软件app安装在哪个手机上，该手机的操作系统版本号 比如8.1对应的SDK_INT是27
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
            //图片的URI
            imageUri = Uri.fromFile(tempFile);
        } else {//如果安装该软件的手机版本大于等于27，则需要使用FileProvider的方法
            //利用FileProvider 将每一张拍照的图片保存到tempFile里面
            imageUri = FileProvider.getUriForFile(NormalCamera_Activity.this, NormalCamera_Activity.this.getPackageName() + ".FileProvider",tempFile);
            System.out.println("authority："+ imageUri.getAuthority());
            System.out.println("path："+ imageUri.getPath());
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);  //这是将系统相机拍照之后的原图存到这个uri所指定的目录之下。
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(tempFile);
            String mFilePath = uri.getPath();

            System.out.println("mFilePath："+ mFilePath);
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
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton("不给", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("拍照需要相机权限，应用将要申请使用相机权限")
                .show();
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void showRecordDenied() {
        Toast.makeText(getApplicationContext(), "权限被拒绝", Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void onRecordNeverAskAgain() {
        new AlertDialog.Builder(this)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 打开系统应用设置
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage("您已经禁止了相机权限,是否现在去开启")
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        NormalCamera_ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}