package ir.m3hdi.agahinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ir.m3hdi.agahinet.R
import ir.m3hdi.agahinet.data.local.entity.City
import ir.m3hdi.agahinet.databinding.FragmentNewAdBinding
import ir.m3hdi.agahinet.domain.model.Category
import ir.m3hdi.agahinet.ui.components.DropDownData
import ir.m3hdi.agahinet.ui.components.MyDropDownMenu
import ir.m3hdi.agahinet.ui.components.MyOutlinedTextField
import ir.m3hdi.agahinet.ui.components.PriceField
import ir.m3hdi.agahinet.ui.components.SpacerV
import ir.m3hdi.agahinet.ui.components.recomposeHighlighter
import ir.m3hdi.agahinet.ui.theme.AppTheme
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel
import ir.m3hdi.agahinet.ui.viewmodel.UiState
import ir.m3hdi.agahinet.util.AppUtils
import ir.m3hdi.agahinet.util.Constants
import ir.m3hdi.agahinet.util.RtlLayout
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NewAdFragment : Fragment() {

    private var _binding: FragmentNewAdBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewAdViewModel by viewModels()

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                AppUtils.currentUser.collect {
                    AppUtils.handleNeedAuthFragment(it != null, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }

    }




    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun NewAdForm(state:UiState){


        RtlLayout{
            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {

                //val scrollState = rememberLazyListState()

                val modifier:Modifier = remember {
                    Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                }

                Column(modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                ) {

                    // header
                    HeaderSection(modifier)

                    // category
                    val onCategoryChanged:(Category)->Unit = remember { { viewModel.setCategory(it) } }
                    CategorySection(modifier = modifier,category = state.category, onCategoryChanged = onCategoryChanged)

                    // title and description
                    val onTitleChanged:(String)->Unit = remember { { viewModel.setTitle(it) } }
                    val onDescriptionChanged:(String)->Unit = remember { { viewModel.setDescription(it) } }

                    MyOutlinedTextField(modifier = modifier,
                        label = stringResource(id = R.string.new_ad_title), value = state.title, maxLength = 50
                    , onValueChange = onTitleChanged)
                    SpacerV(modifier = modifier,8.dp)
                    MyOutlinedTextField(modifier = modifier, multiLine = true,
                        label = stringResource(id = R.string.new_ad_description), value = state.description, onValueChange = onDescriptionChanged)
                    SpacerV(modifier = modifier,16.dp)


                    // images
                    Text(modifier=modifier, text =stringResource(id = R.string.new_ad_images),
                        style = MaterialTheme.typography.titleMedium)
                    SpacerV(modifier = modifier, height = 4.dp)
                    GridList(modifier = modifier) {

                    }
                    Spacer(Modifier.height(16.dp))

                    // price
                    val onPriceChanged:(String?)->Unit = remember{ { viewModel.setPrice(it) } }
                    PriceSection(modifier = modifier,price=state.price, onPriceChanged = onPriceChanged)


                    // province and city
                    val onProvinceChanged:(City)->Unit = remember { { viewModel.setProvince(it) } }
                    val onCityChanged:(City)->Unit = remember { { viewModel.setCity(it) } }

                    LocationSection(modifier = modifier, city = state.city,
                        allProvinces = state.allProvinces ,onCityChanged = onCityChanged, province = state.province,
                        onProvinceChanged = onProvinceChanged, citiesToSelect = state.citiesToSelect)


                    // OK button
                    OkButton(modifier=modifier, onOK = {
                        println("ok")
                    })

                }

            }

        }
    }

    @Composable
    fun LocationSection(
        modifier: Modifier,
        allProvinces:ImmutableList<City>,
        province:City?,onProvinceChanged:(City)->Unit,
        city:City?,onCityChanged:(City)->Unit,
        citiesToSelect:ImmutableList<City>
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
    fun HeaderSection(modifier:Modifier) {
        Spacer(Modifier.height(16.dp))
        Text(modifier=modifier,text =stringResource(id = R.string.title_new_ad),
            style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Divider()
    }

    @Composable
    private fun CategorySection(modifier: Modifier,category: Category?,onCategoryChanged:(Category)->Unit) {
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


    @Composable
    fun PriceSection(modifier:Modifier,price:String?,onPriceChanged:(String?)->Unit)
    {
        Text(modifier=modifier, text =stringResource(id = R.string.new_ad_price),
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
    fun OkButton(modifier:Modifier,onOK: () -> Unit){
        Column(modifier = modifier.recomposeHighlighter(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(modifier = Modifier.padding(horizontal = 32.dp),content = {
                Row(modifier = modifier, horizontalArrangement = Arrangement.Center){
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


    // *******************


    @Composable
    fun NewAdScreen(){
        AppTheme{
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            NewAdForm(uiState)
        }
    }

    // *******************

    @Preview(showSystemUi = false, showBackground = true, device = "id:Galaxy Nexus")
    @Composable
    fun PreviewNewAdScreen(){
        AppTheme{
            val ad = UiState()
            NewAdForm(ad)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}