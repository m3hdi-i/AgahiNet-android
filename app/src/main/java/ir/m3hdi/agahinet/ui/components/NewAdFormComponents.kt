package ir.m3hdi.agahinet.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.domain.model.Category
import ir.m3hdi.agahinet.ui.viewmodel.MAX_IMAGES_COUNT
import ir.m3hdi.agahinet.ui.viewmodel.NewAdImage
import ir.m3hdi.agahinet.util.ComposeFileProvider
import ir.m3hdi.agahinet.util.Constants
import kotlinx.collections.immutable.ImmutableList

@Composable
fun HeaderSection(modifier: Modifier) {
    Spacer(Modifier.height(16.dp))
    Text(modifier=modifier,text = stringResource(id = R.string.title_new_ad),
        style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    Divider()
}

@Composable
fun LocationSection(
    modifier: Modifier,
    allProvinces: ImmutableList<City>,
    province: City?, onProvinceChanged:(City)->Unit,
    city: City?, onCityChanged:(City)->Unit,
    citiesToSelect: ImmutableList<City>
){

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {


        // Provinces dropdown list
        val selectedProvince = province?.let { DropDownData.CityItem(it) }

        MyDropDownMenu(
            modifier = Modifier.weight(0.45f),
            label = stringResource(id = R.string.new_ad_province),
            items = allProvinces.map { DropDownData.CityItem(it) },
            selectedItem = selectedProvince,
            onItemSelected = { onProvinceChanged((it as DropDownData.CityItem).cityItem) }
        )

        Spacer(modifier = Modifier.weight(0.1f))

        // Cities dropdown list
        val cities =  citiesToSelect.map { DropDownData.CityItem(it) }
        val selectedCity = city?.let { DropDownData.CityItem(it) }

        MyDropDownMenu(
            modifier = Modifier.weight(0.45f),
            label = stringResource(id = R.string.new_ad_city),
            items = cities,
            selectedItem= selectedCity,
            onItemSelected = { onCityChanged((it as DropDownData.CityItem).cityItem) }
        )
    }
}

@Composable
fun CategorySection(modifier: Modifier, category: Category?, onCategoryChanged:(Category)->Unit) {
    Spacer(Modifier.height(16.dp))

    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween){
        Text(text = stringResource(id = R.string.new_ad_category),
            modifier = Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(32.dp))

        val selectedCategory = category?.let { DropDownData.CategoryItem(it) }
        MyDropDownMenu(
            modifier= Modifier.align(Alignment.CenterVertically),
            label = stringResource(id = R.string.new_ad_category_label),
            items = Constants.CATEGORIES.map { DropDownData.CategoryItem(it) },
            selectedItem = selectedCategory,
            onItemSelected = {
                onCategoryChanged((it as DropDownData.CategoryItem).catItem)
            }
        )
    }

    Spacer(Modifier.height(8.dp))
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ImagesSection(
    modifier: Modifier,
    images: ImmutableList<NewAdImage>,
    onAddPhoto: () -> Unit,
    onDeletePhoto: (NewAdImage) ->Unit,
    onSetMainPhoto: (NewAdImage) ->Unit,
) {

    Column(modifier) {
        Text(text = stringResource(id = R.string.new_ad_images),
            style = MaterialTheme.typography.titleMedium)

        SpacerV(height = 4.dp)
        FlowRow(
            modifier= Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            content = {
                if (images.size < MAX_IMAGES_COUNT){
                    AddPhotoButton(onAddPhoto)
                }

                images.forEach {
                    ImageItem(it,onDeletePhoto,onSetMainPhoto)
                }
            }
        )
        SpacerV(height = 16.dp)

    }

}

@Composable
fun AddPhotoButton(onAddPhoto: () -> Unit) {
    Box(modifier = Modifier.padding(6.dp)) {
        ElevatedCard(modifier = Modifier
            .size(128.dp)
            .clickable { onAddPhoto() }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {

                Image(modifier = Modifier
                    .size(56.dp)
                    .alpha(0.42f),
                    painter = painterResource(R.drawable.ic_add_a_photo),contentDescription = null)

                Text(text="اضافه کردن عکس" , modifier = Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ImageItem(
    image: NewAdImage,
    onDeletePhoto: (NewAdImage) -> Unit,
    onSetMainPhoto: (NewAdImage) -> Unit
) {
    // Image Item
    Box(modifier = Modifier.padding(6.dp)) {
        ElevatedCard(modifier = Modifier
            .size(128.dp)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer)){

                // image itself
                AsyncImage(
                    model = image.uri,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )


                // options button
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.TopStart)
                ){

                    var menuExpanded by remember { mutableStateOf(false) }
                    FilledTonalIconButton(
                        onClick = {
                            menuExpanded = true
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

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {

                        DropdownMenuItem(
                            text = { Text("حذف") },
                            onClick = {
                                onDeletePhoto(image)
                                menuExpanded = false },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Clear,
                                    contentDescription = null
                                )
                            })
                        if (!image.isMainImage){
                            DropdownMenuItem(
                                text = { Text("تنظیم بعنوان عکس اصلی") },
                                onClick = {
                                    onSetMainPhoto(image)
                                    menuExpanded = false },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Check,
                                        contentDescription = null
                                    )
                                })
                        }
                    }
                }


                // main image label
                if (image.isMainImage){
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.55f))) {
                        Text(
                            text = stringResource(id = R.string.main_image),
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
}

@Composable
fun PriceSection(modifier: Modifier, price:String?, onPriceChanged:(String?)->Unit)
{
    Text(modifier=modifier, text = stringResource(id = R.string.new_ad_price),
        style = MaterialTheme.typography.titleMedium)

    Row(modifier = modifier,verticalAlignment = Alignment.CenterVertically){

        Row(modifier = Modifier.weight(0.4f),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.no_price), modifier = Modifier.padding(end = 8.dp))
            Switch(checked = price==null,
                onCheckedChange = {
                    onPriceChanged(if (it) null else "")
                })
        }
        Spacer(Modifier.weight(0.05f))

        PriceField(modifier = Modifier.weight(0.55f),
            value = price ?: "", onValueChange = onPriceChanged)

    }
}

@Composable
fun OkButton(modifier: Modifier, onOK: () -> Unit){
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Button(modifier = modifier.padding(horizontal = 32.dp),content = {
            Row(horizontalArrangement = Arrangement.Center){
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = stringResource(id = R.string.ok), style =  MaterialTheme.typography.titleMedium )
            }

        },onClick = onOK )
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickerBottomSheet(onDismiss: () -> Unit, onChooseGallery:()->Unit, onChooseCamera:()->Unit) {

    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {


        BottomSheetItem(iconDrawableId = R.drawable.ic_camera, title ="از دوربین", onClick = onChooseCamera)
        Divider()
        BottomSheetItem(iconDrawableId = R.drawable.ic_photo_library, title ="از گالری", onClick = onChooseGallery)

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberImagePickerState(onAddPhoto: (Uri) -> Unit, context: Context): ImagePickerState {

    val state = remember(Unit) {
        ImagePickerState()
    }

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    with (state){

        val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia())
        { uri ->
            uri?.let{ onAddPhoto(it) }
        }
        val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture())
        { success ->
            if (success)
                tempImageUri?.let{ onAddPhoto(it) }
            tempImageUri = null
        }

        if (takePictureFromCamera){
            if (cameraPermissionState.status.isGranted) {

                val uri = ComposeFileProvider.getImageUri(context)
                tempImageUri = uri
                cameraLauncher.launch(uri)
                takePictureFromCamera = false

            }else{
                if (cameraPermissionState.status.shouldShowRationale) {
                    takePictureFromCamera = false
                }else{
                    cameraPermissionState.launchPermissionRequest()
                }
            }
        }
        if (takePictureFromGallery){
            imagePickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
            takePictureFromGallery = false
        }


        if (showImagePickerBottomSheet) {

            ImagePickerBottomSheet(onChooseCamera = {

                takePictureFromCamera = true
                showImagePickerBottomSheet = false

            }, onChooseGallery = {
                takePictureFromGallery = true
                showImagePickerBottomSheet = false
            }, onDismiss = {
                showImagePickerBottomSheet = false
            })
        }
    }

    return state
}


class ImagePickerState internal constructor() {
    var showImagePickerBottomSheet by mutableStateOf(false)
    var takePictureFromCamera by mutableStateOf(false)
    var takePictureFromGallery by mutableStateOf(false)
    var tempImageUri by mutableStateOf<Uri?>(null)
}
