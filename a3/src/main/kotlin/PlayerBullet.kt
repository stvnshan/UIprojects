import javafx.scene.image.Image
import javafx.scene.image.ImageView
class PlayerBullet {
    var pb: ImageView = ImageView()
    init {
        setpb(resDir+"player_bullet.png")
    }

    fun setpb(path: String){
        pb = ImageView(Image(path))
        pb.fitWidth = PB_W
        pb.fitHeight = PB_H
    }
}