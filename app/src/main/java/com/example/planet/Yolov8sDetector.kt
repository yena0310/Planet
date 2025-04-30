package com.example.planet
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel
import android.util.Log

class Yolov8sDetector(context: Context) {

    private val interpreter: Interpreter

    init {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd("best_float32.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val modelBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        interpreter = Interpreter(modelBuffer)
    }

    fun detect(bitmap: Bitmap): List<DetectionResult> {
        // 1. 이미지 전처리: 640x640으로 리사이즈 + float32 변환
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputBuffer = preprocessBitmap(resizedBitmap)

        // 2. 출력 버퍼 준비 (YOLOv8 TFLite 형식에 맞게)
        val outputMap = mutableMapOf<Int, Any>()
        val outputBuffer = Array(1) { Array(16) { FloatArray(8400) } }
        outputMap[0] = outputBuffer

        // 3. 추론 실행
        try {
            interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)
            Log.d("YOLO-TEST", "Detection executed")
        } catch (e: Exception) {
            Log.e("YOLO-TEST", "Error during detection: ${e.message}", e)
            return emptyList()
        }

        // 4. 결과 파싱
        return parseDetections(outputBuffer)
    }


    private fun preprocessBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val input = Array(1) { Array(640) { Array(640) { FloatArray(3) } } }

        for (y in 0 until 640) {
            for (x in 0 until 640) {
                val pixel = bitmap.getPixel(x, y)
                input[0][y][x][0] = ((pixel shr 16 and 0xFF) / 255.0f) // R
                input[0][y][x][1] = ((pixel shr 8 and 0xFF) / 255.0f)  // G
                input[0][y][x][2] = ((pixel and 0xFF) / 255.0f)       // B
            }
        }
        return input
    }

    private fun parseDetections(output: Array<Array<FloatArray>>): List<DetectionResult> {
        val numClasses = 12
        val numBoxes = output[0][0].size

        var bestResult: DetectionResult? = null
        var bestConfidence = 0f

        for (i in 0 until numBoxes) {
            val classScores = FloatArray(numClasses) { c -> output[0][c][i] }
            val maxClassIdx = classScores.indices.maxByOrNull { classScores[it] } ?: continue
            val confidence = classScores[maxClassIdx]

            if (confidence >= 0.9f && confidence > bestConfidence) {
                val x = output[0][numClasses][i]
                val y = output[0][numClasses + 1][i]
                val w = output[0][numClasses + 2][i]
                val h = output[0][numClasses + 3][i]

                bestResult = DetectionResult(
                    boundingBox = RectF(x - w / 2, y - h / 2, x + w / 2, y + h / 2),
                    confidence = confidence,
                    classId = maxClassIdx
                )
                bestConfidence = confidence
            }
        }

        return bestResult?.let { listOf(it) } ?: emptyList()
    }




    data class DetectionResult(
        val boundingBox: RectF,
        val confidence: Float,
        val classId: Int

    )
}