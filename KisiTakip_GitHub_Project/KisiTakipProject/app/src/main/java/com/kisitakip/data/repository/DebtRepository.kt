package com.kisitakip.data.repository

import com.kisitakip.data.local.dao.DebtDao
import com.kisitakip.data.local.entity.Debt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(private val dao: DebtDao) {
    fun getDebtsByContact(contactId: Long) = dao.getDebtsByContact(contactId)
    fun getDebtSummary() = dao.getDebtSummary()
    fun getContactDebtSummaries() = dao.getContactDebtSummaries()
    suspend fun insertDebt(d: Debt) = dao.insertDebt(d)
    suspend fun updateDebt(d: Debt) = dao.updateDebt(d)
    suspend fun deleteDebt(d: Debt) = dao.deleteDebt(d)
}
