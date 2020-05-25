package com.kvladislav.cryptowise.models

import com.kvladislav.cryptowise.models.cmc_map.CMCMapItem
import com.kvladislav.cryptowise.models.coin_cap.assets.CoinCapAssetItem

data class CombinedAssetModel(val cmcMapItem: CMCMapItem, val coinCapAssetItem: CoinCapAssetItem) {

    val cmcId: Int?
        get() = cmcMapItem.id

    val coinCapId: String?
        get() = coinCapAssetItem.id

}