package jp.osdn.gokigen.gokigenassets.liveview

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_BUTTON_SHUTTER
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_0
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_1
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_2
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_3
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_4
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_5
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_6
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CACHE_SEEKBAR_7
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_LIVE_VIEW_LAYOUT_DEFAULT
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SELF_TIMER_SECONDS
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SELF_TIMER_SECONDS_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SHOW_GRID
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SHOW_GRID_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_0
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_1
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_2
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_3
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_4
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_5
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_6
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_AREA_7
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_0
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_1
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_2
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_3
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_4
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_5
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_6
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_FINDER_7
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_LOWERMIDDLE_AREA
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_LOWER_AREA
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_UPPERMIDDLE_AREA
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_VIEW_UPPER_AREA
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver

class LiveImageViewFragment(private val contentLayoutId: Int = ID_LIVE_VIEW_LAYOUT_DEFAULT) : Fragment(contentLayoutId), View.OnClickListener, View.OnLongClickListener
{
    private lateinit var liveviewView : View

    private lateinit var cameraControl0: ICameraControl
    private lateinit var cameraControl1: ICameraControl
    private lateinit var cameraControl2: ICameraControl
    private lateinit var cameraControl3: ICameraControl
    private lateinit var cameraControl4: ICameraControl
    private lateinit var cameraControl5: ICameraControl
    private lateinit var cameraControl6: ICameraControl
    private lateinit var cameraControl7: ICameraControl

    private lateinit var wrapper: PreferenceAccessWrapper
    private lateinit var informationReceiver: IInformationReceiver

    private var isCameraControl0 = false
    private var isCameraControl1 = false
    private var isCameraControl2 = false
    private var isCameraControl3 = false
    private var isCameraControl4 = false
    private var isCameraControl5 = false
    private var isCameraControl6 = false
    private var isCameraControl7 = false

    private var isCacheImage = false
    private var rotationDegrees = 0
    private var isSelfTimerIssued = false
    private var selfTimerCount = 0
    private var isActive = false

    companion object
    {
        private val TAG = LiveImageViewFragment::class.java.simpleName
        fun newInstance() = LiveImageViewFragment().apply { }
    }

    fun isActive() : Boolean
    {
        return (isActive)
    }

    fun setCameraControlFinished(informationReceiver: IInformationReceiver)
    {
        this.informationReceiver = informationReceiver

        cameraControl0.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl0.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl0.setNeighborCameraControlFinished()

        cameraControl1.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl1.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl1.setNeighborCameraControlFinished()

        cameraControl2.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl2.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl2.setNeighborCameraControlFinished()

        cameraControl3.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl3.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl3.setNeighborCameraControlFinished()

        cameraControl4.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl4.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl4.setNeighborCameraControlFinished()

        cameraControl5.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl5.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl5.setNeighborCameraControlFinished()

        cameraControl6.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl6.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl6.setNeighborCameraControlFinished()

        cameraControl7.setNeighborCameraControl(0, cameraControl0, cameraControl1, cameraControl2, cameraControl3)
        cameraControl7.setNeighborCameraControl(1, cameraControl4, cameraControl5, cameraControl6, cameraControl7)
        cameraControl7.setNeighborCameraControlFinished()

        updateCameraLayout()
    }

    fun setCameraControl(index: Int, isCameraControl0 : Boolean, cameraControl0 : ICameraControl, isCameraControl1 : Boolean, cameraControl1 : ICameraControl, isCameraControl2 : Boolean, cameraControl2 : ICameraControl, isCameraControl3 : Boolean, cameraControl3 : ICameraControl)
    {
        Log.v(TAG, "setCameraControl($index) => $isCameraControl0, $isCameraControl1, $isCameraControl2, $isCameraControl3")
        if (index == 0)
        {
            this.isCameraControl0 = isCameraControl0
            this.cameraControl0 = cameraControl0

            this.isCameraControl1 = isCameraControl1
            this.cameraControl1 = cameraControl1

            this.isCameraControl2 = isCameraControl2
            this.cameraControl2 = cameraControl2

            this.isCameraControl3 = isCameraControl3
            this.cameraControl3 = cameraControl3
        }
        else
        {
            this.isCameraControl4 = isCameraControl0
            this.cameraControl4 = cameraControl0

            this.isCameraControl5 = isCameraControl1
            this.cameraControl5 = cameraControl1

            this.isCameraControl6 = isCameraControl2
            this.cameraControl6 = cameraControl2

            this.isCameraControl7 = isCameraControl3
            this.cameraControl7 = cameraControl3
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateCameraLayout()
    {
        if (!::liveviewView.isInitialized)
        {
            //  まだ Viewができていないときには何もしない
            return
        }
        try
        {
            var isGrid = "0"
            if (::wrapper.isInitialized)
            {
                isGrid = wrapper.getString(ID_PREFERENCE_SHOW_GRID, ID_PREFERENCE_SHOW_GRID_DEFAULT_VALUE)
            }
            val showGrid = when (isGrid) {
                "0" -> false
                else -> true
            }

            Log.v(TAG, "updateCameraLayout()")
            if (::cameraControl0.isInitialized)
            {
                val area0 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_0)
                val imageCache0 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_0)
                imageCache0.visibility = View.GONE

                val imageView0 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_0)
                if (isCameraControl0)
                {
                    cameraControl0.setRefresher(0, imageView0, imageView0, imageView0)
                    imageView0.injectDisplay(cameraControl0)
                    imageView0.setOnLongClickListener(this)
                    imageView0.setOnTouchListener(LiveViewOnTouchListener(cameraControl0, 0))
                    if (isCacheImage)
                    {
                        imageCache0.visibility = View.VISIBLE
                        imageCache0.setOnSeekBarChangeListener(imageView0)
                    }
                    if (showGrid)
                    {
                        imageView0.showGridFrame(showGrid)
                    }
                    area0.visibility = View.VISIBLE
                }
                else
                {
                    imageView0.visibility = View.GONE
                    area0.visibility = View.GONE
                }
            }
            if (::cameraControl1.isInitialized)
            {
                val area1 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_1)
                val imageCache1 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_1)
                imageCache1.visibility = View.GONE

                val imageView1 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_1)
                if (isCameraControl1)
                {
                    cameraControl1.setRefresher(1, imageView1, imageView1, imageView1)
                    imageView1.injectDisplay(cameraControl1)
                    imageView1.setOnLongClickListener(this)
                    imageView1.setOnTouchListener(LiveViewOnTouchListener(cameraControl1, 1))
                    if (isCacheImage)
                    {
                        imageCache1.visibility = View.VISIBLE
                        imageCache1.setOnSeekBarChangeListener(imageView1)
                    }
                    if (showGrid)
                    {
                        imageView1.showGridFrame(showGrid)
                    }
                    area1.visibility = View.VISIBLE
                }
                else
                {
                    imageView1.visibility = View.GONE
                    area1.visibility = View.GONE
                }
            }
            if (::cameraControl2.isInitialized)
            {
                val area2 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_2)
                val imageCache2 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_2)
                imageCache2.visibility = View.GONE

                val imageView2 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_2)
                if (isCameraControl2)
                {
                    cameraControl2.setRefresher(2, imageView2, imageView2, imageView2)
                    imageView2.injectDisplay(cameraControl2)
                    imageView2.setOnLongClickListener(this)
                    imageView2.setOnTouchListener(LiveViewOnTouchListener(cameraControl2, 2))
                    if (isCacheImage)
                    {
                        imageCache2.visibility = View.VISIBLE
                        imageCache2.setOnSeekBarChangeListener(imageView2)
                    }
                    if (showGrid)
                    {
                        imageView2.showGridFrame(showGrid)
                    }
                    area2.visibility = View.VISIBLE
                }
                else
                {
                    imageView2.visibility = View.GONE
                    area2.visibility = View.GONE
                }
            }
            if (::cameraControl3.isInitialized)
            {
                val area3 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_3)
                val imageCache3 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_3)
                imageCache3.visibility = View.GONE

                val imageView3 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_3)
                if (isCameraControl3)
                {
                    cameraControl3.setRefresher(3, imageView3, imageView3, imageView3)
                    imageView3.injectDisplay(cameraControl3)
                    imageView3.setOnLongClickListener(this)
                    imageView3.setOnTouchListener(LiveViewOnTouchListener(cameraControl3, 3))
                    if (isCacheImage)
                    {
                        imageCache3.visibility = View.VISIBLE
                        imageCache3.setOnSeekBarChangeListener(imageView3)
                    }
                    if (showGrid)
                    {
                        imageView3.showGridFrame(showGrid)
                    }
                    area3.visibility = View.VISIBLE
                }
                else
                {
                    imageView3.visibility = View.GONE
                    area3.visibility = View.GONE
                }
            }
            if (::cameraControl4.isInitialized)
            {
                val area4 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_4)
                val imageCache4 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_4)
                imageCache4.visibility = View.GONE

                val imageView4 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_4)
                if (isCameraControl4)
                {
                    cameraControl4.setRefresher(4, imageView4, imageView4, imageView4)
                    imageView4.injectDisplay(cameraControl4)
                    imageView4.setOnLongClickListener(this)
                    imageView4.setOnTouchListener(LiveViewOnTouchListener(cameraControl4, 4))
                    if (isCacheImage)
                    {
                        imageCache4.visibility = View.VISIBLE
                        imageCache4.setOnSeekBarChangeListener(imageView4)
                    }
                    if (showGrid)
                    {
                        imageView4.showGridFrame(showGrid)
                    }
                    area4.visibility = View.VISIBLE
                }
                else
                {
                    imageView4.visibility = View.GONE
                    area4.visibility = View.GONE
                }
            }
            if (::cameraControl5.isInitialized)
            {
                val area5 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_5)
                val imageCache5 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_5)
                imageCache5.visibility = View.GONE

                val imageView5 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_5)
                if (isCameraControl5)
                {
                    cameraControl5.setRefresher(5, imageView5, imageView5, imageView5)
                    imageView5.injectDisplay(cameraControl5)
                    imageView5.setOnLongClickListener(this)
                    imageView5.setOnTouchListener(LiveViewOnTouchListener(cameraControl5, 5))
                    if (isCacheImage)
                    {
                        imageCache5.visibility = View.VISIBLE
                        imageCache5.setOnSeekBarChangeListener(imageView5)
                    }
                    if (showGrid)
                    {
                        imageView5.showGridFrame(showGrid)
                    }
                    area5.visibility = View.VISIBLE
                }
                else
                {
                    imageView5.visibility = View.GONE
                    area5.visibility = View.GONE
                }
            }
            if (::cameraControl6.isInitialized)
            {
                val area6 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_6)
                val imageCache6 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_6)
                imageCache6.visibility = View.GONE

                val imageView6 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_6)
                if (isCameraControl6)
                {
                    cameraControl6.setRefresher(6, imageView6, imageView6, imageView6)
                    imageView6.injectDisplay(cameraControl6)
                    imageView6.setOnLongClickListener(this)
                    imageView6.setOnTouchListener(LiveViewOnTouchListener(cameraControl6, 6))
                    if (isCacheImage)
                    {
                        imageCache6.visibility = View.VISIBLE
                        imageCache6.setOnSeekBarChangeListener(imageView6)
                    }
                    if (showGrid)
                    {
                        imageView6.showGridFrame(showGrid)
                    }
                    area6.visibility = View.VISIBLE
                }
                else
                {
                    imageView6.visibility = View.GONE
                    area6.visibility = View.GONE
                }
            }
            if (::cameraControl7.isInitialized)
            {
                val area7 = liveviewView.findViewById<LinearLayout>(ID_VIEW_AREA_7)
                val imageCache7 = liveviewView.findViewById<SeekBar>(ID_CACHE_SEEKBAR_7)
                imageCache7.visibility = View.GONE

                val imageView7 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_7)
                if (isCameraControl7)
                {
                    cameraControl7.setRefresher(7, imageView7, imageView7, imageView7)
                    imageView7.injectDisplay(cameraControl7)
                    imageView7.setOnLongClickListener(this)
                    imageView7.setOnTouchListener(LiveViewOnTouchListener(cameraControl7, 7))
                    if (isCacheImage)
                    {
                        imageCache7.visibility = View.VISIBLE
                        imageCache7.setOnSeekBarChangeListener(imageView7)
                    }
                    if (showGrid)
                    {
                        imageView7.showGridFrame(showGrid)
                    }
                    area7.visibility = View.VISIBLE
                }
                else
                {
                    imageView7.visibility = View.GONE
                    area7.visibility = View.GONE
                }
            }
            if ((!isCameraControl0)&&(!isCameraControl1))
            {
                val area = liveviewView.findViewById<View>(ID_VIEW_UPPER_AREA)
                area.visibility = View.GONE
            }
            if ((!isCameraControl2)&&(!isCameraControl3))
            {
                val area = liveviewView.findViewById<View>(ID_VIEW_UPPERMIDDLE_AREA)
                area.visibility = View.GONE
            }
            if ((!isCameraControl4)&&(!isCameraControl5))
            {
                val area = liveviewView.findViewById<View>(ID_VIEW_LOWERMIDDLE_AREA)
                area.visibility = View.GONE
            }
            if ((!isCameraControl6)&&(!isCameraControl7))
            {
                val area = liveviewView.findViewById<View>(ID_VIEW_LOWER_AREA)
                area.visibility = View.GONE
            }
            liveviewView.postInvalidate()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (::liveviewView.isInitialized)
        {
            return (liveviewView)
        }
        liveviewView = inflater.inflate(contentLayoutId, null, false)
        liveviewView.findViewById<ImageButton>(ID_BUTTON_SHUTTER)?.setOnClickListener(this)

        try
        {
            val context = context
            if (context != null)
            {
                wrapper = PreferenceAccessWrapper(context)
                this.isCacheImage = wrapper.getBoolean(ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES, false)
            }
            else
            {
                val appContext = activity?.applicationContext
                if (appContext != null)
                {
                    val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
                    this.isCacheImage = preferences.getBoolean(ID_PREFERENCE_CACHE_LIVE_VIEW_PICTURES, false)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        updateCameraLayout()
        return (liveviewView)
    }

    override fun onResume()
    {
        super.onResume()
        isActive = true
        if (::wrapper.isInitialized)
        {
            selfTimerCount = 0
            selfTimerCount = try {
                val selfTimerCountStr = wrapper.getString(ID_PREFERENCE_SELF_TIMER_SECONDS, ID_PREFERENCE_SELF_TIMER_SECONDS_DEFAULT_VALUE)
                selfTimerCountStr.toInt()
            } catch (e : Exception) {
                e.printStackTrace()
                0
            }
            updateShowGridFrame(wrapper.getString(ID_PREFERENCE_SHOW_GRID, ID_PREFERENCE_SHOW_GRID_DEFAULT_VALUE))
        }
        Log.v(TAG, " onResume() : orientation ${getRotation()}, Self Timer: $selfTimerCount sec.")
    }

    private fun updateShowGridFrame(isGrid : String)
    {
        try
        {
            val showGrid = when (isGrid) {
                "0" -> false
                else -> true
            }
            val imageView0 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_0)
            if (imageView0.isVisible)
            {
                imageView0.showGridFrame(showGrid)
            }

            val imageView1 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_1)
            if (imageView1.isVisible)
            {
                imageView1.showGridFrame(showGrid)
            }

            val imageView2 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_2)
            if (imageView2.isVisible)
            {
                imageView2.showGridFrame(showGrid)
            }

            val imageView3 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_3)
            if (imageView3.isVisible)
            {
                imageView3.showGridFrame(showGrid)
            }

            val imageView4 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_4)
            if (imageView4.isVisible)
            {
                imageView4.showGridFrame(showGrid)
            }

            val imageView5 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_5)
            if (imageView5.isVisible)
            {
                imageView5.showGridFrame(showGrid)
            }

            val imageView6 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_6)
            if (imageView6.isVisible)
            {
                imageView6.showGridFrame(showGrid)
            }

            val imageView7 = liveviewView.findViewById<LiveImageView>(ID_VIEW_FINDER_7)
            if (imageView7.isVisible)
            {
                imageView7.showGridFrame(showGrid)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    override fun onPause()
    {
        super.onPause()
        isActive = false
        Log.v(TAG, " onPause() : ")
    }

    override fun onClick(v: View?)
    {
        if (isSelfTimerIssued)
        {
            // セルフタイマー動作中...なにもしない
            Log.v(TAG, "onClick() : ${v?.id}, $isSelfTimerIssued")
            return
        }
        if (selfTimerCount > 0)
        {
            // セルフタイマーを駆動させる
            driveSelfShutterOnClick(v)
            return
        }
        issueOnClick(v)
    }

    fun handleKeyDown(keyCode: Int, event: KeyEvent?): Boolean
    {
        //Log.v(TAG, "onKey() : $keyCode")
        if (isSelfTimerIssued)
        {
            // セルフタイマー動作中...イベントを食べる
            return (true)
        }
        if ((event == null)||((event.action != KeyEvent.ACTION_DOWN)||((keyCode != KeyEvent.KEYCODE_VOLUME_UP)&&(keyCode != KeyEvent.KEYCODE_CAMERA))))
        {
            // もしくは、シャッターボタンが押されたわけではない...場合は、何もしない
           Log.v(TAG, "handleKeyDown() : $keyCode, $event, $isSelfTimerIssued")
            return (false)
        }
        if (selfTimerCount > 0)
        {
            // セルフタイマーを駆動させる
            driveSelfShutterOnKeyDown(keyCode, event)
            return (true)
        }
        return (issueKeyDown(keyCode, event))
    }

    private fun issueKeyDown(keyCode: Int, event: KeyEvent) : Boolean
    {
        //Log.v(TAG, "issueKeyDown() keyCode: $keyCode")
        try
        {
            // キーが押された時...
            if ((::cameraControl0.isInitialized)&&(isCameraControl0))
            {
                cameraControl0.keyDownReceiver(0).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl1.isInitialized)&&(isCameraControl1))
            {
                cameraControl1.keyDownReceiver(1).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl2.isInitialized)&&(isCameraControl2))
            {
                cameraControl2.keyDownReceiver(2).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl3.isInitialized)&&(isCameraControl3))
            {
                cameraControl3.keyDownReceiver(3).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl4.isInitialized)&&(isCameraControl4))
            {
                cameraControl4.keyDownReceiver(4).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl5.isInitialized)&&(isCameraControl5))
            {
                cameraControl5.keyDownReceiver(5).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl6.isInitialized)&&(isCameraControl6))
            {
                cameraControl6.keyDownReceiver(6).handleKeyDown(keyCode, event)
            }
            if ((::cameraControl7.isInitialized)&&(isCameraControl7))
            {
                cameraControl7.keyDownReceiver(7).handleKeyDown(keyCode, event)
            }
            return (true)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun issueOnClick(v: View?)
    {
        //Log.v(TAG, "issueOnClick() : ${v?.id}, delay: $selfTimerCount")
        try
        {
            //  シャッターボタンがクリックされたとき...
            if ((::cameraControl0.isInitialized)&&(isCameraControl0))
            {
                cameraControl0.captureButtonReceiver(0).onClick(v)
            }
            if ((::cameraControl1.isInitialized)&&(isCameraControl1))
            {
                cameraControl1.captureButtonReceiver(1).onClick(v)
            }
            if ((::cameraControl2.isInitialized)&&(isCameraControl2))
            {
                cameraControl2.captureButtonReceiver(2).onClick(v)
            }
            if ((::cameraControl3.isInitialized)&&(isCameraControl3))
            {
                cameraControl3.captureButtonReceiver(3).onClick(v)
            }
            if ((::cameraControl4.isInitialized)&&(isCameraControl4))
            {
                cameraControl4.captureButtonReceiver(4).onClick(v)
            }
            if ((::cameraControl5.isInitialized)&&(isCameraControl5))
            {
                cameraControl5.captureButtonReceiver(5).onClick(v)
            }
            if ((::cameraControl6.isInitialized)&&(isCameraControl6))
            {
                cameraControl6.captureButtonReceiver(6).onClick(v)
            }
            if ((::cameraControl7.isInitialized)&&(isCameraControl7))
            {
                cameraControl7.captureButtonReceiver(7).onClick(v)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun driveSelfShutterOnClick(v: View?)
    {
        Log.v(TAG, "driveSelfShutterOnClick() : ${v?.id}, delay: $selfTimerCount")
        try
        {
            val thread = Thread {
                isSelfTimerIssued = true
                for (count in selfTimerCount downTo 1)
                {
                    val color = if (count < 4) { Color.MAGENTA } else { Color.BLUE }
                    updateMessage("WAIT $count sec.",color)
                    Thread.sleep(1000)
                }

                activity?.runOnUiThread {
                    issueOnClick(v)
                    isSelfTimerIssued = false
                }
                updateMessage("", Color.LTGRAY)
            }
            thread.start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            isSelfTimerIssued = false
        }
    }

    private fun driveSelfShutterOnKeyDown(keyCode: Int, event: KeyEvent)
    {
        Log.v(TAG, "driveSelfShutterOnKeyDown() : $keyCode, delay: $selfTimerCount")
        try
        {
            val thread = Thread {
                isSelfTimerIssued = true
                for (count in selfTimerCount downTo 1)
                {
                    val color = if (count < 4) { Color.MAGENTA } else { Color.BLUE }
                    updateMessage("WAIT $count sec.",color)
                    Thread.sleep(1000)
                }

                activity?.runOnUiThread {
                    issueKeyDown(keyCode, event)
                    isSelfTimerIssued = false
                }
                updateMessage("", Color.LTGRAY)
            }
            thread.start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
            isSelfTimerIssued = false
        }
    }

    private fun updateMessage(msg : String, color: Int)
    {
        if (::informationReceiver.isInitialized)
        {
            informationReceiver.updateMessage(msg, isBold = false, isColor = true, color)
        }
    }

    private fun getRotation(): Int
    {
        var rotation = 0
        try
        {
            rotation = when (resources.configuration.orientation) {
                Configuration.ORIENTATION_LANDSCAPE -> 90
                Configuration.ORIENTATION_PORTRAIT -> 0
                else -> 0
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (rotation)
    }

    override fun onLongClick(v: View?): Boolean
    {
        var ret = false
        try
        {
            val id = v?.id
            if ((::cameraControl0.isInitialized)&&(isCameraControl0)&&(id == ID_VIEW_FINDER_0))
            {
                cameraControl0.onLongClickReceiver(0).onLongClick(v)
                ret = true
            }
            if ((::cameraControl1.isInitialized)&&(isCameraControl1)&&(id == ID_VIEW_FINDER_1))
            {
                cameraControl1.onLongClickReceiver(1).onLongClick(v)
                ret = true
            }
            if ((::cameraControl2.isInitialized)&&(isCameraControl2)&&(id == ID_VIEW_FINDER_2))
            {
                cameraControl2.onLongClickReceiver(2).onLongClick(v)
                ret = true
            }
            if ((::cameraControl3.isInitialized)&&(isCameraControl3)&&(id == ID_VIEW_FINDER_3))
            {
                cameraControl3.onLongClickReceiver(3).onLongClick(v)
                ret = true
            }
            if ((::cameraControl4.isInitialized)&&(isCameraControl4)&&(id == ID_VIEW_FINDER_4))
            {
                cameraControl4.onLongClickReceiver(4).onLongClick(v)
                ret = true
            }
            if ((::cameraControl5.isInitialized)&&(isCameraControl5)&&(id == ID_VIEW_FINDER_5))
            {
                cameraControl5.onLongClickReceiver(5).onLongClick(v)
                ret = true
            }
            if ((::cameraControl6.isInitialized)&&(isCameraControl6)&&(id == ID_VIEW_FINDER_6))
            {
                cameraControl6.onLongClickReceiver(6).onLongClick(v)
                ret = true
            }
            if ((::cameraControl7.isInitialized)&&(isCameraControl7)&&(id == ID_VIEW_FINDER_7))
            {
                cameraControl7.onLongClickReceiver(7).onLongClick(v)
                ret = true
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (ret)
    }
}
