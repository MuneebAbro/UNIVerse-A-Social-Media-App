package com.example.universe.ui.profile

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.universe.R
import com.example.universe.databinding.ActivityEditProfileBinding
import com.example.universe.model.User
import com.example.universe.utils.USER_NODE
import com.example.universe.utils.USER_PROFILE_FOLDER
import com.example.universe.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.Calendar

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var user: User
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) { imageUrl ->
                if (imageUrl == null) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                } else {
                    user.image = imageUrl
                    binding.imageEditProfile.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        user = User()
        val firestore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        val sharedPreferences =
            this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val profileImage = sharedPreferences.getString("profile_image_url", "")
        binding.editProfileLoader.visibility = View.VISIBLE
        binding.imageEditProfile.visibility = View.INVISIBLE

        if (!profileImage.isNullOrEmpty()) { // Load image only if URL is not null or empty
            Picasso.get().load(profileImage)
                    .error(R.drawable.avatar) // Fallback image in case of error
                    .into(binding.imageEditProfile, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            binding.editProfileLoader.visibility = View.INVISIBLE
                            binding.imageEditProfile.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            binding.editProfileLoader.visibility = View.INVISIBLE
                            binding.imageEditProfile.setImageResource(R.drawable.avatar)
                            binding.imageEditProfile.visibility = View.VISIBLE
                        }
                    })
        } else {
            // Handle the case where no URL exists
            binding.editProfileLoader.visibility = View.INVISIBLE
            binding.imageEditProfile.setImageResource(R.drawable.avatar)
            binding.imageEditProfile.visibility = View.VISIBLE
        }



        binding.birthdayEditText.setOnClickListener {
            showDatePicker(binding.birthdayEditText)
        }


        // Pre-fill fields with existing user data
        currentUser?.let {
            firestore.collection(USER_NODE).document(it.uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val userName = document.getString("name") ?: ""
                            val userUsername = document.getString("username") ?: ""
                            val userBio = document.getString("bio") ?: ""
                            val userGender = document.getString("gender") ?: "Male"
                            val userImage = document.getString("image") // Fetch the existing image URL
                            val dob = document.getString("dob")
                            val city = document.getString("city")

                            // Set user.image to the fetched image URL
                            user.image = userImage

                            // Set the fetched values to the respective EditTexts
                            binding.nameEditText.editText?.setText(userName) // Update name field
                            binding.userNameEditText.editText?.setText(userUsername) // Update username field
                            binding.bioEditText.editText?.setText(userBio)
                            binding.birthdayEditText.text = dob
                            binding.cityEditText.editText?.setText(city)

                            // Select the corresponding radio button based on saved gender
                            if (userGender == "Male") {
                                binding.radioMale.isChecked = true
                            } else if (userGender == "Female") {
                                binding.radioFemale.isChecked = true
                            }

                            // Load the profile image if available
                            userImage?.let {
                                Picasso.get().load(it)
                                        .error(R.drawable.avatar) // Fallback image
                                        .into(binding.imageEditProfile)
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }

        }



        binding.imageEditProfile.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Save changes
        binding.saveBtn.setOnClickListener {
            val bio = binding.bioEditText.editText?.text.toString()

            // Get selected gender
            val image = user.image
            val dob = binding.birthdayEditText.text.toString()
            val selectedGenderId = binding.radioGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) {
                findViewById<RadioButton>(selectedGenderId).text.toString()
            } else {
                "Male" // Default value
            }
            var city = binding.cityEditText.editText?.text.toString()
            if (!city.endsWith(", Pakistan")) {
                city += ", Pakistan"
            }

            // Save data to Firestore
            currentUser?.let {
                val userId = it.uid
                val updates = mapOf(
                        "bio" to bio,
                        "gender" to gender,
                        "image" to image,
                        "dob" to dob,
                        "city" to city
                )

                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putString("profile_image_url", user.image)
                    putString("user_name", user.name)
                    putString("user_email", user.email)
                    putString("user_username", user.username)
                    putString("user_bio", user.bio)
                    putString("user_gender", user.gender)
                    putString("user_dob", user.dob)
                    putString("user_city", user.city)
                    apply()
                }

                firestore.collection(USER_NODE).document(userId)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after saving
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
            }
        }
    }
    private fun showDatePicker(editText: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    editText.text = formattedDate // Directly assign the formatted string
                    editText.setTextColor(getColorStateList(R.color.adaptive_black))

                },
                year,
                month,
                day
        )
        datePickerDialog.show()
    }

}
