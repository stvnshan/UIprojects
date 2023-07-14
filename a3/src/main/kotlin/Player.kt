import javafx.scene.image.Image
import javafx.scene.image.ImageView

class Player {
    var player: ImageView = ImageView()
    init {
        setPlayer(resDir+"player.png")
    }

    fun setPlayer(path: String){
        player = ImageView(Image(path))
        player.fitWidth = PLAYER_W
        player.fitHeight = PLAYER_H
    }
}