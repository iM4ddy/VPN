package com.supervpn.fast.vpn.free.proxy.unblock.presentation.other.policy

import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import javax.inject.Inject

class PrivatePolicyFragment : BaseFragment<PrivatePolicyView, PrivatePolicyPresenter>(), PrivatePolicyView {

    @Inject
    lateinit var presenter: PrivatePolicyPresenter

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<PrivatePolicyView>? {
        return presenter
    }

    override fun viewIF(): PrivatePolicyView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_private_policy
    }

    override fun initView() {
    }
}
