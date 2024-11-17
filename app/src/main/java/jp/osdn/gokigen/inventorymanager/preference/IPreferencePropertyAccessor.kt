package jp.osdn.gokigen.inventorymanager.preference

interface IPreferencePropertyAccessor
{

    companion object
    {
        // --- PREFERENCE KEY AND DEFAULT VALUE ---
        const val PREFERENCE_CHECK_ISBN_IMMEDIATELY = "check_isbn_immediately"
        const val PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE = false
    }
}