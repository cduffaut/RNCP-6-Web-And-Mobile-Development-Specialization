package com.cduffaut.ft_hangouts.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cduffaut.ft_hangouts.databinding.ItemContactBinding
import com.cduffaut.ft_hangouts.models.Contact

class ContactAdapter(private val onContactClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {

    // creates a new view holder instance to
    // manage the UI components for each contact in the recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContactViewHolder(binding)
    }

    // populates the view holder with data from the contact at the specified
    // position and sets up a click listener to handle selection of this contact
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.bind(contact)
        holder.itemView.setOnClickListener {
            onContactClick(contact)
        }
    }

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // binds contact data to the UI elements, displaying the contact's full name
        // and phone number, and loading their photo
        fun bind(contact: Contact) {
            binding.contactName.text = "${contact.firstname} ${contact.name}"
            binding.contactPhone.text = contact.phone

            // run img if possible
            if (contact.photo.isNotEmpty()) {
                try {
                    binding.contactPhoto.setImageDrawable(null)
                    binding.contactPhoto.setImageURI(Uri.parse(contact.photo))
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.contactPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.contactPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    // determining when items have changed
    // by comparing IDs and content, enabling efficient updates with minimal visual disruption
    private class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}