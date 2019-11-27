package com.nerdstone.neatformcore.views.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import com.nerdstone.neatformcore.domain.data.DataActionListener
import com.nerdstone.neatformcore.domain.model.NFormFieldValidation
import com.nerdstone.neatformcore.domain.model.NFormViewDetails
import com.nerdstone.neatformcore.domain.model.NFormViewProperty
import com.nerdstone.neatformcore.domain.view.NFormView
import com.nerdstone.neatformcore.domain.view.RootView
import com.nerdstone.neatformcore.utils.ViewUtils
import com.nerdstone.neatformcore.views.builders.TextInputEditTextBuilder
import com.nerdstone.neatformcore.views.handlers.ViewDispatcher
import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.jeasy.rules.core.DefaultRulesEngine
import org.jeasy.rules.mvel.MVELRule

class TextInputEditTextNFormView : TextInputLayout, NFormView {

    override lateinit var viewProperties: NFormViewProperty
    override var dataActionListener: DataActionListener? = null
    override val viewBuilder = TextInputEditTextBuilder(this)
    override var viewDetails = NFormViewDetails(this)
    override val nFormRootView get() = this.parent as RootView

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun initView(viewProperty: NFormViewProperty, viewDispatcher: ViewDispatcher)
            : NFormView {
        ViewUtils.setupView(this, viewProperty, viewDispatcher)
        return this
    }


    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        resetValueWhenHidden()
    }

    override fun resetValueWhenHidden() {
        if (visibility == View.GONE) {
            editText?.setText("")
        }
    }

    private fun validate(validation: NFormFieldValidation): Boolean {
        val facts = Facts()
        facts.put("value", editText?.text.toString())

        // define rules
        val customRule: Rule = MVELRule()
            .name(validation.name)
            .description(validation.name)
            .`when`(validation.condition)
            .then("value = true")

        val rules = Rules(customRule)

        val rulesEngine = DefaultRulesEngine()
        rulesEngine.fire(rules, facts)

        if (facts.get<Boolean>("value"))
            return true

        this.error = validation.errorMessage
        return false
    }

    override fun validaValues(): Boolean {
        if (viewProperties.validations != null) {
            viewProperties.validations?.forEach { validation ->
                if (!validate(validation))
                    return false
            }
        }

        return true
    }
}