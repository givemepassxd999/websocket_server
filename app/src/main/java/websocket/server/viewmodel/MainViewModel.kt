package websocket.server.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import websocket.server.data.Server
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val server: Server) : ViewModel() {
    private val _connectionState = MutableStateFlow(UiState())
    val connectionState: StateFlow<UiState> = _connectionState.asStateFlow()

    fun startServer() {
        server.start()
    }

    fun stopServer() {
        server.stop()
    }
}

data class UiState(val connected: Boolean = false)