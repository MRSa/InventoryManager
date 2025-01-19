package jp.osdn.gokigen.inventorymanager.recognize

import android.util.Log
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import kotlinx.serialization.json.Json

class RecognizeFromProductId
{
    // ========== Yahoo! JAPAN ショッピング商品検索（v3）にProduct番号で問い合わせを実行し、タイトル、出版社等を取得する
    //  Yahoo!ショッピング出店API
    //  　　　　- 利用約款 : https://developer.yahoo.co.jp/webapi/shopping/api_contract.html
    //        - ご利用ガイド ： https://developer.yahoo.co.jp/start/
    //        - クレジット表示 ： https://developer.yahoo.co.jp/attribution/
    //        - ガイドライン ：　https://developer.yahoo.co.jp/guideline/
    //        - 商品検索（v3） : https://developer.yahoo.co.jp/webapi/shopping/v3/itemsearch.html
    //        - エラーメッセージおよびコード : https://developer.yahoo.co.jp/appendix/errors.html
    fun recognizeInformationFromProductId(isOverWrite: Boolean, isbn: String, productId: String, title: String, description: String, author: String, publisher: String) : RecognizedData
    {
        // ----- Product番号に ISBN番号が記載されているようなので...ISBN番号で検索する
        val resp = searchShoppingDataFromProductId(isOverWrite = isOverWrite, productId = isbn, title = title, description = description, author = author, publisher = publisher)
        if (resp.isHit)
        {
            return (resp)
        }
        // ----- Product番号で検索する
        return (searchShoppingDataFromProductId(isOverWrite = isOverWrite, productId = productId, title = title, description = description, author = author, publisher = publisher))
    }

    private fun searchShoppingDataFromProductId(isOverWrite: Boolean, productId: String, title: String, description: String, author: String, publisher: String) : RecognizedData
    {
        var newTitle = ""
        var newDescription = ""
        var newAuthor = ""
        var newPublisher = ""
        var isHit = false
        try
        {
            // Yahoo! JAPAN ショッピング商品検索（v3）にProduct番号で問い合わせを実行し、タイトル、出版社等を取得する
            val urlToQuery = "https://shopping.yahooapis.jp/ShoppingWebService/V3/itemSearch?appid=$YAHOO_CLIENT_ID&jan_code=$productId"
            val response = SimpleHttpClient().httpGet(urlToQuery, COMMUNICATION_TIMEOUT)
            val parsedData = if (response.isNotEmpty()) { readJsonData(response) } else { null }

            Thread.sleep(QUERY_WAIT_MS) // ----- 連続実行しないよう、少し待つ... (連続実行しすぎると、 Yahoo! サイドでエラー応答するようになる)

            if ((parsedData != null) && (!parsedData.hits.isNullOrEmpty()))
            {
                Log.v(
                    TAG,
                    "  SEARCH RESP.(PRODID:$productId): ${parsedData.hits.size} ${parsedData.hits[0]?.name} (${parsedData.hits[0]?.brand?.name})"
                )

                if (((isOverWrite) || (title.isEmpty())) && (parsedData.hits[0]?.name?.isEmpty() != true))
                {
                    newTitle = parsedData.hits[0]?.name ?: ""
                    isHit = newTitle.isNotEmpty()
                } else {
                    newTitle = title
                }

                if ((description.isEmpty()) && (parsedData.hits[0]?.description?.isEmpty() != true))
                {
                    // ----- 文字認識データのエリアに書き込む。（既に文字認識を実施している場合には、情報を書き込まない）
                    newDescription = parsedData.hits[0]?.description ?:""
                    isHit = newDescription.isNotEmpty()
                } else {
                    newDescription = description
                }

                if (((isOverWrite) || (publisher.isEmpty())) && (parsedData.hits[0]?.brand?.name?.isEmpty() != true))
                {
                    newPublisher = parsedData.hits[0]?.brand?.name ?: ""
                    isHit = newPublisher.isNotEmpty()
                } else {
                    newPublisher = publisher
                }

                if (((isOverWrite) || (author.isEmpty())) && (parsedData.hits[0]?.genreCategory?.name?.isEmpty() != true))
                {
                    newAuthor = parsedData.hits[0]?.genreCategory?.name ?: ""
                    isHit = newAuthor.isNotEmpty()
                } else {
                    newAuthor = author
                }
            }
            Log.v(TAG, "title: $newTitle, author: $newAuthor, publisher: $newPublisher, description: $newDescription, isHit: $isHit")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (RecognizedData(isHit = isHit, title = newTitle, appendData = newDescription, author = newAuthor, publisher = newPublisher))
    }

    private fun readJsonData(response: String) : YahooShoppingResponseData?
    {
        try
        {
            val json = Json { ignoreUnknownKeys = true }
            val decodeData : YahooShoppingResponseData = json.decodeFromString(response)
            Log.v(TAG, " readJsonData(): total: ${decodeData.totalResultsReturned}  (return: ${decodeData.totalResultsAvailable}) ${decodeData.hits?.size}")
            return (decodeData)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (null)
    }

    companion object
    {
        private val TAG = RecognizeFromProductId::class.java.simpleName
        private const val COMMUNICATION_TIMEOUT = 15000 // 15sec
        private const val QUERY_WAIT_MS = 500L // 500ms → 0.5sec
        private const val YAHOO_CLIENT_ID = ""  // Yahoo! Client ID (コミット時削除)
    }
}
