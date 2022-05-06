package jkey20.errs.activity.reservationholder.menu

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuItemDecoration(context : Context) : RecyclerView.ItemDecoration() {

    private val context = context

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if(position ==  0 ||  position == 1){
            outRect.top = dpToPx(context, 8)
        }

        val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = layoutParams.spanIndex

        if(spanIndex == 0){
            outRect.left = dpToPx(context, 0)
        }
        else if(spanIndex == 1){
            outRect.left = dpToPx(context, 0)
            outRect.right = dpToPx(context, 8)
        }
    }

    private fun dpToPx(context: Context, dp : Int) : Int{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(), context.resources.displayMetrics
        ).toInt()
    }
}