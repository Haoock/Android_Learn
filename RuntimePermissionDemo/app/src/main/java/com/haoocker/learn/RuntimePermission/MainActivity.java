package com.haoocker.learn.RuntimePermission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CALLPHONE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_phoneCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhone();
            }
        });
    }

    private void callPhone(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // 应用没有授予拨打电话权限，请求权限
            requestPhoneCallPermission();
        } else {
            // 应用被授予拨打电话权限 PackageManager.PERMISSION_GRANTED
            makeCall();
        }
    }


    private void requestPhoneCallPermission() {
        // 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)){
            // 向用户详细解释申请该权限的原因
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("拨打电话需要使用电话权限,如果不授予权限会导致该功能无法正常使用")
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},
                                    REQUEST_CALLPHONE//注意这个REQUEST_CALLPHONE会传给回调函数的requestCode
                            );
                        }
                    })
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALLPHONE);
        }
    }

    /**
     * requestPermissions的回调函数
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALLPHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 授予权限，拨打电话
                makeCall();
            } else {
                Toast.makeText(this, "请求权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:123456"));
        startActivity(intent);
    }
}