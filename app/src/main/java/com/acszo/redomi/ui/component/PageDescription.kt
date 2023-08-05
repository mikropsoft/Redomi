package com.acszo.redomi.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PageDescription(
    description: String
) {
    Text(
        modifier = Modifier.padding(horizontal = 28.dp),
        text = description,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}