package jp.osdn.gokigen.gokigenassets.utils.imagefile


import androidx.camera.core.ImageCapture

interface IImageStore
{
    fun takePhoto(id : Int, imageCapture : ImageCapture?) : Boolean
}
