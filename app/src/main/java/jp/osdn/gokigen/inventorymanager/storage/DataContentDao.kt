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

    fun getAllOrderBy(isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (getAll().sortedBy { it.updateDate })
            } else {
                (getAll().sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (getAll().sortedByDescending { it.updateDate })
            } else {
                (getAll().sortedByDescending { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents ORDER BY create_date DESC")
    fun getAllCreateLatest(): List<DataContent>

    @Query("SELECT * FROM contents WHERE id IN (:ids)")
    fun getAllByIds(ids: LongArray): List<DataContent>

    @Query("SELECT DISTINCT category FROM contents")
    fun getCategories(): List<String>

    @Query("SELECT * FROM contents WHERE id = :id LIMIT 1")
    fun findById(id: Long): DataContent?

    @Query("SELECT * FROM contents WHERE category = :category")
    fun findByCategory(category: String): List<DataContent>

    fun findByCategoryOrderBy(category: String, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByCategory(category).sortedBy { it.updateDate })
            } else {
                (findByCategory(category).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByCategory(category).sortedByDescending  { it.updateDate })
            } else {
                (findByCategory(category).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE category = :category AND level = :rating")
    fun findByCategoryWithRatingEqual(category: String, rating: Int): List<DataContent>

    fun findByCategoryWithRatingEqualOrderBy(category: String, rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByCategoryWithRatingEqual(category, rating).sortedBy { it.updateDate })
            } else {
                (findByCategoryWithRatingEqual(category, rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByCategoryWithRatingEqual(category, rating).sortedByDescending  { it.updateDate })
            } else {
                (findByCategoryWithRatingEqual(category, rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE category = :category AND level != :rating")
    fun findByCategoryWithRatingNotEqual(category: String, rating: Int): List<DataContent>

    fun findByCategoryWithRatingNotEqualOrderBy(category: String, rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByCategoryWithRatingNotEqual(category, rating).sortedBy { it.updateDate })
            } else {
                (findByCategoryWithRatingNotEqual(category, rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByCategoryWithRatingNotEqual(category, rating).sortedByDescending  { it.updateDate })
            } else {
                (findByCategoryWithRatingNotEqual(category, rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE category = :category AND level >= :rating")
    fun findByCategoryWithRatingOver(category: String, rating: Int): List<DataContent>

    fun findByCategoryWithRatingOverOrderBy(category: String, rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByCategoryWithRatingOver(category, rating).sortedBy { it.updateDate })
            } else {
                (findByCategoryWithRatingOver(category, rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByCategoryWithRatingOver(category, rating).sortedByDescending  { it.updateDate })
            } else {
                (findByCategoryWithRatingOver(category, rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE category = :category AND level <= :rating")
    fun findByCategoryWithRatingUnder(category: String, rating: Int): List<DataContent>

    fun findByCategoryWithRatingUnderOrderBy(category: String, rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByCategoryWithRatingUnder(category, rating).sortedBy { it.updateDate })
            } else {
                (findByCategoryWithRatingUnder(category, rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByCategoryWithRatingUnder(category, rating).sortedByDescending  { it.updateDate })
            } else {
                (findByCategoryWithRatingUnder(category, rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE level = :rating")
    fun findRatingEqual(rating: Int): List<DataContent>

    fun findByRatingEqualOrderBy(rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findRatingEqual(rating).sortedBy { it.updateDate })
            } else {
                (findRatingEqual(rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findRatingEqual(rating).sortedByDescending  { it.updateDate })
            } else {
                (findRatingEqual(rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE level != :rating")
    fun findByRatingNotEqual(rating: Int): List<DataContent>

    fun findByRatingNotEqualOrderBy(rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByRatingNotEqual(rating).sortedBy { it.updateDate })
            } else {
                (findByRatingNotEqual(rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByRatingNotEqual(rating).sortedByDescending  { it.updateDate })
            } else {
                (findByRatingNotEqual(rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE level >= :rating")
    fun findByRatingOver(rating: Int): List<DataContent>

    fun findByRatingOverOrderBy(rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByRatingOver(rating).sortedBy { it.updateDate })
            } else {
                (findByRatingOver(rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByRatingOver(rating).sortedByDescending  { it.updateDate })
            } else {
                (findByRatingOver(rating).sortedByDescending  { it.createDate })
            }
        }
    }

    @Query("SELECT * FROM contents WHERE level <= :rating")
    fun findByRatingUnder(rating: Int): List<DataContent>

    fun findByRatingUnderOrderBy(rating: Int, isUpdateDate: Boolean, isAscending: Boolean): List<DataContent> {
        return if (isAscending) {
            if (isUpdateDate) {
                (findByRatingUnder(rating).sortedBy { it.updateDate })
            } else {
                (findByRatingUnder(rating).sortedBy { it.createDate })
            }
        } else {
            if (isUpdateDate) {
                (findByRatingUnder(rating).sortedByDescending  { it.updateDate })
            } else {
                (findByRatingUnder(rating).sortedByDescending  { it.createDate })
            }
        }
    }

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

    @Query("UPDATE contents SET title = :title, sub_title = :subTitle, author = :author, publisher = :publisher, description = :description, isbn = :isbn, category = :category, note = :note, level = :level, counter = :counter, update_date = :updateDate WHERE id = :id")
    fun updateContentWithIsbn(id: Long, title: String, subTitle: String, author: String, publisher: String, description: String, isbn: String, category: String, note: String, level: Int, counter: Int, updateDate: Date)

    @Query("UPDATE contents SET image_file_1_name = :imageFile1, image_file_2_name = :imageFile2, image_file_3_name = :imageFile3, image_file_4_name = :imageFile4, image_file_5_name = :imageFile5, update_date = :updateDate WHERE id = :id")
    fun setImageFileName(id: Long, imageFile1: String, imageFile2: String, imageFile3: String, imageFile4: String, imageFile5: String, updateDate: Date)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingle(content: DataContent) : Long

    @Insert
    fun insertAll(vararg contents: DataContent)

    @Delete
    fun delete(content: DataContent)
}
