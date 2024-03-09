package websocket.server.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import websocket.server.data.Server
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val server: Server) : ViewModel() {
    private val _connectionState = MutableStateFlow(UiState())
    val connectionState: StateFlow<UiState> = _connectionState.asStateFlow()

    fun startServer() {
        viewModelScope.launch {
            server.start { running, connected ->
                _connectionState.value = UiState(running = running, connected = connected)
            }
        }
    }

    fun stop() {
        viewModelScope.launch {
            server.stop()
            _connectionState.value = UiState(running = false, connected = false)
        }
    }
}

data class UiState(val running: Boolean = false, val connected: Boolean = false)