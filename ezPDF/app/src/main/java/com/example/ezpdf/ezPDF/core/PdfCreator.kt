package com.example.ezpdf.ezPDF.core

import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import com.example.ezpdf.ezPDF.figures.Image

class PdfCreator(
    val width: Int,
    val height: Int
) {
    private var CODE = "%PDF-1.7\n"

    init {
        initPages()
    }

    fun initPages() {
        CODE += "${IdManager.getId} 0 obj << /Type /Catalog /Pages ${IdManager.getId} 0 R >> endobj\n"
    }

    fun createDocument(pages: List<Page>) {
        CODE += "2 0 obj << /Type /Pages /Kids ["
        for ((index, page) in pages.withIndex()) {
            if (index > 0) {
                CODE += " "
            }
            CODE += page.getReference()
        }
        CODE += "] /Count ${pages.size} /MediaBox [0 0 $width $height] >> endobj\n"
        for (page in pages) {
            CODE += page
        }
    }

   fun endDocument(): String {
       val revNumber = IdManager.getId

       CODE += "xref\n0 $revNumber\n"

       CODE += "trailer << /Size $revNumber /Root 1 0 R >>\n" +
               "startxref\n" +
               "%%EOF"
       return CODE
   }

    fun createPage(figures: List<StreamCode>): Page {
        val id = IdManager.getId
        val parent = Parent(2)
        val resource = Resource(IdManager.getId)
        val stream = Stream(figures)
        val content = Content(IdManager.getId, stream.size)

        return Page(id, parent, resource, content, stream)
    }
}