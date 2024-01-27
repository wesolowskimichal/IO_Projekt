package com.example.ezpdf.ezPDF.core.stream_codes

import com.example.ezpdf.ezPDF.core.stream_codes.templates.Color

class FillColor(red: Int, green: Int, blue: Int) : Color(red, green, blue) {
    override fun toString(): String {
        return super.toString() + "rg\n"
    }
}