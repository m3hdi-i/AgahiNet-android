package ir.m3hdi.agahinet.ui.components.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.m3hdi.agahinet.ui.theme.seed

@Composable
fun SpacerV(modifier: Modifier=Modifier, height: Dp) {
    Spacer(modifier = modifier.height(height))
}


@Composable
fun BottomSheetItem(iconDrawableId:Int, title:String, onClick: () -> Unit){
    Box(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }){

        Row(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center){
            Icon(painter = painterResource(id = iconDrawableId), modifier = Modifier.alpha(0.5f), contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text =title)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(title: String,navigationIcon: @Composable () -> Unit = {}){
    TopAppBar(modifier = Modifier.fillMaxWidth(),
        title = {  Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon=navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = seed,
            navigationIconContentColor = Color.White,
            titleContentColor = Color.White )
    )
}