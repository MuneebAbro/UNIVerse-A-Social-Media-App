package com.example.universe.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        // Load post image and caption
        holder.postCaption.text = post.caption

        // Load post image using Picasso
        Picasso.get()
            .load(post.postUrl)
            .placeholder(R.drawable.dummy) // Placeholder for post image
            .error(R.drawable.dummy) // Error image if loading fails
            .into(holder.postImage)

        // Load user profile picture using Picasso
        Picasso.get()
            .load(post.userProfilePicture)
            .placeholder(R.drawable.avatar) // Placeholder for profile picture
            .error(R.drawable.avatar) // Error profile picture if loading fails
            .into(holder.userProfilePicture)

        // Set user name and email
        holder.timeStamp.text = TimeAgo.using(post.timestamp)

        holder.userName.text = post.userName // Make sure this field is bound to the layout

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
        val userName: TextView = itemView.findViewById(R.id.NameTvQna) // Reference the user name TextView
    }
}
