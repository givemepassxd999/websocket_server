package websocket.server.data

import android.util.Log
import com.google.gson.Gson
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import websocket.server.data.ApiData.Companion.CLEAR
import websocket.server.data.ApiData.Companion.INPUT
import websocket.server.data.ApiData.Companion.MOVE

//ref:https://ktor.io/docs/websocket.html
class Server {
    private var server: NettyApplicationEngine? = null
    private val tag = Server::class.java.simpleName

    private var _input = MutableStateFlow("")
    val input: StateFlow<String> = _input

    private val gson = Gson()

    fun clearInput() {
        _input.value = ""
    }

    fun start(connectionState: ConnectionState) {
        server = embeddedServer(Netty, port = 8888) {
            install(WebSockets)
            connectionState.accept(running = true, connected = false)
            routing {
                webSocket("/") {
                    Log.d(tag, "Started")
                    try {
                        connectionState.accept(running = true, connected = true)
                        for (frame in incoming) {
                            frame as? Frame.Text ?: continue
                            val receivedText = frame.readText()
                            if (receivedText.isNotEmpty()) {
                                Log.d(tag, "@@Received:$receivedText")
                                val apiData = gson.fromJson(receivedText, ApiData::class.java)
                                when (apiData.dataType) {
                                    INPUT -> {
                                        apiData.data.let {
                                            if (it.input == "-1") {
                                                _input.value = ""
                                            } else {
                                                _input.value = it.input
                                            }
                                        }
                                    }

                                    CLEAR -> {
                                        clearInput()
                                    }

                                    MOVE -> {

                                    }

                                    else -> {

                                    }
                                }
                            }
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        Log.d(tag, "ClosedReceiveChannelException")
                        connectionState.accept(running = false, connected = false)
                        server?.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
                    } catch (e: Throwable) {
                        Log.d(tag, "Throwable")
                        connectionState.accept(running = false, connected = false)
                        server?.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
                    } finally {
                        Log.d(tag, "Finally")
                    }
                }
            }
        }
        server?.start()
        Log.d(tag, "Started")
    }

    fun stop() {
        server?.stop(gracePeriodMillis = 500, timeoutMillis = 1000)
        _input.value = ""
        Log.d(tag, "Stopped")
    }

    fun interface ConnectionState {
        fun accept(running: Boolean, connected: Boolean)
    }
}