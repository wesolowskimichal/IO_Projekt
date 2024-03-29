package com.example.ezpdf.ezPDF.core

import com.example.ezpdf.ezPDF.core.stream_codes.Image

class Page(
    override val id: Int,
    val parent: Parent,
    val resource: Resource,
    val content: Content,
    val stream: Stream,
) : ObjCodeBlock() {

    override fun toString(): String {
        return "$id 0 obj << /Type /Page /Resources ${resource.getReference()} /Parent ${parent.getReference()} /Contents ${content.getReference()} >> endobj\n$resource$content$stream"
    }
}