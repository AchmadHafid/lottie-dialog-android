@file:Suppress("WildcardImport")

package io.github.achmadhafid.lottie_dialog.model

import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatDialog
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.zpack.ktx.*

data class LottieDialogAnimation internal constructor(
    @RawRes
    var fileRes: Int? = null,
    @ColorRes @AttrRes
    var bgColorRes: Int? = null,
    @DimenRes
    var paddingRes: Int? = null,
    var animationSpeed: Float = 1f,
    @LottieDrawable.RepeatMode
        var repeatMode: Int = LottieDrawable.RESTART,
    var showCloseButton: Boolean = true,
    @ColorRes @AttrRes
    var closeButtonColorRes: Int? = null
) {
    internal operator fun invoke(
        animationView: LottieAnimationView,
        btnClose: ImageButton? = null,
        dialog: AppCompatDialog,
        type: LottieDialogType
    ) {
        fileRes?.let {
            animationView.show()
            animationView.setAnimation(it)
            animationView.speed      = animationSpeed
            animationView.repeatMode = repeatMode
        } ?: run {
            animationView.gone()
            btnClose?.gone()
            return
        }

        bgColorRes?.let {
            animationView.setBackgroundColorRes(it)
            if (type == LottieDialogType.BOTTOM_SHEET) {
                animationView.makeRoundedCornerOnTop(R.dimen.lottie_dialog_corner_radius_bottom_sheet)
            }
        }
        paddingRes?.let {
            animationView.setPaddingRes(it)
        }

        btnClose?.visibleOrGone { showCloseButton }
        btnClose?.apply {
            visibleOrGone { showCloseButton }
            closeButtonColorRes?.let { setImageTintListRes(it) }
            onSingleClick { dialog.cancel() }
        }
    }
}
