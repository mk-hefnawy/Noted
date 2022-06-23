package com.example.noted.feature_note.presentation.add_edit_note

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Point
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noted.R
import com.example.noted.core.Extensions.animateInvisibility
import com.example.noted.databinding.FragmentAddEditBinding
import com.example.noted.feature_note.domain.model.NoteCategory
import com.example.noted.core.Result
import com.example.noted.feature_note.presentation.add_edit_note.attach_files.*
import com.example.noted.feature_note.presentation.model.AttachedImage
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*


@AndroidEntryPoint
class AddEditFragment : Fragment(), View.OnClickListener, AddEditPresentationController,
    AttachOptionsInterface, AttachedImageInterface {
    private lateinit var addEditFragmentBinding: FragmentAddEditBinding
    lateinit var addEditViewModel: AddEditNoteViewModel
    private lateinit var adapter: ColorPaletteAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<NoteCategory>
    private lateinit var attachedImagesAdapter: AttachedImagesAdapter


    companion object {
        const val GALLERY_CODE = 4
        const val CAMERA_CODE = 9
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        addEditViewModel =
            ViewModelProvider(requireActivity()).get(AddEditNoteViewModel::class.java)
        addEditFragmentBinding = FragmentAddEditBinding.inflate(layoutInflater)
        return addEditFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        registerOnClickListeners()
        observeAddNote()
        observeEditNote()
        observeSaveAttachedImage()
        observeDeleteAttachedImage()
        observeState()
        restoreState()

        if (savedInstanceState == null) {
            handleArguments()
        }

    }

    private fun init() {
        initColorsPaletteAdapter()
        initAttachOptionsAdapter()
        initAttachedImagesAdapter()
        initSpinner()
    }

    private fun observeState() {
        addEditViewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {
                populateViewWithState(state)
            }
        }
    }

    private fun populateViewWithState(state: AddEditState) {
        Log.d("Here", "Populate: Attached Images: ${state.attachedImages.size}")
        addEditFragmentBinding.noteTitleET.setText(state.noteTitle)
        addEditFragmentBinding.noteBodyET.setText(state.noteBody)
        addEditFragmentBinding.categorySpinner.setSelection(spinnerAdapter.getPosition(state.noteCategory))
        addEditFragmentBinding.noteBodyETContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), state.noteColor))

        initAttachedImagesAdapter(state.attachedImages)

        if (state.isColorPaletteVisible) addEditFragmentBinding.colorsRecyclerViewContainer.visibility =
            View.VISIBLE
        else addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE

    }

    private fun handleArguments() {
        arguments?.let { args ->
            val note = Gson().fromJson(args.getString("note"), ViewNote::class.java)
            addEditViewModel.onEvent(
                AddEditEvent.StateChangeEvent(
                    AddEditState(
                        note.id, note.title, note.content, note.category, false,
                        note.color, note.attachedImages as MutableList<AttachedImage>
                    )
                )
            )
        }
    }

    private fun restoreState() {
        restoreColorPaletteVisibilityState()
        restoreNoteColor()
        restoreAttachedImages()
    }

    private fun restoreColorPaletteVisibilityState() {
        if (addEditViewModel.state.value?.isColorPaletteVisible ?: false) {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.VISIBLE
        } else {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE
        }
    }

    private fun restoreNoteColor() {
        val currentColor = addEditViewModel.state.value?.noteColor ?: R.color.white
        addEditFragmentBinding.noteBodyETContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), currentColor))
    }

    private fun restoreAttachedImages() {
        initAttachedImagesAdapter(addEditViewModel.state.value?.attachedImages)
        addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
    }

    private fun initColorsPaletteAdapter() {
        adapter = ColorPaletteAdapter(this)
        addEditFragmentBinding.colorsRecyclerView.adapter = adapter
        addEditFragmentBinding.colorsRecyclerView.layoutManager =
            LinearLayoutManager(this.context).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }
    }

    private fun initAttachOptionsAdapter() {
        val adapter = AttachOptionsAdapter(this)
        adapter.setOptions()
        addEditFragmentBinding.attachOptionsRecyclerView.adapter = adapter
        addEditFragmentBinding.attachOptionsRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)
    }

    private fun initAttachedImagesAdapter(images: List<AttachedImage>? = null) {
        attachedImagesAdapter = AttachedImagesAdapter(this)

        addEditFragmentBinding.attachedImagesRecyclerView.adapter = attachedImagesAdapter
        addEditFragmentBinding.attachedImagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

        images?.let {
            attachedImagesAdapter.setItems(it)
            addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
        }
    }

    private fun initSpinner() {
        val categories = arrayOf(
            NoteCategory.RELIGION,
            NoteCategory.WORK,
            NoteCategory.FAMILY,
            NoteCategory.LEARNING,
            NoteCategory.HOBBY,
            NoteCategory.LIFE
        )
        spinnerAdapter = ArrayAdapter(requireContext(), R.layout.note_category_item, categories)
        spinnerAdapter.setDropDownViewResource(R.layout.note_category_item)
        addEditFragmentBinding.categorySpinner.adapter = spinnerAdapter

        addEditFragmentBinding.categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    val newState = addEditViewModel.state.value?.copy(
                        noteTitle = addEditFragmentBinding.noteTitleET.text.toString(),
                        noteBody = addEditFragmentBinding.noteBodyET.text.toString(),
                        noteCategory = categories[position]
                    ) ?: AddEditState(
                        null,
                        addEditFragmentBinding.noteTitleET.text.toString(),
                        addEditFragmentBinding.noteBodyET.text.toString(),
                        categories[position],
                        false,
                        R.color.white,
                        mutableListOf()
                    )

                    addEditViewModel.onEvent(AddEditEvent.StateChangeEvent(newState))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Toast.makeText(requireContext(), "Selected: Nothing", Toast.LENGTH_LONG).show()
                }

            }

    }

    private fun observeAddNote() {
        addEditViewModel.addNoteLiveData.observe(viewLifecycleOwner) { oneTimeEvent ->
            oneTimeEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is Result.Success -> {
                        onAddNoteCompleted(it.value!!)
                    }
                    is Result.Failure -> {
                        Toast.makeText(requireContext(), "Note Add Failed", Toast.LENGTH_SHORT)
                            .show()
                        it.throwable.printStackTrace()
                    }
                }
            }
        }
    }

    private fun observeEditNote() {
        addEditViewModel.editNoteLiveData.observe(viewLifecycleOwner) { oneTimeEvent ->
            oneTimeEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is Result.Success -> {
                        onEditNoteCompleted(it.value!!)
                    }
                    is Result.Failure -> {
                        Toast.makeText(requireContext(), "Note Edit Failed", Toast.LENGTH_SHORT)
                            .show()
                        it.throwable.printStackTrace()
                    }
                }
            }

        }

    }

    private fun observeSaveAttachedImage() {
        addEditViewModel.saveAttachedImageLiveData.observe(viewLifecycleOwner) { result ->
            result?.let { oneTimeEvent ->
                oneTimeEvent.getContentIfNotHandled()?.let {
                    when (it) {
                        is Result.Success -> {
                            showAttachedImage(it.value!!)
                        }

                        is Result.Failure -> {
                            Toast.makeText(
                                requireContext(),
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            it.throwable.printStackTrace()
                        }
                    }
                }

            }
        }
    }

    private fun observeDeleteAttachedImage() {
        Log.d("Here", "Observe Delete")
        addEditViewModel.deleteAttachedImageLiveData.observe(viewLifecycleOwner) { oneTimeEvent ->
            oneTimeEvent.getContentIfNotHandled()?.let { attachedImage ->
                Log.d("Here", "Attached Image Observe")
                removeAttachedImageFromState(attachedImage)
            }
        }


    }

    /*  private fun deleteAttachedImage(image: AttachedImage) {
          addEditViewModel.state.value?.let {
              val currentImages = it.attachedImages
              currentImages.remove(image)
              addEditViewModel.onEvent(AddEditEvent.EditNoteEvent(
                  ViewNote(
                      id = it.noteId,
                      title = it.noteTitle,
                      content = it.noteBody,
                      category = it.noteCategory,
                      date = Date(),
                      it.noteColor,
                      false,
                      currentImages
                  )
              ))
          }

      }*/

    private fun removeAttachedImageFromState(image: AttachedImage) {
        Log.d("Here", "Remove Image")
        addEditViewModel.state.value?.let {
            val stateImages = it.attachedImages
            // val afterDelete = stateImages.filter { currentImage -> currentImage.id != image.id } as MutableList
            stateImages.remove(image)
            Log.d("Here", "Images After Removal: ${stateImages.size}")
            addEditViewModel.onEvent(
                AddEditEvent.StateChangeEvent(
                    it.copy(
                        attachedImages = stateImages
                    )
                )
            )
        }

    }

    private fun registerOnClickListeners() {
        addEditFragmentBinding.chooseColorImageView.setOnClickListener(this)
        addEditFragmentBinding.attachFile.setOnClickListener(this)
        addEditFragmentBinding.addEditFragmentBack.setOnClickListener(this)
        addEditFragmentBinding.fabSaveNote.setOnClickListener(this)
    }

    private fun onChooseColorClicked() {
        if (addEditViewModel.state.value?.isColorPaletteVisible ?: false) {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE
        } else {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.VISIBLE
        }

        addEditViewModel.onEvent(
            AddEditEvent.StateChangeEvent(
                addEditViewModel.state.value?.copy(
                    noteTitle = addEditFragmentBinding.noteTitleET.text.toString(),
                    noteBody = addEditFragmentBinding.noteBodyET.text.toString(),
                    isColorPaletteVisible = !(addEditViewModel.state.value?.isColorPaletteVisible
                        ?: true)
                ) ?: AddEditState(
                    null,
                    addEditFragmentBinding.noteTitleET.text.toString(),
                    addEditFragmentBinding.noteBodyET.text.toString(),
                    NoteCategory.default, true, R.color.white, mutableListOf()
                )
            )
        )
    }

    private fun onAddNoteCompleted(note: ViewNote) {
        // Hide Progress bar
        // Toast
        Toast.makeText(requireContext(), "Note Added with Id ${note.id}", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun onEditNoteCompleted(note: ViewNote) {
        // Hide Progress bar
        // Toast
        Toast.makeText(requireContext(), "Note Edited with Id ${note.id}", Toast.LENGTH_SHORT)
            .show()
        parentFragmentManager.popBackStack()
    }

    private fun onBackArrowPressed() {
        activity?.onBackPressed()
    }

    private fun onSaveNoteClicked() {
        val note: ViewNote?

        if (arguments != null) {
            val noteId = Gson().fromJson(arguments?.getString("note"), ViewNote::class.java).id
            note = ViewNote(
                id = noteId,
                title = addEditFragmentBinding.noteTitleET.text.toString(),
                content = addEditFragmentBinding.noteBodyET.text.toString(),
                category = addEditViewModel.state.value?.noteCategory ?: NoteCategory.default,
                date = Date(), // Last Edited: new Date
                color = addEditViewModel.state.value?.noteColor ?: R.color.white,
                isSelected = false,
                attachedImages = addEditViewModel.state.value?.attachedImages
                    ?: mutableListOf()
            )
            addEditViewModel.onEvent(AddEditEvent.EditNoteEvent(note))
        } else {
            note = ViewNote(
                title = addEditFragmentBinding.noteTitleET.text.toString(),
                content = addEditFragmentBinding.noteBodyET.text.toString(),
                category = addEditViewModel.state.value?.noteCategory ?: NoteCategory.default,
                date = Date(),
                color = addEditViewModel.state.value?.noteColor ?: R.color.white,
                isSelected = false,
                attachedImages = addEditViewModel.state.value?.attachedImages
                    ?: mutableListOf()
            )
            addEditViewModel.onEvent(AddEditEvent.AddNoteEvent(note))
        }

    }

    override fun onClick(view: View?) {
        when (view) {
            addEditFragmentBinding.chooseColorImageView -> onChooseColorClicked()
            addEditFragmentBinding.attachFile -> onAttachFileClicked()
            addEditFragmentBinding.addEditFragmentBack -> onBackArrowPressed()
            addEditFragmentBinding.fabSaveNote -> onSaveNoteClicked()
        }
    }

    override fun onColorBallClicked(color: Int) {
        addEditFragmentBinding.noteBodyETContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), color))

        addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE

        // if there is a state already
        addEditViewModel.state.value?.let {
            addEditViewModel.onEvent(
                AddEditEvent.StateChangeEvent(
                    addEditViewModel.state.value!!.copy(
                        noteColor = color
                    )
                )
            )
            return
        }
        // if there is no state yet
        addEditViewModel.onEvent(
            AddEditEvent.StateChangeEvent(
                AddEditState(
                    null,
                    addEditFragmentBinding.noteTitleET.text.toString(),
                    addEditFragmentBinding.noteBodyET.text.toString(), NoteCategory.default, false,
                    color, mutableListOf()
                )
            )
        )
    }

    private fun onAttachFileClicked() {
        Toast.makeText(requireContext(), "Attach File Clicked", Toast.LENGTH_SHORT).show()
        getYCoordinateOfEndOfScreen()
        if (addEditFragmentBinding.attachOptionsContainer.visibility == View.INVISIBLE) {
            //addEditFragmentBinding.attachOptionsContainer.animateVisibilityFromOffsetVertically(getYCoordinateOfEndOfScreen())
            addEditFragmentBinding.attachOptionsContainer.visibility = View.VISIBLE
        } else {
            addEditFragmentBinding.attachOptionsContainer.animateInvisibility()
        }
    }

    private fun getYCoordinateOfEndOfScreen(): Float {
        val display =
            (context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        return point.y.toFloat()
    }

    override fun onAttachOptionClicked(type: AttachOptionType) {

        if (type == AttachOptionType.GALLERY) {
            Toast.makeText(requireContext(), "Cicked", Toast.LENGTH_SHORT).show()
            openGallery()
        } else {
            openCamera()
        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Choose Your Picture"), GALLERY_CODE)
    }

    private fun openCamera() {
        val intent = Intent().apply {
            type = "image/*"
            action = "android.media.action.IMAGE_CAPTURE"
        }
        startActivityForResult(intent, CAMERA_CODE)
    }

    private fun showAttachedImage(path: String) {
        val images: MutableList<AttachedImage> =
            addEditViewModel.state.value?.attachedImages ?: mutableListOf()
        images.add(AttachedImage(System.currentTimeMillis().toString(), path, false))

        // if state is not null
        addEditViewModel.state.value?.let {
            addEditViewModel.state.value?.copy(attachedImages = images)
                ?.let { AddEditEvent.StateChangeEvent(it) }
                ?.let { addEditViewModel.onEvent(it) }
            return
        }
        // if state is null
        addEditViewModel.onEvent(
            AddEditEvent.StateChangeEvent(
                AddEditState(
                    null,
                    addEditFragmentBinding.noteTitleET.text.toString(),
                    addEditFragmentBinding.noteBodyET.text.toString(),
                    NoteCategory.default,
                    false,
                    R.color.white, images
                )
            )
        )

        addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE || requestCode == CAMERA_CODE) {
            if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                // save the file to the internal storage and get the new uri
                addEditViewModel.onEvent(
                    AddEditEvent.SaveAttachedImage(
                        bitmap,
                        requireContext().applicationContext
                    )
                )
            }
        }
    }

    override fun onAttachedImageClicked(image: AttachedImage) {
        val bundle = Bundle()
        bundle.putString("image", Gson().toJson(image))
        val fragment = AttachedImageFragment()
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentsContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}