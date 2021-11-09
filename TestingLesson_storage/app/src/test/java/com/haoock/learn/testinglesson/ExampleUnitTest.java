package com.haoock.learn.testinglesson;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        SimpleDateFormat mFormat = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss ", Locale.ENGLISH);//日期格式化
        SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE");//日期格式化 星期几
        assertEquals(4, 2 + 2);
        long currentTimeMillis = System.currentTimeMillis();//获取系统时间 long类型
        Date mDate = new Date(currentTimeMillis);//把获取到的时间值转为时间格式
        System.out.println(mFormat.format(mDate));//把时间格式按我们自定义的格式转换
    }
}