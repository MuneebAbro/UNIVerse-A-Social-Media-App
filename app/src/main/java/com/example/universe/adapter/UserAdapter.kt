package com.example.universe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.universe.R
import com.example.universe.model.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val onUserClick: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val users = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view, onUserClick)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View, private val onUserClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        private val userUsernameTextView: TextView = itemView.findViewById(R.id.userUsernameTextView)
        private val profileImageView: CircleImageView = itemView.findViewById(R.id.profileImageView)

        fun bind(user: User) {
            userNameTextView.text = user.name
            userUsernameTextView.text = user.username
            Glide.with(profileImageView.context)
                    .load(user.image)
                    .placeholder(R.drawable.avatar)
                    .into(profileImageView)

            itemView.setOnClickListener {
                onUserClick(user)  // Trigger the onUserClick function with the clicked user
            }
        }
    }
}
