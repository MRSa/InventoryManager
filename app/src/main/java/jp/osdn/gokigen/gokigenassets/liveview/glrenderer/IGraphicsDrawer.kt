package jp.osdn.gokigen.gokigenassets.liveview.glrenderer

import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import javax.microedition.khronos.opengles.GL10

interface IGraphicsDrawer
{
    /** テクスチャ画像提供オブジェクト **/
    fun setImageProvider(provider: IImageProvider)

    /** 表示の 拡大・縮小 **/
    fun setScaleFactor(scaleFactor: Float)

    /** 視点の移動 **/
    fun setViewMove(x : Float, y : Float, z : Float)

    /** 視点を初期化 **/
    fun resetView()

    /** 準備クラス  */
    fun prepareObject()

    /** 準備クラス(その２)  */
    fun prepareDrawer(gl: GL10?)

    /** 描画前の処理を実行する  */
    fun preprocessDraw(gl: GL10?)

    /** 描画を実行する  */
    fun drawObject(gl: GL10?)
}
