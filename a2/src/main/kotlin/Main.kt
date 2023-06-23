
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.stage.Stage
import kotlin.math.max

// MVC with coupled View and Controller (a more typical method than MVC1)
// A simple MVC example inspired by Joseph Mack, http://www.austintek.com/mvc/
// This version uses MVC: two views coordinated with the observer pattern, but no separate controller.
class Main : Application() {

    override fun start(stage: Stage) {

        stage.title = "a2 lightbox    s22shan"
        stage.minWidth = 750.0
        stage.minHeight = 500.0


        val model = Model(stage)


        val canvas = CanvasView(model,stage)
        val tool = ToolBarView(model,stage)
        val status = StatusBarView(model)
        val root = BorderPane().apply {
            top = tool
            center = ScrollPane(canvas)
            bottom = status
        }



        val scene = Scene(root, 700.0, 500.0)
        scene.widthProperty().addListener { _, _, _ ->
            canvas.updateView()
            canvas.prefWidth = max(scene.width-20,canvas.prefWidth)

        }
        scene.heightProperty().addListener { _, _, _ ->
            canvas.updateView()
            canvas.prefHeight = max(scene.height-60,canvas.prefHeight)
        }


        stage.scene = scene
        stage.show()
    }
}