package com.example.ezpdf

import android.app.Dialog
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import com.example.ezpdf.ezPDF.CanvasView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var canvasView: CanvasView;

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        canvasView = findViewById(R.id.canvasView)


        val exportButton: Button = findViewById(R.id.exportButton)
        exportButton.setOnClickListener { exportToPDF() }
        val circButton: Button = findViewById(R.id.circ)
        circButton.setOnClickListener { setCirc() }
        val rectButton: Button = findViewById(R.id.rect)
        rectButton.setOnClickListener { setRect() }
        val lineButton: Button = findViewById(R.id.line)
        lineButton.setOnClickListener { setLine() }
        val drawButton: Button = findViewById(R.id.draw)
        drawButton.setOnClickListener { setDrawing() }
    }

    private fun setDrawing() {
        canvasView.drawType = CanvasView.DrawType.DRAW
    }

    private fun setCirc() {
        canvasView.drawType = CanvasView.DrawType.CIRCLE
    }

    private fun setRect() {
        canvasView.drawType = CanvasView.DrawType.RECTANGLE
    }

    private fun setLine() {
        canvasView.drawType = CanvasView.DrawType.LINE
    }

    private fun exportToPDF() {
        val document = PdfDocument()

        try {
            // Create a new PDF page
            val pageInfo = PdfDocument.PageInfo.Builder(canvasView.width, canvasView.height, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Draw the content of the CanvasView on the PDF page's canvas
            //val cv = od.findViewById<CanvasView>(R.id.canvasView)
            //cv.draw(canvas)
            canvasView.draw(canvas)

            // Finish the page and save the PDF
            document.finishPage(page)

            // Create a file to save the PDF
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Create a file in the Downloads directory to save the PDF
            val file = File(downloadsDir, "my_pdf_file.pdf")

            // Write the PDF to the file
            document.writeTo(FileOutputStream(file))

            // Close the PDF document
            document.close()

            // Notify the user that the export is complete (you can use a Toast or any other UI element)
            var t = Toast.makeText(this, "PDF Exported to ${file.absolutePath}", Toast.LENGTH_SHORT)
            t.show()
        } catch (e: IOException) {
            // Handle exceptions, e.g., when the file cannot be created
            e.printStackTrace()
        }
    }

}