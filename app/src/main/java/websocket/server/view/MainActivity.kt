package websocket.server.view

import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import websocket.server.R
import websocket.server.databinding.ActivityMainBinding
import websocket.server.viewmodel.MainViewModel
import java.math.BigInteger
import java.net.InetAddress


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.composeView.setContent {
            MainView(viewModel = viewModel) {
                goBrowser()
            }
        }
    }

    private fun goBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.getWebKeyword()))
        startActivity(intent)
    }

    @Composable
    fun MainView(viewModel: MainViewModel, listener: Event) {
        val connectionState = viewModel.connectionState.collectAsState().value
        val serverInputState = viewModel.getServerInputState().collectAsState().value
        val serverGoWeb = viewModel.goWeb
        LaunchedEffect(key1 = Unit) {
            serverGoWeb.collect {
                Log.d("@@", "serverGoWeb: $serverGoWeb")
                if (it) {
                    listener.goBrowser()
                    viewModel.resetServerGoWeb()
                }
            }
        }

        Column(modifier = Modifier.padding(start = 10.dp)) {
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = if (connectionState.running) {
                        getString(
                            R.string.connected_info,
                            getIpAddress() + ":8888",
                            getString(if (connectionState.connected) R.string.connected else R.string.disconnected)
                        )
                    } else {
                        getString(R.string.stopped)
                    }
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Button(onClick = {
                    viewModel.startServer()
                }, enabled = connectionState.running.not()) {
                    Text(text = stringResource(R.string.start))
                }
                Button(onClick = {
                    viewModel.stop()
                }, modifier = Modifier.padding(start = 10.dp), enabled = connectionState.running) {
                    Text(text = stringResource(R.string.stop))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Textarea(
                    value = serverInputState,
                    onValueChange = {
                        viewModel.setInput(
                            serverInputState.ifEmpty {
                                viewModel.input.value
                            }
                        )
                    },
                    placeholderText = stringResource(id = R.string.input_search_keyword),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(300.dp),
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Button(onClick = {
                    listener.goBrowser()
                }) {
                    Text(text = stringResource(R.string.go))
                }
                Button(
                    onClick = {
                        viewModel.clearInput()
                    },
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(text = stringResource(R.string.clear))
                }
            }
        }
    }

    @Composable
    fun Textarea(
        value: String,
        onValueChange: (String) -> Unit,
        placeholderText: String,
        modifier: Modifier = Modifier,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            placeholder = {
                Text(
                    modifier = Modifier.background(color = Color.Transparent),
                    text = placeholderText,
                )
            },
        )
    }

    @Suppress("DEPRECATION")
    private fun getIpAddress(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress =
            BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()
                .reversedArray()
        return InetAddress.getByAddress(ipAddress).hostAddress
    }

    fun interface Event {
        fun goBrowser()
    }
}