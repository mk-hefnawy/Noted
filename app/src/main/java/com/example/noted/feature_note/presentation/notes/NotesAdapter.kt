package com.example.noted.feature_note.presentation.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.noted.R
import com.example.noted.databinding.NoteItemBinding
import com.example.noted.feature_note.domain.model.Note
import com.example.noted.feature_note.presentation.model.ViewNote
import com.google.android.material.card.MaterialCardView

class NotesAdapter(val notesActivityAdapterInterface: NotesActivityAdapterInterface): RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    var notes: ArrayList<ViewNote> = arrayListOf()
    lateinit var noteItemBinding: NoteItemBinding

    class NoteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val noteTitleTV = itemView.findViewById<TextView>(R.id.noteTitle)
        val noteBodyTV = itemView.findViewById<TextView>(R.id.noteBody)
        val noteCategoryTV = itemView.findViewById<TextView>(R.id.noteCategory)
        val noteCardView = itemView.findViewById<MaterialCardView>(R.id.noteCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        noteItemBinding = NoteItemBinding.inflate(LayoutInflater.from(parent.context))
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false))
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
       holder.noteTitleTV.text = notes[position].title
       holder.noteBodyTV.text = notes[position].content
       holder.noteCategoryTV.text = notes[position].category.toString()

        if (notes[position].isSelected){
            holder.noteCardView.strokeWidth = 2
        }

        holder.itemView.setOnLongClickListener {
            notes[position].isSelected = true
            this.notifyItemChanged(position)

            notesActivityAdapterInterface.onNoteLongClicked(notes[position])
            return@setOnLongClickListener true // to notify that I have consumed the event
        }
    }

    override fun getItemCount(): Int {
       return notes.size
    }

    fun setNotes(notes: List<ViewNote>){
        if (!this.notes.isEmpty()){
            this.notes.addAll(notes as ArrayList<ViewNote>)
        }
    }

    fun getSelectedNotes(): List<ViewNote>{
        return this.notes.filter { it.isSelected }
    }
}