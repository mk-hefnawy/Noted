package com.example.noted.feature_note.presentation.add_edit_note.attach_files

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R

class AttachedImagesAdapter: RecyclerView.Adapter<AttachedImagesAdapter.ViewHolder>() {
    var images: List<String> = arrayListOf()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.attachedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.attached_image, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = Uri.parse(images[position])
        val bitmap = MediaStore.Images.Media.getBitmap(holder.itemView.context.contentResolver, uri)
        holder.imageView.setImageBitmap(bitmap)

        holder.imageView.setOnClickListener {

        }
    }

    override fun getItemCount() = images.size

    fun setItems(newImages: List<String>){
        images = newImages
        notifyDataSetChanged()
        Log.d("Here", "Adapter Images Size: ${images.size}")
    }

}