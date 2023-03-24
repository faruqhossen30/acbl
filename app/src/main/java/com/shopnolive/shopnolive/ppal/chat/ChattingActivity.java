package com.shopnolive.shopnolive.ppal.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.calling.CallingScreenActivity;

public class ChattingActivity extends AppCompatActivity {

    private LinearLayout audioCall,videoCall;
    private RecyclerView recyclerViewMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        //find xml
        audioCall=findViewById(R.id.audioCall);
        videoCall=findViewById(R.id.videoCall);
        recyclerViewMsg=findViewById(R.id.rv_msg);

        actions();
    }

    private void actions() {
        audioCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CallingScreenActivity.class);
                startActivity(intent);
            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CallingScreenActivity.class);
                startActivity(intent);
            }
        });
    }
}