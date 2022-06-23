package com.example.noted.feature_note.presentation.add_edit_note.attach_files

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.noted.R
import com.example.noted.databinding.FragmentAttachedImageBinding
import com.example.noted.feature_note.presentation.add_edit_note.AddEditEvent
import com.example.noted.feature_note.presentation.add_edit_note.AddEditFragment
import com.example.noted.feature_note.presentation.add_edit_note.AddEditNoteViewModel
import com.example.noted.feature_note.presentation.model.AttachedImage
import com.google.gson.Gson
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@AndroidEntryPoint
class AttachedImageFragment : Fragment(), View.OnClickListener{
    lateinit var attachedImageBinding: FragmentAttachedImageBinding
    lateinit var viewModel: AddEditNoteViewModel
    var attachedImage: AttachedImage? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity()).get(AddEditNoteViewModel::class.java)
        attachedImageBinding = FragmentAttachedImageBinding.inflate(layoutInflater)
        return attachedImageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleArguments()
        registerClickListeners()
    }

    private fun handleArguments(){
        arguments?.let { bundle ->
            val imageString = bundle.getString("image")
            val image = Gson().fromJson(imageString, AttachedImage::class.java)
            attachedImage = image
            attachedImage?.let { theImage ->
                showImage(theImage)
            }

        }
    }

    private fun registerClickListeners(){
        attachedImageBinding.attachedImageBack.setOnClickListener(this)
        attachedImageBinding.attachedImageOptions.setOnClickListener(this)
    }

    private fun showImage(image: AttachedImage){
        /*val message = if (image.isSaved) "Saved Image" else "Not Saved"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()*/
        val bitmap = BitmapFactory.decodeStream(FileInputStream(File(image.path)))
        attachedImageBinding.attachedImageImageView.setImageBitmap(bitmap)
    }

    private fun onBackPressed(){
        activity?.onBackPressed()
    }

    private fun onAttachedImageOptionsClicked(view: View){
        val menu = PopupMenu(requireContext(), view)
        val menuInflater = menu.menuInflater
        menuInflater.inflate(R.menu.attached_image_options_menu, menu.menu)
        registerMenuItemClickListener(menu)
        menu.show()
    }

    private fun registerMenuItemClickListener(menu: PopupMenu){
        menu.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.deleteAttachedImage -> {
                    onDeleteAttachedImageClicked()
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun onDeleteAttachedImageClicked(){
        viewModel.onEvent(AddEditEvent.DeleteAttachedImage(attachedImage!!))
        activity?.onBackPressed()
    }

    override fun onClick(view: View?) {
        when(view){
            attachedImageBinding.attachedImageBack -> onBackPressed()
            attachedImageBinding.attachedImageOptions -> onAttachedImageOptionsClicked(view)
        }
    }
}