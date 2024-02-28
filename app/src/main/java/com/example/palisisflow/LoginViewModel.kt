package com.example.palisisflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val uiState: StateFlow<UiState<String>> = _uiState

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(2000)

            if(userName == "Sid" && password == "myPassword") {
                _uiState.value = UiState.Success("$userName $password")
            }
            else {
                _uiState.value = UiState.Error("Entered credentials are invalid")
            }
        }
    }
}