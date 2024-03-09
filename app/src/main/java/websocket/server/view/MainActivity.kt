package websocket.server.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import websocket.server.R
import websocket.server.databinding.ActivityMainBinding
import websocket.server.viewmodel.MainViewModel

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
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Text(text = "Running on ip:port")
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.start))
                }
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.stop))
                }
            }
        }
    }
}