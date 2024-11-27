package jp.osdn.gokigen.constants

import jp.osdn.gokigen.inventorymanager.R

interface IPreferenceConstantConvert
{
    companion object
    {
        val ID_LAYOUT_PREFERENCE = R.xml.preference_main

        const val ID_PREFERENCE_LABEL_EXIT_APPLICATION = "exit_application"
        const val ID_PREFERENCE_LABEL_WIFI_SETTINGS = "wifi_settings"
        const val ID_PREFERENCE_LABEL_DEBUG_INFO = "debug_info"

        const val ID_PREFERENCE_SAVE_LOCAL_LOCATION = "save_local_location"
        const val ID_PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE = false

        const val ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW = "capture_both_camera_and_live_view"
        const val ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE = false

        const val ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE = "capture_only_live_view"
        const val ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE = false

        const val ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION = "external_storage_location"
        const val ID_PREFERENCE_EXTERNAL_STORAGE_LOCATION_DEFAULT_VALUE = ""

        const val ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES = "cache_live_view_pictures"
        const val ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES = "number_of_cache_pictures"
        const val ID_PREFERENCE_NUMBER_OF_CACHE_PICTURES_DEFAULT_VALUE = "500"

        const val ID_PREFERENCE_RICOH_GR2_LCD_SLEEP = "gr2_lcd_sleep"
        const val ID_PREFERENCE_USE_GR2_SPECIAL_COMMAND = "use_gr2_command"

        const val ID_PREFERENCE_SELF_TIMER_SECONDS = "self_timer_count"
        const val ID_PREFERENCE_SELF_TIMER_SECONDS_DEFAULT_VALUE = "0"
        val ID_PREFERENCE_SELF_TIMER_ARRAY = R.array.self_timer_count
        val ID_PREFERENCE_SELF_TIMER_ARRAY_VALUE = R.array.self_timer_count_value

        const val ID_PREFERENCE_LABEL_SELECT_CAMERA_CONNECTION_METHOD = "connection_method"

        const val ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION = "theta_liveview_resolution"
        const val ID_PREFERENCE_THETA_LIVEVIEW_RESOLUTION_DEFAULT_VALUE = "{\"width\": 640, \"height\": 320, \"framerate\": 30}"

        val ID_PREFERENCE_ARRAY_CAMERA_METHOD = R.array.connection_method
        val ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE = R.array.connection_sequence
        val ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE = R.array.connection_method_value
        val ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE = R.array.connection_sequence_value

        const val ID_PREFERENCE_SHOW_GRID = "show_grid_frame"
        const val ID_PREFERENCE_SHOW_GRID_DEFAULT_VALUE = "0"
        val ID_PREFERENCE_ARRAY_SHOW_GRID = R.array.array_show_grid
        val ID_PREFERENCE_ARRAY_SHOW_GRID_VALUE = R.array.array_show_grid_value

        const val ID_PREFERENCE_CAMERA_METHOD_1 = "camera_method1"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_1 = "camera_sequence1"
        const val ID_PREFERENCE_CAMERA_OPTION1_1 = "camera_option11"
        const val ID_PREFERENCE_CAMERA_OPTION2_1 = "camera_option21"
        const val ID_PREFERENCE_CAMERA_OPTION3_1 = "camera_option31"
        const val ID_PREFERENCE_CAMERA_OPTION4_1 = "camera_option41"
        const val ID_PREFERENCE_CAMERA_OPTION5_1 = "camera_option51"

        const val ID_PREFERENCE_CAMERA_METHOD_2 = "camera_method2"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_2 = "camera_sequence2"
        const val ID_PREFERENCE_CAMERA_OPTION1_2 = "camera_option12"
        const val ID_PREFERENCE_CAMERA_OPTION2_2 = "camera_option22"
        const val ID_PREFERENCE_CAMERA_OPTION3_2 = "camera_option32"
        const val ID_PREFERENCE_CAMERA_OPTION4_2 = "camera_option42"
        const val ID_PREFERENCE_CAMERA_OPTION5_2 = "camera_option52"

        const val ID_PREFERENCE_CAMERA_METHOD_3 = "camera_method3"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_3 = "camera_sequence3"
        const val ID_PREFERENCE_CAMERA_OPTION1_3 = "camera_option13"
        const val ID_PREFERENCE_CAMERA_OPTION2_3 = "camera_option23"
        const val ID_PREFERENCE_CAMERA_OPTION3_3 = "camera_option33"
        const val ID_PREFERENCE_CAMERA_OPTION4_3 = "camera_option43"
        const val ID_PREFERENCE_CAMERA_OPTION5_3 = "camera_option53"

        const val ID_PREFERENCE_CAMERA_METHOD_4 = "camera_method4"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_4 = "camera_sequence4"
        const val ID_PREFERENCE_CAMERA_OPTION1_4 = "camera_option14"
        const val ID_PREFERENCE_CAMERA_OPTION2_4 = "camera_option24"
        const val ID_PREFERENCE_CAMERA_OPTION3_4 = "camera_option34"
        const val ID_PREFERENCE_CAMERA_OPTION4_4 = "camera_option44"
        const val ID_PREFERENCE_CAMERA_OPTION5_4 = "camera_option54"

        const val ID_PREFERENCE_CAMERA_METHOD_5 = "camera_method5"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_5 = "camera_sequence5"
        const val ID_PREFERENCE_CAMERA_OPTION1_5 = "camera_option15"
        const val ID_PREFERENCE_CAMERA_OPTION2_5 = "camera_option25"
        const val ID_PREFERENCE_CAMERA_OPTION3_5 = "camera_option35"
        const val ID_PREFERENCE_CAMERA_OPTION4_5 = "camera_option45"
        const val ID_PREFERENCE_CAMERA_OPTION5_5 = "camera_option55"

        const val ID_PREFERENCE_CAMERA_METHOD_6 = "camera_method6"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_6 = "camera_sequence6"
        const val ID_PREFERENCE_CAMERA_OPTION1_6 = "camera_option16"
        const val ID_PREFERENCE_CAMERA_OPTION2_6 = "camera_option26"
        const val ID_PREFERENCE_CAMERA_OPTION3_6 = "camera_option36"
        const val ID_PREFERENCE_CAMERA_OPTION4_6 = "camera_option46"
        const val ID_PREFERENCE_CAMERA_OPTION5_6 = "camera_option56"

        const val ID_PREFERENCE_CAMERA_METHOD_7 = "camera_method7"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_7 = "camera_sequence7"
        const val ID_PREFERENCE_CAMERA_OPTION1_7 = "camera_option17"
        const val ID_PREFERENCE_CAMERA_OPTION2_7 = "camera_option27"
        const val ID_PREFERENCE_CAMERA_OPTION3_7 = "camera_option37"
        const val ID_PREFERENCE_CAMERA_OPTION4_7 = "camera_option47"
        const val ID_PREFERENCE_CAMERA_OPTION5_7 = "camera_option57"

        const val ID_PREFERENCE_CAMERA_METHOD_8 = "camera_method8"
        const val ID_PREFERENCE_CAMERA_SEQUENCE_8 = "camera_sequence8"
        const val ID_PREFERENCE_CAMERA_OPTION1_8 = "camera_option18"
        const val ID_PREFERENCE_CAMERA_OPTION2_8 = "camera_option28"
        const val ID_PREFERENCE_CAMERA_OPTION3_8 = "camera_option38"
        const val ID_PREFERENCE_CAMERA_OPTION4_8 = "camera_option48"
        const val ID_PREFERENCE_CAMERA_OPTION5_8 = "camera_option58"
    }
}
