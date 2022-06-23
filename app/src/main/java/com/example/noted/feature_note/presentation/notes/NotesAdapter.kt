package com.example.noted.feature_note.presentation.notes

import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.databinding.NoteItemBinding
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.card.MaterialCardView

class NotesAdapter(val notesActivityAdapterInterface: NotesFragmentAdapterInterface) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    var notes: ArrayList<ViewNote> = arrayListOf()
    var selectedNotes: ArrayList<ViewNote> = arrayListOf()
    lateinit var noteItemBinding: NoteItemBinding


    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTitleTV = itemView.findViewById<TextView>(R.id.noteTitle)
        val noteBodyTV = itemView.findViewById<TextView>(R.id.noteBody)
        val noteCategoryTV = itemView.findViewById<TextView>(R.id.noteCategoryTV)
        val noteCardViewContainer = itemView.findViewById<MaterialCardView>(R.id.noteCardViewContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        noteItemBinding = NoteItemBinding.inflate(LayoutInflater.from(parent.context))
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.noteTitleTV.text = notes[position].title
        holder.noteBodyTV.text = notes[position].content
        holder.noteCategoryTV.text = notes[position].category.toString()
        holder.noteCardViewContainer.backgroundTintList = ColorStateList
            .valueOf(ContextCompat.getColor(holder.itemView.context, notes[position].color))

        if (notes[position].isSelected) {
            holder.noteCardViewContainer.strokeWidth = 15
        }else{
            holder.noteCardViewContainer.strokeWidth = 0
        }

        holder.itemView.setOnLongClickListener {
            notes[position].isSelected = true
            selectedNotes.add(notes[position])

            this.notifyItemChanged(position)
            notesActivityAdapterInterface.onNoteLongClicked(notes[position])
            return@setOnLongClickListener true // to notify that I have consumed the event
        }

        holder.itemView.setOnClickListener {
            if (notes[position].isSelected){
                selectedNotes.remove(notes[position])
                notes[position].isSelected = false
                this.notifyItemChanged(position)
                if (selectedNotes.size == 0){
                    Log.d("Here", "0 selected")
                    notesActivityAdapterInterface.hideNoteOptions()
                }
            }else{
                notesActivityAdapterInterface.onNoteClicked(notes[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun setNotes(notes: List<ViewNote>) {
        if (notes.isNotEmpty()){
            this.notes = notes as ArrayList<ViewNote>
            Log.d("Here", "Set Notes " + this.notes.size)
            notifyDataSetChanged()
        }

    }

    fun deleteNotes(notes: List<ViewNote>){
        this.notes.removeAll(notes)
        notifyDataSetChanged()
    }

    fun getSelectedNotes(): List<ViewNote> {
        Log.d("Here", "Selected Length " + selectedNotes.size)
        return selectedNotes
    }

    fun removeAllSelections() {
        selectedNotes.forEach {
            it.isSelected = false
        }
        selectedNotes = arrayListOf()
        notifyDataSetChanged()
    }
}