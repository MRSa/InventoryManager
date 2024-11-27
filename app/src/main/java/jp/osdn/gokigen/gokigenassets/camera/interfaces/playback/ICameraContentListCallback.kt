package jp.osdn.gokigen.gokigenassets.camera.interfaces.playback

interface ICameraContentListCallback {
    fun onCompleted(contentList: List<ICameraFileInfo?>?)

    //void onCompleted(List<ICameraContent> contentList);
    fun onErrorOccurred(e: Exception?)
}
