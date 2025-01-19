package jp.osdn.gokigen.inventorymanager.recognize

import kotlinx.serialization.Serializable

// ---- https://developer.yahoo.co.jp/webapi/shopping/v3/itemsearch.html より
@Serializable
data class YahooShoppingResponseData(
    val totalResultsAvailable: Int = 0,
    val totalResultsReturned: Int = 0,
    val firstResultsPosition: Int = 0,
    val request: YahooShoppingResponseFieldRequest?,
    val hits: List<YahooShoppingResponseFieldHits?>?
)

@Serializable
data class YahooShoppingResponseFieldRequest(
    val query: String?
)

@Serializable
data class YahooShoppingResponseFieldHits(
    val index: Int? = 0,
    val name: String? = "",
    val description: String? = "",
    val headLine: String? = "",
    val inStock: Boolean? = false,
    val url: String? = "",
    val code: String? = "",
    val condition: String? = "",
    val taxExcludePrice: Int? = 0,
    val taxExcludePremiumPrice: Int? = 0,
    val premiumPrice: Int? = 0,
    val premiumPriceStatus: Boolean = false,
    val premiumDiscountType: String? = "",
    val premiumDiscountRate: Int? = 0,
    val imageId: String? = "",
    val image: YahooShoppingResponseFieldHitsImage? = null,
    val exImage: YahooShoppingResponseFieldHitsExImage? = null,
    val review: YahooShoppingResponseFieldHitsReview? = null,
    val affiliateRate: Float? = 0.0f,
    val price: Int? = 0,
    val priceLabel: YahooShoppingResponseFieldHitsPriceLabel? = null,
    val point: YahooShoppingResponseFieldHitsPoint? = null,
    val shipping: YahooShoppingResponseFieldHitsShipping? = null,
    val genreCategory: YahooShoppingResponseFieldGenreCategory? = null,
    val parentGenreCategories: List<YahooShoppingResponseFieldParentGenreCategories?>? = null,
    val brand: YahooShoppingResponseFieldBrand? = null,
    val parentBrands: List<YahooShoppingResponseFieldParentBrands?>? = null,
    val janCode: String? = "",
    val payment: String? = "",
    val releaseDate: Long? = 0,
    val seller: YahooShoppingResponseFieldSeller? = null,
    val delivery: YahooShoppingResponseFieldSellerDelivery? = null
)

@Serializable
data class YahooShoppingResponseFieldHitsImage(
    val small: String? = "",
    val medium: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldHitsExImage(
    val url: String? = "",
    val width: String? = "",
    val height: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldHitsReview(
    val rate: Float? = 0.0f,
    val count: Int? = 0,
    val url: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldHitsPriceLabel(
    val taxable: Boolean? = false,
    val premiumPrice: Int? = 0,
    val taxExcludePremiumPrice: Int? = 0,
    val defaultPrice: Int? = 0,
    val taxExcludeDefaultPrice: Int? = 0,
    val discountedPrice: Int? = 0,
    val taxExcludeDiscountedPrice: Int? = 0,
    val fixedPrice: Int? = 0,
    val periodStart: Int? = 0,
    val periodEnd: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldHitsPoint(
    val amount: Int? = 0,
    val times: Int? = 0,
    val bonusAmount: Int? = 0,
    val bonusTimes: Int? = 0,
    val premiumAmount: Int? = 0,
    val premiumTimes: Int? = 0,
    val premiumBonusAmount: Int? = 0,
    val premiumBonusTimes: Int? = 0,
    val lyLimitedBonusAmount: Int? = 0,
    val lyLimitedBonusTimes: Int? = 0,
    val lyLimitedPremiumBonusAmount: Int? = 0,
    val lyLimitedPremiumBonusTimes: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldHitsShipping(
    val name: String? = "",
    val code: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldGenreCategory(
    val id: Int? = 0,
    val name: String? = "",
    val depth: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldParentGenreCategories(
    val id: Int? = 0,
    val name: String? = "",
    val depth: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldBrand(
    val id: Int? = 0,
    val name: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldParentBrands(
    val id: Int? = 0,
    val name: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldSeller(
    val sellerId: String? = "",
    val name: String = "",
    val url: String? = "",
    val isBestSeller: Boolean? = false,
    val review: YahooShoppingResponseFieldSellerReview? = null,
    val imageId: String? = ""
)

@Serializable
data class YahooShoppingResponseFieldSellerReview(
    val rate: Float? = 0.0f,
    val count: Int? = 0
)

@Serializable
data class YahooShoppingResponseFieldSellerDelivery(
    val area: String? = "",
    val deadLine: Int? = 0,
    val day: Int? = 0
)
