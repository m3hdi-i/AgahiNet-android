package ir.m3hdi.agahinet.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import ir.m3hdi.agahinet.ui.components.CategorySection
import ir.m3hdi.agahinet.ui.components.HeaderSection
import ir.m3hdi.agahinet.ui.components.ImagesSection
import ir.m3hdi.agahinet.ui.components.LocationSection
import ir.m3hdi.agahinet.ui.components.MyOutlinedTextField
import ir.m3hdi.agahinet.ui.components.OkButton
import ir.m3hdi.agahinet.ui.components.PriceSection
import ir.m3hdi.agahinet.ui.components.RtlLayout
import ir.m3hdi.agahinet.ui.components.SpacerV
import ir.m3hdi.agahinet.ui.components.recomposeHighlighter
import ir.m3hdi.agahinet.ui.components.rememberImagePickerState
import ir.m3hdi.agahinet.ui.theme.AppTheme
import ir.m3hdi.agahinet.ui.viewmodel.NewAdImage
import ir.m3hdi.agahinet.ui.viewmodel.NewAdViewModel
import ir.m3hdi.agahinet.ui.viewmodel.UiState
import ir.m3hdi.agahinet.util.AppUtils
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
                    AppUtils.handleNeedAuthFragment(true, childFragmentManager, binding.layoutParent, binding.layoutNeedAuth, binding.layoutContent)
                }
            }
        }
    }



    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun NewAdForm(state:UiState){

        RtlLayout {
            CompositionLocalProvider(LocalOverscrollConfiguration provides null) {

                val modifier: Modifier = remember {
                    Modifier
                        .recomposeHighlighter()
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                }

                val onAddImage :(Uri)->Unit = { viewModel.addImage(it) }
                val imagePickerState = rememberImagePickerState(onAddImage,requireContext())

                // New ad inputs
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
                    SpacerV(modifier = modifier,height=16.dp)

                    // images
                    val showImagePicker:()->Unit = remember { { imagePickerState.showImagePickerBottomSheet = true} }
                    val onDeletePhoto:(NewAdImage)->Unit = remember { { viewModel.deleteImage(it) } }
                    val onSetMainPhoto:(NewAdImage)->Unit = remember { { viewModel.setMainImage(it) } }
                    ImagesSection(modifier = modifier,images = state.imagesList,
                        onAddPhoto = showImagePicker,
                        onDeletePhoto = onDeletePhoto,
                        onSetMainPhoto = onSetMainPhoto)

                    // price
                    val onPriceChanged:(String?)->Unit = remember{ { viewModel.setPrice(it) } }
                    PriceSection(modifier = modifier,price=state.price, onPriceChanged = onPriceChanged)


                    // location (province and city)
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
            RtlLayout {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
