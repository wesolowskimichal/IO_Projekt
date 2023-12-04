package com.example.ezpdf

import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.ezpdf.ezPDF.CanvasView
import com.example.ezpdf.ezPDF.PDF
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {
    private lateinit var canvasView: CanvasView
    private lateinit var currSelected:ImageButton
    private lateinit var pdf: PDF
    private var strokeSize = 2f
    private var focused = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        canvasView = findViewById(R.id.canvasView)
        pdf = PDF()


        val exportButton: Button = findViewById(R.id.exportButton)
        exportButton.setOnClickListener { exportToPDF() }

        val circButton: ImageButton = findViewById(R.id.circ)
        circButton.setOnClickListener {
            setCirc()
            setSelectedToolIndicator(circButton)
        }

        val rectButton: ImageButton = findViewById(R.id.rect)
        rectButton.setOnClickListener {
            setRect()
            setSelectedToolIndicator(rectButton)

        }
        val lineButton: ImageButton = findViewById(R.id.line)
        lineButton.setOnClickListener {
            setLine()
            setSelectedToolIndicator(lineButton)

        }
        val drawButton: ImageButton = findViewById(R.id.draw)
        drawButton.setOnClickListener {
            setDrawing()
            setSelectedToolIndicator(drawButton)

        }

        val newPageButton: ImageButton = findViewById(R.id.newPage)
        newPageButton.setOnClickListener{
            val canvas = pdf.GetPage().canvas
            canvasView.draw(canvas)
            pdf.ClosePage()
            pdf.CreatePage(canvasView.width, canvasView.height)
            canvasView.clear()
        }



//        zmiana grubości pędzla
        val sizeBar: SeekBar = findViewById(R.id.sb_size)
        sizeBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    strokeSize = seekBar.progress.toFloat()
                    canvasView.strokeSize = strokeSize
                    canvasView.updatePaint()
                    Log.d("d","sSize: $strokeSize")
                }
            }
        })


        val colorButtons = arrayOf(
            findViewById<Button>(R.id.color_black),
            findViewById(R.id.color_white),
            findViewById(R.id.color_cyan),
            findViewById(R.id.color_yellow),
            findViewById(R.id.color_magenta),
            findViewById(R.id.color_red),
            findViewById(R.id.color_blue),
            findViewById(R.id.color_green),
        )
        val colors = arrayOf(
            Color.Black,
            Color.White,
            Color.Cyan,
            Color.Yellow,
            Color.Magenta,
            Color.Red,
            Color.Blue,
            Color.Green
        )
        fun handleColorChange(button:Button){
            val i = colorButtons.indexOf(button)
            canvasView.drawColor = colors[i]
            canvasView.updatePaint()
        }


        for (bt in colorButtons){
            bt.setOnClickListener{
                handleColorChange(bt)
            }
        }



        currSelected = drawButton

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            if(!focused) {
                pdf.CreatePage(canvasView.width, canvasView.height)
                focused = true
            }
        }
    }
//      change bg color of selected tool
    private fun setSelectedToolIndicator(selected:ImageButton){

        currSelected.background.setTint(ContextCompat.getColor(this, R.color.transparent))
        selected.background.setTint(ContextCompat.getColor(this, androidx.constraintlayout.widget.R.color.material_grey_300))
        currSelected = selected

    }


    private fun setDrawing() {
        canvasView.drawType = CanvasView.DrawType.DRAW
        canvasView.strokeSize = strokeSize
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
        val canvas = pdf.GetPage().canvas

        // Draw the content of the CanvasView on the PDF page's canvas
        canvasView.draw(canvas)

        // Close the current PDF page
        pdf.ClosePage()

        // Create a file to save the PDF
        val title = findViewById<EditText>(R.id.et_title)
        var docName = title.text.toString()
        if (docName.isBlank()) {
            docName = "untitled"
        }
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "$docName.pdf")

        // Save the PDF to the file
        pdf.SaveDocument(file)

        // Notify the user that the export is complete (you can use a Toast or any other UI element)
        val t = Toast.makeText(this, "PDF Exported to ${file.absolutePath}", Toast.LENGTH_SHORT)
        t.show()


        /*try {
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
            val file = File(downloadsDir, "$docName.pdf")

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
        }*/
    }

}