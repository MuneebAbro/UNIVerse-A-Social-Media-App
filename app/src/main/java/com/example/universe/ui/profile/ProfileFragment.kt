package com.example.universe.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universe.R
import com.example.universe.adapter.PostCardAdapter
import com.example.universe.databinding.FragmentProfileBinding
import com.example.universe.model.Post
import com.example.universe.model.SharedViewModel
import com.example.universe.ui.login.SignUpActivity
import com.example.universe.ui.post.CommentsActivity
import com.example.universe.utils.POSTS_NODE
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var postAdapter: PostCardAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        postAdapter = PostCardAdapter(emptyList()) { post ->
            // Handle post click if needed
        }
        binding.profileRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Observe user data (profile image, name, email)
        observeUserData()
        fetchPosts(binding.profileRV)


        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(activity, SignUpActivity::class.java)
            intent.putExtra("MODE", 1)
            activity?.startActivity(intent)
        }

        return root
    }

    private fun observeUserData() {
        binding.lottieLoaderProfile.visibility = View.VISIBLE
        binding.circleProfileImage.visibility = View.INVISIBLE

        sharedViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).error(R.drawable.avatar)
                        .into(binding.circleProfileImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                binding.lottieLoaderProfile.visibility = View.INVISIBLE
                                binding.circleProfileImage.visibility = View.VISIBLE
                            }

                            override fun onError(e: Exception?) {
                                binding.lottieLoaderProfile.visibility = View.INVISIBLE
                                binding.circleProfileImage.setImageResource(R.drawable.avatar)
                                binding.circleProfileImage.visibility = View.VISIBLE
                            }
                        })
            } else {
                binding.lottieLoaderProfile.visibility = View.INVISIBLE
                binding.circleProfileImage.setImageResource(R.drawable.avatar)
                binding.circleProfileImage.visibility = View.VISIBLE
            }
        }

        sharedViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.nameTvProfile.text = name ?: "Unknown Name"
        }

        sharedViewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.profileEmailTV.text = email ?: "Unknown Email"
        }
    }

    private fun fetchPosts(recyclerView: RecyclerView) {
        // Get the current user's email (assuming it's stored in SharedViewModel)
        sharedViewModel.userEmail.observe(viewLifecycleOwner) { userEmail ->
            if (!userEmail.isNullOrEmpty()) {
                // Query to get posts where the user's email matches
                firestore.collection(POSTS_NODE)
                        .whereEqualTo("userEmail", userEmail)  // Filter posts by userEmail
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
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
