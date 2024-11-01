package com.example.UNIVERSE.ui.QnA

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.UNIVERSE.databinding.ActivityNewPostBinding
import com.example.UNIVERSE.model.Post
import com.example.UNIVERSE.model.User
import com.example.UNIVERSE.utils.POSTS_NODE
import com.example.UNIVERSE.utils.USER_NODE
import com.example.UNIVERSE.utils.USER_POSTS_FOLDER
import com.example.UNIVERSE.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class NewPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewPostBinding
    private var postImageUrl: String? = null
    private var userName: String? = null
    private var userEmail: String? = null
    private var userProfilePicture: String? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_POSTS_FOLDER) { url ->
                if (url != null) {
                    binding.newPostImageView.setImageURI(uri)
                    binding.newPostImageView.visibility = View.VISIBLE
                    postImageUrl = url
                } else {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Fetch current user's profile from Firestore
        fetchUserProfile()

        binding.postButton.setOnClickListener {
            if (postImageUrl != null) {
                createPost()  // Create a post with the user's data
            } else {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun fetchUserProfile() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection(USER_NODE).document(currentUserUid).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    userName = user.name
                    userEmail = user.email
                    userProfilePicture = user.image

                    // Load the profile picture directly into the ImageView
                    if (!userProfilePicture.isNullOrEmpty()) {
                        Picasso.get().load(userProfilePicture).into(binding.circleImageViewProfileNewPost)
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
                Toast.makeText(this, "Failed to load user profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createPost() {
        // Check if required fields are null
        if (userName.isNullOrEmpty() || userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User profile is not loaded yet", Toast.LENGTH_SHORT).show()
            return
        }

        if (postImageUrl == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val post = Post(
            postUrl = postImageUrl!!,
            caption = binding.captionNewPost.text.toString(),
            userName = userName,
            userEmail = userEmail,
            userProfilePicture = userProfilePicture,
            timestamp = System.currentTimeMillis() // Set the current time as timestamp
        )

        FirebaseFirestore.getInstance().collection(POSTS_NODE).document().set(post)
            .addOnSuccessListener {
                // Post uploaded successfully
                Toast.makeText(this, "Post uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                // Handle error
                Toast.makeText(this, "Failed to upload post", Toast.LENGTH_SHORT).show()
            }
    }

}
