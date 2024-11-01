package com.example.UNIVERSE.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.UNIVERSE.databinding.FragmentProfileBinding
import com.example.UNIVERSE.ui.login.SignUpActivity
import com.example.UNIVERSE.model.SharedViewModel
import com.example.UNIVERSE.model.User
import com.example.UNIVERSE.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Load cached user data
        loadCachedUserData()

        // Clear cache on "Edit Profile" button click
        binding.editProfileBtn.setOnClickListener {
            clearCachedProfileData()
            // Open EditProfile Activity
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            activity?.startActivity(intent)
        }

        return root
    }

    private fun clearCachedProfileData() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("profile_image_url").remove("user_name").remove("user_email").apply()
    }

    private fun loadCachedUserData() {
        val (cachedImageUrl, cachedName, cachedEmail) = getProfileData()

        // Load cached profile image if available
        if (!cachedImageUrl.isNullOrEmpty()) {
            Picasso.get().load(cachedImageUrl).into(binding.circleProfileImage)
        }

        // Load cached user name if available
        cachedName?.let {
            binding.nameTvProfile.text = it
        }

        // Load cached user email if available
        cachedEmail?.let {
            binding.profileEmailTV.text = it
        }

        // If no cached data, fetch from Firebase
        if (cachedImageUrl.isNullOrEmpty() || cachedName.isNullOrEmpty() || cachedEmail.isNullOrEmpty()) {
            fetchAndCacheUserData()
        }
    }

    private fun fetchAndCacheUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection(USER_NODE)
            .document(userId).get()
            .addOnSuccessListener { document ->
                val user: User? = document.toObject(User::class.java)
                if (user != null) {
                    // Ensure name and email are not null before using them
                    binding.nameTvProfile.text = user.name ?: "Unknown Name"
                    binding.profileEmailTV.text = user.email ?: "Unknown Email"

                    // Cache and load image if available
                    user.image?.let { imageUrl ->
                        saveProfileData(imageUrl, user.name ?: "Unknown Name", user.email ?: "Unknown Email")
                        Picasso.get().load(imageUrl).into(binding.circleProfileImage)
                    }
                }
            }
            .addOnFailureListener {
                // Handle error (e.g., show a Toast or log the error)
            }
    }

    private fun saveProfileData(imageUrl: String, name: String, email: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("profile_image_url", imageUrl)
            .putString("user_name", name)
            .putString("user_email", email)
            .apply()
    }

    private fun getProfileData(): Triple<String?, String?, String?> {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("profile_image_url", null)
        val name = sharedPreferences.getString("user_name", null)
        val email = sharedPreferences.getString("user_email", null)
        return Triple(imageUrl, name, email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
