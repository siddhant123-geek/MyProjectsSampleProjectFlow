package com.example.palisisflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class ProgressBarActivityViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Int>>(UiState.Loading)
    val uiState: StateFlow<UiState<Int>> = _uiState

    init {
        doApiCall()
    }

    private fun doApiCall() {
        viewModelScope.launch {
            flow {
                for(i in 1..10) {
                    delay(1000)
                    emit(i)
                }
            }
                .catch {e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect {
                    _uiState.value = UiState.Success(it)
                }

        }
    }
}