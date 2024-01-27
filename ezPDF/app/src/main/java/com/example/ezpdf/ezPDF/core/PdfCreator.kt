package com.example.ezpdf.ezPDF.core

import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import com.example.ezpdf.ezPDF.figures.Image

class PdfCreator(
    val width: Int,
    val height: Int
) {
    private var CODE = "%PDF-1.7\n"
    private val _imgs: MutableList<com.example.ezpdf.ezPDF.core.stream_codes.Image> = mutableListOf()

    init {
        initPages()
    }

    fun initPages() {
        CODE += "${IdManager.getId} 0 obj << /Type /Catalog /Pages ${IdManager.getId} 0 R >> endobj\n"
    }

    fun addImg(img: Image) {
        _imgs.add(com.example.ezpdf.ezPDF.core.stream_codes.Image(
            IdManager.getId,
            img.position.x.toInt(),
            img.position.y.toInt(),
            img.image
        ))
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

    fun createPage(figures: List<StreamCode>, images: List<Image>? = null): Page {
        val id = IdManager.getId
        val parent = Parent(2)
        val resource = Resource(IdManager.getId)
        val streamImages: MutableList<com.example.ezpdf.ezPDF.core.stream_codes.Image> = mutableListOf()
        if (images != null) {
            for(image in images) {
                streamImages.add(com.example.ezpdf.ezPDF.core.stream_codes.Image(
                    IdManager.getId,
                    image.position.x.toInt(),
                    image.position.y.toInt(),
                    image.image
                ))
            }
        }
        val stream = Stream(figures)
        val content = Content(IdManager.getId, stream.size)

        return Page(id, parent, resource, content, stream, streamImages)
    }
}