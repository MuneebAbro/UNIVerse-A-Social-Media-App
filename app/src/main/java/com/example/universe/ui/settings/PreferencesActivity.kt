package com.example.universe.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.universe.R
import com.example.universe.databinding.ActivityPreferencesBinding
import com.example.universe.ui.login.SignUpActivity

class PreferencesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreferencesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnPreferences.setOnClickListener {
            finish()
        }
        binding.accountInfoCL.setOnClickListener {
            val intent = Intent(this@PreferencesActivity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            startActivity(intent)
        }

        binding.passwordCL.setOnClickListener {
            val intent = Intent(this@PreferencesActivity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            startActivity(intent)
        }

    }
}