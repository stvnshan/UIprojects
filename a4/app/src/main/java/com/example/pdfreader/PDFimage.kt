package com.example.pdfreader

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import kotlin.math.sqrt

@SuppressLint("AppCompatCustomView")
class PDFimage  // constructor
    (context: Context?, totalP: Int) : ImageView(context) {
    val LOGNAME = "pdf_image"

    // drawing path
    var path: Path? = null

    var paths: MutableList<MutableList<Path?>> = MutableList(totalP){ mutableListOf<Path?>()}
    var ifBrushs: MutableList<MutableList<Boolean>> = MutableList(totalP){ mutableListOf<Boolean>()}

    //ifAdd, path
    var undoL:  MutableList<Triple<Boolean,Int,Path?>> = mutableListOf()
    var redoL: MutableList<Triple<Boolean,Int,Path?>> = mutableListOf()
    var curpage = 0
    var ifb = false
    var ife = false

    // image to display
    var bitmap: Bitmap? = null
    var paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }
    var brush = Paint().apply {
        color = Color.alpha(300)
        color = Color.YELLOW
        strokeWidth = 10f
        isAntiAlias = true
        style = Paint.Style.STROKE
    }

    var scaleFactor = 1.0f
    var initialScaleFactor = 1.0f
    var initialDistance = 0.0


    // capture touch events (down/move/up) to create a path
    // and use that to create a stroke that we can draw
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(LOGNAME, "Action down")
                path = Path()
                path!!.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // Multi-touch for zooming
                val x = event.getX(0) - event.getX(1)
                val y = event.getY(0) - event.getY(1)
                val distance = sqrt(x * x + y * y)
                initialScaleFactor = scaleFactor
                initialDistance = distance.toDouble()
            }

            MotionEvent.ACTION_MOVE -> {
                if(event.pointerCount == 1){
                    Log.d(LOGNAME, "Action move")
                    path!!.lineTo(event.x, event.y)
                }else if(event.pointerCount == 2){
                    val deltaX = event.getX(0) - event.getX(1)
                    val deltaY = event.getY(0) - event.getY(1)
                    val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
                    scaleFactor = (initialScaleFactor  * (distance / initialDistance)).toFloat()

                }

            }

            MotionEvent.ACTION_UP -> {
                Log.d(LOGNAME, "Action up")
                if(ife){
                    val iterator = paths[curpage].iterator()
                    while(iterator.hasNext()){
                        val another_path = iterator.next()
                        val result = Path()
                        result.op(path!!,another_path!!,Path.Op.INTERSECT)
                        if (!result.isEmpty){
                            undoL.add(Triple(false,curpage,another_path))
                            iterator.remove()
                        }
                    }


                }else{
                    paths[curpage].add(path)
                    ifBrushs[curpage].add(ifb)
                    undoL.add(Triple(true,curpage,path))
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // Ensure that the scaleFactor does not go below 1
                if (scaleFactor < 1.0f) {
                    scaleFactor = 1.0f
                    invalidate()
                }
            }
        }
        return true
    }

    // set image as background
    fun setImage(bitmap: Bitmap?) {
        this.bitmap = bitmap
    }

    // set brush characteristics
    // e.g. color, thickness, alpha
    fun setBrush() {
        ife = false
        ifb = true
    }
    fun setDraw(){
        ife = false
        ifb = false
    }
    fun setErase(){
        ife = true
    }

    fun setPage(p : Int){
        curpage = p
    }

    fun undo(){
        if(!undoL.isEmpty()){
            var tmp = undoL.last()
            redoL.add(tmp)
            if(tmp.first){
                val iterator = paths[tmp.second].iterator()
                while(iterator.hasNext()){
                    val another_path = iterator.next()
                    if (another_path == tmp.third){
                        iterator.remove()
                        undoL.removeLast()
                        break
                    }
                }
            }else{
                paths[tmp.second].add(tmp.third)
                undoL.removeLast()
            }

        }
    }

    fun redo(){
        if(!redoL.isEmpty()){
            var tmp = redoL.last()
            undoL.add(tmp)
            if(!tmp.first){
                val iterator = paths[tmp.second].iterator()
                while(iterator.hasNext()){
                    val another_path = iterator.next()
                    if (another_path == tmp.third){
                        iterator.remove()
                        redoL.removeLast()
                        break
                    }
                }
            }else{
                paths[tmp.second].add(tmp.third)
                redoL.removeLast()
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        // draw background
        if (bitmap != null) {

            setImageBitmap(bitmap)
        }



        // draw lines over it
        var i = 0
        for(path in paths[curpage]){
            if(ifBrushs[curpage][i]){
                path?.let { canvas.drawPath(it, brush) }
            }else{
                path?.let { canvas.drawPath(it, paint) }
            }
            i++
        }


        super.onDraw(canvas)


    }
}