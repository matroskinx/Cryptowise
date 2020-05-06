package com.kvladislav.cryptowise.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kvladislav.cryptowise.models.portfolio.PortfolioItem

@Dao
interface PortfolioDao {

    @Query("SELECT * FROM portfolio_records ORDER BY assetAmount DESC")
    fun getItemsLiveData(): LiveData<List<PortfolioItem>>

    @Query("SELECT * FROM portfolio_records ORDER BY assetAmount DESC")
    suspend fun getItems(): List<PortfolioItem>

    @Insert
    suspend fun insert(portfolioItem: PortfolioItem)

    @Update
    suspend fun update(portfolioItem: PortfolioItem)

    @Delete
    suspend fun delete(portfolioItem: PortfolioItem)

    @Query("DELETE FROM portfolio_records")
    suspend fun clear()
}