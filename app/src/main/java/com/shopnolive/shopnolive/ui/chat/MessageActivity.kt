package com.shopnolive.shopnolive.ui.chat

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.shopnolive.shopnolive.databinding.ActivityMessageBinding

class MessageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        setSupportActionBar(binding.toolbarLayout.toolbar)
        binding.toolbarLayout.toolbar.title = "Chat"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}