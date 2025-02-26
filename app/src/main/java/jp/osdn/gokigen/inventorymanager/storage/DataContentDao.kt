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

    @Query("SELECT * FROM contents WHERE category = :category AND level = :rating")
    fun findByCategoryWithRatingEqual(category: String, rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level != :rating")
    fun findByCategoryWithRatingNotEqual(category: String, rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level >= :rating")
    fun findByCategoryWithRatingOver(category: String, rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level <= :rating")
    fun findByCategoryWithRatingUnder(category: String, rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE level = :rating")
    fun findRatingEqual(rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE level != :rating")
    fun findRatingNotEqual(rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE level >= :rating")
    fun findRatingOver(rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE level <= :rating")
    fun findRatingUnder(rating: Int): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND title LIKE :mainTitle")
    fun findByCategoryWithTitle(category: String, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level = :rating AND title LIKE :mainTitle")
    fun findByCategoryWithRatingEqualWithTitle(category: String, rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level != :rating AND title LIKE :mainTitle")
    fun findByCategoryWithRatingNotEqualWithTitle(category: String, rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level >= :rating AND title LIKE :mainTitle")
    fun findByCategoryWithRatingOverWithTitle(category: String, rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE category = :category AND level <= :rating AND title LIKE :mainTitle")
    fun findByCategoryWithRatingUnderWithTitle(category: String, rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE level = :rating AND title LIKE :mainTitle")
    fun findRatingEqualWithTitle(rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE level != :rating AND title LIKE :mainTitle")
    fun findRatingNotEqualWithTitle(rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE level >= :rating AND title LIKE :mainTitle")
    fun findRatingOverWithTitle(rating: Int, mainTitle: String): List<DataContent>

    @Query("SELECT * FROM contents WHERE level <= :rating AND title LIKE :mainTitle")
    fun findRatingUnderWithTitle(rating: Int, mainTitle: String): List<DataContent>

    fun getDataListWithFilter(filterState: FilterState): List<DataContent> {
        if (!filterState.isTitleChecked)
        {
            // ----- タイトルで絞り込みを行わない場合の処理...
            return (getDataListWithFilterButNotSpecifiedTitle(filterState))
        }
        // ----- タイトルで絞り込みを行う場合の処理...
        return (getDataListWithFilterWithTitle(filterState))
    }

    fun getDataListWithFilterButNotSpecifiedTitle(filterState: FilterState): List<DataContent> {
        // ----- タイトルで絞り込みを行わない場合の処理...
        if ((filterState.isCategoryChecked)&&(filterState.isOperatorChecked)) {
            when (filterState.selectedOperatorIndex) {
                0 -> {  //  =
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.title})
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                1 -> {  // !=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingNotEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                2 -> {  // >=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingOver(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                3 -> {  // <=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingUnder(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.publisher })

                    })
                }
                else -> {
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingEqual(filterState.selectedCategory, filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
            }
        } else if (filterState.isCategoryChecked) {
            return (when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> (findByCategory(filterState.selectedCategory).sortedByDescending { it.createDate })
                SortOrderDirection.CREATE_OLDEST -> (findByCategory(filterState.selectedCategory).sortedBy { it.createDate })
                SortOrderDirection.UPDATE_NEWEST -> (findByCategory(filterState.selectedCategory).sortedByDescending { it.updateDate })
                SortOrderDirection.UPDATE_OLDEST -> (findByCategory(filterState.selectedCategory).sortedBy { it.updateDate })
                SortOrderDirection.TITLE_DESCENDING -> (findByCategory(filterState.selectedCategory).sortedByDescending { it.title })
                SortOrderDirection.TITLE_ASCENDING -> (findByCategory(filterState.selectedCategory).sortedBy { it.title })
                SortOrderDirection.AUTHOR_DESCENDING -> (findByCategory(filterState.selectedCategory).sortedByDescending { it.author })
                SortOrderDirection.AUTHOR_ASCENDING -> (findByCategory(filterState.selectedCategory).sortedBy { it.author })
                SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategory(filterState.selectedCategory).sortedByDescending { it.publisher })
                SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategory(filterState.selectedCategory).sortedBy { it.publisher })

            })
        } else if (filterState.isOperatorChecked) {
            when (filterState.selectedOperatorIndex) {
                0 -> {  //  =
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.publisher })

                    })
                }
                1 -> {  // !=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingNotEqual(filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingNotEqual(filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingNotEqual(filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingNotEqual(filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingNotEqual(filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                2 -> {  // >=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingOver(filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingOver(filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingOver(filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingOver(filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingOver(filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                3 -> {  // <=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingUnder(filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingUnder(filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingUnder(filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingUnder(filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingUnder(filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
                else -> {
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingEqual(filterState.selectedFilterRating).sortedBy { it.publisher })
                    })
                }
            }
        } else {
            return (when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> (getAll().sortedByDescending { it.createDate })
                SortOrderDirection.CREATE_OLDEST -> (getAll().sortedBy { it.createDate })
                SortOrderDirection.UPDATE_NEWEST -> (getAll().sortedByDescending { it.updateDate })
                SortOrderDirection.UPDATE_OLDEST -> (getAll().sortedBy { it.updateDate })
                SortOrderDirection.TITLE_DESCENDING -> (getAll().sortedByDescending { it.title })
                SortOrderDirection.TITLE_ASCENDING -> (getAll().sortedBy { it.title })
                SortOrderDirection.AUTHOR_DESCENDING -> (getAll().sortedByDescending { it.author })
                SortOrderDirection.AUTHOR_ASCENDING -> (getAll().sortedBy { it.author })
                SortOrderDirection.PUBLISHER_DESCENDING -> (getAll().sortedByDescending { it.publisher })
                SortOrderDirection.PUBLISHER_ASCENDING -> (getAll().sortedBy { it.publisher })
            })
        }
    }

    fun getDataListWithFilterWithTitle(filterState: FilterState): List<DataContent> {
        // ----- タイトルで絞り込みを行う場合の処理...
        if ((filterState.isCategoryChecked)&&(filterState.isOperatorChecked)) {
            when (filterState.selectedOperatorIndex) {
                0 -> {  //  =
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title})
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                1 -> {  // !=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingNotEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                2 -> {  // >=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingOverWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                3 -> {  // <=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingUnderWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                else -> {
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithRatingEqualWithTitle(filterState.selectedCategory, filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
            }
        } else if (filterState.isCategoryChecked) {
            return (when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                SortOrderDirection.CREATE_OLDEST -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                SortOrderDirection.UPDATE_NEWEST -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                SortOrderDirection.UPDATE_OLDEST -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                SortOrderDirection.TITLE_DESCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                SortOrderDirection.TITLE_ASCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedBy { it.title })
                SortOrderDirection.AUTHOR_DESCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                SortOrderDirection.AUTHOR_ASCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedBy { it.author })
                SortOrderDirection.PUBLISHER_DESCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                SortOrderDirection.PUBLISHER_ASCENDING -> (findByCategoryWithTitle(filterState.selectedCategory, "%${filterState.targetTitle}%").sortedBy { it.publisher })

            })
        } else if (filterState.isOperatorChecked) {
            when (filterState.selectedOperatorIndex) {
                0 -> {  //  =
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })

                    })
                }
                1 -> {  // !=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingNotEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                2 -> {  // >=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingOverWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                3 -> {  // <=
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingUnderWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
                else -> {
                    return (when (filterState.sortOrderDirection) {
                        SortOrderDirection.CREATE_NEWEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                        SortOrderDirection.CREATE_OLDEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.createDate })
                        SortOrderDirection.UPDATE_NEWEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                        SortOrderDirection.UPDATE_OLDEST -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.updateDate })
                        SortOrderDirection.TITLE_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.title })
                        SortOrderDirection.TITLE_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.title })
                        SortOrderDirection.AUTHOR_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.author })
                        SortOrderDirection.AUTHOR_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.author })
                        SortOrderDirection.PUBLISHER_DESCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                        SortOrderDirection.PUBLISHER_ASCENDING -> (findRatingEqualWithTitle(filterState.selectedFilterRating, "%${filterState.targetTitle}%").sortedBy { it.publisher })
                    })
                }
            }
        } else {
            return (when (filterState.sortOrderDirection) {
                SortOrderDirection.CREATE_NEWEST -> (findByMainTitle("%${filterState.targetTitle}%").sortedByDescending { it.createDate })
                SortOrderDirection.CREATE_OLDEST -> (findByMainTitle("%${filterState.targetTitle}%").sortedBy { it.createDate })
                SortOrderDirection.UPDATE_NEWEST -> (findByMainTitle("%${filterState.targetTitle}%").sortedByDescending { it.updateDate })
                SortOrderDirection.UPDATE_OLDEST -> (findByMainTitle("%${filterState.targetTitle}%").sortedBy { it.updateDate })
                SortOrderDirection.TITLE_DESCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedByDescending { it.title })
                SortOrderDirection.TITLE_ASCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedBy { it.title })
                SortOrderDirection.AUTHOR_DESCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedByDescending { it.author })
                SortOrderDirection.AUTHOR_ASCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedBy { it.author })
                SortOrderDirection.PUBLISHER_DESCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedByDescending { it.publisher })
                SortOrderDirection.PUBLISHER_ASCENDING -> (findByMainTitle("%${filterState.targetTitle}%").sortedBy { it.publisher })
            })
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

    @Query("UPDATE contents SET category = :newCategory, update_date = :updateDate WHERE category = :originalCategory")
    fun updateCategory(originalCategory: String, newCategory: String, updateDate: Date)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingle(content: DataContent) : Long

    @Insert
    fun insertAll(vararg contents: DataContent)

    @Delete
    fun delete(content: DataContent)
}
