package com.kisitakip.presentation.contacts

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kisitakip.data.local.entity.Contact
import com.kisitakip.databinding.ItemContactBinding

class ContactAdapter(
    private val onContactClick: (Contact) -> Unit,
    private val onContactLongClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemContactBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(contact: Contact) {
            b.tvName.text = contact.name
            b.tvPhone.text = contact.phone ?: "Telefon yok"
            b.tvLabel.text = contact.label ?: ""
            b.tvLabel.visibility = if (contact.label.isNullOrBlank()) View.GONE else View.VISIBLE
            b.tvAvatar.text = contact.name.take(1).uppercase()
            b.root.setOnClickListener { onContactClick(contact) }
            b.root.setOnLongClickListener { onContactLongClick(contact); true }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(a: Contact, b: Contact) = a.id == b.id
            override fun areContentsTheSame(a: Contact, b: Contact) = a == b
        }
    }
}
