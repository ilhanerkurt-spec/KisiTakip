package com.kisitakip.presentation.tasks

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
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kisitakip.R
import com.kisitakip.data.local.entity.Priority
import com.kisitakip.data.local.entity.Task
import com.kisitakip.data.repository.TaskRepository
import com.kisitakip.databinding.DialogAddTaskBinding
import com.kisitakip.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val repo: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun loadTasks(contactId: Long) {
        viewModelScope.launch {
            repo.getTasksByContact(contactId).collect { _tasks.value = it }
        }
    }

    fun addTask(task: Task) = viewModelScope.launch { repo.insertTask(task) }

    fun toggleCompleted(task: Task) = viewModelScope.launch {
        repo.setTaskCompleted(task.id, !task.isCompleted)
    }

    fun deleteTask(task: Task) = viewModelScope.launch { repo.deleteTask(task) }
}

@AndroidEntryPoint
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val contactId = arguments?.getLong("CONTACT_ID") ?: return
        viewModel.loadTasks(contactId)
        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tasks.collect { /* adapter güncelle */ }
            }
        }

        binding.fabAddTask.setOnClickListener {
            AddTaskDialog.newInstance(contactId).show(parentFragmentManager, "AddTask")
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }

    companion object {
        fun newInstance(contactId: Long) = TasksFragment().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}

@AndroidEntryPoint
class AddTaskDialog : DialogFragment() {

    private val viewModel: TasksViewModel by viewModels({ requireParentFragment() })
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private var selectedDate: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val contactId = arguments?.getLong("CONTACT_ID") ?: 0L
        val binding = DialogAddTaskBinding.inflate(layoutInflater)

        binding.etTaskDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, y, m, d ->
                cal.set(y, m, d)
                selectedDate = cal.timeInMillis
                binding.etTaskDate.setText(dateFormat.format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Görev Ekle")
            .setView(binding.root)
            .setPositiveButton("Kaydet") { _, _ ->
                val title = binding.etTaskTitle.text.toString().trim()
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Başlık gerekli", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val priority = when (binding.chipGroupPriority.checkedChipId) {
                    R.id.chipLow -> Priority.LOW
                    R.id.chipHigh -> Priority.HIGH
                    else -> Priority.MEDIUM
                }
                viewModel.addTask(Task(
                    contactId = contactId,
                    title = title,
                    description = binding.etTaskDesc.text.toString().trim().ifEmpty { null },
                    dueDate = selectedDate,
                    priority = priority
                ))
            }
            .setNegativeButton("İptal", null)
            .create()
    }

    companion object {
        fun newInstance(contactId: Long) = AddTaskDialog().apply {
            arguments = Bundle().apply { putLong("CONTACT_ID", contactId) }
        }
    }
}
