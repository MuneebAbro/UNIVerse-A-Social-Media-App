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
        // Show loader initially
        binding.lottieLoaderProfile?.visibility = View.VISIBLE
        binding.circleProfileImage.visibility = View.INVISIBLE

        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).error(R.drawable.avatar)
                    .into(binding.circleProfileImage, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            // Hide the loader and show the image
                            binding.lottieLoaderProfile?.visibility = View.INVISIBLE
                            binding.circleProfileImage.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            // Hide loader and show a placeholder image if there's an error
                            binding.lottieLoaderProfile?.visibility = View.INVISIBLE
                            binding.circleProfileImage.setImageResource(R.drawable.avatar)
                            binding.circleProfileImage.visibility = View.VISIBLE
                        }
                    })
            } else {
                // Hide loader and set a placeholder if imageUrl is empty
                binding.lottieLoaderProfile?.visibility = View.INVISIBLE
                binding.circleProfileImage.setImageResource(R.drawable.avatar)
                binding.circleProfileImage.visibility = View.VISIBLE
            }
        }

        // Observe user name and email as before
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
