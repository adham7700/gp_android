package com.google.mediapipe.examples.handlandmarker
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

import java.io.File
import java.io.IOException
import java.nio.FloatBuffer

class KeypointClassifier(private val context: Context) {
    init {

    }
    private lateinit var interpreter: Interpreter
    private lateinit var labels: Array<String>

    init {
        try {
            interpreter = Interpreter(loadModelFile(), Interpreter.Options())
            labels = arrayOf(
                "أ", "ب", "ت","ث","ج","س","ش","ص","ض","م","ك","ل","ي","ن","و","ق","ف",
                "ه","ط","ظ","ع","غ","ح","خ","د","ذ","ر","ز"

            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadModelFile(): ByteBuffer {
        val modelFileDescriptor = context.assets.openFd("keypoint_classifier.tflite")
        val inputStream = modelFileDescriptor.createInputStream()
        val modelFileLength = modelFileDescriptor.length.toInt()
        val buffer = ByteBuffer.allocateDirect(modelFileLength)

        inputStream.use { input ->
            buffer.put(input.readBytes())
        }
        return buffer
    }

    fun classify(keypoints: FloatArray): Pair<String, FloatArray> {
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 42), DataType.FLOAT32)
        inputFeature.loadArray(keypoints, intArrayOf(1, 42))

        val outputs = Array(1) { FloatArray(labels.size) }
        interpreter.run(inputFeature.buffer, outputs)

        val output = outputs[0]
        val maxIndex = output.indices.maxByOrNull { output[it] } ?: -1
        val label = if (maxIndex != -1) labels[maxIndex] else "Unknown"

        return Pair(label, output)
    }
    public fun createORTSession(ortEnvironment: OrtEnvironment): OrtSession {
        try {
            val modelBytes = context.assets.open("Naive Bayes.onnx").readBytes()
            return ortEnvironment.createSession(modelBytes)
        } catch (e: IOException) {
            Log.e("ORTSession", "Error loading ONNX model: ${e.message}")
        }
        return TODO("Provide the return value")
    }


    public fun runPrediction(input: FloatArray, ortSession: OrtSession, ortEnvironment: OrtEnvironment): String {
        // Get the name of the input tensor
        val inputName = ortSession.inputNames?.iterator()?.next()

        // Convert the input FloatArray to a FloatBuffer
        val floatBufferInputs = FloatBuffer.wrap(input)

        // Create the input tensor with the truncated input array
        val inputTensor = OnnxTensor.createTensor(ortEnvironment, floatBufferInputs.limit(42)as FloatBuffer, longArrayOf(42))
        // Run inference
        val results = ortSession.run(mapOf(inputName to inputTensor))


        // Assuming results[0].value is a long array that needs to be converted to a float array
        val longArray = results[0].value as LongArray
        val floatArray = longArray.map { it.toFloat() }.toFloatArray()

        // Log the output for debugging
        val output = arrayOf(floatArray)

        // Return the output
        val gg = output[0]
        val maxIndex = output.indices.maxByOrNull { gg[it] } ?: -1
        val label = if (maxIndex != -1) labels[maxIndex] else "Unknown"

        return label
    }}
