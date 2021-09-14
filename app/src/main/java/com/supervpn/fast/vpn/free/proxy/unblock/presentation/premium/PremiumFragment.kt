package com.supervpn.fast.vpn.free.proxy.unblock.presentation.premium

import com.supervpn.fast.vpn.free.proxy.unblock.R
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseFragment
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BasePresenter
import com.supervpn.fast.vpn.free.proxy.unblock.common.di.component.AppComponent
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.SharePrefs
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.putBooleanPref
import com.supervpn.fast.vpn.free.proxy.unblock.common.utils.updateColorStatusBar
import com.supervpn.fast.vpn.free.proxy.unblock.common.widget.bottomnav.BottomNavBar
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainActivity
import com.supervpn.fast.vpn.free.proxy.unblock.presentation.main.MainFragment
import kotlinx.android.synthetic.main.fragment_premium.*
import javax.inject.Inject

class PremiumFragment : BaseFragment<PremiumView, PremiumPresenter>(), PremiumView, MainFragment.OnTabChanged {
    companion object {
        const val ACTION_GET = 0
        const val ACTION_UPGRADE = 1
        const val ACTION_DOWNGRADE = 2
    }

    @Inject
    lateinit var presenter: PremiumPresenter

    private var action = ACTION_GET

    override fun inject(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    override fun presenter(): BasePresenter<PremiumView>? {
        return presenter
    }

    override fun viewIF(): PremiumView? {
        return this
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_premium
    }

    override fun initView() {
        toggleButton(false)
        sivMonthly.setOnClickListener { toggleButton(false) }
        sivYearly.setOnClickListener { toggleButton(true) }
        btnGetPremium.apply {
            setOnClickListener { presenter.getPremium(activity, sivMonthly.isSelected, action) }
            togglePremiumButton()
        }
    }

    override fun onAcknowledgedPurchase(index: Int, newRequest: Boolean) {
        putBooleanPref(SharePrefs.KEY_PREMIUM, true)
        if (index == 0) {
            sivMonthly.purchase()
            sivYearly.initWith(getString(R.string.gold_yearly))
            btnGetPremium.text = getString(R.string.upgrade)
            action = ACTION_UPGRADE
        }

        if (index == 1) {
            sivMonthly.initWith(getString(R.string.gold_monthly))
            sivYearly.purchase()
            btnGetPremium.text = getString(R.string.downgrade)
            action = ACTION_DOWNGRADE
        }

        if (newRequest) {
            val message = if (index == 0) getString(R.string.message_get_premium_success)
            else getString(R.string.message_upgrade_premium_success)
            showSnackbarInMain(message)
        }
        togglePremiumButton()
    }

    override fun shouldShowAdMod(premium: Boolean) {
        putBooleanPref(SharePrefs.KEY_PREMIUM, premium)
        (activity as? MainActivity)?.showAdMobIfNeeded(premium)
    }

    override fun updatePrice(price1: String?, price2: String?, save: Int) {
        sivMonthly.setPrice(price1)
        sivYearly.setPrice(price2)
        sivYearly.setDescription("save \$$save% - 2 month com.fastvpn.free")
    }

    /**
     * MainFragment.OnTabChanged
     */
    override fun onChange(tabIndex: Int) {
        if (tabIndex != BottomNavBar.TAB_PREMIUM) {
            return
        }
        activity?.updateColorStatusBar(R.color.colorBackgroundPremium, true)
    }

    private fun toggleButton(isYearly: Boolean) {
        sivMonthly.isSelected = !isYearly
        sivYearly.isSelected = isYearly
        togglePremiumButton()
    }

    private fun togglePremiumButton() {
        btnGetPremium.isActivated = (sivMonthly.isSelected && sivMonthly.isEnabled) || (sivYearly.isSelected && sivYearly.isEnabled)
        btnGetPremium.isEnabled = btnGetPremium.isActivated
    }

}
