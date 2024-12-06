package com.example.weatherapp.Model

data class Note(val id: Int, val title: String, val content: String) {
    override fun toString(): String {
        return "Note: { id: $id, title: $title, content: $content }"
    }
}
