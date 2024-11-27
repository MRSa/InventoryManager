package jp.osdn.gokigen.gokigenassets.camera.interfaces

import android.view.KeyEvent

interface IKeyDown
{
    fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
}
