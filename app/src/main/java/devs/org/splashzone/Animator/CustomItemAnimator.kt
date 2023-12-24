package devs.org.splashzone.Animator

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class CustomItemAnimator : DefaultItemAnimator() {
    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        // Add your custom animation logic here
        // Return true if you handled the animation yourself, otherwise, return false
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY)
    }
}
