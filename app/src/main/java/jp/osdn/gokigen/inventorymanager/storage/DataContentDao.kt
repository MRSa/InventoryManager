package jp.osdn.gokigen.inventorymanager.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface DataContentDao
{
    @Query("SELECT * FROM contents")
    fun getAll(): List<DataContent>

    @Query("SELECT * FROM contents WHERE id IN (:ids)")
    fun getAllByIds(ids: IntArray): List<DataContent>

    @Query("SELECT * FROM contents WHERE id = :id LIMIT 1")
    fun findById(id: Int): DataContent?

    @Query("SELECT * FROM contents WHERE hash_value = :hash")
    fun findByHash(hash: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category")
    fun findByCategory(category: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE author = :author")
    fun findByAuthor(author: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE publisher = :publisher")
    fun findByPublisher(publisher: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE publisher = :isbn")
    fun findByIsbn(isbn: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE title LIKE :mainTitle")
    fun findByMainTitle(mainTitle: String): List<DataContent>

    @Query("UPDATE contents SET description = :description, update_date = :updateDate WHERE id = :id")
    fun updateDescription(id: Int, description: String, updateDate: Date)

    @Query("UPDATE contents SET checked = :checked, inform_message = :informData, inform_date = :informDate, update_date = :updateDate WHERE id = :id")
    fun updateInformation(id: Int, checked: Boolean, informData: String, informDate: Date, updateDate: Date)

    @Query("UPDATE contents SET is_delete = :isDelete, delete_date = :deleteDate WHERE id = :id")
    fun markDelete(id: Int, isDelete: Boolean, deleteDate: Date)

    @Query("UPDATE contents SET title = :title, sub_title = :subTitle, author = :author, publisher = :publisher, category = :category WHERE id = :id")
    fun updateContent(id: Int, title: String, subTitle: String, author: String, publisher: String, category: String, updateDate: Date)

    @Insert
    fun insertAll(vararg contents: DataContent)

    @Delete
    fun delete(content: DataContent)
}
