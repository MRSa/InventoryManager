package jp.osdn.gokigen.inventorymanager.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "contents", indices = [Index(value = ["id"], unique = true)])
data class DataContent(
    @PrimaryKey(autoGenerate = true) val id: Long,                     // (id)
    @ColumnInfo(name = "title") val title: String?,                   // AREA 1
    @ColumnInfo(name = "sub_title") val subTitle: String?,            // AREA 2
    @ColumnInfo(name = "author") val author: String?,                 // AREA 3
    @ColumnInfo(name = "publisher") val publisher: String?,           // AREA 4
    @ColumnInfo(name = "description") val description: String?,       // AFTER INPUT AREA (memo)
    @ColumnInfo(name = "isbn") val isbn: String?,                     // BCR (ISBN)
    @ColumnInfo(name = "product_id") val productId: String?,          // BCR (PRD)
    @ColumnInfo(name = "url") val urlStr: String?,                    // BCR (URL)
    @ColumnInfo(name = "bcr_text") val bcrText: String?,             // BCR (TEXT)
    @ColumnInfo(name = "note") val note: String?,                     // TEXT recognition
    @ColumnInfo(name = "category") val category: String?,             // CATEGORY
    @ColumnInfo(name = "image_file_1_name") val imageFile1: String?,  // Image file name (1)
    @ColumnInfo(name = "image_file_2_name") val imageFile2: String?,  // Image file name (2)
    @ColumnInfo(name = "image_file_3_name") val imageFile3: String?,  // Image file name (3)
    @ColumnInfo(name = "image_file_4_name") val imageFile4: String?,  // Image file name (4) : Text
    @ColumnInfo(name = "image_file_5_name") val imageFile5: String?,  // Image file name (5) : BCR
    @ColumnInfo(name = "checked") val checked: Boolean,               // AFTER MARK AREA (check)
    @ColumnInfo(name = "inform_message") val informMessage: String?,  // AFTER MARK AREA (information)
    @ColumnInfo(name = "level") val level: Int,                       // AFTER MARK AREA (level)
    @ColumnInfo(name = "counter") val counter: Int,                   // AFTER MARK AREA (counter)
    @ColumnInfo(name = "inform_date") val informDate: Date?,          // AFTER MARK AREA (date)
    @ColumnInfo(name = "is_delete") val isDelete: Boolean,            // DELETE INFORMATION
    @ColumnInfo(name = "delete_date") val deleteDate: Date?,
    @ColumnInfo(name = "update_date") val updateDate: Date?,
    @ColumnInfo(name = "create_date") val createDate: Date?,
)
{
    companion object {
        fun create(
                   title: String?,
                   subTitle: String?,
                   author: String?,
                   publisher: String?,
                   isbn: String?,
                   productId: String?,
                   urlStr: String?,
                   bcrText: String?,
                   category: String?,
                   imageFile1: String?,
                   imageFile2: String?,
                   imageFile3: String?,
                   imageFile4: String?,
                   imageFile5: String?,
                   textRecognitionData: String?,
                   level: Int = 0,
                   counter: Int = 0,
        ) : DataContent
        {
            val currentDate = Date()
            return (
                    DataContent(
                        id = 0,
                        title = title,
                        subTitle = subTitle,
                        author = author,
                        publisher = publisher,
                        description = "",
                        isbn = isbn,
                        productId = productId,
                        urlStr = urlStr,
                        bcrText = bcrText,
                        note = textRecognitionData,
                        category = category,
                        imageFile1 = imageFile1,
                        imageFile2 = imageFile2,
                        imageFile3 = imageFile3,
                        imageFile4 = imageFile4,
                        imageFile5 = imageFile5,
                        checked = false,
                        informMessage = null,
                        level = level,
                        counter = counter,
                        informDate = null,
                        isDelete = false,
                        deleteDate = null,
                        updateDate = null,
                        createDate = currentDate)
                    )
        }
    }
}
