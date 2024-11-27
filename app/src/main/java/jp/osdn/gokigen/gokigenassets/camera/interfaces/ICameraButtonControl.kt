package jp.osdn.gokigen.gokigenassets.camera.interfaces


interface ICameraButtonControl
{
    fun pushedButton(code: String?, isLongPress: Boolean): Boolean

    companion object
    {
        const val SPECIAL_GREEN_BUTTON = "btn_green"
        const val FRONT_LEFT = "bjogleft"
        const val FRONT_RIGHT = "bjogright"
        const val ADJ_LEFT = "badjleft"
        const val ADJ_ENTER = "badjok"
        const val ADJ_RIGHT = "badjright"
        const val TOGGLE_AEAF_OFF = "baf 0"
        const val TOGGLE_AEAF_ON = "baf 1"
        const val LEVER_AEAFL = "bafl"
        const val LEVER_CAF = "bafc"
        const val BUTTON_UP = "bup"
        const val BUTTON_LEFT = "bleft"
        const val BUTTON_ENTER = "bok"
        const val BUTTON_RIGHT = "bright"
        const val BUTTON_DOWN = "bdown"
        const val BUTTON_FUNCTION_1 = "bdisp"
        const val BUTTON_FUNCTION_2 = "btrash"
        const val BUTTON_FUNCTION_3 = "beffect"
        const val BUTTON_PLUS = "btele"
        const val BUTTON_MINUS = "bwide"
        const val BUTTON_PLAYBACK = "bplay"
        const val KEYLOCK_ON = "uilock on"
        const val KEYLOCK_OFF = "uilock off"
        const val LENS_OPEN = "acclock off"
        const val LENS_RETRACT = "acclock on"
        const val MUTE_ON = "audio mute on"
        const val MUTE_OFF = "audio mute off"
        const val LCD_SLEEP_ON = "lcd sleep on"
        const val LCD_SLEEP_OFF = "lcd sleep off"
        const val LED1_ON = "led on 1"
        const val LED1_OFF = "led off 1"
        const val BEEP = "audio resplay 0 1 3"
        const val MODE_REFRESH = "mode refresh"
        const val SHUTTER = "brl 0"
        const val SHUTTER_PRESS_AND_HALF_HOLD = "brl 2 1"
        const val TAKEMODE_M = "bdial M"
        const val TAKEMODE_TAV = "bdial TAV"
        const val TAKEMODE_AV = "bdial AV"
        const val TAKEMODE_TV = "bdial TV"
        const val TAKEMODE_P = "bdial P"
        const val TAKEMODE_AUTO = "bdial AUTO"
        const val TAKEMODE_MY1 = "bdial MY1"
        const val TAKEMODE_MY2 = "bdial MY2"
        const val TAKEMODE_MY3 = "bdial MY3"
        const val TAKEMODE_MOVIE = "bdial MOVIE"
    }
}
