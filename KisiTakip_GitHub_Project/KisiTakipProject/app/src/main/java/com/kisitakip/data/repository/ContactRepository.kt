package com.kisitakip.data.repository

import com.kisitakip.data.local.dao.ContactDao
import com.kisitakip.data.local.entity.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(private val dao: ContactDao) {
    fun getAllContacts(): Flow<List<Contact>> = dao.getAllContacts()
    fun searchContacts(q: String): Flow<List<Contact>> = dao.searchContacts(q)
    suspend fun getContactById(id: Long) = dao.getContactById(id)
    suspend fun insertContact(c: Contact) = dao.insertContact(c)
    suspend fun updateContact(c: Contact) = dao.updateContact(c)
    suspend fun deleteContact(c: Contact) = dao.deleteContact(c)
}
