package com.example.planet.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.planet.data.QuizDatabase
import com.example.planet.data.QuizQuestionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {  // ✅ 클래스!

    private val quizDao = QuizDatabase.getDatabase(application).quizQuestionDao()

    private val _quizList = MutableStateFlow<List<QuizQuestionEntity>>(emptyList())
    val quizList: StateFlow<List<QuizQuestionEntity>> = _quizList

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            val result = quizDao.getAllQuestions()
            _quizList.value = result
        }
    }

    fun nextQuestion() {
        if (_currentIndex.value < _quizList.value.size - 1) {
            _currentIndex.value += 1
        }
    }
}
