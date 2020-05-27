package com.kvladislav.cryptowise.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kvladislav.cryptowise.models.transactions.BuySellTransaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transaction_table ORDER BY timestamp DESC")
    fun getTransactions(): LiveData<List<BuySellTransaction>>

    @Insert
    suspend fun insert(transaction: BuySellTransaction)

    @Update
    suspend fun update(transaction: BuySellTransaction)

    @Delete
    suspend fun delete(transaction: BuySellTransaction)

    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM transaction_table")
    suspend fun clear()
}