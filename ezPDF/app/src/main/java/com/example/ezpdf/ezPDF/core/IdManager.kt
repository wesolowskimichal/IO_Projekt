package com.example.ezpdf.ezPDF.core

class IdManager {
    companion object {
        private var _id = 1
        val getId: Int
            get() {
                val id = _id
                ++_id
                return id
            }
        val getByteLines: Int
            get() = _id
    }
}