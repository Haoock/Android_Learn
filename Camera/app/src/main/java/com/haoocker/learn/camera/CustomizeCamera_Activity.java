package com.haoocker.learn.camera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
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
public class CustomizeCamera_Activity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_camera_);
        mCamera = Camera.open();
        // 创建预览类，并与Camera关联，最后添加到界面布局中
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        Button captureButton = (Button)findViewById(R.id.camera_preview2);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               CustomizeCamera_ActivityPermissionsDispatcher.takePhotosWithPermissionCheck(CustomizeCamera_Activity.this);
            }
        });
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void takePhotos(){
        //得到照相机的参数
        Camera.Parameters parameters = mCamera.getParameters();
        //图片的格式
        parameters.setPictureFormat(ImageFormat.JPEG);
        //预览的大小是多少
        parameters.setPreviewSize(800, 400);
        //设置对焦模式，自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        // 在捕获图片前进行自动对焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    //获取照片
                    mCamera.takePicture(null, null, mPictureCallback);
                }
            }
        });
    }

    //获取照片中的接口回调
    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 获取Jpeg图片，并保存在sd卡上
            FileOutputStream fos = null;
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File tempFile = new File(dir, "tt001.png");
            //文件
            try {
                //
                fos = new FileOutputStream(tempFile);
                fos.write(data);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //实现连续拍多张的效果
//                mCamera.startPreview();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                .setMessage("需要存储权限，应用将要申请使用存储权限")
                .show();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRecordDenied() {
        Toast.makeText(getApplicationContext(), "权限被拒绝", Toast.LENGTH_LONG).show();
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                .setMessage("您已经禁止了存储权限,是否现在去开启")
                .show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CustomizeCamera_ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}