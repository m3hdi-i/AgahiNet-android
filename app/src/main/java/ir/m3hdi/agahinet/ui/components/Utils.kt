package ir.m3hdi.agahinet.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpacerV(modifier: Modifier=Modifier, height: Dp) {
    Spacer(modifier = modifier.height(height))
}