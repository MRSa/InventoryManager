package jp.osdn.gokigen.inventorymanager.storage

data class FilterState(
    var isTitleChecked: Boolean = false,
    var targetTitle: String = "",
    var isCategoryChecked: Boolean = false,
    var selectedCategory: String = "",
    var isOperatorChecked: Boolean = false,
    var selectedOperatorIndex: Int = 0,
    var selectedFilterRating: Int = 0,
    var sortOrderDirection: SortOrderDirection = SortOrderDirection.CREATE_NEWEST
)
