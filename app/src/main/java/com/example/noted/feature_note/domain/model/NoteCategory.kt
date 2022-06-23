package com.example.noted.feature_note.domain.model

enum class NoteCategory {
    RELIGION, WORK, HOBBY, FAMILY, LEARNING, LIFE;

    override fun toString(): String {
        return this.name.lowercase().replaceFirstChar {
            it.uppercase()
        }
    }

    companion object{
        val default = RELIGION
    }
}