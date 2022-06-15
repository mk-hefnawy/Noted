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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noted.R
import com.example.noted.core.Extensions.animateInvisibility
import com.example.noted.core.Extensions.animateVisibility
import com.example.noted.core.Extensions.animateVisibilityFromOffsetVertically
import com.example.noted.databinding.FragmentAddEditBinding
import com.example.noted.feature_note.domain.model.NoteCategory
import com.example.noted.feature_note.presentation.add_edit_note.attach_files.AttachOptionType
import com.example.noted.feature_note.presentation.add_edit_note.attach_files.AttachOptionsAdapter
import com.example.noted.feature_note.presentation.add_edit_note.attach_files.AttachOptionsInterface
import com.example.noted.feature_note.presentation.add_edit_note.attach_files.AttachedImagesAdapter
import com.example.noted.feature_note.presentation.model.ViewNote
import com.example.noted.feature_note.presentation.notes.NotesFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddEditFragment : Fragment(), View.OnClickListener, AddEditPresentationController,
    AttachOptionsInterface {
    private lateinit var addEditFragmentBinding: FragmentAddEditBinding
    val addEditViewModel: AddEditNoteViewModel by viewModels()
    private lateinit var adapter: ColorPaletteAdapter
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
        addEditFragmentBinding = FragmentAddEditBinding.inflate(layoutInflater)
        return addEditFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        registerOnClickListeners()
        observeAddNote()
        observeEditNote()
        observeState()
        restoreState()
        if (savedInstanceState == null){
            handleArguments()
        }

    }

    private fun init() {
        initColorsPaletteAdapter()
        initAttachOptionsAdapter()
        initAttachedImagesAdapter()
    }

    private fun observeState() {
        addEditViewModel.state.observe(viewLifecycleOwner) { state ->
            state?.let {
                populateViewWithState(state)
            }
        }
    }

    private fun populateViewWithState(state: AddEditState) {
        Log.d("Here", "populate")
        addEditFragmentBinding.noteTitleET.setText(state.noteTitle)
        addEditFragmentBinding.noteBodyET.setText(state.noteBody)
        addEditFragmentBinding.noteBodyETContainer.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), state.noteColor))

        initAttachedImagesAdapter(state.attachedImagesUris)

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
                        note.title, note.content, false,
                        note.color, note.imagesUris ?: mutableListOf()
                    )
                )
            )
            //addEditFragmentBinding.noteTitleET.setText(note.title)
            //addEditFragmentBinding.noteBodyET.setText(note.content)
            /*ote.imagesUris?.let { uris ->
                addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
                initAttachedImagesAdapter(uris)
            }*/
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
        initAttachedImagesAdapter(addEditViewModel.state.value?.attachedImagesUris)
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

    private fun initAttachedImagesAdapter(uris: List<String>? = null) {
        attachedImagesAdapter = AttachedImagesAdapter()

        addEditFragmentBinding.attachedImagesRecyclerView.adapter = attachedImagesAdapter
        addEditFragmentBinding.attachedImagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext()).apply {
                orientation = LinearLayoutManager.HORIZONTAL
            }

        uris?.let {
            Log.d("Here", "uris?.let ${uris.size}")
            attachedImagesAdapter.setItems(uris)
            addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
        }
    }

    private fun observeAddNote() {
        addEditViewModel.addNoteSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ViewNote> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(note: ViewNote) {
                    onAddNoteCompleted(note)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(requireContext(), "Note Add Failed", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

                override fun onComplete() {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun observeEditNote() {
        addEditViewModel.editNoteSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ViewNote> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(note: ViewNote) {
                    onEditNoteCompleted(note)
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(requireContext(), "Note Edit Failed", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }

                override fun onComplete() {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun registerOnClickListeners() {
        addEditFragmentBinding.chooseColorImageView.setOnClickListener(this)
        addEditFragmentBinding.attachFile.setOnClickListener(this)
        addEditFragmentBinding.addEditFragmentBack.setOnClickListener(this)
        addEditFragmentBinding.fabSaveNote.setOnClickListener(this)
    }

    private fun onChooseColorClicked() {
        Log.d("Here", "Choose Color Image View Clicked")
        if (addEditViewModel.state.value?.isColorPaletteVisible ?: false) {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE
        } else {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.VISIBLE
        }

        addEditViewModel.onEvent(AddEditEvent.ToggleColorPaletteVisibility)
    }

    private fun onAddNoteCompleted(note: ViewNote) {
        // Hide Progress bar
        // Toast
        Toast.makeText(requireContext(), "Note Added with Id ${note.id}", Toast.LENGTH_SHORT).show()
        // Open Notes Fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentsContainer, NotesFragment())
            .commit()
    }

    private fun onEditNoteCompleted(note: ViewNote) {
        // Hide Progress bar
        // Toast
        Toast.makeText(requireContext(), "Note Edited with Id ${note.id}", Toast.LENGTH_SHORT)
            .show()
        // Open Notes Fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentsContainer, NotesFragment())
            .commit()
    }

    private fun onBackArrowPressed() {
        activity?.onBackPressed()
    }

    private fun onSaveNoteClicked() {
        var note: ViewNote? = null

        if (arguments != null) {
            val noteId = Gson().fromJson(arguments?.getString("note"), ViewNote::class.java).id
            note = ViewNote(
                id = noteId,
                title = addEditFragmentBinding.noteTitleET.text.toString(),
                content = addEditFragmentBinding.noteBodyET.text.toString(),
                category = NoteCategory.WORK,
                date = Date(),
                color = addEditViewModel.state.value?.noteColor ?: R.color.white,
                isSelected = false
            )
            addEditViewModel.onEvent(AddEditEvent.EditNoteEvent(note))
        } else {
            note = ViewNote(
                title = addEditFragmentBinding.noteTitleET.text.toString(),
                content = addEditFragmentBinding.noteBodyET.text.toString(),
                category = NoteCategory.WORK,
                date = Date(),
                color = addEditViewModel.state.value?.noteColor ?: R.color.white,
                isSelected = false
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
        addEditViewModel.onEvent(AddEditEvent.ChangeNoteColor(color))
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

        }
    }

    private fun openGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Choose Your Picture"), GALLERY_CODE)
    }

    private fun addAttachedImage(uri: String) {
        val newImages = mutableListOf<String>()
        newImages.addAll(attachedImagesAdapter.images)
        newImages.add(uri)
        attachedImagesAdapter.setItems(newImages)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_CODE) {
            if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                // save the uri in the viewModel state
                val uris: MutableList<String> =
                    addEditViewModel.state.value?.attachedImagesUris ?: mutableListOf()
                uris.add(imageUri.toString())

                addEditViewModel.state.value?.copy(attachedImagesUris = uris)
                    ?.let { AddEditEvent.StateChangeEvent(it) }
                    ?.let { addEditViewModel.onEvent(it) }
                // show it for the user
                //addAttachedImage(imageUri.toString())
                //initAttachedImagesAdapter(addEditViewModel.state.value.attachedImagesUris)
                addEditFragmentBinding.attachedImagesContainer.visibility = View.VISIBLE
            }
        }

    }
}