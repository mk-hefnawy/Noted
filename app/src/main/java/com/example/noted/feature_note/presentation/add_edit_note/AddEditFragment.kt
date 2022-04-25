package com.example.noted.feature_note.presentation.add_edit_note

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.databinding.FragmentAddEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(), View.OnClickListener, AddEditPresentationController {
    private lateinit var addEditFragmentBinding: FragmentAddEditBinding
    val addEditViewModel: AddEditNoteViewModel by viewModels()
    private lateinit var adapter: ColorPaletteAdapter

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
        initOnClickListeners()
        restoreState()
    }

    private fun init(){
        initColorsPaletteAdapter()
    }

    private fun restoreState(){
        restoreColorPaletteVisibilityState()
        restoreNoteColor()
    }
    private fun restoreColorPaletteVisibilityState(){
        if (addEditViewModel.state.value.isColorPaletteVisible){
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.VISIBLE
        }else{
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE
        }
    }

    private fun restoreNoteColor(){
        val currentColor = addEditViewModel.state.value.noteColor
        addEditFragmentBinding.addEditFragmentRoot.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), currentColor))
    }

    private fun initColorsPaletteAdapter(){
        adapter = ColorPaletteAdapter(this)
        addEditFragmentBinding.colorsRecyclerView.adapter = adapter
        addEditFragmentBinding.colorsRecyclerView.layoutManager = LinearLayoutManager(this.context).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
    }

    private fun initOnClickListeners() {
        addEditFragmentBinding.chooseColorImageView.setOnClickListener(this)
    }

    private fun onChooseColorClicked() {
        Log.d("Here", "Choose Color Image View Clicked")
        if (addEditViewModel.state.value.isColorPaletteVisible) {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.GONE
        } else {
            addEditFragmentBinding.colorsRecyclerViewContainer.visibility = View.VISIBLE
        }

        addEditViewModel.onEvent(AddEditEvent.ToggleColorPaletteVisibility)
    }

    override fun onClick(view: View?) {
        when (view) {
            addEditFragmentBinding.chooseColorImageView -> onChooseColorClicked()
        }
    }

    override fun onColorBallClicked(color: Int) {
        // change color of fragment root
        Log.d("Here", "Fragment Change Note Color")
        addEditFragmentBinding.addEditFragmentRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), color))

        addEditViewModel.onEvent(AddEditEvent.ChangeNoteColor(color))
    }


}