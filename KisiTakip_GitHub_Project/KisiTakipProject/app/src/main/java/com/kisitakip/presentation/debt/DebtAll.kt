package com.kisitakip.presentation.debt

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kisitakip.R
import com.kisitakip.data.local.entity.Debt
import com.kisitakip.data.local.entity.DebtType
import com.kisitakip.data.repository.DebtRepository
import com.kisitakip.databinding.DialogAddDebtBinding
import com.kisitakip.databinding.FragmentDebtBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DebtViewModel @Inject constructor(private val repo: DebtRepository) : ViewModel() {

    private val _debts = MutableStateFlow<List<Debt>>(emptyList())
    val debts: StateFlow<List<Debt>> = _debts

    val debtSummary = repo.getDebtSummary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadDebts(contactId: Long) {
        viewModelScope.launch {
            repo.getDebtsByContact(contactId).collect { _debts.value = it }
        }
    }

    fun addDebt(debt: Debt) = viewModelScope.launch { repo.insertDebt(debt) }
    fun deleteDebt(debt: Debt) = viewModelScope.launch { repo.deleteDebt(debt) }
    fun markPaid(debt: Debt) = viewModelScope.launch {
        repo.updateDebt(debt.copy(isPaid = true, paidDate = System.currentTimeMillis()))
    }
}

@AndroidEntryPoint
class DebtFragment : Fragment() {

    private var _binding: FragmentDebtBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DebtViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDebtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val contactId = arguments?.getLong("CONTACT_ID") ?: return
        viewModel.loadDebts(contactId)
        binding.recyclerDebts.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.debtSummary.collect { summary ->
                        summary ?: return@collect
                        binding.tvNetBalance.text = "%.2f ₺".format(summary.netBalance)
                        binding.tvTotalCredit.text = "%.2f ₺".format(summary.totalCredit)
                        binding.tvTotalDebt.text = "%.2f ₺".format(summary.totalDebt)
                    }
                }
            }
        }

        binding.fabAddDebt.setOnClickListener {
            AddDebtDialog.newInstance(contactId).show(parentFragmentManager, "AddDebt")
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        fun newInstance(contactId: Long) = DebtFragment().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}

@AndroidEntryPoint
class AddDebtDialog : DialogFragment() {

    private val viewModel: DebtViewModel by viewModels({ requireParentFragment() })
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedDueDate: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contactId = arguments?.getLong("CONTACT_ID") ?: 0L
        val binding = DialogAddDebtBinding.inflate(layoutInflater)

        binding.etDebtDate.setText(dateFormat.format(Date()))
        binding.etDebtDate.setOnClickListener { pickDate { ts -> selectedDate = ts; binding.etDebtDate.setText(dateFormat.format(Date(ts))) } }
        binding.etDueDate.setOnClickListener { pickDate { ts -> selectedDueDate = ts; binding.etDueDate.setText(dateFormat.format(Date(ts))) } }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Borç / Alacak Ekle")
            .setView(binding.root)
            .setPositiveButton("Kaydet") { _, _ ->
                val amountStr = binding.etAmount.text.toString().trim()
                if (amountStr.isEmpty()) { Toast.makeText(requireContext(), "Tutar gerekli", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                val type = if (binding.chipCredit.isChecked) DebtType.CREDIT else DebtType.DEBT
                viewModel.addDebt(Debt(
                    contactId = contactId,
                    amount = amountStr.toDoubleOrNull() ?: 0.0,
                    description = binding.etDebtDesc.text.toString().trim().ifEmpty { null },
                    date = selectedDate,
                    dueDate = selectedDueDate,
                    type = type
                ))
            }
            .setNegativeButton("İptal", null)
            .create()
    }

    private fun pickDate(onPick: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            cal.set(y, m, d)
            onPick(cal.timeInMillis)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    companion object {
        fun newInstance(contactId: Long) = AddDebtDialog().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}
