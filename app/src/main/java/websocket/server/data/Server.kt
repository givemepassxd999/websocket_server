package websocket.server.data

import android.util.Log
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

class Server {
    private var server: NettyApplicationEngine? = null
    private val tag = Server::class.java.simpleName

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
                                Log.d(tag, "Received:$receivedText")
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
        Log.d(tag, "Stopped")
    }

    fun interface ConnectionState {
        fun accept(running: Boolean, connected: Boolean)
    }
}