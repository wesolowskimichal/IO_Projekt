package com.example.ezpdf.ezPDF.core

abstract class ObjCodeBlock {
    abstract val id: Int
    fun getReference(): String {
        return "$id 0 R"
    }
}