package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.databinding.FragmentNewAdBinding
import ir.m3hdi.agahinet.ui.adapter.NewAdImagesAdapter
import ir.m3hdi.agahinet.ui.theme.AppTheme
import ir.m3hdi.agahinet.ui.viewmodel.CitiesViewModel
import ir.m3hdi.agahinet.ui.viewmodel.NewAd
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Constants
import ir.m3hdi.agahinet.util.RtlLayout
import kotlinx.coroutines.launch

class NewAdFragment : Fragment() {

    private var _binding: FragmentNewAdBinding? = null
    private val binding get() = _binding!!

    private val newAdViewModel: NewAdViewModel by viewModels()
    private val citiesViewModel: CitiesViewModel by activityViewModels()

    private val imagesAdapter: NewAdImagesAdapter by lazy { NewAdImagesAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewAdBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.layoutContent.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NewAdScreen()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        setupViewModelObservers()

    }
    private fun setupUI()
    {

        /*binding.recyclerViewImages.layoutManager= GridLayoutManager(context,3).apply {
            orientation = GridLayoutManager.VERTICAL
        }
        binding.recyclerViewImages.adapter = imagesAdapter

        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
        (binding.textFieldCategory.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        (binding.textFieldProvince.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        (binding.textFieldCity.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)*/
    }

    private fun setupViewModelObservers(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                AppUtils.currentUser.collect {
                    AppUtils.handleNeedAuthFragment(it != null, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }
    }

    @Composable
    fun NewAdScreen(){
        AppTheme{
            val ad = newAdViewModel.newAd.collectAsStateWithLifecycle()
            NewAdForm(ad.value)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun NewAdForm(ad: NewAd){
        RtlLayout{
            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {

                val scrollState = rememberLazyListState()

                val modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                LazyColumn(state= scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {

                    item {
                        Spacer(Modifier.height(16.dp))
                        Text(modifier=modifier,text =stringResource(id = R.string.title_new_ad),
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Divider()
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween){
                            Text(text = stringResource(id = R.string.new_ad_category_label),
                                modifier = Modifier.align(Alignment.CenterVertically),
                                style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.width(32.dp))
                            MyDropDownMenu(
                                Modifier.align(Alignment.CenterVertically),
                                Constants.CATEGORIES.map { it.title }, ad.category) {
                                newAdViewModel.setCategory(it)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }

                    item{
                        MyOutlinedTextField(modifier = modifier, maxLines = 1,
                            label = stringResource(id = R.string.new_ad_title), value = ad.title) {
                            newAdViewModel.setTitle(it)
                        }
                        Spacer(Modifier.height(8.dp))
                        MyOutlinedTextField(modifier = modifier, minLines = 4, maxLines = 4,
                            label = stringResource(id = R.string.new_ad_description), value = ad.description) {
                            newAdViewModel.setDescription(it)
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        Text(modifier=modifier, text =stringResource(id = R.string.new_ad_images),
                            style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        GridList(modifier = modifier) {

                        }
                        Spacer(Modifier.height(16.dp))
                    }
                    item {

                        Text(modifier=modifier, text =stringResource(id = R.string.new_ad_price),
                            style = MaterialTheme.typography.titleMedium)

                        Row(modifier = modifier,verticalAlignment = Alignment.CenterVertically){

                            Row(modifier = Modifier
                                .weight(0.35f)
                                .align(Alignment.CenterVertically),verticalAlignment = Alignment.CenterVertically) {
                                Text(text = stringResource(id = R.string.no_price),
                                modifier = Modifier.padding(end = 8.dp))
                                Switch(checked = ad.price==null,
                                    onCheckedChange = {
                                        newAdViewModel.setPrice(if (it) null else "")
                                    })
                            }
                            Spacer(Modifier.weight(0.05f))
                            MyOutlinedTextField(modifier = Modifier
                                .weight(0.55f)
                                .align(Alignment.CenterVertically)
                                .padding(bottom = 8.dp),maxLines = 1, label = stringResource(id = R.string.price),
                                value = ad.price.toString(), onValueChange = {
                                    newAdViewModel.setPrice(it)
                                })

                        }
                    }

                    item {
                        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                            MyDropDownMenu(
                                modifier = Modifier.weight(0.45f),
                                items = listOf("", ""),
                                selectedItemPosition = 0,
                                onItemSelected = {}
                            )

                            Spacer(modifier = Modifier.weight(0.1f))

                            MyDropDownMenu(
                                modifier = Modifier.weight(0.45f),
                                items = listOf("", ""),
                                selectedItemPosition = 0,
                                onItemSelected = {}
                            )
                        }
                    }
                    item{
                        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {}, content = {
                                Text(text = stringResource(id = R.string.ok) )
                            })
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                    }

                }

            }

        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun GridList(modifier: Modifier, onItemClick: () -> Unit) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement =Arrangement.SpaceAround,
            content = {
                listOf(0,1,2,3,4,5).forEach {
                    GridItem(it,onItemClick)
                }
            }
        )
    }

    @Composable
    fun GridItem(item: Int, onItemClick:()->Unit) {
        Box(modifier = Modifier.padding(vertical = 6.dp)) {
            ElevatedCard(modifier = Modifier
                .size(128.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)){

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))) {
                        Text(
                            text = stringResource(id = R.string.main_image),
                            color=Color.White,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .align(Alignment.Center)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {  onItemClick() },
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
                }
            }
        }

    }


    @Composable
    fun MyOutlinedTextField(modifier: Modifier=Modifier,minLines:Int=1,maxLines:Int=1,label:String,value:String,onValueChange:(String)->Unit){
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            modifier=modifier,
            minLines = minLines,
            maxLines=maxLines,
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = {
                        onValueChange("")
                    }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }
            },
            label = { Text(text = label) },
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyDropDownMenu(modifier: Modifier, items:List<String>, selectedItemPosition:Int?, onItemSelected:(String)->Unit){
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            },
            modifier = modifier
        ) {
            val title = selectedItemPosition?.let {
                items[it]
            }?: ""
            Crossfade(targetState = title) {
                OutlinedTextField(
                    value = it ,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("دسته بندی...")
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
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
                        Text(text = selectedItem)
                    })
                }
            }
        }
    }

    // *******************

    @Preview(showSystemUi = false, showBackground = true, device = "id:Galaxy Nexus")
    @Composable
    fun PreviewNewAdScreen(){
        AppTheme{
            val ad = NewAd()
            NewAdForm(ad)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}