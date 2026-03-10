package com.kisitakip.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisitakip.data.local.entity.Contact
import com.kisitakip.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditContactViewModel @Inject constructor(
    private val repo: ContactRepository
) : ViewModel() {

    suspend fun loadContact(id: Long): Contact? = repo.getContactById(id)

    fun saveContact(contact: Contact) = viewModelScope.launch {
        if (contact.id == 0L) repo.insertContact(contact)
        else repo.updateContact(contact)
    }
}
