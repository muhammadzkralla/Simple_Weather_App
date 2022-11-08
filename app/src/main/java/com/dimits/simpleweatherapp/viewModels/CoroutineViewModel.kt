package com.dimits.simpleweatherapp.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dimits.simpleweatherapp.API
import com.dimits.simpleweatherapp.model.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CoroutineViewModel : ViewModel() {

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<State>(State.Loading)
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<State> = _uiState

    fun getWeather(city: String, key: String){
        viewModelScope.launch {
            try {
                API.apiService.getMainResponseWithCoroutines(city, "metric", key).collect{
                    _uiState.emit(State.Success(it))
                }
            } catch (e: Throwable){
                _uiState.emit(State.Error(e))
            }
        }
    }

}
// Represents different states for the LatestNews screen
sealed class State {
    object Loading:State()
    data class Success(val response: Response): State()
    data class Error(val throwable: Throwable): State()
}