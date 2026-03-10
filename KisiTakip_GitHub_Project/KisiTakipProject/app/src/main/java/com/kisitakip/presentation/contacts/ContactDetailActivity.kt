package com.kisitakip.presentation.contacts

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.kisitakip.databinding.ActivityContactDetailBinding
import com.kisitakip.presentation.debt.DebtFragment
import com.kisitakip.presentation.notes.NotesFragment
import com.kisitakip.presentation.tasks.TasksFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactDetailBinding
    private var contactId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactId = intent.getLongExtra("CONTACT_ID", 0L)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Kişi Detayı"

        val fragments: List<Fragment> = listOf(
            NotesFragment.newInstance(contactId),
            TasksFragment.newInstance(contactId),
            DebtFragment.newInstance(contactId)
        )
        val titles = listOf("Notlar", "Görevler", "Borç/Alacak")

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = titles[pos]
        }.attach()
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
