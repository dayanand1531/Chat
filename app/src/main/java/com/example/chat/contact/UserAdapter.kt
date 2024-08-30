package com.example.chat.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.R
import com.example.chat.api.model.Contact
import com.example.chat.api.model.entity.User

class UserAdapter(
    private val itemClickListener: (User) -> Unit
) : ListAdapter<User, UserAdapter.UserViewHolder>(UserItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_layout, parent, false)
        return UserViewHolder(view)

    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(content: User) {
            val name = content.name
            val number = content.UID
            val nameView = itemView.findViewById<TextView>(R.id.tvName)
            nameView.text = name
            val numberView = itemView.findViewById<TextView>(R.id.tvId)
            numberView.text = number + " " + content.user
            itemView.setOnClickListener {
                itemClickListener.invoke(content)
            }
        }
    }
}

class UserItemDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id

    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem

    }
}


