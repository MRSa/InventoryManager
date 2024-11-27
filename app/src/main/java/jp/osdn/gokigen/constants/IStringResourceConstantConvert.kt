package jp.osdn.gokigen.constants

import jp.osdn.gokigen.inventorymanager.R

interface IStringResourceConstantConvert
{
    companion object
    {
        val ID_STRING_CAMERA_NOT_FOUND = R.string.camera_not_found
        val ID_STRING_CAMERA_CONNECT_RESPONSE_NG = R.string.camera_connect_response_ng

        val ID_STRING_DIALOG_TITLE_CONNECT_FAILED = R.string.dialog_title_connect_failed
        val ID_STRING_DIALOG_BUTTON_RETRY = R.string.dialog_button_retry
        val ID_STRING_DIALOG_BUTTON_NETWORK_SETTINGS = R.string.dialog_button_network_settings

        val ID_STRING_CONNECT_START = R.string.connect_start
        val ID_STRING_CONNECT_CHECK_WIFI = R.string.connect_check_wifi
        val ID_STRING_CONNECT_CONNECT = R.string.connect_connect
        val ID_STRING_CONNECT_CONNECTING = R.string.connect_connecting
        val ID_STRING_CONNECT_CONNECTED = R.string.connect_connected
        val ID_STRING_CONNECT_CONNECT_FINISHED = R.string.connect_connect_finished
        val ID_STRING_CONNECT_CHANGE_RUN_MODE = R.string.connect_change_run_mode
        val ID_STRING_CONNECT_CAMERA_DETECTED = R.string.connect_camera_detected
        val ID_STRING_CONNECT_CAMERA_SEARCH_REQUEST = R.string.connect_camera_search_request
        val ID_STRING_CONNECT_CAMERA_FOUND = R.string.connect_camera_found
        val ID_STRING_CONNECT_WAIT_REPLY_CAMERA = R.string.connect_camera_wait_reply
        val ID_STRING_CONNECT_CAMERA_RECEIVED_REPLY = R.string.connect_camera_received_reply
        val ID_STRING_CONNECT_CAMERA_REJECTED = R.string.connect_camera_rejected
        val ID_STRING_CONNECT_UNKNOWN_MESSAGE = R.string.connect_receive_unknown_message

        val ID_STRING_COMMAND_LINE_DISCONNECTED = R.string.command_line_disconnected

        val ID_LABEL_APP_NAME = R.string.app_name
        val ID_LABEL_APP_LOCATION = R.string.app_location

        val ID_DIALOG_TITLE_CONFIRMATION = R.string.dialog_title_confirmation
        val ID_DIALOG_EXIT_APPLICATION = R.string.dialog_message_exit_application
        val ID_DIALOG_EXIT_POWER_OFF = R.string.dialog_message_power_off

        val ID_DIALOG_BUTTON_LABEL_POSITIVE = R.string.dialog_positive_execute
        val ID_DIALOG_BUTTON_LABEL_NEGATIVE = R.string.dialog_negative_cancel

        val ID_MESSAGE_LABEL_CAPTURE_SUCCESS = R.string.capture_success

        val ID_LABEL_FINISHED_REFRESH = R.string.finish_refresh
        val ID_LABEL_TITLE_CONFIRMATION_FOR_EXPORT_LOG = R.string.dialog_confirm_title_output_log
        val ID_LABEL_MESSAGE_CONFIRMATION_FOR_EXPORT_LOG = R.string.dialog_confirm_message_output_log
    }
}
