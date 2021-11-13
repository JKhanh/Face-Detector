package com.stp.faced

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.util.concurrent.Executor

class CameraViewModel: ViewModel() {
    lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    lateinit var camera: Camera
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val faces = MutableLiveData<Bitmap?>(null)

    init{
        bindImageCapture()
    }

    private fun bindImageCapture() {
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(720,1080))
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .build()
    }

    fun buildImageAnalyst(executors: Executor){
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(640, 480))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executors, FaceAnalysis(object : FaceListener {
                    override fun addFace(face: Bitmap) {
                        faces.value = face
                    }
                }))
            }
    }

    fun startCamera(previewView: PreviewView, context: Context, lifecycleOwner: LifecycleOwner){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            bindImageCapture()

            bindCamera(lifecycleOwner)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner){
        try{
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch(exc: Exception) {
            Timber.e(exc, "Use case binding failed")
        }
    }
}