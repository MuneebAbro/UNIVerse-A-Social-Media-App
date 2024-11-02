package com.example.universe.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universe.adapter.PostCardAdapter
import com.example.universe.databinding.FragmentPostBinding
import com.example.universe.model.Post
import com.example.universe.model.SharedViewModel
import com.example.universe.utils.POSTS_NODE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

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

        return root
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
