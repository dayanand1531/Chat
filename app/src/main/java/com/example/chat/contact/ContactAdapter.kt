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

class ContactAdapter :
    ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactItemDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact_adapter, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(content: Contact) {
            val name = content.name
            val number = content.number
            val nameView = itemView.findViewById<TextView>(R.id.tvName)
            nameView.text = name
            val numberView = itemView.findViewById<TextView>(R.id.tvNumber)
            numberView.text = number
        }
    }
}

class ContactItemDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}


