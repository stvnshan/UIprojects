import javafx.animation.PauseTransition
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import javafx.scene.input.KeyEvent
import javafx.scene.control.Label
import javafx.scene.text.Font
import java.util.Random
import javafx.scene.media.AudioClip
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer



const val WIDTH = 300.0
const val HEIGHT = 300.0

const val PLAYER_SPEED = 3.0
const val PLAYER_BULLET_SPEED = 6.0
const val ENEMY_SPEED = 0.5
const val ENEMY_VERTICAL_SPEED = 2.0
const val PLAYER_Y = 500.0
const val WINDOW_WIDTH = 1000.0
const val WINDOW_HEIGHT = 600.0
const val L2_SPEED = 2.0
const val L3_SPEED = 4.0

const val ENEMY_W = 30.0
const val ENEMY_H = 30.0
const val PLAYER_W = 30.0
const val PLAYER_H = 30.0
const val PB_W = 10.0
const val PB_H = 10.0
const val EB_W = 10.0
const val EB_H = 10.0
val resDir = "${System.getProperty("user.dir")}/src/main/resources/"
class Main : Application() {

    override fun start(stage: Stage) {
        val s = StartScene(stage)
        val start = Scene(s,WINDOW_WIDTH,WINDOW_HEIGHT)
        s.requestFocus()

        stage.title = ""
        stage.isResizable = false
        stage.scene = start
        stage.show()
    }
}



class StartScene(stage: Stage): Pane() {
    val logo = ImageView(Image("logo.png"))
    val info = Text("Steven Shan   20940336")
    val Instructions = Text("Instructions\nENTER - Start Game\nA or D - Move ship left or right\nSPACE - Fire!\nQ - Quit Game\n1 or 2 or 3 - Start Game at a specific level")
    init {
        children.addAll(logo,info,Instructions)
        logo.layoutX = WINDOW_WIDTH/2 - 300.0

        info.layoutX = WINDOW_WIDTH/2
        info.layoutY = 300.0
        Instructions.layoutX = WINDOW_WIDTH/2
        Instructions.layoutY = 400.0
        val gs1 = GameScene(1,0,3,stage)
        val gs2 = GameScene(2,0,3,stage)
        val gs3 = GameScene(3,0,3,stage)
        setOnKeyPressed { event->
            if (event.code == KeyCode.DIGIT1 || event.code == KeyCode.ENTER) {
                stage.scene = Scene(gs1,WINDOW_WIDTH,WINDOW_HEIGHT)
                gs1.requestFocus()
                addTimer(gs1,stage)
            }else if(event.code == KeyCode.DIGIT2){
                stage.scene = Scene(gs2,WINDOW_WIDTH,WINDOW_HEIGHT)
                gs2.requestFocus()
                addTimer(gs2,stage)
            }else if(event.code == KeyCode.DIGIT3) {
                stage.scene = Scene(gs3, WINDOW_WIDTH, WINDOW_HEIGHT)
                gs3.requestFocus()
                addTimer(gs3, stage)
            }else if(event.code == KeyCode.Q){
                stage.close()
            }
        }

    }

}

class EndScene(stage:Stage, w: Int, score:Int): Pane(){
    val a = Text("You Lost")
    val b = Text("You Win")
    val c = Text("Your Highest Score:  " + score)
    val d = Text("press q to quit")
    val e = Text("press r to restart")
    init {
        if(w == 0){
            children.add(a)
        }else{
            children.add(b)
        }
        children.addAll(c,d,e)
        a.layoutY = 0.0
        b.layoutY = 0.0
        c.layoutY = 50.0
        d.layoutY = 100.0
        e.layoutY = 150.0
        setOnKeyPressed { event->
            if (event.code == KeyCode.R) {
                val s = StartScene(stage)
                stage.scene = Scene(s,WINDOW_WIDTH,WINDOW_HEIGHT)
                s.requestFocus()
            }else if(event.code == KeyCode.Q){
                stage.close()
            }
        }

    }
}

class GameScene(level: Int,score:Int, lives:Int,stage: Stage): Pane() {

    var enemies: ArrayList<Enemy> = ArrayList()
    var enemyDirRight: Boolean = true
    var player: Player = Player()
    var health: Int = lives
    var pBullets: ArrayList<PlayerBullet> = ArrayList()
    var eBullets: ArrayList<EnemyBullet> = ArrayList()
    var score: Int = score
    val level: Int = level
    var init: Boolean = false
    var scoreLabel: Label = Label()
    var livesLabel: Label = Label()
    var levelLabel: Label = Label()
    var shipLabel:  Label = Label()
    init {
        level()
        for(e in enemies){
            children.add(e.enemy)
        }
        children.add(player.player)

        setOnKeyPressed { event->
            if (event.code == KeyCode.A) {
                if(player.player.layoutX > 0){
                    player.player.layoutX -= PLAYER_SPEED
                }

            }else if(event.code == KeyCode.D){
                if(player.player.layoutX < WINDOW_WIDTH){
                    player.player.layoutX += PLAYER_SPEED
                }
            }else if(event.code == KeyCode.SPACE){
                shoot()
            }else if(event.code == KeyCode.Q){
                stage.close()
            }
        }
        init = true
        scoreLabel.apply{
            text = "Score: " + score.toString()
            font = Font.font(30.0)
            layoutX = 20.0
        }
        livesLabel.apply {
            text = "Lives: "+health.toString()
            font = Font.font(30.0)
            layoutX = 500.0
        }
        levelLabel.apply {
            text = "Level: "+level.toString()
            font = Font.font(30.0)
            layoutX = 700.0
        }


        children.addAll(scoreLabel,livesLabel,levelLabel)
    }
    fun collision(pb: PlayerBullet): Boolean {
        var remove = false
        for(e in enemies){
            if( e.enemy.layoutX - pb.pb.layoutX <= PB_W && pb.pb.layoutX - e.enemy.layoutX <= ENEMY_W ){
                if(e.enemy.layoutY - pb.pb.layoutY <= PB_H && pb.pb.layoutY - e.enemy.layoutY <= ENEMY_H ){
                    remove = true
                    children.remove(e.enemy)
                    enemies.remove(e)
                    break
                }
            }
        }
        return remove
    }
    fun collisionE(eb: EnemyBullet): Boolean{
        if(eb.eb.layoutX - player.player.layoutX <= PLAYER_W && player.player.layoutX - eb.eb.layoutX <= EB_W){
            if(player.player.layoutY - eb.eb.layoutY <= EB_H && eb.eb.layoutY - player.player.layoutY <= PLAYER_H ){
                return true
            }
        }
        return false
    }
    fun shoot(){
        //val file = ("${System.getProperty("user.dir")}/src/main/resources/sounds/shoot.wav")
        //val file = ("E:/cs349/Timers/src/main/resources/sounds/shoot.wav")
        //val shootSound = MediaPlayer(Media(file))
        //shootSound.play()
        var bullet = PlayerBullet()
        bullet.pb.layoutX = player.player.layoutX + PLAYER_W/2 - PB_W/2
        bullet.pb.layoutY = player.player.layoutY
        pBullets.add(bullet)
        children.add(bullet.pb)
    }
    fun level(){
        player.player.layoutX = WINDOW_WIDTH/2
        player.player.layoutY = PLAYER_Y
        for(i in 0..9){
            var tmp: Enemy = Enemy(1)
            tmp.enemy.layoutY = 50.0
            tmp.enemy.layoutX = ENEMY_W*i
            enemies.add(tmp)
        }
        for(i in 0..9){
            var tmp: Enemy = Enemy(2)
            tmp.enemy.layoutY = 50.0 + ENEMY_H + 5.0
            tmp.enemy.layoutX = ENEMY_W*i
            enemies.add(tmp)
        }
        for(i in 0..9){
            var tmp: Enemy = Enemy(2)
            tmp.enemy.layoutY = 50.0 + 2*(ENEMY_H + 5.0)
            tmp.enemy.layoutX = ENEMY_W*i
            enemies.add(tmp)
        }
        for(i in 0..9){
            var tmp: Enemy = Enemy(3)
            tmp.enemy.layoutY = 50.0 + 3*(ENEMY_H + 5.0)
            tmp.enemy.layoutX = ENEMY_W*i
            enemies.add(tmp)
        }
        for(i in 0..9){
            var tmp: Enemy = Enemy(3)
            tmp.enemy.layoutY = 50.0 + 4*(ENEMY_H + 5.0)
            tmp.enemy.layoutX = ENEMY_W*i
            enemies.add(tmp)
        }
    }
}



fun addTimer(gs: GameScene,stage: Stage) {
    val timer = PauseTransition(Duration.millis(1000.0/60))
    timer.onFinished = EventHandler { _: ActionEvent ->
        var flag = false
        for(e in gs.enemies){
            if(gs.enemyDirRight){
                e.enemy.layoutX +=(ENEMY_SPEED + (50.0 - gs.enemies.size)/20 )* gs.level * 1.5
            }else{
                e.enemy.layoutX -=(ENEMY_SPEED + (50.0 - gs.enemies.size)/20)* gs.level * 1.5
            }
            if(e.enemy.layoutX<=0 || e.enemy.layoutX + ENEMY_W >=gs.width){
                flag = true
            }
            val ran = Random().nextInt(1000/gs.level)
            if(ran<1 && gs.eBullets.size <= 10){
                var tmp = EnemyBullet(e.type)
                tmp.eb.layoutX = e.enemy.layoutX
                tmp.eb.layoutY = e.enemy.layoutY
                gs.eBullets.add(tmp)
                gs.children.add(tmp.eb)
            }
            if(e.enemy.layoutX - gs.player.player.layoutX <= PLAYER_W && gs.player.player.layoutX - e.enemy.layoutX <= ENEMY_W){
                if(gs.player.player.layoutY - e.enemy.layoutY <= ENEMY_H && e.enemy.layoutY - gs.player.player.layoutY <= PLAYER_H ){
                    gs.health -=1
                }
            }
        }
        if(flag){
            if(gs.enemyDirRight){
                gs.enemyDirRight = false
            }else{
                gs.enemyDirRight = true
            }
            val des = 50.0
            var fflag = true
            for(e in gs.enemies){
                if(e.enemy.layoutY + des + ENEMY_H >= WINDOW_HEIGHT){
                    fflag = false
                }

            }
            if(fflag){
                for(e in gs.enemies){
                    e.enemy.layoutY += des
                }
            }
            if(gs.enemies.size > 0){
                val ran = Random().nextInt(gs.enemies.size )
                var tmp = EnemyBullet(gs.enemies[ran].type)
                tmp.eb.layoutX = gs.enemies[ran].enemy.layoutX
                tmp.eb.layoutY = gs.enemies[ran].enemy.layoutY
                gs.eBullets.add(tmp)
                gs.children.add(tmp.eb)
            }

        }
        var it = gs.pBullets.iterator()
        while(it.hasNext()){
            var pb = it.next()
            pb.pb.layoutY -= PLAYER_BULLET_SPEED * gs.level
            //check collision
            if(gs.collision(pb)){
                gs.children.remove(pb.pb)
                it.remove()
                gs.score += 1 * gs.level
                gs.scoreLabel.text = "Scores: "+gs.score.toString()
                continue
            }
            //if pb goes out of screen
            if(pb.pb.layoutY + PB_H < 0){
                gs.children.remove(pb.pb)
                it.remove()
                continue
            }
        }
        var itt = gs.eBullets.iterator()
        while(itt.hasNext()){
            var eb = itt.next()
            eb.eb.layoutY += ENEMY_VERTICAL_SPEED * gs.level
            //check collision
            if(gs.collisionE(eb)){
                gs.children.remove(eb.eb)
                gs.health -= 1
                gs.livesLabel.text = "Lives: "+gs.health.toString()
                gs.player.player.layoutX = Random().nextDouble(WINDOW_WIDTH-PLAYER_W)
                itt.remove()
                continue
            }
            //if pb goes out of screen
            if(eb.eb.layoutY - PB_H > WINDOW_HEIGHT){
                gs.children.remove(eb.eb)
                itt.remove()
                continue
            }
        }
        if(gs.init && gs.enemies.size == 0){
            if(gs.level == 1){
                val gs2 = GameScene(2,gs.score,gs.health,stage)
                stage.scene = Scene(gs2,WINDOW_WIDTH,WINDOW_HEIGHT)
                gs2.requestFocus()
                addTimer(gs2,stage)
            }else if(gs.level == 2){
                val gs3 = GameScene(3,gs.score,gs.health,stage)
                stage.scene = Scene(gs3,WINDOW_WIDTH,WINDOW_HEIGHT)
                gs3.requestFocus()
                addTimer(gs3,stage)
            }else if(gs.level == 3){
                val end = EndScene(stage,1,gs.score)
                stage.scene = Scene(end,WINDOW_WIDTH,WINDOW_HEIGHT)
                end.requestFocus()
            }
        }else if(gs.init && gs.health < 0){
                val end = EndScene(stage,0,gs.score)
                stage.scene = Scene(end,WINDOW_WIDTH,WINDOW_HEIGHT)
                end.requestFocus()
        }else{
            timer.playFromStart()
        }

    }
    timer.play()
}


