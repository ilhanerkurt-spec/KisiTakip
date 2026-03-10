package com.kisitakip.presentation.notes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.kisitakip.databinding.FragmentNotesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val contactId = arguments?.getLong("CONTACT_ID") ?: return
        viewModel.loadNotes(contactId)

        binding.recyclerNotes.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notes.collect { notes ->
                    binding.emptyNotes.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerNotes.visibility = if (notes.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        }

        binding.fabAddNote.setOnClickListener {
            val dialog = AddNoteDialog.newInstance(contactId)
            dialog.show(parentFragmentManager, "AddNote")
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        fun newInstance(contactId: Long) = NotesFragment().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}
