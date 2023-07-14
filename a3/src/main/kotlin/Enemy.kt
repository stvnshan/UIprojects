import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.layout.*

class Enemy(val type: Int) {
    var enemy: ImageView = ImageView()
    var etype  = type
    init {
        when(type){
            1->setEnemy(resDir+"enemy1.png")
            2->setEnemy(resDir+"enemy2.png")
            3->setEnemy(resDir+"enemy3.png")
        }
    }

    fun setEnemy(path: String){
        enemy = ImageView(Image(path))
        enemy.fitHeight = ENEMY_H
        enemy.fitWidth = ENEMY_W
    }
}