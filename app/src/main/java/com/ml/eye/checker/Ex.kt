package com.ml.eye.checker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


enum class ButtonState { Pressed, Idle }
fun Modifier.bounceClick() = composed {
    var buttonState by remember { mutableStateOf(ButtonState.Idle) }
    val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.85f else 1f,
        label = ""
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { }
        )
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState == ButtonState.Pressed) {
                    waitForUpOrCancellation()
                    ButtonState.Idle
                } else {
                    awaitFirstDown(false)
                    ButtonState.Pressed
                }
            }
        }
}

fun Uri.toBitmap(context: Context): Bitmap? {
    var image: Bitmap? = null
    try {
        val parcelFileDescriptor =
            context.contentResolver.openFileDescriptor(this, "r")
        parcelFileDescriptor?.use {
            val fileDescriptor = it.fileDescriptor
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        }
    }
    catch (e: IOException) {
        e.printStackTrace()
    }
    return image
}

fun Uri.toByteBuffer(context: Context): ByteBuffer? {
    try {
        val inputStream = context.contentResolver.openInputStream(this)
        inputStream?.use {
            val MAX_SIZE = 5000000
            val byteArr = ByteArray(MAX_SIZE)
            var arrSize = 0
            while (true) {
                val value = inputStream.read(byteArr)
                arrSize += if (value == -1) {
                    break
                } else {
                    value
                }
            }
            return ByteBuffer.wrap(byteArr, 0, arrSize)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

fun Bitmap.toUri(context: Context): Uri {
    val outputFile = File(context.cacheDir,"tmp.jpg")
    val outputStream = FileOutputStream(outputFile)
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.close()
    return Uri.fromFile(outputFile)
}

fun FloatArray.joinToFormattedString(): String {
    val pattern = "#.0#" // rounds to 2 decimal places if needed
    val locale = Locale.ENGLISH
    val formatter = DecimalFormat(pattern, DecimalFormatSymbols(locale))
    formatter.roundingMode = RoundingMode.HALF_EVEN // this is the default rounding mode anyway
    return this.joinToString(
        separator = ", ",
        prefix = "[",
        postfix = "]",
    ) { value ->
        formatter.format(value)
    }
}
