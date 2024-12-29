package jp.osdn.gokigen.inventorymanager.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface DataContentDao
{
    @Query("SELECT * FROM contents")
    fun getAll(): List<DataContent>

    @Query("SELECT * FROM contents WHERE id IN (:ids)")
    fun getAllByIds(ids: LongArray): List<DataContent>

    @Query("SELECT * FROM contents WHERE id = :id LIMIT 1")
    fun findById(id: Long): DataContent?

    @Query("SELECT * FROM contents WHERE category = :category")
    fun findByCategory(category: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE author = :author")
    fun findByAuthor(author: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE publisher = :publisher")
    fun findByPublisher(publisher: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE isbn = :isbn")
    fun findByIsbn(isbn: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE product_id = :productId")
    fun findByProductId(productId: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE title LIKE :mainTitle")
    fun findByMainTitle(mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE sub_title LIKE :subTitle")
    fun findBySubTitle(subTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE checked = :checked")
    fun findByChecked(checked: Boolean): List<DataContent>

    @Query("SELECT * FROM contents WHERE is_delete = :isDelete")
    fun findByIsDelete(isDelete: Boolean): List<DataContent>

    @Query("UPDATE contents SET description = :description, update_date = :updateDate WHERE id = :id")
    fun updateDescription(id: Long, description: String, updateDate: Date)

    @Query("UPDATE contents SET note = :note, update_date = :updateDate WHERE id = :id")
    fun updateNote(id: Long, note: String, updateDate: Date)

    @Query("UPDATE contents SET checked = :checked, inform_message = :informData, inform_date = :informDate, update_date = :updateDate WHERE id = :id")
    fun updateInformation(id: Long, checked: Boolean, informData: String, informDate: Date, updateDate: Date)

    @Query("UPDATE contents SET is_delete = :isDelete, delete_date = :deleteDate WHERE id = :id")
    fun markDelete(id: Long, isDelete: Boolean, deleteDate: Date)

    @Query("UPDATE contents SET title = :title, sub_title = :subTitle, author = :author, publisher = :publisher, category = :category, update_date = :updateDate WHERE id = :id")
    fun updateContent(id: Long, title: String, subTitle: String, author: String, publisher: String, category: String, updateDate: Date)

    @Query("UPDATE contents SET title = :title, sub_title = :subTitle, author = :author, publisher = :publisher, isbn = :isbn, category = :category, note = :note, update_date = :updateDate WHERE id = :id")
    fun updateContentWithIsbn(id: Long, title: String, subTitle: String, author: String, publisher: String, isbn: String, category: String, note: String, updateDate: Date)

    @Query("UPDATE contents SET image_file_1_name = :imageFile1, image_file_2_name = :imageFile2, image_file_3_name = :imageFile3, image_file_4_name = :imageFile4, image_file_5_name = :imageFile5, update_date = :updateDate WHERE id = :id")
    fun setImageFileName(id: Long, imageFile1: String, imageFile2: String, imageFile3: String, imageFile4: String, imageFile5: String, updateDate: Date)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingle(content: DataContent) : Long

    @Insert
    fun insertAll(vararg contents: DataContent)

    @Delete
    fun delete(content: DataContent)
}
