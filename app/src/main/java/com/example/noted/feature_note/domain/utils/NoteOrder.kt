package com.example.noted.feature_note.domain.utils

sealed class NoteOrder(var orderType: OrderType){
    class Title(orderType: OrderType) : NoteOrder(orderType)
    class Category(orderType: OrderType) : NoteOrder(orderType)
    class Date(orderType: OrderType) : NoteOrder(orderType)

    // like copy() of data classes, which allows you to copy an object with changing some of its fields
    fun copy(orderType: OrderType): NoteOrder{
        return when(this){
            is Title -> Title(orderType)
            is Date ->  Date(orderType)
            is Category -> Category(orderType)
        }
    }
}
