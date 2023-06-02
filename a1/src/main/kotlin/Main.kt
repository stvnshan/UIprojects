import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import java.io.File
import javafx.scene.input.MouseEvent
import java.security.Key
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.control.ScrollBar
import javafx.geometry.Orientation
import javafx.stage.DirectoryChooser


class Main : Application() {
    var dir = File("${System.getProperty("user.dir")}/test/")
    val leftPane = ListView<String>()
    val centrePane = Pane()
    val scrollCentrePane = ScrollPane(centrePane)
    val statusLine = Text()

    override fun start(primaryStage: Stage?) {

        scrollCentrePane.prefWidth = 100.0
        statusLine.text = dir.path

        // create panels
        leftPane.apply {
            dir.listFiles().forEach {
                if(it.isDirectory){
                    items.add(it.name+"/")
                }else{
                    items.add(it.name)
                }
            }
            selectionModel.selectionMode = SelectionMode.SINGLE
            selectionModel.select(0);
        }
        leftPane.items.sort()
        leftPane.setOnMouseClicked { event: MouseEvent->
            if(event.clickCount == 2){
                val selectedItemName = leftPane.selectionModel.selectedItem
                if(selectedItemName != null){
                    if(selectedItemName.toString().last() == '/') {
                        updateDirctory(selectedItemName.toString())
                    }else{
                        updateContent(selectedItemName.toString())
                    }
                }
            }
        }
        leftPane.setOnKeyPressed { event: KeyEvent->
            if(event.code == KeyCode.ENTER){
                val selectedItemName = leftPane.selectionModel.selectedItem
                if(selectedItemName != null){
                    if(selectedItemName.toString().last() == '/') {
                        updateDirctory(selectedItemName.toString())
                    }else{
                        updateContent(selectedItemName.toString())
                    }
                }
            }else if(event.code == KeyCode.DELETE || event.code == KeyCode.BACK_SPACE){
                if(dir != File("${System.getProperty("user.dir")}/test/")){
                    loadDirectory(dir.path.substring(0,dir.path.lastIndexOf("\\")))
                }
            }

        }
        leftPane.requestFocus()
        val topPane = VBox().apply {
            prefHeight = 30.0
            background = Background(BackgroundFill(Color.valueOf("#00ffff"), null, null))
            setOnMouseClicked { println("top pane clicked") }

            children.addAll(
                MenuBar().apply {
                    menus.add(Menu("File").apply {
                        val homeItem = MenuItem("Home")
                        val preItem = MenuItem("Prev")
                        val nextItem = MenuItem("Next")
                        items.addAll(homeItem,preItem,nextItem)
                        homeItem.setOnAction {
                            loadDirectory("${System.getProperty("user.dir")}/test/")
                        }
                        preItem.setOnAction {
                            if(dir != File("${System.getProperty("user.dir")}/test/")){
                                loadDirectory(dir.path.substring(0,dir.path.lastIndexOf("\\")))
                            }
                        }
                        nextItem.setOnAction {
                            val selectedItemName = leftPane.selectionModel.selectedItem
                            if(selectedItemName != null){
                                if(selectedItemName.toString().last() == '/') {
                                    updateDirctory(selectedItemName.toString())
                                }else{
                                    updateContent(selectedItemName.toString())
                                }
                            }
                        }
                    })
                    menus.add(Menu("View"))
                    menus.add(Menu("Actions").apply {
                        val renameItem = MenuItem("Rename")
                        val deleteItem = MenuItem("Delete")
                        val moveItem = MenuItem("Move")
                        items.addAll(renameItem,deleteItem,moveItem)
                        renameItem.setOnAction {
                            renameProcess();
                        }
                        deleteItem.setOnAction {
                            deleteProcess()
                        }
                        moveItem.setOnAction {
                            moveProcess(primaryStage)
                        }
                    })
                    menus.add(Menu("Options"))
                },
                ToolBar().apply {
                    val picDir = "${System.getProperty("user.dir")}/src/pics/"

                    val homePic = ImageView(picDir+"home.png")
                    val deletePic = ImageView(picDir+"delete.png")
                    val leftPic = ImageView(picDir+"left.png")
                    val renamePic = ImageView(picDir+"rename.png")
                    val rightPic = ImageView(picDir+"right.png")
                    renamePic.fitWidth = 20.0
                    renamePic.fitHeight = 20.0
                    renamePic.isPreserveRatio = true
                    homePic.fitWidth = 20.0
                    homePic.fitHeight = 20.0
                    homePic.isPreserveRatio = true
                    deletePic.fitWidth = 20.0
                    deletePic.fitHeight = 20.0
                    deletePic.isPreserveRatio = true
                    leftPic.fitWidth = 20.0
                    leftPic.fitHeight = 20.0
                    leftPic.isPreserveRatio = true
                    rightPic.fitWidth = 20.0
                    rightPic.fitHeight = 20.0
                    rightPic.isPreserveRatio = true
                    items.add(Button("Home").apply {
                        graphic = homePic
                        onAction = EventHandler{
                            loadDirectory("${System.getProperty("user.dir")}/test/")
                        }
                        isFocusTraversable = false
                    })
                    items.add(Button("Prev",leftPic).apply{
                        onAction = EventHandler {
                            if(dir != File("${System.getProperty("user.dir")}/test/")){
                                loadDirectory(dir.path.substring(0,dir.path.lastIndexOf("\\")))
                            }
                        }
                        isFocusTraversable = false
                    })
                    items.add(Button("Next",rightPic).apply{
                        onAction = EventHandler {
                            val selectedItemName = leftPane.selectionModel.selectedItem
                            if(selectedItemName != null){
                                if(selectedItemName.toString().last() == '/') {
                                    updateDirctory(selectedItemName.toString())
                                }else{
                                    updateContent(selectedItemName.toString())
                                }
                            }
                        }
                        isFocusTraversable = false
                    })
                    items.add(Button("Delete",deletePic).apply {
                        onAction = EventHandler {
                            deleteProcess()
                        }
                        isFocusTraversable = false

                    })
                    items.add(Button("Rename",renamePic).apply {
                        onAction = EventHandler {
                            renameProcess()
                        }
                        isFocusTraversable = false
                    })
                    items.add(Button("Move").apply {
                        onAction = EventHandler{
                            moveProcess(primaryStage)
                        }
                        isFocusTraversable = false
                    })
                })
        }



        // put the panels side-by-side in a container
        val root = BorderPane().apply {
            left = leftPane
            center = scrollCentrePane
            top = topPane
            bottom = statusLine
        }
        root.left.requestFocus()

        // create the scene and show the stage
        with (primaryStage!!) {
            scene = Scene(root, 600.0, 400.0)
            title = "A1"
            show()
        }

    }

    fun deleteProcess(){
        val confirm = Alert(Alert.AlertType.CONFIRMATION)
        confirm.title = "Confirmation"
        confirm.headerText = "Do you want to delete this file or directory?"
        val result =  confirm.showAndWait()
        if(result.isPresent && result.get() == ButtonType.OK){
            val selectedItemName = leftPane.selectionModel.selectedItem
            val file = File(dir.path + "/" + selectedItemName)
            if(file.isDirectory){
                file.deleteRecursively()
            }else{
                file.delete()
            }
            loadDirectory(dir.path)
        }
    }

    fun renameProcess(){
        val renameInput = TextInputDialog(leftPane.selectionModel.selectedItem.toString())
        renameInput.title = "Rename"
        //renameInput.headerText = "Rename this file:"
        renameInput.contentText = "New file name:"
        val result = renameInput.showAndWait()
        if(result.isPresent ){
            if(isValid(result.get())){
                val oldNameFile = File(dir.path+"/"+leftPane.selectionModel.selectedItem.toString())
                val ifRenamed = oldNameFile.renameTo(File(dir.path+"/"+result.get()))
                if(ifRenamed){
                    loadDirectory(dir.path)
                }else{
                    val alert = Alert(Alert.AlertType.ERROR)
                    alert.title = "ERROR"
                    alert.contentText = "Invalid file name!"
                    alert.showAndWait()
                }
            }else{
                val alert = Alert(Alert.AlertType.ERROR)
                alert.title = "ERROR"
                alert.contentText = "Invalid file name!"
                alert.showAndWait()
            }
        }
    }

    fun isValid(name: String): Boolean{
        val invalidCharsRegex = "[<>:\"/\\|?*]".toRegex()
        return (!invalidCharsRegex.containsMatchIn(name)) && (!name.isBlank())
    }

    fun updateDirctory(selectedItemName: String){
        leftPane.items.clear()
        centrePane.children.clear()
        dir  = File(dir.path + "/" +selectedItemName)
        dir.listFiles().forEach {
            if(it.isDirectory){
                leftPane.items.add(it.name+"/")
            }else{
                leftPane.items.add(it.name)
            }
        }
        statusLine.text = dir.path
    }

    fun loadDirectory(loadDir: String){
        leftPane.items.clear()
        centrePane.children.clear()
        dir  = File(loadDir)
        dir.listFiles().forEach {
            if(it.isDirectory){
                leftPane.items.add(it.name+"/")
            }else{
                leftPane.items.add(it.name)
            }
        }
        leftPane.items.sort()
        statusLine.text = dir.path
    }

    fun updateContent(selectedItemName: String){
        centrePane.children.clear()

        val file = File(dir.path + "/" + selectedItemName)
        if(file.canRead()){
            val exten = selectedItemName.substring(selectedItemName.lastIndexOf('.')+1)
            if(exten == "png" || exten == "jpg" || exten == "bmp"){
                val imageV = ImageView()
                imageV.image = Image(file.path)
                imageV.isPreserveRatio = true
                imageV.fitWidthProperty().bind(scrollCentrePane.widthProperty())
                imageV.fitHeightProperty().bind(scrollCentrePane.heightProperty())
                centrePane.children.add(imageV)
            }else if(exten == "txt" || exten == "md"){
                val textV = Text(file.readText())
                textV.layoutX = 30.0
                textV.layoutY = 30.0
                centrePane.children.add(textV)
            }else{
                val textV = Text("Unsupported type")
                textV.layoutX = 30.0
                textV.layoutY = 30.0
                centrePane.children.add(textV)
            }
        }else{
            val textV = Text("File cannot be read")
            textV.layoutX = 30.0
            textV.layoutY = 30.0
            centrePane.children.add(textV)

        }
        leftPane.items.sort()
        statusLine.text = dir.path + "/" + selectedItemName

    }
    fun moveProcess(ps: Stage?){
        val selectedItemName = leftPane.selectionModel.selectedItem.toString()
        val dest =  DirectoryChooser().showDialog(ps)
        if(dest!=null){
            File(dir.path+"/"+selectedItemName).renameTo(File(dest.path+"/"+selectedItemName.toString()))
        }
        loadDirectory(dir.path)
    }

}