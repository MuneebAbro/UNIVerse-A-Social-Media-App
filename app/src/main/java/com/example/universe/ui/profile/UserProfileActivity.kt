package com.example.universe.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universe.R
import com.example.universe.adapter.PostCardAdapter
import com.example.universe.databinding.ActivityUserProfileBinding
import com.example.universe.model.Post
import com.example.universe.ui.post.CommentsActivity
import com.example.universe.utils.POSTS_NODE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.UserProfileRV.layoutManager = LinearLayoutManager(this)

        // Retrieve the passed data
        val userEmail = intent.getStringExtra("userEmail") ?: ""
        val name = intent.getStringExtra("userName") ?: ""
        val username = intent.getStringExtra("userUsername") ?: ""
        val imageUrl = intent.getStringExtra("userImageUrl") ?: ""

        binding.nameTvProfile.text = name
        binding.profileUserName.text = username
        Picasso.get().load(imageUrl).into(binding.circleProfileImage)

        // Fetch posts for the user
        fetchPosts(userEmail)
    }

    private fun fetchPosts(userEmail: String) {
        if (userEmail.isNotEmpty()) {
            Log.d("UserProfile", "Fetching posts for user: $userEmail")

            firestore.collection(POSTS_NODE)
                    .whereEqualTo("userEmail", userEmail)  // Ensure 'email' is the correct field name in Firestore
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val posts = querySnapshot.toObjects(Post::class.java)
                        Log.d("UserProfile", "Posts fetched: ${posts.size}")

                        if (posts.isNotEmpty()) {
                            val sortedPosts = posts.sortedByDescending { it.timestamp }
                            val adapter = PostCardAdapter(sortedPosts) { post ->
                                val intent = Intent(this@UserProfileActivity, CommentsActivity::class.java)
                                intent.putExtra("post_data", post)
                                startActivity(intent)
                            }
                            binding.UserProfileRV.adapter = adapter
                        } else {
                            Toast.makeText(this, "No posts available", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Log.e("UserProfile", "Failed to fetch posts", it)
                        Toast.makeText(this, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
                    }
        } else {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show()
        }
    }

}
