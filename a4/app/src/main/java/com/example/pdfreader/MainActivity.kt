package com.example.pdfreader

import android.content.Context

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.graphics.Color
import android.widget.Button
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so we should expect people to need this.
// We may wish to provide this code.
class MainActivity : AppCompatActivity() {
    val LOGNAME = "pdf_viewer"
    val FILENAME = "shannon1948.pdf"
    val FILERESID = R.raw.shannon1948

    // manage the pages of the PDF, see below
    lateinit var pdfRenderer: PdfRenderer
    lateinit var parcelFileDescriptor: ParcelFileDescriptor
    var currentPage: PdfRenderer.Page? = null


    // custom ImageView class that captures strokes and draws them over the image
    lateinit var pageImage: PDFimage
    var pageNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val layout = findViewById<LinearLayout>(R.id.pdfLayout)
        val prePage = findViewById<Button>(R.id.prepage)
        val nextPage = findViewById<Button>(R.id.nextpage)
        val draw = findViewById<Button>(R.id.draw)
        val highlight = findViewById<Button>(R.id.highlight)
        val erase = findViewById<Button>(R.id.erase)
        val undo = findViewById<Button>(R.id.undo)
        val redo = findViewById<Button>(R.id.redo)

        prePage.setOnClickListener{
                if(pageNum > 0){
                    pageNum = pageNum - 1
                    showPage(pageNum)
                }
        }

        nextPage.setOnClickListener{
                if(pageNum < pdfRenderer.pageCount){
                    pageNum = pageNum + 1
                    showPage(pageNum)
                }

        }


        draw.setOnClickListener {
            pageImage.setDraw()
        }
        highlight.setOnClickListener {
            pageImage.setBrush()
        }
        erase.setOnClickListener {
            pageImage.setErase()
        }

        undo.setOnClickListener {
            pageImage.undo()
        }
        redo.setOnClickListener {
            pageImage.redo()
        }


        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
            openRenderer(this)


            layout.isEnabled = true
            pageImage = PDFimage(this,pdfRenderer.pageCount)
            layout.addView(pageImage)
            pageImage.minimumWidth = 1000
            pageImage.minimumHeight = 2000


            showPage(pageNum)
            //closeRenderer()
        } catch (exception: IOException) {
            Log.d(LOGNAME, "Error opening PDF")
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            closeRenderer()
        } catch (ex: IOException) {
            Log.d(LOGNAME, "Unable to close PDF renderer")
        }
    }

    @Throws(IOException::class)
    private fun openRenderer(context: Context) {
        // In this sample, we read a PDF from the assets directory.
        val file = File(context.cacheDir, FILENAME)
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            val asset = this.resources.openRawResource(FILERESID)
            val output = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var size: Int
            while (asset.read(buffer).also { size = it } != -1) {
                output.write(buffer, 0, size)
            }
            asset.close()
            output.close()
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        pdfRenderer = PdfRenderer(parcelFileDescriptor)
    }

    // do this before you quit!
    @Throws(IOException::class)
    private fun closeRenderer() {
        currentPage?.close()
        pdfRenderer.close()
        parcelFileDescriptor.close()
    }

    private fun showPage(index: Int) {

        if (pdfRenderer.pageCount <= index) {
            return
        }
        // Close the current page before opening another one.
        currentPage?.close()

        // Use `openPage` to open a specific page in PDF.
        currentPage = pdfRenderer.openPage(index)

        if (currentPage != null) {
            // Important: the destination bitmap must be ARGB (not RGB).
            val bitmap = Bitmap.createBitmap(currentPage!!.getWidth(), currentPage!!.getHeight(), Bitmap.Config.ARGB_8888)

            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Display the page
            pageImage.setImage(bitmap)
            pageImage.setPage(pageNum)
        }
    }


}