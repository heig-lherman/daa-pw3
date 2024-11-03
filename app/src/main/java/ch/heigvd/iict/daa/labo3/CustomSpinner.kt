package ch.heigvd.iict.daa.labo3

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Custom spinner adapter,
 * it disables the first item (Sélectionner)
 * and hides it in the dropdown list
 */
class CustomSpinnerAdapter(
    context: Context,
    layoutResource: Int,
    private val items: List<String>
) : ArrayAdapter<String>(context, layoutResource, items) {

    override fun isEnabled(position: Int): Boolean {
        // Disable the first item (Sélectionner)
        return position != 0
    }

    /**
     * Hide the first item (Sélectionner) in the dropdown list,
     * @param position the position of the item
     * @param convertView the view of the item
     * @param parent the parent view
     * @return the view of the item
     *
     */

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        // Hide the first item (Sélectionner)
        if (position == 0) {
            view.visibility = View.GONE
            view.layoutParams = ViewGroup.LayoutParams(0, 0)
        } else {
            view.visibility = View.VISIBLE
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return view
    }
}