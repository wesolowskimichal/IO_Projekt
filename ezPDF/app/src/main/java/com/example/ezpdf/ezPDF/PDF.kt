package com.example.ezpdf.ezPDF

import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.FileOutputStream

class PDF{
    private lateinit var _document : PdfDocument
    private lateinit var _pageInfo: PdfDocument.PageInfo
    private lateinit var _page : PdfDocument.Page
    private val _paint : Paint = Paint()
    private var _isPageOpened : Boolean = false
    private var _isDocumentCreated : Boolean = false;

    init {
        CreateDocument()
    }

    fun CreateDocument() {
        _isDocumentCreated = true
        _document = PdfDocument()
    }

    fun CloseDocument() {
        if(_isPageOpened) {
            ClosePage()
        }
        if(!_isDocumentCreated) {
            throw RuntimeException("ezPDF::PDF::Error: Cannot close not created document!")
        }
        _document.close()
        _isDocumentCreated = false
    }

    fun CreatePage(pageWidth : Int, pageHeight : Int, pageNumber : Int = 1) {
        if(_isPageOpened) {
            ClosePage()
        }
        _pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        _page = _document.startPage(_pageInfo)
        _isPageOpened = true
    }

    fun ClosePage() {
        if(!_isPageOpened) {
            throw RuntimeException("ezPDF::PDF::Error: Cannot close not opened page!")
        }
        _document.finishPage(_page)
        _isPageOpened = false
    }

    fun GetPage() : PdfDocument.Page {
        return _page;
    }

    fun SaveDocument(file: File) {
        _document.writeTo(FileOutputStream(file))
    }

    fun GetPages() : MutableList<PdfDocument.PageInfo>? {
        if(!_isDocumentCreated) {
            throw RuntimeException("ezPDF::PDF::Error: Cannot get pages from not created page!")
        }
        return _document.pages
    }

    fun IsPageOpened() : Boolean {
        return _isPageOpened
    }

}