package com.kisitakip.presentation.notes

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kisitakip.data.local.entity.Note
import com.kisitakip.databinding.DialogAddNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteDialog : DialogFragment() {

    private val viewModel: NotesViewModel by viewModels({ requireParentFragment() })

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contactId = arguments?.getLong("CONTACT_ID") ?: 0L
        val binding = DialogAddNoteBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Not Ekle")
            .setView(binding.root)
            .setPositiveButton("Kaydet") { _, _ ->
                val title = binding.etNoteTitle.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Başlık gerekli", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val note = Note(
                    contactId = contactId,
                    title = title,
                    description = binding.etNoteDesc.text.toString().trim().ifEmpty { null },
                    tag = binding.etNoteTag.text.toString().trim().ifEmpty { null }
                )
                viewModel.addNote(note)
            }
            .setNegativeButton("İptal", null)
            .create()
    }

    companion object {
        fun newInstance(contactId: Long) = AddNoteDialog().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}
