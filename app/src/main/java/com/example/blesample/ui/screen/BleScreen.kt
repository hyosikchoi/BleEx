package com.example.blesample.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BleScreen(
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit,
    onSendClick: () -> Unit,
    receivedText: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onScanClick) {
            Text("Scan & Connect")
        }

        Button(onClick = onSendClick) {
            Text("Send Data")
        }

        Text("Received Data: $receivedText")
    }
}
