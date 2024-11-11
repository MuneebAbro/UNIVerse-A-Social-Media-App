package com.example.universe.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.universe.MainActivity
import com.example.universe.R
import com.example.universe.databinding.ActivitySignUpBinding
import com.example.universe.model.User
import com.example.universe.utils.USER_NODE
import com.example.universe.utils.USER_PROFILE_FOLDER
import com.example.universe.utils.uploadImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: User
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) { imageUrl ->
                if (imageUrl == null) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                } else {
                    user.image = imageUrl
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        user = User()

        // Handle case for editing profile (Edit Profile Mode)
        if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
            binding.linearLayoutSignUp.visibility = View.GONE
            binding.signupLoader.visibility = View.GONE
            binding.profileImage.visibility = View.VISIBLE
            binding.emailEditText.editText?.isEnabled = false
            binding.signUpText.text = "Update Profile"
            binding.createAccountTVDummy.text = "Update Your \nAccount"

            // Fetch user data from Firestore for editing
            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        user = it.toObject(User::class.java)!!
                        binding.nameEditText.editText?.setText(user.name)
                        binding.emailEditText.editText?.setText(user.email)
                        binding.passwordEditText.editText?.setText(user.password)

                        // Load profile image if available
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }
                    }
        } else {
            // Handle case for new signup
            binding.linearLayoutSignUp.visibility = View.VISIBLE
            binding.signupLoader.visibility = View.GONE
            binding.profileImage.visibility = View.VISIBLE
        }

        // Login redirection
        binding.LoginTV.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Continue button click handler for both modes (sign up / edit)
        binding.continueBtn.setOnClickListener {
            if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
                // Update existing profile
                Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
            } else {
                // Normal sign up
                if (binding.nameEditText.editText?.text.toString().isEmpty() ||
                    binding.emailEditText.editText?.text.toString().isEmpty() ||
                    binding.passwordEditText.editText?.text.toString().isEmpty()) {

                    Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                } else if (!binding.emailEditText.editText?.text.toString().contains("iqra.edu.pk")) {
                    Toast.makeText(this, "Please use your IQRA email", Toast.LENGTH_SHORT).show()
                } else {
                    // Register new user
                    Firebase.auth.createUserWithEmailAndPassword(
                            binding.emailEditText.editText?.text.toString(),
                            binding.passwordEditText.editText?.text.toString()
                    ).addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            user.name = binding.nameEditText.editText?.text.toString()
                            user.email = binding.emailEditText.editText?.text.toString()
                            user.password = binding.passwordEditText.editText?.text.toString()

                            // Set username to the user's name with spaces replaced by underscores
                            user.username = user.name!!.replace(" ", "_")

                            // Save user data to Firestore
                            Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        saveUserDataToSharedPreferences()
                                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                                        // Redirect to the main activity
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                        } else {
                            Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.materialCardView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Open image picker
        binding.profileImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private fun saveUserDataToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("profile_image_url", user.image)
            putString("user_name", user.name)
            putString("user_email", user.email)
            putString("user_username", user.username)
            apply()
        }
    }
}
