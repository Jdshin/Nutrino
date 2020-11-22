package edu.utap.nutrino.glide

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.ImageView
import androidx.core.content.res.TypedArrayUtils.getString
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.fitCenter
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import edu.utap.nutrino.R

@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

object Glide {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private val backupImageURL = "https://2.bp.blogspot.com/-tzm1twY_ENM/XlCRuI0ZkRI/AAAAAAAAOso/BmNOUANXWxwc5vwslNw3WpjrDlgs9PuwQCLcBGAsYHQ/s1600/pasted%2Bimage%2B0.png"
    private var glideOptions = RequestOptions
        .fitCenterTransform()
        .transform(RoundedCorners(20))

    // Might possibly need fromHtml like from Reddit HW, not sure what it does though, probably cleans the URL
    fun glideFetch(urlString: String, imageView: ImageView) {
        Glide.with(imageView.context)
            .asBitmap()
            .load(urlString)
            .apply(glideOptions)
            .error(R.color.design_default_color_error)
            .override(width, height)
            .error(
                Glide.with(imageView.context)
                    .asBitmap()
                    .load(backupImageURL)
                    .apply(glideOptions)
                    .error(R.color.design_default_color_error)
                    .override(500, 500)
            )
            .into(imageView)
    }
}