@file:Suppress("WildcardImport")

package io.github.achmadhafid.sample_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.florent37.viewanimator.ViewAnimator
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.switchmaterial.SwitchMaterial
import com.ramotion.fluidslider.FluidSlider
import io.github.achmadhafid.lottie_dialog.*
import io.github.achmadhafid.lottie_dialog.model.LottieDialogTheme
import io.github.achmadhafid.lottie_dialog.model.LottieDialogType
import io.github.achmadhafid.simplepref.extension.clearLocalPref
import io.github.achmadhafid.simplepref.extension.liveDataPref
import io.github.achmadhafid.simplepref.extension.simplePref
import io.github.achmadhafid.zpack.ktx.d
import io.github.achmadhafid.zpack.ktx.resolveColor
import io.github.achmadhafid.zpack.ktx.toastShort
import kotlin.math.floor

class LoadingDialogFragment : Fragment(R.layout.fragment_loading_dialog) {

    //region Resource Binding

    private val maxTimeout by lazy { resources.getInteger(R.integer.loading_timeout).toLong() }

    //endregion
    //region Preference

    private var typeDialog by simplePref(isLocal = true) { true }
    private var typeBottomSheet by simplePref(isLocal = true) { false }
    private var themeDayNight by simplePref(isLocal = true) { true }
    private var themeLight by simplePref(isLocal = true) { false }
    private var themeDark by simplePref(isLocal = true) { false }
    private var useCustomText by simplePref(isLocal = true) { false }
    private var cancelOnBackPressed by simplePref(isLocal = true) { true }
    private var cancelOnTouchOutside by simplePref(isLocal = true) { true }
    private var timeoutValue by simplePref(isLocal = true) { maxTimeout }
    private var showTimeoutProgressView by simplePref(isLocal = true) { true }

    //endregion

    //region Lifecycle Callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_action_bar, menu)
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //region bind views

        val tglGroupDialogType: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_dialog_type)
        val btnDialogTypeDialog: MaterialButton = view.findViewById(R.id.btn_dialog_type_dialog)
        val btnDialogTypeBottomSheet: MaterialButton = view.findViewById(R.id.btn_dialog_type_bottom_sheet)
        val tglGroupTheme: MaterialButtonToggleGroup = view.findViewById(R.id.toggle_button_group_theme)
        val btnThemeDayNight: MaterialButton = view.findViewById(R.id.btn_theme_day_night)
        val btnThemeLight: MaterialButton = view.findViewById(R.id.btn_theme_light)
        val btnThemeDark: MaterialButton = view.findViewById(R.id.btn_theme_dark)
        val smUseCustomText: SwitchMaterial = view.findViewById(R.id.sm_useCustomText)
        val smCancelOnBackPressed: SwitchMaterial = view.findViewById(R.id.sm_cancelOnBackPressed)
        val smCancelOnTouchOutside: SwitchMaterial = view.findViewById(R.id.sm_cancelOnTouchOutside)
        val tvActionTimeout: TextView = view.findViewById(R.id.tv_actionTimeout)
        val sbActionTimeout: FluidSlider = view.findViewById(R.id.sb_actionTimeout)
        val smShowTimeoutProgress: SwitchMaterial = view.findViewById(R.id.sm_showTimeoutProgress)

        //endregion

        //region setup dialog type

        tglGroupDialogType.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                btnDialogTypeDialog.id -> typeDialog = isChecked
                btnDialogTypeBottomSheet.id -> typeBottomSheet = isChecked
            }
        }
        liveDataPref(::typeDialog) {
            btnDialogTypeDialog.apply {
                isChecked = it
                isCheckable = !it
            }
        }
        liveDataPref(::typeBottomSheet) {
            btnDialogTypeBottomSheet.apply {
                isChecked = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup theme

        tglGroupTheme.addOnButtonCheckedListener { _, id, isChecked ->
            when (id) {
                btnThemeDayNight.id -> themeDayNight = isChecked
                btnThemeLight.id -> themeLight = isChecked
                btnThemeDark.id -> themeDark = isChecked
            }
        }
        liveDataPref(::themeDayNight) {
            btnThemeDayNight.apply {
                isChecked = it
                isCheckable = !it
            }
        }
        liveDataPref(::themeLight) {
            btnThemeLight.apply {
                isChecked = it
                isCheckable = !it
            }
        }
        liveDataPref(::themeDark) {
            btnThemeDark.apply {
                isChecked = it
                isCheckable = !it
            }
        }

        //endregion
        //region setup custom text

        smUseCustomText.apply {
            setOnCheckedChangeListener { _, isChecked ->
                useCustomText = isChecked
            }
            liveDataPref(::useCustomText) {
                isChecked = it
            }
        }

        //endregion
        //region setup cancel options

        smCancelOnBackPressed.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnBackPressed = isChecked
            }
            liveDataPref(::cancelOnBackPressed) {
                isChecked = it
            }
        }

        smCancelOnTouchOutside.apply {
            setOnCheckedChangeListener { _, isChecked ->
                cancelOnTouchOutside = isChecked
            }
            liveDataPref(::cancelOnTouchOutside) {
                isChecked = it
            }
        }

        //endregion
        //region setup timeout

        @Suppress("MagicNumber")
        with(sbActionTimeout) {
            colorBubble     = context.resolveColor(R.attr.colorPrimary)
            colorBubbleText = context.resolveColor(R.attr.colorOnPrimary)
            colorBar        = colorBubble

            val partition = 100
            fun posValue() = (floor(maxTimeout * position / partition) * partition).toLong()

            positionListener = {
                bubbleText = "${posValue()}"
            }

            val height by lazy { tvActionTimeout.height.toFloat() }
            val animation by lazy { ViewAnimator.animate(tvActionTimeout) }

            beginTrackingListener = {
                animation.translationY(0F, -height)
                    .alpha(1F, 0F)
                    .duration(200L)
                    .start()
            }
            endTrackingListener = {
                animation.translationY(-height, 0F)
                    .alpha(0F, 1F)
                    .duration(400L)
                    .start()
                timeoutValue = posValue()
            }

            liveDataPref(::timeoutValue) {
                position   = timeoutValue / maxTimeout.toFloat()
                bubbleText = "$timeoutValue"
            }
        }

        smShowTimeoutProgress.apply {
            setOnCheckedChangeListener { _, isChecked ->
                showTimeoutProgressView = isChecked
            }
            liveDataPref(::showTimeoutProgressView) {
                isChecked = it
            }
        }

        //endregion
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_dialog -> {
                lottieLoadingDialog(BaseDialog.doSomething) {
                    //region setup type
                    when {
                        typeDialog      -> type = LottieDialogType.DIALOG
                        typeBottomSheet -> type = LottieDialogType.BOTTOM_SHEET
                    }
                    //endregion
                    //region setup theme
                    when {
                        themeDayNight -> theme = LottieDialogTheme.DAY_NIGHT
                        themeLight    -> theme = LottieDialogTheme.LIGHT
                        themeDark     -> theme = LottieDialogTheme.DARK
                    }
                    //endregion
                    //region setup timeout
                    timeout = timeoutValue
                    showTimeOutProgress = showTimeoutProgressView
                    //endregion
                    //region setup animation
                    withAnimation {
                        fileRes        = R.raw.lottie_animation_loading
                        animationSpeed = 2f
                    }
                    //endregion
                    //region setup title
                    withTitle {
                        text = "Please wait..."
                        if (useCustomText) {
                            fontRes  = R.font.lobster_two_bold
                            styleRes = R.style.AppTheme_TextAppearance
                        }
                    }
                    //endregion
                    //region setup cancel options
                    withCancelOption {
                        onBackPressed  = cancelOnBackPressed
                        onTouchOutside = cancelOnTouchOutside
                    }
                    //endregion
                    //region setup listener
                    onCancel {
                        d("loading dialog canceled")
                        toastShort("You canceled the dialog")
                    }
                    onTimeout {
                        toastShort("Timeout")
                    }
                    //endregion
                }
                true
            }
            R.id.action_reset_preferences -> {
                clearLocalPref()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //endregion

}
