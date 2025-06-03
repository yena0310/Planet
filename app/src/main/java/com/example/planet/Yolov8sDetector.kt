package com.example.planet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.channels.FileChannel

class Yolov8sDetector(context: Context) {

    private val interpreter: Interpreter
    private val classIdToGuide = mapOf(
        0 to "종이는 깨끗이 접어서\n종이류로 배출하세요.",
        1 to "종이팩은 헹군 후\n종이팩 전용 수거함에\n배출하세요.",
        2 to "종이컵은 오염이 있을 시\n일반 쓰레기로 버리세요.",
        3 to "캔류는 내용물을 비우고\n헹군 후 배출하세요.",
        4 to "유리는 병뚜껑을\n제거하고 배출하세요.",
        5 to "페트는 라벨을 제거하고\n압축해서 배출하세요.",
        6 to "플라스틱은 음식물 등을\n제거하고 배출하세요.",
        7 to "비닐은 이물질을 제거한 뒤\n배출하세요.",
        8 to "다중포장재+유리는 분리 후\n각각 재질에 맞게 배출하세요.",
        9 to "다중포장재_페트는 분리 후\n페트와 종이 등 따로 배출하세요.",
        10 to "스티로폼은 깨끗이\n정리 후 배출하세요.",
        11 to "배터리는 전용 폐건전지\n수거함에 배출하세요."
    )

    init {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd("best_float32.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val modelBuffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
        interpreter = Interpreter(modelBuffer)
        //Log.d("YOLO-INFO", "클래스 목록: ${classIdToGuide.map { (id, guide) -> "$id: $guide" }.joinToString()}")
    }

    fun detect(bitmap: Bitmap, confidenceThreshold: Float = 0.5f): List<DetectionResult> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 640, true)
        val inputBuffer = preprocessBitmap(resizedBitmap)

        val outputMap = mutableMapOf<Int, Any>()
        val outputBuffer = Array(1) { Array(16) { FloatArray(8400) } }
        outputMap[0] = outputBuffer

        try {
            interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)
            Log.d("YOLO-TEST", "Detection executed")
        } catch (e: Exception) {
            Log.e("YOLO-TEST", "Error during detection: ${e.message}", e)
            return emptyList()
        }

        return parseDetections(outputBuffer, confidenceThreshold)
    }

    private fun preprocessBitmap(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val input = Array(1) { Array(640) { Array(640) { FloatArray(3) } } }
        for (y in 0 until 640) {
            for (x in 0 until 640) {
                val pixel = bitmap.getPixel(x, y)
                input[0][y][x][0] = ((pixel shr 16 and 0xFF) / 255.0f)
                input[0][y][x][1] = ((pixel shr 8 and 0xFF) / 255.0f)
                input[0][y][x][2] = ((pixel and 0xFF) / 255.0f)
            }
        }
        return input
    }

    private fun parseDetections(output: Array<Array<FloatArray>>, confidenceThreshold: Float): List<DetectionResult> {
        val numClasses = 12
        val numBoxes = output[0][0].size
        val results = mutableListOf<DetectionResult>()

        for (i in 0 until numBoxes) {
            val classScores = FloatArray(numClasses) { c -> output[0][c][i] }
            val maxClassIdx = classScores.indices.maxByOrNull { classScores[it] } ?: continue
            val confidence = classScores[maxClassIdx]

            if (confidence >= confidenceThreshold) {
                val x = output[0][numClasses][i]
                val y = output[0][numClasses + 1][i]
                val w = output[0][numClasses + 2][i]
                val h = output[0][numClasses + 3][i]
                val guide = classIdToGuide[maxClassIdx] ?: "해당 항목에 대한 가이드를 찾을 수 없습니다."

                results.add(
                    DetectionResult(
                        boundingBox = RectF(x - w / 2, y - h / 2, x + w / 2, y + h / 2),
                        confidence = confidence,
                        classId = maxClassIdx,
                        guide = guide
                    )
                )
            }
        }

        return results.sortedByDescending { it.confidence }
    }

    data class DetectionResult(
        val boundingBox: RectF,
        val confidence: Float,
        val classId: Int,
        val guide: String
    )
}
