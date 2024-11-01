package com.example.UNIVERSE.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.UNIVERSE.MainActivity
import com.example.UNIVERSE.R
import com.example.UNIVERSE.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import jp.wasabeef.glide.transformations.BlurTransformation

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val splash_background = findViewById<ImageView>(R.id.splash_background)
        // Apply blur effect to background image using Glide
        Glide.with(this)
            .load(R.drawable.circle)  // Replace with your actual drawable or image URL
            .transform(BlurTransformation(40, 20))  // Adjust blur level as needed
            .into(splash_background)

        // Splash screen delay with navigation logic
        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null) {
                // If no user is logged in, navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // If the user is logged in, navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)  // Adjust delay as needed (e.g., 2 seconds)
    }
}
