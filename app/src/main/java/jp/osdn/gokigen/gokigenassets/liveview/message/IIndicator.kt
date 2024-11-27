package jp.osdn.gokigen.gokigenassets.liveview.message

interface IIndicator
{
    enum class Area
    {
        AREA_NONE,
        AREA_1,
        AREA_2,
        AREA_3,
        AREA_4,
        AREA_5,
        AREA_6,
        AREA_7,
        AREA_8,
        AREA_9,
        AREA_10,
        AREA_11,
        AREA_12,
        AREA_A,
        AREA_B,
        AREA_C,
        AREA_D,
        AREA_E,
        AREA_F,
        AREA_G,
        AREA_H,
        AREA_I,
        AREA_J,
        AREA_K,
        AREA_L,
        AREA_M,
        AREA_N,
        AREA_O,
        AREA_P,
        AREA_Q,
    }

    fun setMessage(area: Area, color: Int, message: String)
    fun invalidate()
}
