import javafx.scene.image.Image
import javafx.scene.image.ImageView
class EnemyBullet(type: Int) {
    var eb: ImageView = ImageView()
    var ebtype = type
    init {
        if(ebtype == 1){
            seteb(resDir+"bullet1.png")
        }else if(ebtype == 2){
            seteb(resDir+"bullet2.png")
        }else{
            seteb(resDir+"bullet3.png")
        }

    }

    fun seteb(path: String){
        eb = ImageView(Image(path))
        eb.fitWidth = EB_W
        eb.fitHeight = EB_H
    }
}