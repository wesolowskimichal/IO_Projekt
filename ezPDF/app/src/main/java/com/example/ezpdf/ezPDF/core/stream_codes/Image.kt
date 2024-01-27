package com.example.ezpdf.ezPDF.core.stream_codes

import android.graphics.Bitmap
import android.graphics.Color
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import java.io.ByteArrayOutputStream

class Image(
    val id: Int,
    override var x: Int,
    override var y: Int,
    val img: Bitmap
):StreamCode {
    override val color = FillColor(0, 0, 0)
    override val strokeWidth = null

    private fun bitmapToAsciiHex(bitmap: Bitmap): String {
        val result = StringBuilder()

        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val pixel = bitmap.getPixel(x, y)
                val hexColor = String.format("%06X", Color.rgb(
                    Color.red(pixel),
                    Color.green(pixel),
                    Color.blue(pixel)
                ))

                result.append(hexColor)
            }
            result.append("\n")
        }

        return result.toString()
    }

    override fun toString(): String {
        return "$id 0 obj\n" +
                "<<\n" +
                "/Type /XObject\n" +
                "/Subtype /Image\n" +
                "/Width ${img.width}\n" +
                "/Height ${img.height}\n" +
                "/ColorSpace /DeviceRGB\n" +
                "/BitsPerComponent 8\n" +
                "/Filter /ASCIIHexDeCode\n" +// Compression method (JPEG)
                "/Length 99999\n" +// Length of the image data
                ">>\n" +
                "stream\n" +
                "${bitmapToAsciiHex(img)}\n" +// Binary image data (JPEG, PNG, etc.)
                "endstream\n" +
                "endobj\n"
    }
}