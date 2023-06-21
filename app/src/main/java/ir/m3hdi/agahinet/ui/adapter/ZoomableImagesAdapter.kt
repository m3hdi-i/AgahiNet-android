package ir.m3hdi.agahinet.ui.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.launch

/**
 *
 *  A recyclerView adapter to create with zoomable images slider with viewPager2
 *  source: https://github.com/MikeOrtiz/TouchImageView/blob/master/app/src/main/java/info/touchimage/demo/custom/AdapterImages.kt
 *
 */
class ZoomableImagesAdapter(private val images: List<String>) : RecyclerView.Adapter<ZoomableImagesAdapter.ViewHolder>() {

    override fun getItemCount() = images.size

    class ViewHolder(view: TouchImageView) : RecyclerView.ViewHolder(view) {
        val imagePlace = view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TouchImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)

            setOnTouchListener { view, event ->
                var result = true
                //can scroll horizontally checks if there's still a part of the image
                //that can be scrolled until you reach the edge
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && canScrollHorizontally(-1)) {
                    //multi-touch event
                    result = when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            // Disallow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(true)
                            // Disable touch on view
                            false
                        }
                        MotionEvent.ACTION_UP -> {
                            // Allow RecyclerView to intercept touch events.
                            parent.requestDisallowInterceptTouchEvent(false)
                            true
                        }
                        else -> true
                    }
                }
                result
            }
        })
    }

    override fun getItemViewType(i: Int): Int {
        return 0
    }
    override fun getItemId(i: Int): Long {
        return i.toLong()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val context= holder.itemView.context
        var disposable:Disposable?=null

        val request = ImageRequest.Builder(context)
            .data(images[position])
            .target { drawable ->
                // Handle the result.
                holder.imagePlace.setImageDrawable(drawable)
                disposable?.dispose()
            }
            .build()

        disposable = context.imageLoader.enqueue(request)

    }


}