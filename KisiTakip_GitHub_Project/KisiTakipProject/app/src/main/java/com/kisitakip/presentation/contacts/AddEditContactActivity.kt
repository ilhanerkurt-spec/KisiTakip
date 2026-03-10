package com.kisitakip.presentation.contacts

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kisitakip.data.local.entity.Contact
import com.kisitakip.databinding.ActivityAddEditContactBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditContactActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditContactBinding
    private val viewModel: AddEditContactViewModel by viewModels()
    private var contactId: Long = 0L
    private var existingContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactId = intent.getLongExtra("CONTACT_ID", 0L)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (contactId == 0L) "Kişi Ekle" else "Kişi Düzenle"

        val labels = arrayOf("Müşteri", "Arkadaş", "İş Arkadaşı", "Aile", "Tedarikçi", "Diğer")
        binding.actvLabel.setAdapter(
            android.widget.ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, labels)
        )

        if (contactId != 0L) {
            lifecycleScope.launch {
                existingContact = viewModel.loadContact(contactId)
                existingContact?.let { c ->
                    binding.etName.setText(c.name)
                    binding.etPhone.setText(c.phone)
                    binding.actvLabel.setText(c.label)
                    binding.etDescription.setText(c.description)
                }
            }
        }

        binding.btnSave.setOnClickListener { saveContact() }
    }

    private fun saveContact() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etName.error = "Ad Soyad zorunlu"
            return
        }
        val contact = Contact(
            id = contactId,
            name = name,
            phone = binding.etPhone.text.toString().trim().ifEmpty { null },
            label = binding.actvLabel.text.toString().trim().ifEmpty { null },
            description = binding.etDescription.text.toString().trim().ifEmpty { null }
        )
        viewModel.saveContact(contact)
        Toast.makeText(this, "Kaydedildi", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
