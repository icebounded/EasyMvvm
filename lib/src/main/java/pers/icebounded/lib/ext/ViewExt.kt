package pers.icebounded.lib.ext

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.Px
import androidx.annotation.RequiresApi

/**
 * Created by Andy
 * on 2021/3/5
 */
/**
 * Set view visible
 */
fun View.visible() {
    visibility = View.VISIBLE
}

/**
 * Set view invisible
 */
fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Set view gone
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.isInvisible(): Boolean {
    return visibility == View.INVISIBLE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

/**
 * Reverse the view's visibility
 */
fun View.reverseVisibility(needGone: Boolean = true) {
    if (isVisible()) {
        if (needGone) gone() else invisible()
    } else {
        visible()
    }
}

/**
 * Set padding
 * @param size top, bottom, left, right padding are same
 */
fun View.setPadding(@Px size: Int) {
    setPadding(size, size, size, size)
}

/**
 * Set margin
 * @param size top, bottom, left, right padding are same
 */
fun View.setMargin(@Px size: Int) {
    setMargin(size, size, size, size)
}

fun View.setMargin(@Px l: Int, @Px t: Int, @Px r: Int, @Px b: Int) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        (layoutParams as ViewGroup.MarginLayoutParams).apply {
            leftMargin = l
            topMargin = t
            rightMargin = r
            bottomMargin = b
        }
    }
}

/**
 * Causes the Runnable which contains action() to be added to the message queue, to be run
 * after the specified amount of time elapses.
 * The runnable will be run on the user interface thread
 *
 * @param action Will be invoked in the Runnable
 * @param delayInMillis The delay (in milliseconds) until the action() will be invoked
 */
inline fun View.postDelayed(delayInMillis: Long, crossinline action: () -> Unit): Runnable {
    val runnable = Runnable { action() }
    postDelayed(runnable, delayInMillis)
    return runnable
}

@Deprecated("use View.drawToBitmap()")
fun View.toBitmap(scale: Float = 1f, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    if (this is ImageView) {
        if (drawable is BitmapDrawable) return (drawable as BitmapDrawable).bitmap
    }
    this.clearFocus()
    val bitmap = createBitmapSafely((width * scale).toInt(), (height * scale).toInt(), config, 1)
    if (bitmap != null) {
        Canvas().run {
            setBitmap(bitmap)
            save()
            drawColor(Color.WHITE)
            scale(scale, scale)
            this@toBitmap.draw(this)
            restore()
            setBitmap(null)
        }
    }
    return bitmap
}

fun createBitmapSafely(width: Int, height: Int, config: Bitmap.Config, retryCount: Int): Bitmap? {
    try {
        return Bitmap.createBitmap(width, height, config)
    } catch (e: OutOfMemoryError) {
        e.printStackTrace()
        if (retryCount > 0) {
            System.gc()
            return createBitmapSafely(width, height, config, retryCount - 1)
        }
        return null
    }

}

/**
 * Register a callback to be invoked when the global layout state or the visibility of views
 * within the view tree changes
 *
 * @param callback The callback() to be invoked
 */
inline fun View.onGlobalLayout(crossinline callback: () -> Unit) = with(viewTreeObserver) {
    addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            removeOnGlobalLayoutListener(this)
            callback()
        }
    })
}

/**
 * Register a callback to be invoked after the view is measured
 *
 * @param callback The callback() to be invoked
 */
inline fun View.afterMeasured(crossinline callback: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                callback()
            }
        }
    })
}

fun View.throttleClick(block: View.() -> Unit) = setOnClickListener(ThrottleClickListener(block))

class ThrottleClickListener(
    private val block: View.() -> Unit,
    private val interval: Long = 500
) : View.OnClickListener {
    private var lastTime: Long = 0
    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > interval) {
            lastTime = currentTime
            block(v)
        }
    }
}

inline fun View.longClick(crossinline block: View.() -> Unit) = setOnClickListener { block() }