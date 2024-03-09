package websocket.server.view

import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
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
            MainView(viewModel = viewModel)
        }
    }

    @Composable
    fun MainView(viewModel: MainViewModel) {
        val connectionState = viewModel.connectionState.collectAsState().value
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
        }
    }

    @Suppress("DEPRECATION")
    private fun getIpAddress(): String {
        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager
        val ipAddress =
            BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()
                .reversedArray()
        return InetAddress.getByAddress(ipAddress).hostAddress
    }
}