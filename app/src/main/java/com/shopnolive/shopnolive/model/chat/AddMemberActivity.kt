package com.shopnolive.shopnolive.model.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shopnolive.shopnolive.databinding.ActivityAddMemberBinding

class AddMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}