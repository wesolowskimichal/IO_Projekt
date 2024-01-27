package com.example.ezpdf.ezPDF.core

import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode

class Stream(
    val streamCodes: List<StreamCode>
){
    val size: Int
        get() {
            var bytes = 0
            for(streamCode in streamCodes) {
                bytes += streamCode.toString().length
            }
            return bytes-1
        }
    override fun toString(): String {
        var stream = "stream\n"
        for(streamCode in streamCodes) {
            stream += streamCode
        }
        stream += "endstream\nendobj\n"
        return stream
    }
}