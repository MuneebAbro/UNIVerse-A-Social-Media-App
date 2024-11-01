package com.example.UNIVERSE.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.UNIVERSE.MainActivity
import com.example.UNIVERSE.databinding.ActivityLoginBinding
import com.example.UNIVERSE.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
                    .isEmpty() or binding.loginPasswordEditText.editText?.text.toString().isEmpty()
            ) {
                binding.loginEmailEditText.error = "Please enter email"
                binding.loginPasswordEditText.error = "Please enter password"
            } else {
                var user: User = User(
                    binding.loginEmailEditText.editText?.text.toString(),
                    binding.loginPasswordEditText.editText?.text.toString()
                )

                Firebase.auth.signInWithEmailAndPassword(
                    binding.loginEmailEditText.editText?.text.toString(),
                    binding.loginPasswordEditText.editText?.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity,"Something went wrong",Toast.LENGTH_SHORT).show()
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
}