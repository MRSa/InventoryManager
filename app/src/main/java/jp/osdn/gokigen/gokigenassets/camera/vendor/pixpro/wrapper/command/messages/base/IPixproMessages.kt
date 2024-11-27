package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base

interface IPixproMessages
{
    companion object
    {
        const val  SEQ_DUMMY = 0
        const val  SEQ_RECEIVE_ONLY = 1

        const val  SEQ_EVENT_REQUEST = 5

        const val  SEQ_SHUTTER = 10
        const val  SEQ_FOCUS = 11
        const val  SEQ_ZOOM = 12
        const val  SEQ_VIDEO = 13

        const val  SEQ_FLASH_OFF = 20
        const val  SEQ_FLASH_ON = 21
        const val  SEQ_FLASH_AUTO = 22

        const val  SEQ_WHITE_BALANCE = 30
        const val  SEQ_EXP_REV = 31
        const val  SEQ_ISO_SENSITIVITY = 32
        const val  SEQ_SHUTTER_SPEED = 33

        const val  SEQ_IMAGE_SIZE = 34
        const val  SEQ_VIDEO_SIZE = 35

        const val  SEQ_CHANGE_MODE_VIDEO = 40
        const val  SEQ_CHANGE_MODE = 41

        const val  SEQ_CONNECT_01 = 101
        const val  SEQ_CONNECT_02 = 102
        const val  SEQ_CONNECT_03 = 103
        const val  SEQ_CONNECT_04 = 104
        const val  SEQ_CONNECT_05 = 105
        const val  SEQ_CONNECT_06 = 106
        const val  SEQ_CONNECT_07 = 107
        const val  SEQ_CONNECT_08 = 108
        const val  SEQ_CONNECT_09 = 109
        const val  SEQ_CONNECT_10 = 110
        const val  SEQ_CONNECT_11 = 111
    }
}
