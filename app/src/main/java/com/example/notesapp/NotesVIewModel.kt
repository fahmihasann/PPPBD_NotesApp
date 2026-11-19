package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesVIewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
    fun addNote(title: String, note: String) {
        val t = title.trim()
        val n = note.trim()
        if (t.isEmpty() && n.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(title = t, note = n)) }
    }

    // UPDATE
    fun updateNote(noteEntity: Note, newTitle: String, newNote: String) {
        val t = newTitle.trim()
        val n = newNote.trim()
        if (t.isEmpty() && n.isEmpty()) return
        viewModelScope.launch { dao.update(noteEntity.copy(title = t, note = n)) }
    }

    // DELETE
    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }
}
