package ch.heigvd.iict.daa.labo3

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

/**
 * Custom spinner adapter that allows to set a default value
 *
 * @param T the type of the items in the adapter
 * @param ctx the context
 * @param defaultValue the default value
 * @param entries the list of items
 *
 * @author Emilie Bressoud
 * @author Loïc Herman
 * @author Sacha Butty
 */
class SpinnerDefaultValueAdapter<T>(
    ctx: Context,
    defaultValue: T,
    entries: Array<T>
) : ArrayAdapter<T>(ctx, android.R.layout.simple_spinner_dropdown_item) {
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