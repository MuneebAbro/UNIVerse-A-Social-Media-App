package com.example.universe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universe.R
import com.example.universe.data.Comment


class CommentAdapter(
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImageUserComment)
        val nameTextView: TextView = itemView.findViewById(R.id.NameTvComment)
        val usernameTextView: TextView = itemView.findViewById(R.id.UserNameTvComment)
        val commentTextView: TextView = itemView.findViewById(R.id.textComment)

        fun bind(comment: Comment) {
            profileImage.setImageResource(comment.profileImageResId)
            nameTextView.text = comment.name
            usernameTextView.text = comment.username
            commentTextView.text = comment.commentText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)  // Assuming your layout file is item_comment.xml
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size
}
