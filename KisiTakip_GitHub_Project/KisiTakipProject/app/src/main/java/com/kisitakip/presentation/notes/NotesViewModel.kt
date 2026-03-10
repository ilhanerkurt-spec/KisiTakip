package com.kisitakip.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisitakip.data.local.entity.Note
import com.kisitakip.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(private val repo: NoteRepository) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    fun loadNotes(contactId: Long) {
        viewModelScope.launch {
            repo.getNotesByContact(contactId).collect { _notes.value = it }
        }
    }

    fun addNote(note: Note) = viewModelScope.launch { repo.insertNote(note) }
    fun deleteNote(note: Note) = viewModelScope.launch { repo.deleteNote(note) }
}
