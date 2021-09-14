package com.supervpn.fast.vpn.free.proxy.unblock.presentation.auth

import android.view.View
import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.dialog.ConfirmDialogFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.Util
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import javax.inject.Inject

class ForgotPasswordFragment : BaseFragment<ForgotPasswordView, ForgotPasswordPresenter>(), ForgotPasswordView {

    @Inject
    lateinit var presenter: ForgotPasswordPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<ForgotPasswordView>? {
        return presenter
    }

    override fun viewIF(): ForgotPasswordView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun initView() {
        inputAddress.onTextChanged = {
            toggleButton()
        }

        tvBottomAction.apply {
            setOnClickListener(this@ForgotPasswordFragment::handleForgotPassword)
            toggleButton()
        }
    }

    private fun handleForgotPassword(view: View?) {
        val email = inputAddress.text
        if (!Util.validateEmail(email)) {
            inputAddress.error = true
            snackBar.show(getString(R.string.message_invalid_email))
            return
        }
        snackBar.hide()
        presenter.forgotPassword(email)
    }

    private fun toggleButton() {
        val email = inputAddress.text
        val disableButton = email.isEmpty()
        tvBottomAction.isActivated = !disableButton
        tvBottomAction.isClickable = !disableButton
    }

    override fun onForgotPasswordSuccess() {
        context?.let {
            ConfirmDialogFragment.resetPassword(it, childFragmentManager)
        }
    }
}
