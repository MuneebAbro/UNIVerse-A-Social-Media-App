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

class MessageFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var navHeaderImage: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var navHeaderUsername: com.google.android.material.textview.MaterialTextView
    private lateinit var navHeaderEmail: com.google.android.material.textview.MaterialTextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup DrawerLayout and NavigationView
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView




        // Get references to the Navigation View header components
        val headerView = navigationView.getHeaderView(0)
        navHeaderImage = headerView.findViewById(R.id.nav_header_image)
        navHeaderUsername = headerView.findViewById(R.id.nav_header_username)
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email)
        loadUserData()
        // Set the listener for navigation item clicks
        navigationView.setNavigationItemSelectedListener(this)


        // Observe SharedViewModel for the image URL and set it in top bar and navigation drawer
        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(binding.UserImageTopBar)
                Picasso.get().load(imageUrl).into(navHeaderImage)
            }
        }

        sharedViewModel.userName.observe(viewLifecycleOwner) { name ->
            navHeaderUsername.text = name
        }

        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            navHeaderEmail.text = email
        }


        // Open Drawer when HomeLogoLL is clicked
        binding.HomeLogoLL.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return root
    }

    private fun loadUserData() {
        val (cachedImageUrl, cachedName, cachedEmail) = getUserData()
        if (!cachedImageUrl.isNullOrEmpty()) {
            Picasso.get().load(cachedImageUrl).into(binding.UserImageTopBar)
            Picasso.get().load(cachedImageUrl).into(navHeaderImage)
        }
        navHeaderUsername.text = cachedName ?: ""
        navHeaderEmail.text = cachedEmail ?: ""
    }

    private fun getUserData(): Triple<String?, String?, String?> {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("profile_image_url", null)
        val name = sharedPreferences.getString("user_name", null)
        val email = sharedPreferences.getString("user_email", null)
        return Triple(imageUrl, name, email)
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                // Handle navigation item click
                // Example: Open profile screen or perform any other action
                return true
            }
            R.id.nav_logout -> {

                showLogoutDialog(requireContext()){
                    logout()
                }
                return true
            }
        }

        // Close the drawer after an item is selected
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        // Clear shared preferences
        requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).edit().clear().apply()

        // Sign out from Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Navigate to LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
