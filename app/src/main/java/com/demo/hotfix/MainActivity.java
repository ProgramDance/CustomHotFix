package com.demo.hotfix;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;
import dalvik.system.DexClassLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tag", "MainActivity oncreate");
    }

    public void doRun(View view) {
        int i = 10 / 0;
        Log.d("tag", "doRun>>result:" + i);
        Toast.makeText(this, "计算结果是：" + i, Toast.LENGTH_SHORT).show();
    }
}





