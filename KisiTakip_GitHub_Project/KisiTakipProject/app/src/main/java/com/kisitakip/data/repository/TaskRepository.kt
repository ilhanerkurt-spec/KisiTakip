package com.kisitakip.data.repository

import com.kisitakip.data.local.dao.TaskDao
import com.kisitakip.data.local.entity.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val dao: TaskDao) {
    fun getTasksByContact(contactId: Long) = dao.getTasksByContact(contactId)
    fun getPendingTasks() = dao.getPendingTasks()
    suspend fun insertTask(t: Task) = dao.insertTask(t)
    suspend fun updateTask(t: Task) = dao.updateTask(t)
    suspend fun deleteTask(t: Task) = dao.deleteTask(t)
    suspend fun setTaskCompleted(id: Long, done: Boolean) = dao.setTaskCompleted(id, done)
}
