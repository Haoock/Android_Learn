package com.haoock.learn.testinglesson;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Activity_add extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        dbHelper = new MyDatabaseHelper(this, "TodoList.db",null, 1);
        button = findViewById(R.id.button_submit);
        editText = findViewById(R.id.edittext_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                SimpleDateFormat mFormat = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss ", Locale.ENGLISH);//日期格式化
                long currentTimeMillis = System.currentTimeMillis();//获取系统时间 long类型
                Date mDate = new Date(currentTimeMillis);//把获取到的时间值转为时间格式
                String time = mFormat.format(mDate);//把时间格式按我们自定义的格式转换
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("content", content);
                values.put("dateTime", time);
                values.put("state", "0");
                db.insert("Table_List",null,values);//插入数据
                values.clear();
                Intent intent = new Intent(Activity_add.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}