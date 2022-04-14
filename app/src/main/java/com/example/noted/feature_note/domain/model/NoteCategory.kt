package com.example.noted.feature_note.domain.model

enum class NoteCategory {
    WORK, HOBBY, FAMILY, LEARNING, LIFE;

    override fun toString(): String {
        return this.name
    }
}