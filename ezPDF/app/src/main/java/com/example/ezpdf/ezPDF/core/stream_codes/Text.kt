package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode

class Text(
    override var x: Int,
    override var y: Int,
    fontSize: Int,
    var text: String,
    textColor: FillColor
): StreamCode {
    override val color = textColor
    override val strokeWidth = fontSize

    init {
        text = text.replace("(", "\\(")
        text = text.replace(")", "\\)")
    }

    override fun toString(): String {
        return "${color}BT /F1 $strokeWidth Tf $x $y Td ($text)Tj ET\n"
    }
}