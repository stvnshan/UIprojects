
import javafx.scene.text.Text
import javafx.scene.layout.VBox
import IView
import Model

internal class StatusBarView(private val model: Model) : Text(), IView {

    // When notified by the model that things have changed,
    // update to display the new value
    override fun updateView() {
        text = model.myImages.size.toString() + " Images Loaded "
    }

    init {
        text = model.myImages.size.toString() + " Images Loaded "
        model.addView(this)
    }
}