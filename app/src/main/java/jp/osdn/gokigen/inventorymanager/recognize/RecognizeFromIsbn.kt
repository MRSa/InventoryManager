package jp.osdn.gokigen.inventorymanager.recognize

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor
import java.util.Date

data class UpdateRecordInformation(val id: Long, val title: String, val subTitle: String, val author: String, val publisher: String, val category: String)

interface RecognizeFromIsbnCallback
{
    fun recognizedDataFromIsbnCallback(data: UpdateRecordInformation, isOverwrite: Boolean)
    fun finishRecognizedDataFromIsbn(needUpdate: Boolean)
}

class RecognizeFromIsbn(private val activity: AppCompatActivity)
{
    private val storageDao = AppSingleton.db.storageDao()

    fun doRecognizeAllFromIsbn(callback: RecognizeDataFromIsbnCallback?)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isOverwrite = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )
            Log.v(TAG, "doRecognizeAllFromIsbn($isOverwrite)")
            Thread { recognizeAllFromIsbn(isOverwrite, callback) }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun doRecognizeFromIsbn(id: Long, callback: RecognizeFromIsbnCallback)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isOverwrite = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )
            Log.v(TAG, "doRecognizeFromIsbn($isOverwrite, $id)")
            Thread { recognizeFromIsbn(id, isOverwrite, callback) }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun recognizeFromIsbn(id: Long, isOverwrite : Boolean, callback: RecognizeFromIsbnCallback)
    {
        try
        {
            Log.v(TAG, "recognizeFromIsbn(id: $id, isOverwrite: $isOverwrite) : start")
            val data = storageDao.findById(id)

            // ----- サーバからデータを取得する
            if ((data?.isbn?:"").isNotEmpty())
            {
                var needUpdate = false
                val isbn = data?.isbn?:""
                val urlToQuery = "https://ndlsearch.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=$isbn"
                val response = SimpleHttpClient().httpGet(urlToQuery, -1)

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
                    val updateData = UpdateRecordInformation(id, updateTitle, updateSubtitle, updateAuthor, updatePublisher, data?.category?:"")
                    activity.runOnUiThread {
                        // ----- 更新が必要な情報を提供する
                        callback.recognizedDataFromIsbnCallback(updateData, isOverwrite)
                    }
                }
                activity.runOnUiThread {
                    // ----- 処理の終了を通知する
                    callback.finishRecognizedDataFromIsbn(needUpdate)

                    // ----- 更新結果をToast表示
                    val resultMessage = if (needUpdate) {
                        activity.getString(R.string.label_data_updated_from_isbn)
                    } else {
                        activity.getString(R.string.label_no_data_from_isbn)
                    }
                    Toast.makeText(activity, resultMessage, Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                // --- データは何もないので、何もない、という。
                activity.runOnUiThread {
                    // ----- 処理の終了を通知する
                    callback.finishRecognizedDataFromIsbn(false)

                    Toast.makeText(activity, activity.getString(R.string.label_no_data_from_isbn), Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun recognizeAllFromIsbn(isOverwrite: Boolean, callback: RecognizeDataFromIsbnCallback?)
    {
        try
        {
            activity.runOnUiThread {
                callback?.startRecognizeFromIsbn()
                Toast.makeText(activity, activity.getString(R.string.label_data_start_update_record), Toast.LENGTH_SHORT).show()
            }

            Log.v(TAG, "recognizeFromIsbn($isOverwrite) : start")
            val updateRecordList = ArrayList<UpdateRecordInformation>()
            updateRecordList.clear()
            storageDao.getAll().forEach { data ->
                try
                {
                    // -----  データ更新用のリストを作る
                    if ((data.isbn?:"").isNotEmpty())
                    {
                        var needUpdate = false
                        val isbn = data.isbn?:""
                        val urlToQuery = "https://ndlsearch.ndl.go.jp/api/sru?operation=searchRetrieve&query=isbn=$isbn"
                        val response = SimpleHttpClient().httpGet(urlToQuery, -1)

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

                        val updateTitle = if ((title.isNotEmpty())&&(((data.title?:"").isEmpty())||(isOverwrite))) {
                            needUpdate = true
                            title
                        } else { data.title ?: "" }

                        val updateSubtitle = if ((subTitle.isNotEmpty())&&(((data.subTitle?:"").isEmpty())||(isOverwrite))) {
                            needUpdate = true
                            subTitle
                        } else { data.subTitle ?: "" }

                        val updateAuthor = if ((author.isNotEmpty())&&(((data.author?:"").isEmpty())||(isOverwrite))) {
                            needUpdate = true
                            author
                        } else { data.author ?: "" }

                        val updatePublisher = if ((publisher.isNotEmpty())&&(((data.publisher?:"").isEmpty())||(isOverwrite))) {
                            needUpdate = true
                            publisher
                        } else { data.publisher ?: "" }

                        if (needUpdate)
                        {
                            updateRecordList.add(UpdateRecordInformation(data.id, updateTitle, updateSubtitle, updateAuthor, updatePublisher, data.category?:""))
                        }
                    }
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
            }
            Log.v(TAG, " ----- recognizeFromIsbn() : update ${updateRecordList.size} records Done.")

            activity.runOnUiThread {
                callback?.finishRecognizeFromIsbn()
                Toast.makeText(activity, "${activity.getString(R.string.label_data_finish_update_record)} ${updateRecordList.size} ${activity.getString(R.string.label_data_finish_update_record_counts)}", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    interface RecognizeDataFromIsbnCallback
    {
        fun startRecognizeFromIsbn()
        fun finishRecognizeFromIsbn()
    }

    companion object
    {
        private val TAG = RecognizeFromIsbn::class.java.simpleName
    }
}
