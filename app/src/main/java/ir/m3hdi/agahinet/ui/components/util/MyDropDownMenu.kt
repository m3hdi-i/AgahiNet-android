package ir.m3hdi.agahinet.ui.components.util

import androidx.compose.animation.Crossfade
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.domain.model.ad.Category

sealed class DropDownData(val value:Any) {
    data class CategoryItem(val catItem: Category) : DropDownData(catItem)
    data class CityItem(val cityItem: City) : DropDownData(cityItem)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropDownMenu(modifier: Modifier, label: String, items:List<DropDownData>, selectedItem: DropDownData?, onItemSelected:(DropDownData)->Unit){
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier,
    ) {

        val title = selectedItem?.value?.toString() ?: ""

        Crossfade(targetState = title) {
            CompositionLocalProvider(LocalTextInputService provides null) {
                OutlinedTextField(
                    value = it,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = label) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor(),
                )
            }


        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { selectedItem ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onItemSelected(selectedItem)
                }, text = {
                    Text(text = selectedItem.value.toString())
                })
            }
        }
    }
}
