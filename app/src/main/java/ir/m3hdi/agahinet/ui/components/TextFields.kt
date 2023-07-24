package ir.m3hdi.agahinet.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.util.AppUtils


@Composable
fun MyOutlinedTextField(modifier: Modifier = Modifier, multiLine:Boolean=false, maxLength:Int?=null, label:String, value:String, onValueChange:(String)->Unit){

    val lines = if (multiLine) 4 else 1

    val supportingText:@Composable (() -> Unit)? = maxLength?.let {
        { Text(text = "$it / ${value.length}") }
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (maxLength == null || it.length <= maxLength) {
                onValueChange(it)
            }
        },
        modifier=modifier,
        singleLine = !multiLine,
        minLines = lines,
        maxLines= lines,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(Icons.Filled.Clear,contentDescription = null)
                }
            }
        },
        supportingText = supportingText,
        label = { Text(text = label) },
    )
}

@Composable
fun PriceField(modifier: Modifier = Modifier, value:String, onValueChange:(String)->Unit){

    val supportingText = AppUtils.priceToPersianCurrencyLetters(value)

    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length<=18)
                onValueChange(it)
        },
        modifier=modifier,
        singleLine = true,
        textStyle = TextStyle(textAlign = TextAlign.Center, textDirection = TextDirection.Ltr),
        label = { Text(text = stringResource(id = R.string.price)) },
        suffix = {
            Text(text = stringResource(id = R.string.currency_suffix))
        },
        supportingText = {
            Text(text = supportingText)
        },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done)
    )
}

