package app.kaup.feature.auth.ui.hotp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotpProvisioningScreen(
    viewModel: HotpProvisioningViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isSaved) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            onComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Manager Authorization Setup") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isGenerating) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Generating secure key...")
            } else if (state.otpAuthUri != null) {
                Text(
                    text = "Scan this QR code using the staff device.",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
                ) {
                    QrCodeImage(
                        content = state.otpAuthUri!!,
                        sizePixels = 600,
                        modifier = Modifier.padding(16.dp).size(250.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Or enter this secret manually:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = state.base32Secret ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = { viewModel.saveAndComplete() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I have scanned this code on staff devices")
                }
            }
        }
    }
}
