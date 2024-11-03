package com.example.universe.ui.profile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.universe.R
import com.example.universe.databinding.FragmentProfileBinding
import com.example.universe.ui.login.SignUpActivity
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.LoginActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.google.firebase.auth.FirebaseAuth
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

        // Observe user data from SharedViewModel
        observeUserData()

        // Clear data on "Edit Profile" button click
        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            activity?.startActivity(intent)
        }

        return root
    }

    private fun observeUserData() {
        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(binding.circleProfileImage)
            }
        }

        sharedViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.nameTvProfile.text = name ?: "Unknown Name"
        }

        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.profileEmailTV.text = email ?: "Unknown Email"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
