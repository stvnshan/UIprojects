

import javafx.scene.image.ImageView
import IView
import javafx.event.EventHandler
import kotlin.random.Random
import javafx.scene.effect.DropShadow
import javafx.scene.effect.Effect
import javafx.scene.paint.Color
import javafx.scene.layout.Pane
import javafx.stage.Stage

class Model(private val ps: Stage) {


    //image 100*100
    //height unknown
    //window size 1000 * 1000
    private val views: ArrayList<IView> = ArrayList()
    val myImages: ArrayList<ImageView> = ArrayList()
    var selectedImage: ImageView = ImageView()
    private var tile = false
    private val rotate = 12.5
    private val scale = 1.5

    // method that the views can use to register themselves with the mvc.Model
    // once added, they are told to update and get state from the mvc.Model
    fun addView(view: IView) {
        views.add(view)
        view.updateView()
    }

    // the model uses this method to notify all of the Views that the data has changed
    // the expectation is that the Views will refresh themselves to display new data when appropriate
    private fun notifyObservers() {
        for (view in views) {
            view.updateView()
        }
    }


    fun setTile(){
        tile = true
        for(item in myImages){
            item.onMouseDragged = EventHandler{}
        }
        reposition()
        notifyObservers()
    }

    fun setCascade(){
        tile = false
        for(item in myImages){
            var offsetX = 0.0
            var offsetY = 0.0
            item.onMousePressed = EventHandler {
                offsetX = item.translateX - it.sceneX
                offsetY = item.translateY - it.sceneY
                selectedImage.effect = DropShadow()
                it.consume()
            }
            item.onMouseDragged = EventHandler{
                item.translateX = it.sceneX + offsetX
                item.translateY = it.sceneY + offsetY
                selectedImage.effect = DropShadow()

                it.consume()
            }
        }
        notifyObservers()
    }


    fun addImages(i: ImageView){
        var x = Random.Default.nextDouble(0.0,ps.width-250)
        var y = Random.Default.nextDouble(0.0,ps.height-250)
        i.layoutX = x
        i.layoutY = y
        i.onMouseClicked = EventHandler{
            if(selectedImage != ImageView()){
                selectedImage.effect = null
            }
            selectedImage = i
            selectedImage.effect = DropShadow()
            selectedImage.toFront()
            it.consume()
        }
        myImages.add(i)
        if(tile){
            setTile()
        }else{
            setCascade()
        }
        notifyObservers()
    }
    fun deleteImages(){
        for(j in myImages ){
            if(j == selectedImage){
                myImages.remove(j)
                break
            }
        }
        notifyObservers()
    }

    fun reposition(){
        var x = 0.0
        var y = 0.0
        for(i in myImages){
            i.layoutX = x
            i.layoutY = y
            i.translateX = 0.0
            i.translateY = 0.0
            if(x+400 < ps.width){
                x = x+200 + 10
            }else{
                x= 0.0
                y = y + 200 + 10
            }
        }
        notifyObservers()
    }

    fun rotateLeft(){
        if(tile) return
        selectedImage.rotate += -rotate
        notifyObservers()
    }
    fun rotateRight(){
        if(tile) return
        selectedImage.rotate += rotate
        notifyObservers()
    }
    fun zoomIn(){
        if(tile) return
        selectedImage.fitHeight *= scale
        selectedImage.fitHeight *= scale
        selectedImage.scaleX = selectedImage.scaleX * scale
        selectedImage.scaleY = selectedImage.scaleY *scale

    }
    fun zoomOut(){
        if(tile) return
        selectedImage.fitHeight /= scale
        selectedImage.fitHeight /= scale
        selectedImage.scaleX = selectedImage.scaleX * 1/scale
        selectedImage.scaleY = selectedImage.scaleY * 1/scale

    }

    fun reset(){
        if(tile) return
//        selectedImage.fitHeight = 200.0
//        selectedImage.fitHeight = 200.0
        selectedImage.rotate = 0.0
        selectedImage.scaleX = 1.0
        selectedImage.scaleY = 1.0
    }
}