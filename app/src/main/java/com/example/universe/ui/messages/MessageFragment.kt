package com.example.universe.ui.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.universe.R
import com.example.universe.databinding.FragmentMessageBinding
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.LoginActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MessageFragment : Fragment(){

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        loadUserData()



        // Observe SharedViewModel for the image URL and set it in top bar and navigation drawer
        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(binding.UserImageTopBar)

            }
        }


        return root
    }

    private fun loadUserData() {
        val (cachedImageUrl) = getUserData()
        if (!cachedImageUrl.isNullOrEmpty()) {
            Picasso.get().load(cachedImageUrl).into(binding.UserImageTopBar)
        }
    }

    private fun getUserData(): Triple<String?, String?, String?> {
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
