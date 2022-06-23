package com.example.noted.feature_note.presentation.add_edit_note.attach_files

import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.feature_note.presentation.model.AttachedImage
import java.io.File
import java.io.FileInputStream

class AttachedImagesAdapter(val attachedImageInterface: AttachedImageInterface): RecyclerView.Adapter<AttachedImagesAdapter.ViewHolder>() {
    var images: List<AttachedImage> = arrayListOf()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.attachedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attached_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile = File(images[position].path)
        val bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile))
        holder.imageView.setImageBitmap(bitmap)

        holder.imageView.setOnClickListener {
            attachedImageInterface.onAttachedImageClicked(images[position])
        }
    }

    override fun getItemCount() = images.size

    fun setItems(newImages: List<AttachedImage>){
        images = newImages
        notifyDataSetChanged()
    }

}