package com.kisitakip.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Priority { LOW, MEDIUM, HIGH }

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Contact::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("contactId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val title: String,
    val description: String? = null,
    val dueDate: Long? = null,
    val dueTime: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
