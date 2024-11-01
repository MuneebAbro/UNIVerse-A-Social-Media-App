package com.example.UNIVERSE.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.UNIVERSE.MainActivity
import com.example.UNIVERSE.R
import com.example.UNIVERSE.ui.login.LoginActivity
import com.example.UNIVERSE.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.wasabeef.glide.transformations.BlurTransformation

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val splashBackground = findViewById<ImageView>(R.id.splash_background)
        // Apply blur effect to background image using Glide
        Glide.with(this)
            .load(R.drawable.circle)  // Replace with your actual drawable or image URL
            .transform(BlurTransformation(40, 20))  // Adjust blur level as needed
            .into(splashBackground)

        // Check authentication and fetch user data
        checkAuthenticationAndFetchData()
    }

    private fun checkAuthenticationAndFetchData() {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            // User is not logged in, navigate to LoginActivity
            navigateToLogin()
        } else {
            // User is logged in, fetch user data from Firestore
            fetchUserData(userId)
        }
    }

    private fun fetchUserData(userId: String) {
        FirebaseFirestore.getInstance().collection(USER_NODE)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // User data exists, save it to SharedPreferences
                    val name = document.getString("name") ?: "Unknown Name"
                    val email = document.getString("email") ?: "Unknown Email"
                    val imageUrl = document.getString("image") ?: ""

                    saveUserData(name, email, imageUrl)  // Save data locally

                    // Navigate to MainActivity
                    navigateToMain()
                } else {
                    // User data does not exist, navigate to LoginActivity
                    navigateToLogin()
                }
            }
            .addOnFailureListener {
                // If fetching user data fails, navigate to LoginActivity
                navigateToLogin()
            }
    }

    private fun saveUserData(name: String, email: String, imageUrl: String) {
        sharedPreferences.edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("profile_image_url", imageUrl)
            .apply()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
