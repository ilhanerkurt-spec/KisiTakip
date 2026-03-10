package com.kisitakip.data.local.dao

import androidx.room.*
import com.kisitakip.data.local.entity.Debt
import kotlinx.coroutines.flow.Flow

data class DebtSummary(
    val totalCredit: Double,
    val totalDebt: Double,
    val netBalance: Double
)

data class ContactDebtSummary(
    val contactId: Long,
    val contactName: String,
    val netBalance: Double
)

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts WHERE contactId = :contactId ORDER BY date DESC")
    fun getDebtsByContact(contactId: Long): Flow<List<Debt>>

    @Query("SELECT * FROM debts")
    suspend fun getAllDebts(): List<Debt>

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'CREDIT' AND isPaid = 0 THEN amount ELSE 0 END), 0) as totalCredit,
            COALESCE(SUM(CASE WHEN type = 'DEBT' AND isPaid = 0 THEN amount ELSE 0 END), 0) as totalDebt,
            COALESCE(SUM(CASE WHEN type = 'CREDIT' AND isPaid = 0 THEN amount ELSE 0 END), 0) -
            COALESCE(SUM(CASE WHEN type = 'DEBT' AND isPaid = 0 THEN amount ELSE 0 END), 0) as netBalance
        FROM debts
    """)
    fun getDebtSummary(): Flow<DebtSummary>

    @Query("""
        SELECT d.contactId, c.name as contactName,
            SUM(CASE WHEN d.type = 'CREDIT' AND d.isPaid = 0 THEN d.amount ELSE 0 END) -
            SUM(CASE WHEN d.type = 'DEBT' AND d.isPaid = 0 THEN d.amount ELSE 0 END) as netBalance
        FROM debts d
        INNER JOIN contacts c ON d.contactId = c.id
        WHERE d.isPaid = 0
        GROUP BY d.contactId
        ORDER BY netBalance DESC
    """)
    fun getContactDebtSummaries(): Flow<List<ContactDebtSummary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt): Long

    @Update
    suspend fun updateDebt(debt: Debt)

    @Delete
    suspend fun deleteDebt(debt: Debt)
}
