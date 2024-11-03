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
import com.example.universe.R
import com.example.universe.adapter.PostCardAdapter
import com.example.universe.databinding.FragmentPostBinding
import com.example.universe.model.Post
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.LoginActivity
import com.example.universe.utils.DialogUtils.showLogoutDialog
import com.example.universe.utils.POSTS_NODE
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener  {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var navHeaderImage: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var navHeaderUsername: com.google.android.material.textview.MaterialTextView
    private lateinit var navHeaderEmail: com.google.android.material.textview.MaterialTextView

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
        loadUserData()
        // Set the listener for navigation item clicks
        navigationView.setNavigationItemSelectedListener(this)

        val recyclerView: RecyclerView = binding.recyclerViewPost
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch posts from Firestore
        fetchPosts(recyclerView)

        // Observe profile image URL from SharedViewModel
        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            loadImage(imageUrl)
        }

        // Load image directly from SharedPreferences as a fallback
        loadImageFromPrefs()

        // Handle FAB click for creating a new post
        binding.fabPost.setOnClickListener {
            val intent = Intent(context, NewPostActivity::class.java)
            startActivity(intent)
        }

        binding.UserImagePostTopBar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return root
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

    private fun loadUserData() {
        val (cachedImageUrl, cachedName, cachedEmail) = getUserData()
        if (!cachedImageUrl.isNullOrEmpty()) {
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

    private fun fetchPosts(recyclerView: RecyclerView) {
        firestore.collection(POSTS_NODE)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val posts = querySnapshot.toObjects(Post::class.java)
                val adapter = PostCardAdapter(posts) { post ->
                    val intent = Intent(context, CommentsActivity::class.java)
                    intent.putExtra("post_data", post)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadImage(imageUrl: String?) {
        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(binding.UserImagePostTopBar)
        }
    }

    private fun loadImageFromPrefs() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val imageUrl = sharedPreferences.getString("profile_image_url", null)
        loadImage(imageUrl)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
