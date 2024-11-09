package com.example.universe.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.universe.R
import com.example.universe.databinding.ActivitySettingsBinding
import com.example.universe.ui.login.LoginActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        loadUserData()

        binding.backBtnSettings.setOnClickListener {
            finish()
        }
        binding.logoutCl.setOnClickListener {

            showLogoutDialog(this){
                logout()
            }

        }

        binding.preferencesCL.setOnClickListener {
            preferenceActivity()
        }
    }

    private fun preferenceActivity() {
        val intent = Intent(this,PreferencesActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        // Clear shared preferences
        this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()

        // Sign out from Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Navigate to LoginActivity
        val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val profileImageUrl = sharedPreferences.getString("profile_image_url", "")
        val userName = sharedPreferences.getString("user_name", "Unknown Name")
        val userEmail = sharedPreferences.getString("user_email", "Unknown Email")

        // Show Lottie animation initially
        binding.lottieLoader.visibility = View.VISIBLE
        binding.profileImageSettings.visibility = View.INVISIBLE

        // Set user name and email
        binding.nameTvSettings.text = userName
        binding.settingsEmailTV.text = userEmail

        if (!profileImageUrl.isNullOrEmpty()) {
            Picasso.get().load(profileImageUrl)
                .into(binding.profileImageSettings, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        // Hide the Lottie animation on success
                        binding.lottieLoader.visibility = View.GONE
                        binding.profileImageSettings.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        // Hide Lottie and show a default image in case of error
                        binding.lottieLoader.visibility = View.GONE
                        binding.profileImageSettings.setImageResource(R.drawable.avatar)
                        binding.profileImageSettings.visibility = View.VISIBLE
                    }
                })
        } else {
            // Set default image if URL is empty
            binding.lottieLoader.visibility = View.GONE
            binding.profileImageSettings.setImageResource(R.drawable.avatar)
            binding.profileImageSettings.visibility = View.VISIBLE
        }
    }


}