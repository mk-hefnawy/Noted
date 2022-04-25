package com.example.noted.feature_note.presentation.add_edit_note

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R

class ColorPaletteAdapter(val presentationController: AddEditPresentationController): RecyclerView.Adapter<ColorPaletteAdapter.PaletteViewHolder>() {
    private val colors = arrayListOf(
        R.color.lightRed,
        R.color.midRed,
        R.color.red,
        R.color.orange,
        R.color.deepOrange
    )

    inner class PaletteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val colorCardView = itemView.rootView as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        return PaletteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.color_item, parent, false))
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.colorCardView.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, colors.get(position)))

        holder.colorCardView.setOnClickListener{
            Log.d("Here", "Color Ball Clicked")
            presentationController.onColorBallClicked(colors.get(position))
        }
    }

    override fun getItemCount(): Int {
        return colors.size
    }
}