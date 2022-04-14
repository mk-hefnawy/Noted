package com.example.noted.feature_note.presentation.notes

import com.example.noted.feature_note.domain.utils.NoteOrder
import com.example.noted.feature_note.domain.utils.OrderType

interface PresentationController {
    fun onRadioButtonChecked(radioButtonId: Int, stateOrder: NoteOrder, stateOrderType: OrderType)
}