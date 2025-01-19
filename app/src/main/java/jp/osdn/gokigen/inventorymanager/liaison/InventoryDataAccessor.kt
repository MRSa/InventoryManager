package jp.osdn.gokigen.inventorymanager.liaison

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.export.InOutExportImage
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbn
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromProductId
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import java.util.Date

class InventoryDataAccessor(private val activity: ComponentActivity)
{
    private val imageInOutManager = InOutExportImage(activity)
    fun entryData(category: String, data1: String, data2: String, data3: String, data4: String,
                  data5: String, isbn: String, productId: String, readText: String, readUrl: String,
                  image1: Bitmap?, image2: Bitmap?, image3: Bitmap?, image4: Bitmap?, image5: Bitmap?)
    {
        try
        {
            if ((data1.isNotEmpty())||(data2.isNotEmpty())||(data3.isNotEmpty())||
                (data4.isNotEmpty())||(data5.isNotEmpty())||(isbn.isNotEmpty())||
                (productId.isNotEmpty())||(readText.isNotEmpty())||(readUrl.isNotEmpty())||
                (image1 != null)||(image2 != null)||(image3 != null)||
                (image4 != null)||(image5 != null))
            {
                Thread {
                    entryDataImpl(category, data1, data2, data3, data4, data5, isbn, productId, readText, readUrl, image1, image2, image3, image4, image5)
                }.start()
            }
            else
            {
                // ----- 「データが登録されていません」を表示する
                activity.runOnUiThread {
                    Toast.makeText(activity, activity.getString(R.string.label_data_not_entry), Toast.LENGTH_SHORT).show()
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun entryDataImpl(
        category: String,
        data1: String,    // title
        data2: String,    // subTitle
        data3: String,    // author
        data4: String,    // publisher
        data5: String,    // textRecognitionData
        isbn: String,
        productId: String,
        readText: String,
        readUrl: String,
        image1: Bitmap?,
        image2: Bitmap?,
        image3: Bitmap?,
        image4: Bitmap?,
        image5: Bitmap?
    )
    {
        try
        {
            val storageDao = AppSingleton.db.storageDao()

            Log.v(TAG, "data entry ... : $category, $data1, $data2, $data3, $data4, $data5, $isbn, $productId, $readText, $readUrl")
            var title = data1
            var subTitle = data2
            var author = data3
            var publisher = data4
            var dataIsUpdated = false
            var description = ""

            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isCheck = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY,
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE
            )
            val isOverWrite = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )
            if (isCheck)
            {
                // ----- 登録時にバーコードデータの情報から題名等を取得する設定だった場合...
                if (isbn.isNotEmpty()) {
                    // ----- ISBNが取得できていた場合の特殊処理...
                    val recognizer = RecognizeFromIsbn(activity)
                    val response = recognizer.recognizeInformationFromIsbn(
                        isbn = isbn,
                        title = data1,
                        subTitle = data2,
                        author = data3,
                        publisher = data4
                    )

                    // 結果をコピー
                    title = response.title
                    subTitle = response.appendData
                    author = response.author
                    publisher = response.publisher
                    dataIsUpdated = response.isHit
                }
                if ((!dataIsUpdated)&&((productId.isNotEmpty())||(isbn.isNotEmpty())))
                {
                    // ----- product IDが取得できていた場合の処理 (ただし、先にISBNでデータが更新されていた場合は除く)
                    val recognizer = RecognizeFromProductId()
                    val response = recognizer.recognizeInformationFromProductId(
                        isOverWrite = isOverWrite,
                        isbn = isbn,
                        productId = productId,
                        title = data1,
                        description = description,
                        author = data3,
                        publisher = data4
                    )

                    // 結果をコピー
                    title = response.title
                    description = response.appendData
                    author = response.author
                    publisher = response.publisher
                }
            }

            // データをデータベースに登録する
            val content = DataContent.create(title, subTitle, author, publisher, isbn, productId ,readUrl, readText, category, "", "", "", "", "", data5, description)
            val entryId = storageDao.insertSingle(content)

            val image1FileName = if (image1 != null) { "${entryId}_img01.jpg" } else { "" }
            val image2FileName = if (image2 != null) { "${entryId}_img02.jpg" } else { "" }
            val image3FileName = if (image3 != null) { "${entryId}_img03.jpg" } else { "" }
            val image4FileName = if (image4 != null) { "${entryId}_img04.jpg" } else { "" }
            val image5FileName = if (image5 != null) { "${entryId}_img05.jpg" } else { "" }

            if (image1FileName.isNotEmpty())
            {
                imageInOutManager.storeImageLocal(entryId, fileName = image1FileName, image1)
            }
            if (image2FileName.isNotEmpty())
            {
                imageInOutManager.storeImageLocal(entryId, fileName = image2FileName, image2)
            }
            if (image3FileName.isNotEmpty())
            {
                imageInOutManager.storeImageLocal(entryId, fileName = image3FileName, image3)
            }
            if (image4FileName.isNotEmpty())
            {
                imageInOutManager.storeImageLocal(entryId, fileName = image4FileName, image4)
            }
            if (image5FileName.isNotEmpty())
            {
                imageInOutManager.storeImageLocal(entryId, fileName = image5FileName, image5)
            }
            storageDao.setImageFileName(entryId, image1FileName, image2FileName, image3FileName, image4FileName, image5FileName, Date())

            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.result_data_entry)}[$entryId]: $title $author", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val  TAG = InventoryDataAccessor::class.java.simpleName
    }
}
