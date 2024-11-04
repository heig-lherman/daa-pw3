package ch.heigvd.iict.daa.labo3

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Custom spinner adapter that allows to set a default value
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class SpinnerDefaultValueAdapter<T>(
    ctx: Context,
    resource: Int,
    defaultValue: T,
    entries: Array<T>
) : ArrayAdapter<T>(ctx, resource) {
    init {
        add(defaultValue)
        addAll(*entries)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Hides the first item (default value) in the dropdown list
        if (position == 0) {
            val v = View(context)
            v.visibility = View.GONE
            return v
        }

        return super.getDropDownView(position, null, parent)
    }

    override fun isEnabled(position: Int): Boolean {
        // Sets the first item (default value) as non-selectable
        return position != 0 && super.isEnabled(position)
    }
}