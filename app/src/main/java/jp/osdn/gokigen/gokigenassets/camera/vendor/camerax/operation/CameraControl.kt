package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.annotation.SuppressLint
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest.*
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import jp.osdn.gokigen.gokigenassets.camera.preference.ICameraPreferenceProvider
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_CAMERA_X_PREVIEW_LAYOUT
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import jp.osdn.gokigen.gokigenassets.liveview.ILiveView
import jp.osdn.gokigen.gokigenassets.liveview.ILiveViewRefresher
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.liveview.storeimage.StoreImage
import jp.osdn.gokigen.gokigenassets.utils.imagefile.FileControl
import jp.osdn.gokigen.gokigenassets.liveview.ICachePositionProvider
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraControl(private val activity : AppCompatActivity, private val preference: ICameraPreferenceProvider, private val vibrator : IVibrator, private val informationReceiver : IInformationReceiver, private val statusReceiver : ICameraStatusReceiver, private val number : Int = 0, private val liveViewListener:CameraLiveViewListenerImpl = CameraLiveViewListenerImpl(activity, informationReceiver)) : ICameraControl, ICameraShutter
{
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var fileControl : FileControl
    private lateinit var storeImage : StoreImage
    private lateinit var cameraXCamera : Camera
    private lateinit var clickKeyDownListener : CameraClickKeyDownListener
    private var cameraIsStarted = false
    private var isPendingTask = false
    private val cameraXCameraControl = CameraXCameraControl()
    private val cameraXCameraStatusHolder = CameraXCameraStatusHolder(cameraXCameraControl)
    private val cameraXZoomControl = CameraZoomLensControl(cameraXCameraControl)
    private val clickKeyDownListeners = mutableMapOf<Int, CameraClickKeyDownListener>()
    private val cachePositionProviders = mutableMapOf<Int, ICachePositionProvider>()

    private var camera0: ICameraControl? = null
    private var camera1: ICameraControl? = null
    private var camera2: ICameraControl? = null
    private var camera3: ICameraControl? = null
    private var camera4: ICameraControl? = null
    private var camera5: ICameraControl? = null
    private var camera6: ICameraControl? = null
    private var camera7: ICameraControl? = null
    private val cameraXControlList = ArrayList<Boolean>()
    override fun getConnectionMethod(): String
    {
        return ("camerax")
    }

    override fun initialize()
    {
        Log.v(TAG, " initialize()")
        cameraExecutor = Executors.newSingleThreadExecutor()
        storeImage = StoreImage(activity, liveViewListener)
        clickKeyDownListeners.clear()
        fileControl = FileControl(activity, storeImage, vibrator)
        clickKeyDownListener = CameraClickKeyDownListener(0, fileControl, null)
    }

    override fun connectToCamera()
    {
        Log.v(TAG, " connectToCamera() : camerax ")
    }

    override fun changeCaptureMode(mode : String)
    {
        Log.v(TAG, " changeCaptureMode() : $mode ")
    }

    override fun needRotateImage(): Boolean
    {
        return (true)
    }

    override fun setRefresher(id: Int, refresher: ILiveViewRefresher, imageView : ILiveView, cachePosition : ICachePositionProvider)
    {
        liveViewListener.setRefresher(refresher)
        imageView.setImageProvider(liveViewListener)
        cachePositionProviders[id] = cachePosition
    }

    override fun startCamera(isPreviewView : Boolean, cameraSequence : Int)
    {
        Log.v(TAG, " startCamera()")
        val cameraSelector : CameraSelector = when (cameraSequence) {
                1 -> CameraSelector.DEFAULT_FRONT_CAMERA
                else -> CameraSelector.DEFAULT_BACK_CAMERA
        }

        if (cameraIsStarted)
        {
            Log.v(TAG, " ALREADY STARTED...")
            try
            {
                val cameraProvider: ProcessCameraProvider = ProcessCameraProvider.getInstance(activity).get()
                cameraProvider.unbindAll()
                cameraIsStarted = false
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }
        cameraIsStarted = true
        if (isPreviewView)
        {
            // Preview View
            startCameraForPreviewView(cameraSelector)
        }
        else
        {
            // Liveview View
            startCameraForLiveView(cameraSelector)
        }

        try
        {
            statusReceiver.onStatusNotify(activity.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTED))
            statusReceiver.onCameraConnected()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun startCameraForPreviewView(cameraSelector : CameraSelector)
    {
        Log.v(TAG, " startCameraPreviewView()")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener( {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(activity.findViewById<androidx.camera.view.PreviewView>(ID_CAMERA_X_PREVIEW_LAYOUT).surfaceProvider)
                }
            val imageCapture = fileControl.prepare()

            try
            {
                cameraProvider.unbindAll()
                cameraXCamera = cameraProvider.bindToLifecycle(activity, cameraSelector, preview, imageCapture)
                cameraXCameraControl.setCameraControl(cameraXCamera)

                setCaptureRequestOptions(cameraXCamera.cameraControl)
            }
            catch(e : Exception)
            {
                Log.e(TAG, "Use case binding failed", e)
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    private fun getImageRotation() : Int
    {
        return (Surface.ROTATION_0)
    }

    private fun startCameraForLiveView(cameraSelector : CameraSelector)
    {
        Log.v(TAG, " startCameraForLiveView()")

        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
        cameraProviderFuture.addListener( {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val imageCapture = fileControl.prepare()
            val option1 = preference.getCameraOption1()  // プレビューサイズを設定する
            val option2 = preference.getCameraOption2()  // テスト用オプション...
            try
            {
                val imageAnalyzer = getImageAnalysis(option1, option2)
                cameraProvider.unbindAll()
                cameraXCamera = cameraProvider.bindToLifecycle(activity, cameraSelector, imageCapture, imageAnalyzer)
                cameraXCameraControl.setCameraControl(cameraXCamera)

                setCaptureRequestOptions(cameraXCamera.cameraControl)
            }
            catch(e : Exception)
            {
                Log.e(TAG, "Use case binding failed", e)
                e.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    private fun getImageAnalysis(option1 : String, option2 : String) : ImageAnalysis
    {
        Log.v(TAG, " getImageAnalysis(option1 = '$option1', option2 = '$option2') ")
        val previewSize = if (option1.isNotBlank()) {
            when (option1) {
                "_16K" -> Size(8640, 15360)
                "_10K" -> Size(4320, 10240)
                "_8K" -> Size(4320, 7680)
                "_6K" -> Size(3384, 6016)
                "_4K"   -> Size(4096, 2160)
                "_WQHD" -> Size(2560, 1440)
                "_2K" -> Size(2048, 1080)
                "_FHD" -> Size(1920, 1080)
                "_SXGA" -> Size(1280, 1024) // SXGA : 1600x1200 @ Pixel3a
                "_XGA" -> Size(1024, 768)   // XGA  : 1600x1200 @ Pixel3a
                "_SVGA" -> Size(800, 600)   // SVGA : 1280x960  @ Pixel3a
                "_VGA" -> Size(640, 480)   // SVGA : 1280x960  @ Pixel3a
                "16K" -> Size(15360, 8640)
                "10K" -> Size(10240, 4320)
                "8K" -> Size(7680, 4320)
                "6K" -> Size(6016, 3384)
                "4K"   -> Size(2160, 4096)
                "WQHD" -> Size(1440, 2560)
                "2K" -> Size(1080, 2048)
                "FHD" -> Size(1080, 1920)
                "SXGA" -> Size(1024, 1280)
                "XGA" -> Size(768, 1024)
                "SVGA" -> Size(600, 800)
                "VGA" -> Size(480, 640)
                else -> Size(480, 640)     // VGA : 1024x768   @ Pixel3a
            }
        }
        else
        {
            Size(640, 480)
        }

        val resolutionSelector = if (option1.isNotBlank())
        {
            ResolutionSelector.Builder()
                .setResolutionStrategy(
                    ResolutionStrategy(previewSize, ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                .build()
        }
        else
        {
            ResolutionSelector.Builder().build()
        }

        //val useImageFormatOption = false
        return (//if (!useImageFormatOption) {
                    if (option1.isNotBlank()) {
                        ImageAnalysis.Builder()
                            .setResolutionSelector(resolutionSelector)
                            //.setTargetResolution(previewSize)
                            .setTargetRotation(getImageRotation())
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, liveViewListener)
                            }
                    } else {
                        ImageAnalysis.Builder()
                            .setTargetRotation(getImageRotation())
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, liveViewListener)
                            }
                    }
                //}
                /*else
                {
                    if (option1.isNotBlank()) {
                        ImageAnalysis.Builder()
                            .setTargetResolution(previewSize)
                            .setTargetRotation(getImageRotation())
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, liveViewListener)
                            }
                    } else {
                        ImageAnalysis.Builder()
                            .setTargetRotation(getImageRotation())
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, liveViewListener)
                            }
                    }
                }*/
                )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setCaptureRequestOptions(cameraControl : androidx.camera.core.CameraControl)
    {
        try
        {
            val requestOptionsBuilder = CaptureRequestOptions.Builder()
            requestOptionsBuilder.setCaptureRequestOption(CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)   // CONTROL_MODE_USE_SCENE_MODE
            requestOptionsBuilder.setCaptureRequestOption(CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO)
            //requestOptionsBuilder.setCaptureRequestOption(CONTROL_SCENE_MODE, CameraMetadata.CONTROL_SCENE_MODE_DISABLED)
            //requestOptionsBuilder.setCaptureRequestOption(CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON)  // CONTROL_AE_MODE_ON
            //requestOptionsBuilder.setCaptureRequestOption(CONTROL_EFFECT_MODE, CameraMetadata.CONTROL_EFFECT_MODE_OFF)
            //requestOptionsBuilder.setCaptureRequestOption(LENS_OPTICAL_STABILIZATION_MODE, CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON)
            requestOptionsBuilder.setCaptureRequestOption(FLASH_MODE, FLASH_MODE_OFF)  // CameraMetadata.FLASH_MODE_TORCH or FLASH_MODE_OFF
            //requestOptionsBuilder.setCaptureRequestOption(EDGE_MODE, CameraMetadata.EDGE_MODE_HIGH_QUALITY)  // CameraMetadata.EDGE_MODE_OFF
            //requestOptionsBuilder.setCaptureRequestOption(COLOR_CORRECTION_MODE, CameraMetadata.COLOR_CORRECTION_MODE_HIGH_QUALITY)
            //requestOptionsBuilder.setCaptureRequestOption(CONTROL_AE_ANTIBANDING_MODE, CameraMetadata.CONTROL_AE_ANTIBANDING_MODE_AUTO)
            //requestOptionsBuilder.setCaptureRequestOption(HOT_PIXEL_MODE, CameraMetadata.HOT_PIXEL_MODE_OFF)
            //requestOptionsBuilder.setCaptureRequestOption(NOISE_REDUCTION_MODE, CameraMetadata.NOISE_REDUCTION_MODE_HIGH_QUALITY)
            Camera2CameraControl.from(cameraControl).captureRequestOptions = requestOptionsBuilder.build()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun finishCamera(isPowerOff: Boolean)
    {
        try
        {
            val cameraProvider: ProcessCameraProvider = ProcessCameraProvider.getInstance(activity).get()
            cameraProvider.unbindAll()
            cameraIsStarted = false
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        try
        {
            cameraExecutor.shutdown()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        fileControl.finish()
    }

    override fun captureButtonReceiver(id : Int) : View.OnClickListener
    {
        return (getClickKeyDownListener(id))
    }

    override fun onLongClickReceiver(id: Int): View.OnLongClickListener
    {
        return (getClickKeyDownListener(id))
    }

    override fun keyDownReceiver(id: Int): IKeyDown
    {
        return (getClickKeyDownListener(id))
    }

    override fun getFocusingControl(id: Int): IFocusingControl
    {
        return (cameraXCameraControl)
    }

    override fun getDisplayInjector(): IDisplayInjector
    {
        return (cameraXCameraControl)
    }

    override fun getCameraNumber(): Int
    {
        return (number)
    }

    override fun getCameraShutter(id: Int): ICameraShutter { return (this) }
    override fun getZoomControl(id: Int): IZoomLensControl { return (cameraXZoomControl) }

    private fun getClickKeyDownListener(id : Int) : CameraClickKeyDownListener
    {
        try
        {
            val listener = clickKeyDownListeners[id]
            if (listener != null)
            {
                // すでに登録されていた場合は、応答する
                return (listener)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        val listener = CameraClickKeyDownListener(id, fileControl, cachePositionProviders[id])
        clickKeyDownListeners[id] = listener
        return (listener)
    }
    override fun setNeighborCameraControl(index: Int, camera0: ICameraControl?, camera1: ICameraControl?, camera2: ICameraControl?, camera3: ICameraControl?)
    {
        Log.v(TAG, " setNeighborCameraControl($index) ")
        if (index == 0)
        {
            this.camera0 = camera0
            this.camera1 = camera1
            this.camera2 = camera2
            this.camera3 = camera3
        }
        else  // if (index == 1)
        {
            this.camera4 = camera0
            this.camera5 = camera1
            this.camera6 = camera2
            this.camera7 = camera3
        }
    }
    override fun setNeighborCameraControlFinished()
    {
        try
        {
            checkCameraXControl()
            checkIsPendingCameraXControl()
            Log.v(TAG, " -=-=-=-=- camerax check ($number)  isPending : $isPendingTask -=-=-=-=- ")
            checkConcurrentCameras()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkCameraXControl()
    {
        cameraXControlList.clear()
        cameraXControlList.add(camera0?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera1?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera2?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera3?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera4?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera5?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera6?.getConnectionMethod().equals("camerax"))
        cameraXControlList.add(camera7?.getConnectionMethod().equals("camerax"))
    }

    private fun checkIsPendingCameraXControl()
    {
        val startIndex = number
        if ((startIndex >= 0)&&(startIndex < 7))
        {
            for (index in startIndex..7)
            {
                if (cameraXControlList[index])
                {
                    // 自分より大きい ... cameraX はそっち(大きい方)を使う
                    isPendingTask = true
                }
            }
        }
    }

    private fun checkConcurrentCameras()
    {
        try
        {
            Log.v(TAG, "      checkConcurrentCameras() ")
            var index = 0
            val cameraProvider: ProcessCameraProvider = ProcessCameraProvider.getInstance(activity).get()
            for (cameraInfo in cameraProvider.availableCameraInfos)
            {
                Log.v(TAG, "-----****************** $index *******************-----")
                Log.v(TAG, "lensFacing : ${cameraInfo.lensFacing}")
                Log.v(TAG, "exposureState : ${cameraInfo.exposureState}")
                Log.v(TAG, "sensorRotationDegrees : ${cameraInfo.sensorRotationDegrees}")
                Log.v(TAG, "hasFlashUnit : ${cameraInfo.hasFlashUnit()}")
                index++
                Log.v(TAG, "-----****************************************-----")
            }
            index = 0
            for (cameraInfos in cameraProvider.availableConcurrentCameraInfos)
            {
                Log.v(TAG, "****************** $index *******************")
                for (cameraInfo in cameraInfos)
                {
                    Log.v(TAG, "lensFacing : ${cameraInfo.lensFacing}")
                    Log.v(TAG, "exposureState : ${cameraInfo.exposureState}")
                    Log.v(TAG, "sensorRotationDegrees : ${cameraInfo.sensorRotationDegrees}")
                    Log.v(TAG, "hasFlashUnit : ${cameraInfo.hasFlashUnit()}")
                }
                index++
                Log.v(TAG, "****************************************")
            }

            }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getCameraStatus(): ICameraStatus { return (cameraXCameraStatusHolder) }






    override fun doShutter()
    {
        try
        {
            if (::fileControl.isInitialized)
            {
                fileControl.takePhoto(0)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
    override fun doShutterOff() { }

    companion object
    {
        private val TAG = CameraControl::class.java.simpleName
    }
}
