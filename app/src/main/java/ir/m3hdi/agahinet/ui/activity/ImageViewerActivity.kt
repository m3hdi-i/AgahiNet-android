package ir.m3hdi.agahinet.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import ir.m3hdi.agahinet.databinding.ActivityImageViewerBinding
import ir.m3hdi.agahinet.ui.adapter.ZoomableImagesAdapter
import kotlinx.coroutines.launch

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringArrayListExtra("images")?.let {
            val imagesAdapter=ZoomableImagesAdapter(it)
            binding.viewPagerImages.adapter=imagesAdapter
            binding.dotsIndicator.attachTo(binding.viewPagerImages)
            binding.dotsIndicator.isVisible=true
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

    }
}