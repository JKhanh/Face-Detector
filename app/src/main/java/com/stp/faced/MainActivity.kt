package com.stp.faced

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.stp.faced.databinding.ActivityMainBinding
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CameraViewModel by viewModel()
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        cameraExecutor = Executors.newSingleThreadExecutor()
        checkPermission()
    }

    private fun startCamera(){
        viewModel.buildImageAnalyst(ContextCompat.getMainExecutor(this))
        viewModel.startCamera(binding.preview, this, this)

        viewModel.faces.observe(this){
            it?.let { face ->
                Glide.with(this)
                    .load(face)
                    .into(binding.facePreview)
            }
        }
    }

    private fun checkPermission() {
        if(isPermissionGranted()){
            startCamera()
        } else {
            requestPermission()
        }
    }

    private fun isPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(isPermissionGranted()){
                startCamera()
            } else {
                Toast.makeText(this,
                    "FaceD need to have Camera permission to perform this feature",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}