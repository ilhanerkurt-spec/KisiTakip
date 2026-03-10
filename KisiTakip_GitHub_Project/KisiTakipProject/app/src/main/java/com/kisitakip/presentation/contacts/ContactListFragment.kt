package com.kisitakip.presentation.contacts

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kisitakip.R
import com.kisitakip.databinding.FragmentContactListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactListFragment : Fragment() {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactListViewModel by viewModels()
    private lateinit var adapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactAdapter(
            onContactClick = { contact ->
                val intent = Intent(requireContext(), ContactDetailActivity::class.java)
                intent.putExtra("CONTACT_ID", contact.id)
                startActivity(intent)
            },
            onContactLongClick = { contact ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Kişiyi Sil")
                    .setMessage("${contact.name} silinsin mi?")
                    .setPositiveButton("Sil") { _, _ -> viewModel.deleteContact(contact) }
                    .setNegativeButton("İptal", null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(q: String?) = false
            override fun onQueryTextChange(q: String?): Boolean {
                viewModel.setSearchQuery(q ?: "")
                return true
            }
        })

        binding.fabAddContact.setOnClickListener {
            val intent = Intent(requireContext(), AddEditContactActivity::class.java)
            startActivity(intent)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contacts.collect { list ->
                    adapter.submitList(list)
                    binding.emptyState.isVisible = list.isEmpty()
                    binding.recyclerView.isVisible = list.isNotEmpty()
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
