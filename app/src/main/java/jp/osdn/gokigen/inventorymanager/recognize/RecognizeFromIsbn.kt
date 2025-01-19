package jp.osdn.gokigen.inventorymanager.recognize

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor
import java.util.Date

class RecognizeFromIsbn(private val activity: ComponentActivity)
{
    private val storageDao = AppSingleton.db.storageDao()
    private val shoppingSearch = RecognizeFromProductId()

    fun recognizeFromIsbn(id: Long, isOverwrite : Boolean, useProductIdSearch: Boolean, callback: IRecognizedDataCallback)
    {
        try
        {
            Log.v(TAG, "recognizeFromIsbn(id: $id, isOverwrite: $isOverwrite) : start")
            val data = storageDao.findById(id)
            var needUpdate = false

            // ----- サーバからデータを取得する
            if ((data?.isbn?:"").isNotEmpty())
            {
                val isbn = data?.isbn?:""
                val urlToQuery = "https://ndlsearch.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=$isbn"
                Log.v(TAG, " QUERY: $urlToQuery")
                val response = SimpleHttpClient().httpGet(urlToQuery, COMMUNICATION_TIMEOUT)

                val title = if (response.contains("&lt;dc:title&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:title&gt;")
                    val endIndex = response.indexOf("&lt;/dc:title&gt;")
                    response.substring(startIndex + "&lt;dc:title&gt;".length, endIndex)
                } else {
                    ""
                }

                val subTitle = if (response.contains("&lt;dc:description&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:description&gt;")
                    val endIndex = response.indexOf("&lt;/dc:description&gt;")
                    response.substring(startIndex + "&lt;dc:description&gt;".length, endIndex)
                } else {
                    ""
                }

                val author = if (response.contains("&lt;dc:creator&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:creator&gt;")
                    val endIndex = response.indexOf("&lt;/dc:creator&gt;")
                    response.substring(startIndex + "&lt;dc:creator&gt;".length, endIndex)
                } else {
                    ""
                }

                val publisher = if (response.contains("&lt;dc:publisher&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:publisher&gt;")
                    val endIndex = response.indexOf("&lt;/dc:publisher&gt;")
                    response.substring(startIndex + "&lt;dc:publisher&gt;".length, endIndex)
                } else {
                    ""
                }

                val updateTitle = if ((title.isNotEmpty())&&(((data?.title?:"").isEmpty())||(isOverwrite))) {
                    needUpdate = true
                    title
                } else { data?.title ?: "" }

                val updateSubtitle = if ((subTitle.isNotEmpty())&&(((data?.subTitle?:"").isEmpty())||(isOverwrite))) {
                    needUpdate = true
                    subTitle
                } else { data?.subTitle ?: "" }

                val updateAuthor = if ((author.isNotEmpty())&&(((data?.author?:"").isEmpty())||(isOverwrite))) {
                    needUpdate = true
                    author
                } else { data?.author ?: "" }

                val updatePublisher = if ((publisher.isNotEmpty())&&(((data?.publisher?:"").isEmpty())||(isOverwrite))) {
                    needUpdate = true
                    publisher
                } else { data?.publisher ?: "" }

                if (needUpdate)
                {
                    val updateData = UpdateRecordInformation(id, updateTitle, updateSubtitle, updateAuthor, updatePublisher, data?.category?:"", data?.description?:"")
                    activity.runOnUiThread {
                        // ----- 更新が必要な情報を提供する
                        callback.recognizedData(updateData, isOverwrite)
                    }
                }
            }
            if ((data != null)&&(useProductIdSearch)&&(!needUpdate))
            {
                val recognizedData = shoppingSearch.recognizeInformationFromProductId(
                    isOverWrite = isOverwrite,
                    isbn = data.isbn ?: "",
                    productId = data.productId ?: "",
                    title = data.title ?: "",
                    description = data.description ?: "",
                    author = data.author ?: "",
                    publisher = data.publisher ?: ""
                )
                if (recognizedData.isHit)
                {
                    val updateData = UpdateRecordInformation(
                        id = id,
                        title = recognizedData.title,
                        subTitle = data.subTitle?:"",
                        author = recognizedData.author,
                        publisher = recognizedData.publisher,
                        category = data.category?:"",
                        description = recognizedData.appendData
                    )
                    needUpdate = true
                    activity.runOnUiThread {
                        // ----- 更新が必要な情報を提供する
                        callback.recognizedData(updateData, isOverwrite)
                    }
                }
            }
            activity.runOnUiThread {
                // ----- 処理の終了を通知する
                        callback.finishRecognizedData(needUpdate)

                // ----- 更新結果をToast表示
                val resultMessage = if (needUpdate) {
                    activity.getString(R.string.label_data_updated_from_isbn)
                } else {
                    activity.getString(R.string.label_no_data_from_isbn)
                }
                Toast.makeText(activity, resultMessage, Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun recognizeAllFromIsbn(isOverWrite: Boolean, useProductIdSearch: Boolean, callback: IRecognizeDataFromInternetCallback?) : Int
    {
        var updateCount = 0
        try
        {
            Log.v(TAG, "recognizeAllFromIsbn($isOverWrite) : start")
            val updateRecordList = ArrayList<UpdateRecordInformation>()
            updateRecordList.clear()
            val dataList = storageDao.getAll()
            dataList.forEachIndexed { index, data ->
                try
                {
                    // -----  データ更新の通知
                    activity.runOnUiThread {
                        callback?.progressRecognizeFromInternet(RecognizeDataProgress.CHECK_ISBN, (index + 1), dataList.size)
                    }

                    // -----  データ更新用のリストを作る
                    var needUpdate = false
                    if ((data.isbn?:"").isNotEmpty())
                    {
                        val isbn = data.isbn?:""
                        val urlToQuery = "https://ndlsearch.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=$isbn"

                        Log.v(TAG, " --- QUERY: $urlToQuery")
                        val response = SimpleHttpClient().httpGet(urlToQuery, COMMUNICATION_TIMEOUT)

                        val title = if (response.contains("&lt;dc:title&gt;")) {
                            val startIndex = response.indexOf("&lt;dc:title&gt;")
                            val endIndex = response.indexOf("&lt;/dc:title&gt;")
                            response.substring(startIndex + "&lt;dc:title&gt;".length, endIndex)
                        } else {
                            ""
                        }

                        val subTitle = if (response.contains("&lt;dc:description&gt;")) {
                            val startIndex = response.indexOf("&lt;dc:description&gt;")
                            val endIndex = response.indexOf("&lt;/dc:description&gt;")
                            response.substring(startIndex + "&lt;dc:description&gt;".length, endIndex)
                        } else {
                            ""
                        }

                        val author = if (response.contains("&lt;dc:creator&gt;")) {
                            val startIndex = response.indexOf("&lt;dc:creator&gt;")
                            val endIndex = response.indexOf("&lt;/dc:creator&gt;")
                            response.substring(startIndex + "&lt;dc:creator&gt;".length, endIndex)
                        } else {
                            ""
                        }

                        val publisher = if (response.contains("&lt;dc:publisher&gt;")) {
                            val startIndex = response.indexOf("&lt;dc:publisher&gt;")
                            val endIndex = response.indexOf("&lt;/dc:publisher&gt;")
                            response.substring(startIndex + "&lt;dc:publisher&gt;".length, endIndex)
                        } else {
                            ""
                        }

                        val updateTitle = if ((title.isNotEmpty())&&(((data.title?:"").isEmpty())||(isOverWrite))) {
                            needUpdate = true
                            title
                        } else { data.title ?: "" }

                        val updateSubtitle = if ((subTitle.isNotEmpty())&&(((data.subTitle?:"").isEmpty())||(isOverWrite))) {
                            needUpdate = true
                            subTitle
                        } else { data.subTitle ?: "" }

                        val updateAuthor = if ((author.isNotEmpty())&&(((data.author?:"").isEmpty())||(isOverWrite))) {
                            needUpdate = true
                            author
                        } else { data.author ?: "" }

                        val updatePublisher = if ((publisher.isNotEmpty())&&(((data.publisher?:"").isEmpty())||(isOverWrite))) {
                            needUpdate = true
                            publisher
                        } else { data.publisher ?: "" }

                        if (needUpdate)
                        {
                            updateRecordList.add(
                                UpdateRecordInformation(
                                    id = data.id,
                                    title = updateTitle,
                                    subTitle = updateSubtitle,
                                    author = updateAuthor,
                                    publisher = updatePublisher,
                                    category = data.category?:"",
                                    description = data.description?:""
                                )
                            )
                        }
                    }
                    if ((useProductIdSearch)&&(!needUpdate))
                    {
                        val recognizedData = shoppingSearch.recognizeInformationFromProductId(
                            isOverWrite = isOverWrite,
                            isbn = data.isbn ?: "",
                            productId = data.productId ?: "",
                            title = data.title ?: "",
                            description = data.description ?: "",
                            author = data.author ?: "",
                            publisher = data.publisher ?: ""
                        )
                        if (recognizedData.isHit)
                        {
                            updateRecordList.add(
                                UpdateRecordInformation(
                                    id = data.id,
                                    title = recognizedData.title,
                                    subTitle = data.subTitle?:"",
                                    author = recognizedData.author,
                                    publisher = recognizedData.publisher,
                                    category = data.category?:"",
                                    description = recognizedData.appendData
                                )
                            )
                        }
                    }
                    Thread.sleep(QUERY_WAIT_MS)
                }
                catch (ee: Exception)
                {
                    ee.printStackTrace()
                }
            }

            val currentDate = Date()
            Log.v(TAG, " ----- recognizeFromIsbn() : update ${updateRecordList.size} records start.")
            for (item in updateRecordList)
            {
                storageDao.updateContent(item.id, item.title, item.subTitle, item.author, item.publisher, item.category, updateDate = currentDate)
                storageDao.updateDescription(item.id, item.description, updateDate = currentDate)
            }
            Log.v(TAG, " ----- recognizeFromIsbn() : update ${updateRecordList.size} records Done.")
            updateCount = updateRecordList.size
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (updateCount)
    }

    fun recognizeInformationFromIsbn(isbn: String, title: String, subTitle: String, author: String, publisher: String) : RecognizedData
    {
        var newTitle = ""
        var newSubtitle = ""
        var newAuthor = ""
        var newPublisher = ""
        var isHit = false
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isOverWrite = preferences.getBoolean(IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE, IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE)

            // 国立国会図書館のAPIにISBN番号で問い合わせを実行し、タイトル、著者、出版社を取得する
            val urlToQuery = "https://ndlsearch.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=$isbn"
            val response = SimpleHttpClient().httpGet(urlToQuery, COMMUNICATION_TIMEOUT)

            // PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE が true あるいは 入力されていない場合は、問い合わせ結果を反映させる (ちょー手抜きの解析処理)
            if ((isOverWrite)||(title.isEmpty()))
            {
                if (response.contains("&lt;dc:title&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:title&gt;")
                    val endIndex = response.indexOf("&lt;/dc:title&gt;")
                    newTitle = response.substring(startIndex + "&lt;dc:title&gt;".length, endIndex)
                    isHit = newTitle.isNotEmpty()
                }
            }
            else
            {
                newTitle = title
            }

            if ((isOverWrite)||(subTitle.isEmpty()))
            {
                if (response.contains("&lt;dc:description&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:description&gt;")
                    val endIndex = response.indexOf("&lt;/dc:description&gt;")
                    newSubtitle = response.substring(
                        startIndex + "&lt;dc:description&gt;".length,
                        endIndex
                    )
                    isHit = isHit || newSubtitle.isNotEmpty()
                }
            }
            else
            {
                newSubtitle = subTitle
            }

            if ((isOverWrite)||(author.isEmpty()))
            {
                if (response.contains("&lt;dc:creator&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:creator&gt;")
                    val endIndex = response.indexOf("&lt;/dc:creator&gt;")
                    newAuthor = response.substring(
                        startIndex + "&lt;dc:creator&gt;".length,
                        endIndex
                    )
                    isHit = isHit || newAuthor.isNotEmpty()
                }
            }
            else
            {
                newAuthor = author
            }

            if ((isOverWrite)||(publisher.isEmpty()))
            {
                if (response.contains("&lt;dc:publisher&gt;")) {
                    val startIndex = response.indexOf("&lt;dc:publisher&gt;")
                    val endIndex = response.indexOf("&lt;/dc:publisher&gt;")
                    newPublisher = response.substring(
                        startIndex + "&lt;dc:publisher&gt;".length,
                        endIndex
                    )
                    isHit = isHit || newPublisher.isNotEmpty()
                }
            }
            else
            {
                newPublisher = publisher
            }
            Log.v(TAG, "title: $newTitle, subTitle: $newSubtitle, author: $newAuthor, publisher: $newPublisher isHit: $isHit")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (RecognizedData(isHit = isHit, title = newTitle, appendData = newSubtitle, author = newAuthor, publisher = newPublisher))
    }

    companion object
    {
        private val TAG = RecognizeFromIsbn::class.java.simpleName
        private const val COMMUNICATION_TIMEOUT = 15000 // 15sec
        private const val QUERY_WAIT_MS = 1500L // 1500ms → 1.5sec
    }
}
