package com.example.ezpdf.ezPDF.core

class Content(
    override val id: Int,
    val size: Int
): ObjCodeBlock() {

    // nwm co to to 1000
    override fun toString(): String {
        return "$id 0 obj << /Length $size >>\n"
    }
}