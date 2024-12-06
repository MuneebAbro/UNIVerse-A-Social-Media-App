package com.example.universe.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.universe.R
import com.example.universe.adapter.PostCardAdapter
import com.example.universe.databinding.FragmentPostBinding
import com.example.universe.model.Post
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.LoginActivity
import com.example.universe.ui.settings.SettingsActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.example.universe.utils.POSTS_NODE
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.Locale

class PostFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener  {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var navHeaderImage: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var navHeaderUsername: com.google.android.material.textview.MaterialTextView
    private lateinit var navHeaderEmail: com.google.android.material.textview.MaterialTextView
    private lateinit var navImageLoader: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navigationView: NavigationView = binding.navigationView

        val headerView = navigationView.getHeaderView(0)
        navHeaderImage = headerView.findViewById(R.id.nav_header_image)
        navHeaderUsername = headerView.findViewById(R.id.nav_header_username)
        navHeaderEmail = headerView.findViewById(R.id.nav_header_email)
        navImageLoader = headerView.findViewById(R.id.navImageLoader)
        // Set the listener for navigation item clicks
        navigationView.setNavigationItemSelectedListener(this)

        val recyclerView: RecyclerView = binding.recyclerViewPost
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch posts from Firestore
        fetchPosts(recyclerView)

        // Observe profile image URL from SharedViewModel
        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            loadData(imageUrl)
        }

        // Load image directly from SharedPreferences as a fallback
        loadImageFromPrefs()


        binding.PostSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.UserImagePostTopBar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return root
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.nav_logout -> {

                showLogoutDialog(requireContext()){
                    logout()
                }
                return true
            }

            R.id.nav_settings -> {

                val intent = Intent(requireContext(), SettingsActivity::class.java)
                startActivity(intent)
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


    private fun fetchPosts(recyclerView: RecyclerView) {
        firestore.collection(POSTS_NODE)
            .get()
            .addOnSuccessListener { querySnapshot ->

                val posts = querySnapshot.toObjects(Post::class.java)
                val sortedPosts = posts.sortedByDescending { it.timestamp }
                val adapter = PostCardAdapter(sortedPosts)
                
                recyclerView.adapter = adapter

            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData(imageUrl: String?) {
        // Show the Lottie loader
        binding.profileImageLoader.visibility = View.VISIBLE

        // Load the image using Picasso
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(binding.UserImagePostTopBar, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    // Hide the loader on success
                    binding.profileImageLoader.visibility = View.GONE
                }


                override fun onError(e: Exception?) {
                    // Hide the loader on error
                    binding.profileImageLoader.visibility = View.GONE
                    Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            })


            Picasso.get().load(imageUrl).into(navHeaderImage)
        } else {
            // Hide the loader if the URL is empty
            binding.profileImageLoader.visibility = View.GONE
        }
    }


    private fun loadImageFromPrefs()
    {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("profile_image_url", null)

        val userUsername = sharedPreferences.getString("user_username",
                                                       sharedPreferences.getString("user_name", "Unknown Name")
                                                               ?.replace(" ", "_")
                                                               ?.lowercase(Locale.ROOT)
        )

        val userName = sharedPreferences.getString("user_name", "Unknown Name")

        // Set the username to the TextView
       navHeaderEmail.text = userUsername
        navHeaderUsername.text = userName
        loadData(imageUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
