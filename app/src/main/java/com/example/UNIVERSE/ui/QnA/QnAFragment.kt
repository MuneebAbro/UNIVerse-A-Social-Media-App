package com.example.UNIVERSE.ui.QnA

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
import com.example.UNIVERSE.adapter.PostCardAdapter
import com.example.UNIVERSE.databinding.FragmentQnaBinding
import com.example.UNIVERSE.model.Post
import com.example.UNIVERSE.model.SharedViewModel
import com.example.UNIVERSE.utils.POSTS_NODE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class QnAFragment : Fragment() {

    private var _binding: FragmentQnaBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQnaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.recyclerViewQnA
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch posts from Firestore
        fetchPosts(recyclerView)

        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->       // this will get the image from the Shared View Model and set it to the user image
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(binding.UserImageQnaTopBar)
            }
        }

        // Handle FAB click for creating a new post
        binding.fabQna.setOnClickListener {
            val intent = Intent(context, NewPostActivity::class.java)
            startActivity(intent)
        }

        binding.UserImageQnaTopBar

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
                // Handle the error (log or show a message)
                Toast.makeText(context, "Failed to fetch posts", Toast.LENGTH_SHORT).show()
            }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
