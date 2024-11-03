package com.example.universe.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.universe.R
import com.example.universe.model.Post
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.squareup.picasso.Picasso


class PostCardAdapter(
    private val posts: List<Post>,
    private val onClick: (Post) -> Unit
) : RecyclerView.Adapter<PostCardAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_qna, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Set caption
        holder.postCaption.text = post.caption

        // Show the Lottie loader initially
        holder.lottieLoaderPostImage.visibility = View.VISIBLE
        holder.postImage.visibility = View.INVISIBLE

        // Load post image using Picasso
        Picasso.get()
            .load(post.postUrl)
            .placeholder(R.drawable.placeholder_image_drawable)
            .error(R.drawable.placeholder_image_drawable)
            .into(holder.postImage, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    // Hide the loader once the image is loaded successfully
                    holder.lottieLoaderPostImage.visibility = View.GONE
                    holder.postImage.visibility = View.VISIBLE
                }

                override fun onError(e: Exception?) {
                    // Hide the loader and show the placeholder if loading fails
                    holder.lottieLoaderPostImage.visibility = View.GONE
                    holder.postImage.setImageResource(R.drawable.placeholder_image_drawable)
                    holder.postImage.visibility = View.VISIBLE
                }
            })

        // Load user profile picture
        Picasso.get()
            .load(post.userProfilePicture)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(holder.userProfilePicture)

        // Set user name and timestamp
        holder.userName.text = post.userName
        holder.timeStamp.text = TimeAgo.using(post.timestamp)

        // Set click listener for each post
        holder.itemView.setOnClickListener {
            onClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.QnaImageView)
        val postCaption: TextView = itemView.findViewById(R.id.textQna)
        val userProfilePicture: ImageView = itemView.findViewById(R.id.profileImageUserQna)
        val timeStamp: TextView = itemView.findViewById(R.id.TimeStampTV)
        val userName: TextView = itemView.findViewById(R.id.NameTvQna)
        val lottieLoaderPostImage: LottieAnimationView = itemView.findViewById(R.id.lottieLoaderPostImage) // Reference Lottie loader
    }
}
