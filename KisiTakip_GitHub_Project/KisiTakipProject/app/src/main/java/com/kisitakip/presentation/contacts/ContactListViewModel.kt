package com.kisitakip.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kisitakip.data.local.entity.Contact
import com.kisitakip.data.repository.ContactRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val repo: ContactRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val contacts: StateFlow<List<Contact>> = _searchQuery
        .debounce(300)
        .flatMapLatest { q ->
            if (q.isBlank()) repo.getAllContacts() else repo.searchContacts(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(q: String) { _searchQuery.value = q }

    fun deleteContact(c: Contact) = viewModelScope.launch { repo.deleteContact(c) }
}
