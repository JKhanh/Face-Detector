package com.stp.faced

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

class FaceAnalysis(private val listener: FaceListener): ImageAnalysis.Analyzer {
    private val option = FirebaseVisionFaceDetectorOptions.Builder()
        .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
        .setContourMode(FirebaseVisionFaceDetectorOptions.NO_CONTOURS)
        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.NO_LANDMARKS)
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .build()

    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val imageRotation = degreesToFirebaseRotation(imageProxy.imageInfo.rotationDegrees)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            val detector = FirebaseVision.getInstance().getVisionFaceDetector(option)

            detector.detectInImage(image)
                .addOnSuccessListener { faces ->
                    val face = faces.maxByOrNull { it.boundingBox.height()*it.boundingBox.width() }?.boundingBox
                    if(face != null){
                        if(image.bitmap.width >= face.left + face.width() &&
                            image.bitmap.height >= face.top + face.height()) {
                            listener.addFace(
                                Bitmap.createBitmap(
                                    image.bitmap,
                                    face.left, face.top,
                                    face.width(), face.height()
                                )
                            )
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}