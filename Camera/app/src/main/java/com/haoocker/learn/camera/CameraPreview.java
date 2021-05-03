package com.haoocker.learn.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;

/**
 * 这是相机的预览类，此类能够显示相机的实时预览图像。
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mySurfaceHolder;
    private Camera myCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        //初始化Camera对象、
        myCamera = camera;
        //得到SurfaceHolder对象
        mySurfaceHolder = getHolder();
        //添加回调，得到Surface的三个声明周期方法
        mySurfaceHolder.addCallback(this);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // 当Surface被创建之后，开始Camera的预览
        try {
            //设置预览方向
            myCamera.setDisplayOrientation(90);
            //把这个预览效果展示在SurfaceView上面
            myCamera.setPreviewDisplay(holder);
            //开启预览效果
            myCamera.startPreview();
        } catch (IOException e) {
            System.out.println("预览失败");
//            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // Surface发生改变的时候将被调用，第一次显示到界面的时候也会被调用
        if (holder.getSurface() == null) {
            // 如果Surface为空，不继续操作
            return;
        }
        //停止预览效果
        myCamera.stopPreview();
        //重新设置预览效果
        try {
            myCamera.setPreviewDisplay(mySurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        myCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
