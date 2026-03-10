package com.kisitakip.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class DebtType { DEBT, CREDIT }

@Entity(
    tableName = "debts",
    foreignKeys = [ForeignKey(
        entity = Contact::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("contactId")]
)
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val contactId: Long,
    val amount: Double,
    val description: String? = null,
    val date: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val type: DebtType,
    val isPaid: Boolean = false,
    val paidDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
