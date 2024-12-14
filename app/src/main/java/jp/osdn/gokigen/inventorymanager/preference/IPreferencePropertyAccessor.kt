package jp.osdn.gokigen.inventorymanager.preference

interface IPreferencePropertyAccessor
{

    companion object
    {
        // --- PREFERENCE KEY AND DEFAULT VALUE ---
        const val PREFERENCE_CHECK_ISBN_IMMEDIATELY = "check_isbn_immediately"
        const val PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE = false

        // --- CAMERA CONNECTION METHOD PREFERENCES
        const val PREFERENCE_CAMERA_METHOD_INDEX = "camera_method"
        const val PREFERENCE_CAMERA_METHOD_INDEX_DEFAULT_VALUE = "1"

        const val PREFERENCE_CAMERA_METHOD_1 = "camera_method1"
        const val PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE = "camerax"
        const val PREFERENCE_CAMERA_SEQUENCE_1 = "camera_sequence1"
        const val PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE = "0"
        const val PREFERENCE_CAMERA_OPTION1_1 = "camera_option11"
        const val PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE = "WQHD"
        const val PREFERENCE_CAMERA_OPTION2_1 = "camera_option21"
        const val PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE = ""
        const val PREFERENCE_CAMERA_OPTION3_1 = "camera_option31"
        const val PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE = ""
        const val PREFERENCE_CAMERA_OPTION4_1 = "camera_option41"
        const val PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE = ""
        const val PREFERENCE_CAMERA_OPTION5_1 = "camera_option51"
        const val PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE = ""
    }
}