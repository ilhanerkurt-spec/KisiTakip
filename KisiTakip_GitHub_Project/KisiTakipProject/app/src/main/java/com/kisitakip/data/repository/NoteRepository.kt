package com.kisitakip.data.repository

import com.kisitakip.data.local.dao.NoteDao
import com.kisitakip.data.local.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(private val dao: NoteDao) {
    fun getNotesByContact(contactId: Long) = dao.getNotesByContact(contactId)
    fun searchNotes(q: String) = dao.searchNotes(q)
    suspend fun insertNote(n: Note) = dao.insertNote(n)
    suspend fun updateNote(n: Note) = dao.updateNote(n)
    suspend fun deleteNote(n: Note) = dao.deleteNote(n)
}
