package com.example.ezpdf.ezPDF.core.stream_codes

import android.graphics.Bitmap
import android.graphics.Color
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import java.io.ByteArrayOutputStream

class Image(
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
        val encodedImg = bitmapToAsciiHex(img)
        return "q\n"+
                "${img.width} 0 0 ${img.height} ${x} ${y} cm"+
                "BI\n" +
                "/W ${img.width}\n" +
                "/H ${img.height}\n" +
                "/CS /RGB\n" +
                "/BPC 8\n" +
                "/F /ASCIIHexDecode\n" +
                "ID\n" +
                "$encodedImg\n" +
                "EI\n" +
                "Q\n"
    }
}