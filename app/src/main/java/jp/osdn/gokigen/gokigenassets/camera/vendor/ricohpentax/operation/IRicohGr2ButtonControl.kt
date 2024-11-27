package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

interface IRicohGr2ButtonControl
{
    fun pushedButton(code: String, isLongPress: Boolean): Boolean

    companion object
    {
        var SPECIAL_GREEN_BUTTON = "btn_green"

        var FRONT_LEFT = "bjogleft"
        var FRONT_RIGHT = "bjogright"
        var ADJ_LEFT = "badjleft"
        var ADJ_ENTER = "badjok"
        var ADJ_RIGHT = "badjright"

        var TOGGLE_AEAF_OFF = "baf 0"
        var TOGGLE_AEAF_ON = "baf 1"

        var LEVER_AEAFL = "bafl"
        var LEVER_CAF = "bafc"

        var BUTTON_UP = "bup"
        var BUTTON_LEFT = "bleft"
        var BUTTON_ENTER = "bok"
        var BUTTON_RIGHT = "bright"
        var BUTTON_DOWN = "bdown"

        var BUTTON_FUNCTION_1 = "bdisp"
        var BUTTON_FUNCTION_2 = "btrash"
        var BUTTON_FUNCTION_3 = "beffect"

        var BUTTON_PLUS = "btele"
        var BUTTON_MINUS = "bwide"
        var BUTTON_PLAYBACK = "bplay"

        var KEYLOCK_ON = "uilock on"
        var KEYLOCK_OFF = "uilock off"

        var LENS_OPEN = "acclock off"
        var LENS_RETRACT = "acclock on"

        var MUTE_ON = "audio mute on"
        var MUTE_OFF = "audio mute off"

        var LCD_SLEEP_ON = "lcd sleep on"
        var LCD_SLEEP_OFF = "lcd sleep off"

        var LED1_ON = "led on 1"
        var LED1_OFF = "led off 1"

        var BEEP = "audio resplay 0 1 3"

        var MODE_REFRESH = "mode refresh"

        var SHUTTER = "brl 0"
        var SHUTTER_PRESS_AND_HALF_HOLD = "brl 2 1"

        var TAKEMODE_M = "bdial M"
        var TAKEMODE_TAV = "bdial TAV"
        var TAKEMODE_AV = "bdial AV"
        var TAKEMODE_TV = "bdial TV"
        var TAKEMODE_P = "bdial P"
        var TAKEMODE_AUTO = "bdial AUTO"
        var TAKEMODE_MY1 = "bdial MY1"
        var TAKEMODE_MY2 = "bdial MY2"
        var TAKEMODE_MY3 = "bdial MY3"
        var TAKEMODE_MOVIE = "bdial MOVIE"
    }
}
