package com.kvladislav.cryptowise.screens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.CandlePeriodicData
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.cmc_map.CMCMapItem
import com.kvladislav.cryptowise.models.coin_cap.assets.CoinCapAssetItem
import com.kvladislav.cryptowise.models.portfolio.DisplayPortfolioItem
import com.kvladislav.cryptowise.models.portfolio.FullPortfolio
import com.kvladislav.cryptowise.models.portfolio.PortfolioItem
import com.kvladislav.cryptowise.repositories.CoinCapRepository
import com.kvladislav.cryptowise.repositories.CurrencyRepository
import com.kvladislav.cryptowise.repositories.PortfolioRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class AppViewModel : BaseViewModel(), KoinComponent {

    private val coinMarketCapRepo: CurrencyRepository by inject()
    private val coinCapRepository: CoinCapRepository by inject()
    private val portfolioRepository: PortfolioRepository by inject()
    val fullPortfolio: MutableLiveData<FullPortfolio> = MutableLiveData()

    val portfolioAssets = liveData(Dispatchers.IO) {
        emitSource(portfolioRepository.allAssets)
    }

    private val portfolioObserver = Observer<List<PortfolioItem>> {
        tryUpdatePortfolio()
    }

    init {
        portfolioAssets.observeForever(portfolioObserver)
    }

    val assetListings = liveData(Dispatchers.IO) {
        val assets = coinCapRepository.getAssets()
        val cmcMap = coinMarketCapRepo.getIDMap()
        val coinIds = getCoinIdsFromTrustworthyProviders()
        val cmcMapData = cmcMap.data?.sortedBy { it.rank }
        cmcMapData?.let { cmc ->
            assets.data?.let { assets ->
                emit(combineCMCWithCoinCap(cmc, assets, coinIds))
            }
        }
    }

    val candlePeriodicData = MutableLiveData<CandlePeriodicData>()

    private suspend fun getCoinIdsFromTrustworthyProviders(): HashSet<String> {
        val coinIds = hashSetOf<String>()
        for (provider in DataStorage.TRUSTWORTHY_PROVIDERS) {
            val response = coinCapRepository.getMarketsByExchangeId(exchangeId = provider)
            response.data?.run {
                val ids = this.map {
                    it.baseId ?: ""
                }
                coinIds.addAll(ids)
            }
        }
        return coinIds
    }

    private fun combineCMCWithCoinCap(
        cmcMap: List<CMCMapItem>,
        assets: List<CoinCapAssetItem>,
        trustyCoins: HashSet<String>
    ): List<CombinedAssetModel> {
        val filteredAssets = mutableListOf<CombinedAssetModel>()
        for (coinCapItem in assets) {
            for (cmcItem in cmcMap) {
                if (cmcItem.symbol.equals(coinCapItem.symbol) && trustyCoins.contains(coinCapItem.id)) {
                    filteredAssets.add(CombinedAssetModel(cmcItem, coinCapItem))
                    break
                }
            }
        }
        return filteredAssets;
    }

    fun tryUpdatePortfolio() {
        Timber.d("Setting up portfolio value")
        val listings = assetListings.value ?: return
        val portfolioAssets = portfolioAssets.value ?: return

        var sum = 0.0
        val displayItems = mutableListOf<DisplayPortfolioItem>()

        portfolioAssets.forEach { portfolioItem ->
            listings.find {
                portfolioItem.coinCapId == it.coinCapAssetItem.id
            }?.run {
                val price = this.coinCapAssetItem.priceUsd ?: 0.0
                sum += price * portfolioItem.assetAmount
                displayItems.add(DisplayPortfolioItem(portfolioItem, price))
            }
        }

        val portfolio = FullPortfolio(sum, displayItems)
        fullPortfolio.postValue(portfolio)
    }

    override fun onCleared() {
        super.onCleared()
        portfolioAssets.removeObserver(portfolioObserver)
    }
}