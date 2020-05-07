package com.kvladislav.cryptowise.screens

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.kvladislav.cryptowise.DataStorage
import com.kvladislav.cryptowise.base.BaseViewModel
import com.kvladislav.cryptowise.models.CombinedAssetModel
import com.kvladislav.cryptowise.models.cmc_map.CMCMapItem
import com.kvladislav.cryptowise.models.coin_cap.assets.CoinCapAssetItem
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

    val portfolioValue: MutableLiveData<Double> = MutableLiveData(0.0)

    val portfolioAssets = liveData(Dispatchers.IO) {
        Timber.d("BLOOOOCK")
        emitSource(portfolioRepository.allAssets)
    }

    val currencyListings = liveData(Dispatchers.IO) {
        val assets = coinCapRepository.getAssets()
        val cmcMap = coinMarketCapRepo.getIDMap()
        val coinIds = getCoinIdsFromTrustworthyProviders()
//        dataStorage.setTrustworthyCoins(coinIds)
        val cmcMapData = cmcMap.data?.sortedBy { it.rank }
        cmcMapData?.let { cmc ->
            assets.data?.let { assets ->
                emit(combineCMCWithCoinCap(cmc, assets, coinIds))
            }
        }
    }

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

    fun tryUpdatePortfolioValue() {
        Timber.d("Setting up portfolio value")
        val listings = currencyListings.value ?: return
        val portfolioAssets = portfolioAssets.value ?: return
        Timber.d("Track is ready!")

        var sum = 0.0
        portfolioAssets.forEach { portfolioItem ->
            val asset = listings.find {
                portfolioItem.coinCapId == it.coinCapAssetItem.id
            }
            if (asset != null && asset.coinCapAssetItem.id != null) {
                val price = asset.coinCapAssetItem.priceUsd!!.toDouble()
                Timber.d("Asset price: $price; Sum before: $sum")
                sum += price * portfolioItem.assetAmount
                Timber.d("Sum after: $sum")
            }
        }

        portfolioValue.postValue(sum)
    }
}