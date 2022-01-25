package com.nerdstone.neatformcore.views.widgets

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat.getColor
import com.nerdstone.neatformcore.R
import com.nerdstone.neatformcore.domain.listeners.DataActionListener
import com.nerdstone.neatformcore.domain.listeners.VisibilityChangeListener
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.domain.view.FormValidator
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.utils.handleRequiredStatus
import com.nerdstone.neatformcore.utils.removeAsterisk
import com.nerdstone.neatformcore.utils.setReadOnlyState
import com.nerdstone.neatformcore.views.builders.MaskedEditTextViewBuilder
import com.nerdstone.neatformcore.views.handlers.ViewVisibilityChangeHandler
import com.rengwuxian.materialedittext.MaterialEditText
import com.softmed.masked.MaskedEditText

class MaskedEditTextNFormView : MaskedEditText, NFormView {

    override lateinit var viewProperties: NFormViewProperty
    override lateinit var formValidator: FormValidator
    override var dataActionListener: DataActionListener? = null
    override var visibilityChangeListener: VisibilityChangeListener? = ViewVisibilityChangeHandler
    override val viewBuilder = MaskedEditTextViewBuilder(this)
    override var viewDetails = NFormViewDetails(this)
    override var initialValue: Any? = null

    constructor(context: Context) : super(context,null){
        setFloatingLabel(MaterialEditText.FLOATING_LABEL_NORMAL)
        floatingLabelTextSize = resources.getDimension(R.dimen.default_text_size).toInt()
        setHintTextColor(getColor(context,R.color.colorRed))
        floatingLabelTextColor = getColor(context,R.color.design_default_color_on_secondary)
        invalidate()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun onTextChanged(
        text: CharSequence, start: Int, lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        this.dataActionListener?.also {
            if (rawText.isNotEmpty()) {
                this.viewDetails.value =
                    text.toString().removeAsterisk()

            } else {
                this.viewDetails.value = null
            }
            it.onPassData(this.viewDetails)
        }
    }


    override fun resetValueWhenHidden() = setText("")

    override fun trackRequiredField() = handleRequiredStatus()

    override fun validateValue(): Boolean {
        val validationPair = formValidator.validateField(this)
        if (!validationPair.first) {
            this.error = validationPair.second
            error = validationPair.second
            isAutoValidate = true
            errorColor = getColor(context,R.color.colorRed)
            floatingLabelTextColor = getColor(context,R.color.colorRed)

        } else {
            this.error = null
            floatingLabelTextColor = getColor(context,R.color.colorDeepBlack)
        }
        return validationPair.first
    }

    override fun setValue(value: Any, enabled: Boolean) {
        initialValue = value
        setText(value.toString())
        setReadOnlyState(enabled)
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        visibilityChangeListener?.onVisibilityChanged(this, visibility)
    }
}