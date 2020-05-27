package com.kvladislav.cryptowise.repositories

import androidx.lifecycle.LiveData
import com.kvladislav.cryptowise.database.dao.PortfolioDao
import com.kvladislav.cryptowise.models.portfolio.PortfolioItem
import org.koin.core.KoinComponent
import org.koin.core.inject

class PortfolioRepository : KoinComponent {
    private val portfolioDao: PortfolioDao by inject()
    val allAssets: LiveData<List<PortfolioItem>> = portfolioDao.getItemsLiveData()

    suspend fun getAssets(): List<PortfolioItem> {
        return portfolioDao.getItems()
    }

    suspend fun addAsset(asset: PortfolioItem) {
        portfolioDao.insert(asset)
    }

    suspend fun updateAsset(asset: PortfolioItem) {
        portfolioDao.update(asset)
    }

    suspend fun removeAsset(asset: PortfolioItem) {
        portfolioDao.delete(asset)
    }
}