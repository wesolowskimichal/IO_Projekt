package com.example.ezpdf.ezPDF.core

class Resource(
    override val id: Int
): ObjCodeBlock() {

    // narazie font nwm jak dalej
    override fun toString(): String {
        return "$id 0 obj << /Font << /F1 << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> >> >> endobj\n"
    }
}