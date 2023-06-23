

import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.stage.Stage


internal class CanvasView( private val model: Model,ps:Stage) : Pane(), IView {


    override fun updateView() {

        children.clear()
        var x = 0.0
        var y = 0.0
        for(item in model.myImages){

            children.add(item)
            if(item.layoutX+item.translateX+200*item.scaleX > x) x = item.layoutX+item.translateX+200*item.scaleX
            if(item.layoutY+item.translateY+200*item.scaleY > y) y = item.layoutY+item.translateY+200*item.scaleY

        }
        prefWidth = x
        prefHeight = y
    }

    init {

        onMouseClicked = EventHandler{

            model.selectedImage.effect = null
            model.selectedImage = ImageView()
            it.consume()
        }
        requestFocus()
        model.addView(this)
    }
}