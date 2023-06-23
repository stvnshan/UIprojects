

import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.stage.Stage
import java.io.File
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser

internal class ToolBarView(private val model: Model, ps: Stage): ToolBar(), IView {
    override fun updateView() {
    }

    init {
        val picDir = "${System.getProperty("user.dir")}/src/main/resources/"

        val addPic = ImageView(picDir+"add.png").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val removePic = ImageView(picDir+"remove.png").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val resetPic = ImageView(picDir+"reset.jpg").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val rotateLeftPic = ImageView(picDir+"rotate_left.jpg").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val rotateRightPic = ImageView(picDir+"rotate_right.png").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val zoomInPic = ImageView(picDir+"zoom_in.png").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }
        val zoomOutPic = ImageView(picDir+"zoom_out.png").apply {
            fitWidth = 10.0
            fitHeight = 10.0
            isPreserveRatio = true
        }

        var addImage = Button("Add Image",addPic)
        var DelImage = Button("Del Image",removePic)
        var RotateLeft = Button("RotateLeft",rotateLeftPic)
        var RotateRight = Button("RotateRight",rotateRightPic)
        var ZoomIn = Button("Zoom In",zoomInPic)
        var ZoomOut = Button("Zoom Out",zoomOutPic)
        var Reset = Button("Reset",resetPic)
        addImage.apply {
            onAction = EventHandler {
                val image_path =FileChooser().showOpenDialog(ps)
                if(image_path != null){
                    val file = File(image_path.toURI().toString())
                    //val ext = file.extension.toString()
                    val iv = ImageView()
                    val exten = image_path.extension
                    if(exten == "jpeg" || exten == "jpg" ||exten == "png" || exten == "jfif" || exten == "heif" || exten == "svg"){

                        try{
                            iv.image = Image(image_path.path)
                            iv.fitWidth = 200.0
                            iv.fitHeight = 200.0
                            iv.isPreserveRatio = true
                            iv.isSmooth = true
                            model.addImages(iv)
                        }catch (e: IllegalArgumentException){
                        }
                    }




                }
            }
        }
        DelImage.apply {
            onAction = EventHandler{
                model.deleteImages()
            }
        }
        RotateLeft.apply{
            onAction = EventHandler{
                model.rotateLeft()
            }
        }
        RotateRight.apply{
            onAction = EventHandler{
                model.rotateRight()
            }
        }
        ZoomIn.apply{
            onAction = EventHandler{
                model.zoomIn()
            }
        }
        ZoomOut.apply{
            onAction = EventHandler{
                model.zoomOut()
            }
        }
        Reset.apply{
            onAction = EventHandler{
                model.reset()
            }
        }
        val cascade = RadioButton("Cascade")
        val tile = RadioButton("Tile")
        val tg = ToggleGroup().apply {
            cascade.toggleGroup = this
            tile.toggleGroup = this
            selectedToggleProperty().addListener { _, oldValue, newValue ->
                if(newValue == tile){
                    model.setTile()
                }else{
                    model.setCascade()
                }
            }
            selectToggle(cascade)
            model.setCascade()

        }

        DelImage.disableProperty().bind(model.selectedImage.imageProperty().isEqualTo(ImageView()))

        items.addAll(addImage,DelImage,RotateLeft,RotateRight,ZoomIn,ZoomOut,Reset,cascade, tile)


    }

}