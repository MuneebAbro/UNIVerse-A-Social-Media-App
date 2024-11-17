package com.example.universe.ui.profile

import android.content.Context
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
import com.example.universe.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

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
        postAdapter = PostCardAdapter(emptyList()){

        }
        binding.profileRV.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Observe user data (profile image, name, email)
        observeUserData()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        fetchPosts(userEmail = currentUserEmail!!)


        binding.editProfileBtn.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)

            activity?.startActivity(intent)
        }

        return root
    }

    private fun observeUserData() {
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val profileImage = sharedPreferences.getString("profile_image_url", "")
        binding.lottieLoaderProfile.visibility = View.VISIBLE
        binding.circleProfileImage.visibility = View.INVISIBLE

        if (!profileImage.isNullOrEmpty()) { // Load image only if URL is not null or empty
            Picasso.get().load(profileImage)
                    .error(R.drawable.avatar) // Fallback image in case of error
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
            // Handle the case where no URL exists
            binding.lottieLoaderProfile.visibility = View.INVISIBLE
            binding.circleProfileImage.setImageResource(R.drawable.avatar)
            binding.circleProfileImage.visibility = View.VISIBLE
        }



        val userUsername = sharedPreferences.getString("user_username",
                                                       sharedPreferences.getString("user_name", "Unknown Name")
                                                               ?.replace(" ", "_")
                                                               ?.lowercase(Locale.ROOT)
        )
        val userName = sharedPreferences.getString("user_name", "Unknown Name")



        val userBio = sharedPreferences.getString("user_bio", "No Bio Yet")
        val userGender = sharedPreferences.getString("user_gender", "Male")?.trim() ?: "Male"

        val userCity = sharedPreferences.getString("user_city", "Unknown City")
        binding.locationTV.text = userCity

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        if (userUid != null) {
            val userDocument = FirebaseFirestore.getInstance()
                    .collection(
                            USER_NODE)
                    .document(userUid)

            userDocument.get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val userDob = document.getString("dob") // Ensure "dob" is the exact field name in Firestore
                            if (!userDob.isNullOrEmpty()) {
                                binding.bdayTV.text = userDob
                                binding.bdayLL.visibility = View.VISIBLE
                            } else {
                                binding.bdayLL.visibility = View.GONE
                                binding.bdayTV.text = ""
                            }
                        } else {
                            Toast.makeText(requireContext(), "No document found!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Error retrieving data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
        } else {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
        }





        binding.textView7.text = userBio
        if (userGender.equals("Male", ignoreCase = true)) { // Case-insensitive comparison
            binding.gender.text = "he/him"
        } else {
            binding.gender.text = "she/her"
        }

        // Set the username to the TextView
        binding.profileUserName.text = userUsername
        binding.nameTvProfile.text = userName


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
                                val intent = Intent(requireContext(), CommentsActivity::class.java)
                                intent.putExtra("post_data", post)
                                startActivity(intent)
                            }
                            binding.profileRV.adapter = adapter
                        } else {
                            Toast.makeText(requireContext(), "No posts available", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Log.e("UserProfile", "Failed to fetch posts", it)
                        Toast.makeText(requireContext(), "Failed to fetch posts", Toast.LENGTH_SHORT).show()
                    }
        } else {
            Toast.makeText(requireContext(), "Invalid email", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
