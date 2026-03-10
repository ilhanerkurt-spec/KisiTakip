package com.kisitakip.presentation.reports

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.kisitakip.databinding.FragmentReportsBinding
import com.kisitakip.presentation.debt.DebtViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DebtViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.debtSummary.collect { summary ->
                    summary ?: return@collect
                    binding.tvTotalCredit.text = "%.2f ₺".format(summary.totalCredit)
                    binding.tvTotalDebt.text = "%.2f ₺".format(summary.totalDebt)
                    binding.tvNetBalance.text = "%.2f ₺".format(summary.netBalance)
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
