package com.shopnolive.shopnolive.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.databinding.ActivityBannedBinding

class BannedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBannedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val message=getIntent().getStringExtra("message").toString();

        Log.d("BANNED",message);

        val textView: TextView = findViewById(R.id.message) as TextView
        if(message.isEmpty()){

        }
        else
        {
            textView.text = message

        }

        binding.okBtn.setOnClickListener {
            finish()
        }

    }
}