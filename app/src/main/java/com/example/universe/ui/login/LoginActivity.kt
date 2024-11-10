package com.example.universe.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.universe.MainActivity
import com.example.universe.databinding.ActivityLoginBinding
import com.example.universe.model.User
import com.example.universe.utils.USER_NODE
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.forgetPasswordTV.setOnClickListener {
            val intent = Intent(this, PasswordResetActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLogin.setOnClickListener {
            if (binding.loginEmailEditText.editText?.text.toString()
                        .isEmpty() || binding.loginPasswordEditText.editText?.text.toString().isEmpty()
            ) {
                binding.loginEmailEditText.error = "Please enter email"
                binding.loginPasswordEditText.error = "Please enter password"
            } else {
                Firebase.auth.signInWithEmailAndPassword(
                        binding.loginEmailEditText.editText?.text.toString(),
                        binding.loginPasswordEditText.editText?.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Fetch user data from Firestore
                        Firebase.firestore.collection(USER_NODE)
                                .document(Firebase.auth.currentUser!!.uid)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val user = documentSnapshot.toObject(User::class.java)
                                    if (user != null) {
                                        // Save the user data to SharedPreferences
                                        saveUserDataToSharedPreferences(user)
                                    }

                                    // Navigate to MainActivity
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                                }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.materialCardView2.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.SignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveUserDataToSharedPreferences(user: User) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("profile_image_url", user.image)
            putString("user_name", user.name)
            putString("user_email", user.email)
            apply()
        }
    }

}