package jp.osdn.gokigen.inventorymanager.export

import kotlinx.serialization.Serializable

@Serializable
data class DataContentSerializable(
    val id: Long = 0,
    val title: String? = "",
    val subTitle: String? = "",       // AREA 2
    val author: String? = "",         // AREA 3
    val publisher: String? = "",      // AREA 4
    val description: String? = "",    // AFTER INPUT AREA (memo)
    val isbn: String? = "",           // BCR (ISBN)
    val productId: String? = "",      // BCR (PRD)
    val urlStr: String? = "",         // BCR (URL)
    val bcrText: String? = "",        // BCR (TEXT)
    val note: String? = "",           // TEXT recognition
    val category: String? = "",       // CATEGORY
    val imageFile1: String? = "",     // Image file name (1)
    val imageFile2: String? = "",     // Image file name (2)
    val imageFile3: String? = "",     // Image file name (3)
    val imageFile4: String? = "",     // Image file name (4) : Text
    val imageFile5: String? = "",     // Image file name (5) : BCR
    val checked: Boolean = false,     // AFTER MARK AREA (check)
    val informMessage: String? = "",  // AFTER MARK AREA (information)
    val informDate: Long? = 0,        // AFTER MARK AREA (date)
    val isDelete: Boolean = false,    // DELETE INFORMATION
    val deleteDate: Long? = 0,
    val updateDate: Long? = 0,
    val createDate: Long? = 0,
)

@Serializable
data class DataContentListSerializer(val list: List<DataContentSerializable>)
