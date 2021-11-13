package com.stp.faced

//import android.graphics.Bitmap
//import androidx.camera.core.ImageAnalysis
//import androidx.camera.core.ImageProxy
//import com.google.mlkit.vision.common.InputImage
//import com.google.mlkit.vision.face.FaceDetection
//import com.google.mlkit.vision.face.FaceDetectorOptions
//
//class FaceAnalysisMLKit(private val listener: FaceListener): ImageAnalysis.Analyzer {
//    private val option = FaceDetectorOptions.Builder()
//        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
//        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
//        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//        .build()
//
////    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
////        0 -> FirebaseVisionImageMetadata.ROTATION_0
////        90 -> FirebaseVisionImageMetadata.ROTATION_90
////        180 -> FirebaseVisionImageMetadata.ROTATION_180
////        270 -> FirebaseVisionImageMetadata.ROTATION_270
////        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
////    }
//
//    @androidx.camera.core.ExperimentalGetImage
//    override fun analyze(imageProxy: ImageProxy) {
//        val mediaImage = imageProxy.image
//        val imageRotation = imageProxy.imageInfo.rotationDegrees
//        if (mediaImage != null) {
//            val image = InputImage.fromMediaImage(mediaImage, imageRotation)
//            val detector = FaceDetection.getClient(option)
//
//            detector.process(image)
//                .addOnSuccessListener { faces ->
//                    val face = faces.maxByOrNull { it.boundingBox.height()*it.boundingBox.width() }
//                    if(face != null){
//                        listener.addFace(Bitmap.createBitmap(image.bitmapInternal!!, face.boundingBox.left, face.boundingBox.top, face.boundingBox.width(), face.boundingBox.height()))
//                    }
//                }
//        }
//        imageProxy.close()
//    }
//}