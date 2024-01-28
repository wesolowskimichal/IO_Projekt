package com.example.ezpdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.ezpdf.ezPDF.CanvasView
import com.example.ezpdf.ezPDF.PDF
import com.example.ezpdf.ezPDF.core.Page
import com.example.ezpdf.ezPDF.core.PdfCreator
import com.example.ezpdf.ezPDF.core.stream_codes.templates.StreamCode
import com.example.ezpdf.ezPDF.figures.Image
import com.example.ezpdf.ezPDF.figures.Line
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter

class MainActivity : ComponentActivity() {
    private lateinit var canvasView: CanvasView
    private lateinit var currSelected:ImageButton
    private lateinit var editText: EditText
    private lateinit var pdf: PDF
    private var bitmap: Bitmap? = null

    private var strokeSize = 2f
    private var focused = false

    private lateinit var pdfCreator: PdfCreator
    val pages: MutableList<Page> = mutableListOf()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainlayout)
        canvasView = findViewById(R.id.canvasView)
        pdf = PDF()
        editText = findViewById(R.id.et_addText)
        val titleView = findViewById<EditText>(R.id.et_title)




        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                GlobalScope.launch {
                    bitmap = loadBitmapFromUri(this@MainActivity, uri)

                    // Now you can use the bitmap as needed
                    bitmap?.let { setImage(it) }

                }

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }



        val exportButton: Button = findViewById(R.id.exportButton)
        exportButton.setOnClickListener { exportToPDF(titleView.text.toString()) }

        val circButton: ImageButton = findViewById(R.id.circ)
        circButton.setOnClickListener {
            handleToolSwitch()
            setCirc()
            setSelectedToolIndicator(circButton)
        }

        val rectButton: ImageButton = findViewById(R.id.rect)
        rectButton.setOnClickListener {
            handleToolSwitch()
            setRect()
            setSelectedToolIndicator(rectButton)

        }
        val lineButton: ImageButton = findViewById(R.id.line)
        lineButton.setOnClickListener {
            handleToolSwitch()
            setLine()
            setSelectedToolIndicator(lineButton)

        }
        val drawButton: ImageButton = findViewById(R.id.draw)
        drawButton.setOnClickListener {
            handleToolSwitch()
            setDrawing()
            setSelectedToolIndicator(drawButton)

        }

        val newPageButton: ImageButton = findViewById(R.id.newPage)
        newPageButton.setOnClickListener{
            handleToolSwitch()
            val figures = canvasView.getFigures()
            val figuresStreamCodes: MutableList<StreamCode> = mutableListOf()
            val images: MutableList<Image> = mutableListOf()
            for(figure in figures) {
                if(figure is Image) {
                    images.add(figure)
                } else {
                    figuresStreamCodes.add(figure.convertToStreamCode())
                    figuresStreamCodes.last().transpose(canvasView.width, canvasView.height)
                }
            }
            if(!this::pdfCreator.isInitialized) {
                pdfCreator = PdfCreator(canvasView.width, canvasView.height)
            }
            pages.add(pdfCreator.createPage(figuresStreamCodes, images))
            val canvas = pdf.GetPage().canvas
            canvasView.draw(canvas)
            pdf.ClosePage()
            pdf.CreatePage(canvasView.width, canvasView.height)
            canvasView.clear()
        }
        val addImageButton: ImageButton = findViewById(R.id.addImage)
        addImageButton.setOnClickListener {

            handleToolSwitch()
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            setSelectedToolIndicator(addImageButton)

        }

        editText.doOnTextChanged { _, _, _, _ ->
            val txt = editText.text.toString()
            canvasView.textToDraw = txt

        }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus){
                editText.visibility = View.GONE
            }
        }

        val textButton: ImageButton = findViewById(R.id.bt_text)
        textButton.setOnClickListener {

            handleToolSwitch()
            setSelectedToolIndicator(textButton)
            setText()
            editText.visibility = View.VISIBLE
            editText.requestFocus()
            showKeyboard(editText)
        }

        val editButton: ImageButton = findViewById(R.id.bt_edit)
        editButton.setOnClickListener {
            handleToolSwitch()
            setEdit()
            setSelectedToolIndicator(editButton)
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
                    canvasView.imageScale = 100 + seekBar.progress*10
                    canvasView.myTextSize = 50f + seekBar.progress
                    canvasView.updatePaint()
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
        editText.visibility = View.GONE
        canvasView.updatePaint()
        editText.text.clear()

    }
    private fun handleToolSwitch(){
        if (canvasView.drawType == CanvasView.DrawType.TEXT){
            canvasView.applyText()
        }

    }

    private fun setDrawing() {

        canvasView.drawType = CanvasView.DrawType.DRAW
        canvasView.strokeSize = strokeSize

    }

    private fun setEdit(){
        canvasView.drawType = CanvasView.DrawType.EDIT

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
    private fun setImage(img:Bitmap){

        canvasView.drawType = CanvasView.DrawType.IMAGE
        canvasView.image = img
    }
    private fun setText(){
        canvasView.drawType = CanvasView.DrawType.TEXT
    }
    private fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }



    suspend fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                // Open an input stream from the Uri
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    // Decode the stream into a Bitmap
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    null
                }
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
    private fun exportToPDF(fileName:String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if(!this::pdfCreator.isInitialized) {
            pdfCreator = PdfCreator(canvasView.width, canvasView.height)
        }
        val figures = canvasView.getFigures()
        val figuresStreamCodes: MutableList<StreamCode> = mutableListOf()
        for(figure in figures) {
            figuresStreamCodes.add(figure.convertToStreamCode())
            figuresStreamCodes.last().transpose(canvasView.width, canvasView.height)

        }
        pages.add(pdfCreator.createPage(figuresStreamCodes))
        pdfCreator.createDocument(pages)
        val res = pdfCreator.endDocument()
        val file = File(downloadsDir, "${fileName}.pdf")
        val txt = File(downloadsDir, "${fileName}.txt")
        val fileOutputStream = FileOutputStream(file)
        val fileOutputStream2 = FileOutputStream(txt)

        // Create a BufferedWriter to write the content to the file
        val bufferedWriter = BufferedWriter(OutputStreamWriter(fileOutputStream))
        val bufferedWriter2 = BufferedWriter(OutputStreamWriter(fileOutputStream2))
        try {
            // Write the content of 'res' to the file
            bufferedWriter.write(res)
            bufferedWriter2.write(res)
            println("File saved successfully.")
            Toast.makeText(this, "Zapisano w $downloadsDir", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            println("Error saving the file: ${e.message}")
        } finally {
            // Close the BufferedWriter and FileOutputStream
            bufferedWriter.close()
            bufferedWriter2.close()
            fileOutputStream.close()
            fileOutputStream2.close()
        }
        /*val canvas = pdf.GetPage().canvas

        // Draw the content of the CanvasView on the PDF page's canvas
        canvasView.draw(canvas)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val f = File(downloadsDir, "TEST.png")

        try {
            val fileOutputStream = FileOutputStream(f)
            val v = replaceWhiteWithTransparent(canvasView._pathBitmap, 255)
            val stream = ByteArrayOutputStream()
            val d = v?.compress(Bitmap.CompressFormat.PNG, 100, stream)
            Log.d("asdasdasdsadsadsda", stream.toByteArray().toString())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


        // Close the current PDF page
        pdf.ClosePage()

        // Create a file to save the PDF
        val title = findViewById<EditText>(R.id.et_title)
        var docName = title.text.toString()
        if (docName.isBlank()) {
            docName = "untitled"
        }
        val file = File(downloadsDir, "$docName.pdf")

        // Save the PDF to the file
        pdf.SaveDocument(file)

        // Notify the user that the export is complete (you can use a Toast or any other UI element)
        val t = Toast.makeText(this, "PDF Exported to ${file.absolutePath}", Toast.LENGTH_SHORT)
        t.show()*/


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

    fun replaceWhiteWithTransparent(originalBitmap: Bitmap, threshold: Int): Bitmap? {
        val width = originalBitmap.width
        val height = originalBitmap.height

        // Create a new Bitmap with alpha channel
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = originalBitmap.getPixel(x, y)

                // Extract the RGB values
                val red = pixel shr 16 and 0xFF
                val green = pixel shr 8 and 0xFF
                val blue = pixel and 0xFF

                // Check if the pixel is white (you may want to adjust the threshold)
                if (red >= threshold && green >= threshold && blue >= threshold) {
                    // If white, set alpha to 0 (fully transparent)
                    newBitmap.setPixel(x, y, Color(red, green, blue, 0).toArgb())
                } else {
                    // If not white, keep the original color
                    newBitmap.setPixel(x, y, pixel)
                }
            }
        }
        return newBitmap
    }

}