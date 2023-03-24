package com.shopnolive.shopnolive.statusCheck;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.util.Common;

public class StatusCheckActivity extends AppCompatActivity {

    private TextView ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.changeStatusBarColor(this);
        setContentView(R.layout.activity_status_check);

        //find xml
        ok = findViewById(R.id.ok_btn);
        ok.setOnClickListener(view -> finish());
    }
}