package com.kvladislav.cryptowise.screens

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.R
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.base.SingleLiveEvent
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
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.net.UnknownHostException

class AppViewModel(private val context: Context) : BaseViewModel(), KoinComponent {

    private val coinMarketCapRepo: CurrencyRepository by inject()
    private val coinCapRepository: CoinCapRepository by inject()
    private val portfolioRepository: PortfolioRepository by inject()
    val fullPortfolio: MutableLiveData<FullPortfolio> = MutableLiveData()
    val connectionErrorLiveData = SingleLiveEvent<String?>()
    val portfolioAssets = liveData(Dispatchers.IO) { emitSource(portfolioRepository.allAssets) }
    private val portfolioObserver = Observer<List<PortfolioItem>> { tryUpdatePortfolio() }
    val assetListings = MutableLiveData<List<CombinedAssetModel>>()

    init {
        portfolioAssets.observeForever(portfolioObserver)
        loadAssetListings()
    }

    private fun loadAssetListings() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val assets = coinCapRepository.getAssets()
            val cmcMap = coinMarketCapRepo.getIDMap()
            val coinIds = getCoinIdsFromTrustworthyProviders()
            val cmcMapData = cmcMap.data?.sortedBy { it.rank }
            cmcMapData?.let { cmc ->
                assets.data?.let { assets ->
                    connectionErrorLiveData.postValue(null)
                    assetListings.postValue(combineCMCWithCoinCap(cmc, assets, coinIds))
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            handleException(e)
        }
    }

    private fun handleException(e: Exception) {
        when {
            e is UnknownHostException ->
                connectionErrorLiveData.postValue(context.getString(R.string.no_connection_error))
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

    fun tryRefreshListings() {
        loadAssetListings()
    }
}