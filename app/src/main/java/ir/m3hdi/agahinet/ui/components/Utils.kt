package ir.m3hdi.agahinet.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ir.m3hdi.agahinet.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SpacerV(modifier: Modifier=Modifier, height: Dp) {
    Spacer(modifier = modifier.height(height))
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ZZZZZZ(
    modifier: Modifier,
    images: ImmutableList<Pair<String, Boolean>>,
    //onAddImage: () -> Unit
) {

    Column(modifier) {
        Text(text = stringResource(id = R.string.new_ad_images),
            style = MaterialTheme.typography.titleMedium)

        SpacerV(height = 4.dp)
        FlowRow(
            modifier= Modifier
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            content = {
                /*if (images.size < MAX_IMAGES_COUNT)
                    AddPhotoButton(onAddImage)*/

                YYY(persistentListOf())

            }
        )
        SpacerV(height = 16.dp)

    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YYY(images: ImmutableList<Pair<String, Boolean>>){

    FlowRow(
        modifier= Modifier
            //.animateContentSize()
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        content = {
            /*if (images.size < MAX_IMAGES_COUNT)
                AddPhotoButton(onAddImage)*/

            images.forEach {
                WWW()
            }
        }
    )
}


@Composable
fun WWW() {
    Box(modifier = Modifier.padding(6.dp)) {
        ElevatedCard(modifier = Modifier
            .size(128.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)){

                FilledTonalIconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .size(24.dp)
                        .align(Alignment.TopStart)
                        .alpha(0.75f)
                ) {
                    Icon(
                        Icons.Filled.MoreVert,
                        contentDescription = "More",
                    )
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))) {
                    Text(
                        text =stringResource(id = R.string.main_image),
                        color= Color.White,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

}
