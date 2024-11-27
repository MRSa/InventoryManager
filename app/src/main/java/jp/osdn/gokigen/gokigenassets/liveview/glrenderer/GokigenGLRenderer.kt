package jp.osdn.gokigen.gokigenassets.liveview.glrenderer

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GokigenGLRenderer(context: Context?, private val mDrawer: IGraphicsDrawer): GLSurfaceView.Renderer
{
    init
    {
        // 準備
        mDrawer.prepareObject()
    }

    /**
     * 準備処理
     */
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?)
    {
        gl.glDisable(GL10.GL_DITHER)
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
        gl.glShadeModel(GL10.GL_SMOOTH)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        mDrawer.prepareDrawer(gl)
    }

    /**
     * 描画処理
     */
    override fun onDrawFrame(gl: GL10)
    {
        gl.glDisable(GL10.GL_DITHER) // DITHERをOFFにする
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT) // スクリーンを消去する
        gl.glMatrixMode(GL10.GL_MODELVIEW) // モデル視点にする
        gl.glLoadIdentity() // 単位行列をセット

        // 視点の設定 : (0, 0, -5)の位置から、原点を見る。上方向はY軸とする(0, 1, 0)
        GLU.gluLookAt(gl, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) // 頂点座標をON
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) // テクスチャ座標を ON

        mDrawer.preprocessDraw(gl)
        mDrawer.drawObject(gl)
    }

    /**
     * 画面サイズが変わったときの処理
     */
    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int)
    {
        gl.glViewport(0, 0, width, height)

        val ratio = width.toFloat() / height
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 7f)
    }
}
