package com.shopnolive.shopnolive.ppal.calling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.ppal.util.Common;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallingScreenActivity extends AppCompatActivity {

    ImageView acceptCall,rejectAudio;
    private CircleImageView userImage;
    private TextView userName,userPhoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.changeStatusBarColor(this);
        setContentView(R.layout.activity_calling_screen);
        //find xml
        rejectAudio=findViewById(R.id.img_cancel_call);
        acceptCall=findViewById(R.id.image_audio);
        userImage=findViewById(R.id.img_user);
        userName=findViewById(R.id.user_name);
        userPhoneNo=findViewById(R.id.user_ph_no);

        //actions
        initActions();
    }

    private void initActions() {
        acceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AudioCallActivity.class);
                startActivity(intent);

            }
        });

        rejectAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}