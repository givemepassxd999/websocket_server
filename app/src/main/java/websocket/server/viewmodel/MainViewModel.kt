package websocket.server.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import websocket.server.data.Server
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val server: Server) : ViewModel() {
}