package com.example.noted.feature_note.presentation.add_edit_note.attach_files

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R

class AttachOptionsAdapter(val mediator: AttachOptionsInterface) :
    RecyclerView.Adapter<AttachOptionsAdapter.ViewHolder>() {
    lateinit var options: List<AttachOption>

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val attachOptionImage = itemView.findViewById<ImageView>(R.id.attachOptionImage)
        val attachOptionText = itemView.findViewById<TextView>(R.id.attachOptionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.attach_option, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.attachOptionImage.setImageResource(options[position].imageResourceId)
        holder.attachOptionText.setText(options[position].title)

        holder.itemView.setOnClickListener {
            mediator.onAttachOptionClicked(options[position].type)
        }
    }

    override fun getItemCount() = options.size

    fun setOptions() {
        options = arrayListOf(
            AttachOption("Gallery", R.drawable.outline_image_black_36, AttachOptionType.GALLERY),
            AttachOption("Camera", R.drawable.outline_photo_camera_black_36, AttachOptionType.CAMERA)
        )
    }
}